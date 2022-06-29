package com.example.instagram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.databinding.ActivityCadastroBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;


public class CadastroActivity extends AppCompatActivity {
    private ActivityCadastroBinding binding;

    private EditText usuario;
    private EditText email;
    private EditText senha;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuario = binding.editCadastroUsuario;
        email = binding.editCadastroEmail;
        senha = binding.editCadastroSenha;
        progressBar = binding.progressCadastro;
        Button cadastrar = binding.cadastroEntrar;

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarUsuario();
            }
        });

    }

    private void cadastrarUsuario() {

        String nomeUsuario = usuario.getText().toString();
        String emailUsuario = email.getText().toString();
        String senhaUsuario = senha.getText().toString();

        if (!nomeUsuario.isEmpty()) {
            if (!emailUsuario.isEmpty()) {
                if (!senhaUsuario.isEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    String nomePesquisa = nomeUsuario.toLowerCase();

                    Usuario usuario = new Usuario();
                    usuario.setSenha(senhaUsuario);
                    usuario.setEmail(emailUsuario);
                    usuario.setNome(nomeUsuario);
                    usuario.setNomePesquisa(nomePesquisa);
                    validarCadastro(usuario);

                } else {
                    Toast.makeText(this, "Digite sua senha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
        }
    }

    private void validarCadastro(Usuario usuario) {

        FirebaseAuth auth = InstanciasFirebase.firebaseAuth();

        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            try {

                                progressBar.setVisibility(View.GONE);

                                //Salvar dados no firebse
                                String uid = UsuarioFirebase.getUsuarioAtual().getUid();

                                usuario.setId(uid);
                                usuario.salvar();

                                //Salvar nome no perfil
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                Toast.makeText(getApplicationContext(),
                                        "Sucesso ao cadastrar",
                                        Toast.LENGTH_SHORT)
                                        .show();

                                Intent intent = new Intent(CadastroActivity.this,
                                        MainActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                startActivity(intent);

                                finish();

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {
                            progressBar.setVisibility(View.GONE);
                            String exception = "";

                            try {
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {
                                exception = "Digite uma senha mais forte!";

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                exception = "E-mail invalido, digite um e-mail válido";

                            } catch (FirebaseAuthUserCollisionException e) {
                                exception = "Usuário já cadastrado";

                            } catch (Exception e) {
                                exception = "Erro ao cadastrar usuario:\n" + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(),
                                    exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}