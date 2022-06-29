package com.example.instagram.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class AdapterMiniaturas extends RecyclerView.Adapter<AdapterMiniaturas.MyViewHolder> {

    private List<ThumbnailItem> listaFiltros;
    private Context context;

    public AdapterMiniaturas(List<ThumbnailItem> thumbnailItems, Context context){
        this.listaFiltros = thumbnailItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_filtros,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ThumbnailItem item = listaFiltros.get(position);
        String nomeFiltro = item.filterName;
        Bitmap fotoFiltro = item.image;

        holder.nomeFiltro.setText(nomeFiltro);
        holder.fotoFiltro.setImageBitmap(fotoFiltro);
    }

    @Override
    public int getItemCount() {
        return listaFiltros.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView fotoFiltro;
        TextView nomeFiltro;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoFiltro = itemView.findViewById(R.id.imageViewFiltro);
            nomeFiltro = itemView.findViewById(R.id.textViewNomeFiltro);
        }
    }
}
