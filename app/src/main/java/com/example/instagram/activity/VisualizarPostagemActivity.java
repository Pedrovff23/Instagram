package com.example.instagram.activity;

import android.app.usage.NetworkStatsManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityVisualizarPostagemBinding;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private ActivityVisualizarPostagemBinding binding;
    private Toolbar toolbar;
    private CircleImageView imagemIcone;
    private ImageView imagemPrincipal;
    private TextView nomeUsuario;
    private TextView curtidas;
    private TextView descricao;
    private Postagem postagem;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisualizarPostagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configuraçoes inicias
        toolbar = binding.visualizarPostagemToolbar.toolbar;
        imagemIcone = binding.imagemVisualizarPostagem;
        imagemPrincipal = binding.imagemPrincipalVisualizarPostagem;
        nomeUsuario = binding.nomeVisualizarPostagem;
        curtidas = binding.curtidasVisualizarPostagem;
        descricao = binding.descricaoVisualizarPostagem;

        //Configurar toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vizualizar postagem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //recuperar usuario
        recuperarDados();
    }


    private void recuperarDados(){

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            postagem = (Postagem) bundle.getSerializable("postagem");
            usuario = (Usuario) bundle.getSerializable("usuario");
        }
        preencherDados();
    }

    private void preencherDados(){

        String usuarioCaminhoFoto = usuario.getCaminhoDaFoto();
        String usuarioNome = usuario.getNome();
        String postagemDescricao = postagem.getDescricao();
        String postagemCaminhoFoto = postagem.getCaminhoDaFoto();

        //Foto Usuario
        if(usuarioCaminhoFoto != null && !usuarioCaminhoFoto.isEmpty()){
            Uri uriUsuario = Uri.parse(usuarioCaminhoFoto);
            Glide.with(this).load(uriUsuario).into(imagemIcone);
        }else {
            imagemIcone.setImageResource(R.drawable.avatar);
        }

        //Foto Postagem
        if(postagemCaminhoFoto != null && !postagemCaminhoFoto.isEmpty()){
            Uri uriUsuario = Uri.parse(postagemCaminhoFoto);
            Glide.with(this).load(uriUsuario).into(imagemPrincipal);
        }else {
            imagemPrincipal.setImageResource(R.drawable.avatar);
        }

       nomeUsuario.setText(usuarioNome);
        descricao.setText(postagemDescricao);
    }



    @Override
    public boolean onSupportNavigateUp() {


        finish();
        return false;
    }
}