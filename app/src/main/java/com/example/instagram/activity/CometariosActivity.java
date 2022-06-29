package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adpter.AdapterCometario;
import com.example.instagram.databinding.ActivityCometariosBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Cometario;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CometariosActivity extends AppCompatActivity {
    private ActivityCometariosBinding binding;
    private EditText editTextComentario;
    private String idPostagem;
    private Usuario usuario;
    private RecyclerView recyclerViewComentarios;
    private DatabaseReference reference;
    private DatabaseReference comentariosRef;
    private AdapterCometario adapterCometario;
    private ValueEventListener eventListener;
    private List<Cometario> cometarioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCometariosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurações iniciais
        editTextComentario = binding.editCometario;
        recyclerViewComentarios = binding.recyclerViewComentario;
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        reference = InstanciasFirebase.databaseReference().child("comentarios");

        //configurar toolbar
        setSupportActionBar(binding.toolbarCometario.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Comentários");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Recuperar id postagem
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }

        //Configurar RecyclerView
        configurarRecyclerView();

        //Listar os Cometario
        listarCometario();
    }

    private void configurarRecyclerView(){
        adapterCometario = new AdapterCometario(cometarioList, this);
        recyclerViewComentarios.setHasFixedSize(true);
        recyclerViewComentarios.setAdapter(adapterCometario);

    }


    private void listarCometario(){
        comentariosRef = reference.child(idPostagem);
        eventListener = comentariosRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cometarioList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    cometarioList.add(ds.getValue(Cometario.class));
                }
                adapterCometario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void salvarComenterio(View view){

        String textoComentario = editTextComentario.getText().toString();
        if(!textoComentario.equals("") && textoComentario != null){

            Cometario cometario = new Cometario();
            cometario.setIdPostagem(idPostagem);
            cometario.setIdUsuario(usuario.getId());
            cometario.setNomeUsuario(usuario.getNome());
            cometario.setCaminhoDaFoto(usuario.getCaminhoDaFoto());
            cometario.setCometario(textoComentario);

            if(cometario.salvar()){
                Toast.makeText(this,
                        "Cometário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }


        }else {
            Toast.makeText(this,
                    "Digite seu cometário",
                    Toast.LENGTH_SHORT).show();
        }
        editTextComentario.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(eventListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}