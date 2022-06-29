package com.example.instagram.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.adpter.AdapterGrid;
import com.example.instagram.databinding.ActivityPerfilAmigoBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PerfilAmigoActivity extends AppCompatActivity {

    private ActivityPerfilAmigoBinding binding;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresAmigosRef;
    private DatabaseReference postagemUsuarioRef;
    private ValueEventListener eventListener;
    private Usuario usuarioAmigo;
    private Usuario usuarioLogado;
    private String uidUsuarioAtual;
    private AdapterGrid adapterGrid;
    private List<Postagem> postagens;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilAmigoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurações inicias
        firebaseRef = InstanciasFirebase.databaseReference();
        usuariosRef = firebaseRef.child("usuario");
        seguidoresAmigosRef = firebaseRef.child("seguidores");
        uidUsuarioAtual = UsuarioFirebase.getUsuarioAtual().getUid();
        binding.contentPerfilAmigo.buttonSeguirPerfilAmigos.setText("Carregando");

        //Configurar toolbar
        setSupportActionBar(binding.perfilAmigoToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Pegar dados do amigo selecionado
        pegarDadosAmigos();
        recuperarSeguindoSegidoresPostagem();

        //Recuperar dados usuário logado
        recuperarDadosUsuarioLogado();
    }

    private void pegarDadosAmigos() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            usuarioAmigo = (Usuario) bundle.getSerializable("usuarioSelecionado");
            //Configurar referencia postagens usuário
            postagemUsuarioRef = firebaseRef.child("postagens").child(usuarioAmigo.getId());

            String caminhoFoto = usuarioAmigo.getCaminhoDaFoto();
            String nome = usuarioAmigo.getNome();

            Objects.requireNonNull(getSupportActionBar()).setTitle(nome);

            if (caminhoFoto != null && !caminhoFoto.isEmpty()) {

                Uri uri = Uri.parse(caminhoFoto);

                Glide.with(getApplicationContext()).load(uri).into(binding
                        .contentPerfilAmigo
                        .perfilAmigoImagem);
            } else {
                binding.contentPerfilAmigo.perfilAmigoImagem.setImageResource(R.drawable.avatar);
            }
        }
        //inicilaizar image loader
        inicializarImageLoder();

        //Carrega fotos do usuário
        carregarFotosPostagem();

        //Abrir foto clicada
        binding.contentPerfilAmigo.gridViewPerfilAmigo
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Postagem postagem = postagens.get(position);
                        Intent i = new Intent(PerfilAmigoActivity.this,
                                VisualizarPostagemActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("postagem", postagem);
                        i.putExtra("usuario", usuarioAmigo);
                        startActivity(i);
                    }
                });

    }

    private void recuperarSeguindoSegidoresPostagem() {

        usuarioAmigosRef = usuariosRef.child(usuarioAmigo.getId());
        eventListener = usuarioAmigosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                String seguindo = String.valueOf(usuario.getSeguindo());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String postagem = String.valueOf(usuario.getPostagem());

                binding.contentPerfilAmigo.numberPublicPerfilAmigo.setText(postagem);
                binding.contentPerfilAmigo.numberSeguidPerfilAmigo.setText(seguidores);
                binding.contentPerfilAmigo.numberSeguindPerfilAmigo.setText(seguindo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Inicializar UniversalImagemLoader
    public void inicializarImageLoder() {

        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void carregarFotosPostagem() {

        //Recuperar as fotos postadas pelo usuario
        postagens = new ArrayList<>();
        postagemUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> urlsFotos = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Postagem postagem = ds.getValue(Postagem.class);
                    postagens.add(postagem);
                    urlsFotos.add(postagem.getCaminhoDaFoto());
                }

                //Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(),
                        R.layout.grid_postagem,
                        urlsFotos);

                binding.contentPerfilAmigo.gridViewPerfilAmigo.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarDadosUsuarioLogado() {

        usuarioLogadoRef = usuariosRef.child(uidUsuarioAtual);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //recuperar usuario logado
                usuarioLogado = snapshot.getValue(Usuario.class);

                /* verificar se usuário já está seguindo amigo selecionado */
                verificaSegueUsuarioAmigo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificaSegueUsuarioAmigo() {


        DatabaseReference seguidorRef = seguidoresAmigosRef
                .child(usuarioAmigo.getId())
                .child(uidUsuarioAtual);


        seguidorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSeguindo(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void isSeguindo(boolean isSeguindo) {
        if (isSeguindo) {
            binding.contentPerfilAmigo.buttonSeguirPerfilAmigos.setText("Seguindo");
        } else {
            binding.contentPerfilAmigo.buttonSeguirPerfilAmigos.setText("Seguir");

            //botão para seguir usuário
            binding.contentPerfilAmigo.buttonSeguirPerfilAmigos
                    .setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {

                            //salvar Seguidor
                            salvarSeguidor(usuarioLogado, usuarioAmigo);
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo) {

        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("Nome", usuarioLogado.getNome());
        dadosUsuarioLogado.put("caminhoDaFoto", usuarioLogado.getCaminhoDaFoto());

        DatabaseReference seguidorRef = seguidoresAmigosRef
                .child(usuarioAmigo.getId())
                .child(usuarioLogado.getId());


        seguidorRef.setValue(dadosUsuarioLogado);

        binding.contentPerfilAmigo.buttonSeguirPerfilAmigos.setText("Seguindo");
        binding.contentPerfilAmigo.buttonSeguirPerfilAmigos.setOnClickListener(null);

        //atualizar seguidores e seguindo
        adicionarSeguidoresESeguindo(usuarioLogado, usuarioAmigo);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void adicionarSeguidoresESeguindo(Usuario uLogado, Usuario uAmigo) {

        uAmigo.setNomePesquisa(uAmigo.getNome().toLowerCase());
        Map<String, Object> atualizarSeguidor = uAmigo.getMap();
        atualizarSeguidor.replace("seguidores", uAmigo.getSeguidores() + 1);

        DatabaseReference seguidoresRef = usuariosRef.child(usuarioAmigo.getId());
        seguidoresRef.updateChildren(atualizarSeguidor);

        uLogado.setNomePesquisa(uLogado.getNome().toLowerCase());
        Map<String, Object> atualizarSeguindo = uLogado.getMap();
        atualizarSeguindo.replace("seguindo", uLogado.getSeguindo() + 1);

        DatabaseReference seguindoRef = usuariosRef.child(uidUsuarioAtual);
        seguindoRef.updateChildren(atualizarSeguindo);
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigosRef.removeEventListener(eventListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}