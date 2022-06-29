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
import com.example.instagram.model.Cometario;
import com.example.instagram.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCometario extends RecyclerView.Adapter<AdapterCometario.MyViewHolder> {

    private List<Cometario> cometarioList;
    private Context context;

    public AdapterCometario(List<Cometario> cometarioList, Context context) {

        this.cometarioList = cometarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_cometario,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Cometario cometarios = cometarioList.get(position);

        String cometario = cometarios.getCometario();
        String nomeUsuarioCometario = cometarios.getNomeUsuario();
        String caminhoImagemComentario = cometarios.getCaminhoDaFoto();

        holder.textViewNome.setText(nomeUsuarioCometario);
        holder.textViewCometario.setText(cometario);

        if(caminhoImagemComentario != null && !caminhoImagemComentario.isEmpty()){

            Uri uri = Uri.parse(caminhoImagemComentario);
            Glide.with(context).load(uri).into(holder.imageView);

        }else {
            holder.imageView.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return cometarioList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView textViewNome;
        private TextView textViewCometario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagemComentario);
            textViewNome = itemView.findViewById(R.id.textNomeComentario);
            textViewCometario = itemView.findViewById(R.id.textComentario);
        }
    }
}
