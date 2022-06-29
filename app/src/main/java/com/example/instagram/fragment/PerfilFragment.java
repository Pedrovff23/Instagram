package com.example.instagram.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;
import com.example.instagram.adpter.AdapterGrid;
import com.example.instagram.databinding.FragmentPerfilBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;


public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private Context context;
    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference usuarioRef;
    private AdapterGrid adapterGrid;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        //Configuraçoes inicias
        user = UsuarioFirebase.getUsuarioAtual();
        reference = InstanciasFirebase.databaseReference();
        usuarioRef = reference.child("usuario").child(UsuarioFirebase.getUsuarioAtual().getUid());

        binding.buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEditarPerfil();
            }
        });

        return binding.getRoot();
    }


    private void recuperarFotoFireBase(){
        Uri uri = user.getPhotoUrl();
        if(uri != null && !uri.toString().isEmpty()){
            Glide.with(context).load(uri).into(binding.perfilImagem);
        }else {
            binding.perfilImagem.setImageResource(R.drawable.avatar);
        }
    }

    private void abrirEditarPerfil(){
        Intent intent = new Intent(context, EditarPerfilActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Recuperar foto no firebase
        recuperarFotoFireBase();
        recuperarSeguindoSeguirPostagem();
        iniciarImagemLoder();
        carregarFotos();
    }

    private void recuperarSeguindoSeguirPostagem(){

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario!= null){

                    String postagem = String.valueOf(usuario.getPostagem());
                    String seguidores = String.valueOf(usuario.getSeguidores());
                    String seguindo = String.valueOf(usuario.getSeguindo());

                        binding.numberPublicPerfil.setText(postagem);
                        binding.numberSeguidPerfil.setText(seguidores);
                        binding.numberSeguindPerfil.setText(seguindo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Inicializar UniversalImagemLoader
    private void iniciarImagemLoder(){

        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void carregarFotos(){

        List<String> fotosUsuario = new ArrayList<>();

        DatabaseReference postagemRef = reference.child("postagens")
                .child(UsuarioFirebase.getUsuarioAtual().getUid());

        postagemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    fotosUsuario.add(postagem.getCaminhoDaFoto());
                }

                //configurar o adpter
                 adapterGrid = new AdapterGrid(context,R.layout.grid_postagem,fotosUsuario);
                 binding.gridViewPerfil.setAdapter(adapterGrid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }
}