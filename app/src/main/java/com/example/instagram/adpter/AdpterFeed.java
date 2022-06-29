package com.example.instagram.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.CometariosActivity;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.PostagemCurtida;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdpterFeed extends RecyclerView.Adapter<AdpterFeed.MyViewHolder>{

    private List<Feed> listaFeed;
    private Context context;

    public AdpterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_feed,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Feed feed = listaFeed.get(position);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        String uriFotoUsuario = feed.getFotoUsuario();
        String uriFotoFeed = feed.getCaminhoDaFoto();

        //carregando os dados do feed

        //Foto Usuário
        if(uriFotoUsuario != null && !uriFotoUsuario.isEmpty()){
            Uri uri = Uri.parse(uriFotoUsuario);
            Glide.with(context).load(uri).into(holder.fotoPerfil);
        }else {
            holder.fotoPerfil.setImageResource(R.drawable.avatar);
        }

        //foto Postagem
        if(uriFotoFeed != null && !uriFotoFeed.isEmpty()){
            Uri uri = Uri.parse(uriFotoFeed);
            Glide.with(context).load(uri).into(holder.fotoPostagem);
        }else {
            holder.fotoPostagem.setImageResource(R.drawable.avatar);
        }
        holder.descricao.setText(feed.getDescricao());
        holder.nome.setText(feed.getNomeUsuario());

        //Adicionar evento de clique nos cometários
        holder.visualizarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CometariosActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("idPostagem",feed.getId());
                context.startActivity(i);
            }
        });


        //Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = InstanciasFirebase.databaseReference()
                .child("postagens-curtidas")
                .child(feed.getId());

        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int qtsCurtidas = 0;

                if(snapshot.hasChild("qtsCurtidas")){
                    PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                    qtsCurtidas = postagemCurtida.getQtsCurtidas();
                }

                 //Verificar se já foi clicado
                if(snapshot.hasChild(usuarioLogado.getId())){
                    holder.likeButton.setLiked(true);
                }else {
                    holder.likeButton.setLiked(false);
                }

                //Montar objeto postagem curtida
                PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed(feed);
                curtida.setUsuario(usuarioLogado);
                curtida.setQtsCurtidas(qtsCurtidas);

                //Adicionar eventos para curtir uma foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        holder.qtsCurtidas.setText(curtida.getQtsCurtidas() + " curtidas");
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.remover();
                        holder.qtsCurtidas.setText(curtida.getQtsCurtidas() + " curtidas");
                    }
                });
                holder.qtsCurtidas.setText(curtida.getQtsCurtidas() + " curtidas");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final CircleImageView fotoPerfil;
        private final TextView nome, descricao, qtsCurtidas;
        private final ImageView fotoPostagem, visualizarComentarios;
        private LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoPerfil = itemView.findViewById(R.id.imagemFeed);
            nome = itemView.findViewById(R.id.nomeFeed);
            descricao = itemView.findViewById(R.id.descricaoFeed);
            qtsCurtidas = itemView.findViewById(R.id.curtidasFeed);
            fotoPostagem = itemView.findViewById(R.id.imagemPrincipalFeed);
            visualizarComentarios = itemView.findViewById(R.id.imagemCometarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);
        }
    }
}
