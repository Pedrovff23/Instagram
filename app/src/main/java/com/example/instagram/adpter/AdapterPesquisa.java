package com.example.instagram.adpter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {

    private List<Usuario> usuarioList;
    private Context context;

    public AdapterPesquisa(List<Usuario> list, Context context) {

        this.usuarioList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_pesquisa,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);
        String nome = usuario.getNome();
        String email = usuario.getEmail();
        String caminhoImagem = usuario.getCaminhoDaFoto();

        holder.textViewNome.setText(nome);
        holder.textViewEmail.setText(email);

        if(caminhoImagem != null && !caminhoImagem.isEmpty()){

            Uri uri = Uri.parse(caminhoImagem);
            Glide.with(context).load(uri).into(holder.imageView);

        }else {
            holder.imageView.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView textViewNome;
        private TextView textViewEmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagePesquisa);
            textViewNome = itemView.findViewById(R.id.textPesquisaNome);
            textViewEmail = itemView.findViewById(R.id.textPesquisaEmail);
        }
    }
}
