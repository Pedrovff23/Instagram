package com.example.instagram.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.instagram.R;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adpter.AdapterPesquisa;
import com.example.instagram.databinding.FragmentPesquisaBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;


public class PesquisaFragment extends Fragment {
    private FragmentPesquisaBinding binding;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<Usuario> usuariosList;
    private DatabaseReference usuarioRef;
    private Context context;
    private AdapterPesquisa adapterPesquisa;


    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPesquisaBinding.inflate(inflater,container,false);

        //Configurações inicias
        searchView = binding.searchViewPesquisa;
        recyclerView = binding.recyclerPesquisa;
        usuariosList = new ArrayList<>();
        usuarioRef = InstanciasFirebase.databaseReference().child("usuario");

        //Configurar RecyclerView
        configurarRecyclerView();

        //configurar searchview
        searchView.setQueryHint("Buscar Usuários");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarUsuarios(newText.toLowerCase());
                return true;
            }
        });

        //Item selecionado
        itemClickLista();

        return binding.getRoot();
    }

    private void pesquisarUsuarios(String texto){
        //Limpar lista
        usuariosList.clear();

        //Pesquisar usuário caso tenha texto na pesquisa
        if (texto.length() > 0){
            Query query = usuarioRef.orderByChild("nomePesquisa")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Limpar lista
                    usuariosList.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Usuario usuario = ds.getValue(Usuario.class);
                        String uid = UsuarioFirebase.getUsuarioAtual().getUid();

                        //Poderia usar o continue
                        if(!usuario.getId().equals(uid)){
                            usuariosList.add(usuario);
                        }
                    }
                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configurarRecyclerView(){
        adapterPesquisa = new AdapterPesquisa(usuariosList,context);
        recyclerView.setAdapter(adapterPesquisa);
        recyclerView.setHasFixedSize(true);
    }

    private void itemClickLista(){
        binding.recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(context,
                binding.recyclerPesquisa, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Usuario usuario = usuariosList.get(position);

                Intent intent = new Intent(context, PerfilAmigoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("usuarioSelecionado", usuario);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}