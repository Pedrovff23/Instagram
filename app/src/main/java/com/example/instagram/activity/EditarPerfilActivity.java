package com.example.instagram.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityEdtitarPerfilBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.Permissao;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class EditarPerfilActivity extends AppCompatActivity {
    private ActivityEdtitarPerfilBinding binding;
    private Usuario usuarioLogado;
    private StorageReference storage;
    private FirebaseUser usuarioAtual;
    private ActivityResultLauncher<Intent> resultLauncher;
    private final String[] permissoes = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUESTE_CODE = 1;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEdtitarPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurações inicias
        storage = InstanciasFirebase.firebaseStorage();
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //pedir permissões
        Permissao.validarPermissoes(permissoes,this,REQUESTE_CODE);

        //Configurar o ActivityResult
        configurarActivityResult();


        //configurar toolbar
        setSupportActionBar(binding.toolbarEditarPerfil.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Recuperar dados do firebase
        recuperarDadosFirebase();

        //Salvar alteraçoes
        binding.bottomSalvarAlteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAlteracaoNoFirebase();
            }
        });

        //Alterar foto do usuario
        binding.textEditarFoto.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"QueryPermissionsNeeded", "IntentReset"})
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                resultLauncher.launch(intent);
            }
        });

    }


    private void recuperarDadosFirebase() {

        FirebaseUser usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        String nome = usuarioAtual.getDisplayName();
        String email = usuarioAtual.getEmail();
        Uri caminhoFoto = usuarioAtual.getPhotoUrl();

        if(caminhoFoto != null && !caminhoFoto.toString().isEmpty()){

            Glide.with(EditarPerfilActivity.this)
                    .load(caminhoFoto)
                    .into(binding.imagemEditarPerfil);
        }else {
            binding.imagemEditarPerfil.setImageResource(R.drawable.avatar);
        }

        binding.textNomeUsuario.setText(nome);
        binding.textEmailUsuario.setText(email);
        binding.textNomeUsuario.setSelection(nome.length());
    }

    private void salvarAlteracaoNoFirebase() {

        String nomeAtualizado = binding.textNomeUsuario.getText().toString();

        // Atualizar no perfil
        UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

        // Atualizar no database
        usuarioLogado.setNome(nomeAtualizado);
        usuarioLogado.setNomePesquisa(nomeAtualizado.toLowerCase());
        usuarioLogado.atualizarDados();

        Toast.makeText(getApplicationContext(),
                        "Dados atualizados com sucesso",
                        Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void configurarActivityResult() {

        resultLauncher = registerForActivityResult(new ActivityResultContracts
                        .StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Bitmap imagem = null;

                            try {
                                //Selecao apenas da galeria
                                Uri localImagemSelecionada = result.getData().getData();
                                imagem = MediaStore
                                        .Images
                                        .Media
                                        .getBitmap(getContentResolver(),
                                                localImagemSelecionada);

                                if (imagem != null) {

                                    //Configurar imagem na tela
                                    binding.imagemEditarPerfil.setImageBitmap(imagem);

                                    //Salvar foto no Firebase
                                    salvarImgemNoFirebase(imagem);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void salvarImgemNoFirebase(Bitmap caminhoDaFoto){

        //Iniciar o dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Salvando alterações, aguarde!");
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        caminhoDaFoto.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] dadosImagem = baos.toByteArray();

        String nomeFoto = usuarioAtual.getUid() + ".jpeg";
        StorageReference imagemRef = storage
                .child("imagens")
                .child("perfil")
                .child(nomeFoto);

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Salvar no Database
                Usuario usuario = UsuarioFirebase.getDadosUsuarioLogado();
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String caminhoDaFoto = uri.toString();
                        String nomePesquisa = usuario.getNome().toLowerCase();
                        usuario.setCaminhoDaFoto(caminhoDaFoto);
                        usuario.setNomePesquisa(nomePesquisa);
                        usuario.atualizarDados();

                        //Salvar no Perfil
                        UsuarioFirebase.autalizarFotoUsuario(uri);
                        dialog.cancel();
                        Toast.makeText(getApplicationContext()
                                        ,"Sucesso ao salvar foto"
                                        , Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(getApplicationContext()
                                ,"Erro ao salvar foto"
                                , Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}