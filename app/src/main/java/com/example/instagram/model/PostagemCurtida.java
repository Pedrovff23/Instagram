package com.example.instagram.model;

import com.example.instagram.helper.InstanciasFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    public int qtsCurtidas = 0;
    public Usuario usuario;
    public Feed feed;

    public PostagemCurtida() {
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public int getQtsCurtidas() {
        return qtsCurtidas;
    }

    public void setQtsCurtidas(int qtsCurtidas) {
        this.qtsCurtidas = qtsCurtidas;
    }
    public void salvar(){

        //Objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome());
        dadosUsuario.put("caminhoDaFoto", usuario.getCaminhoDaFoto());

        DatabaseReference firebaseRef = InstanciasFirebase.databaseReference();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId()) // id_postagem
                .child(usuario.getId()); // id_usuario;

        pCurtidasRef.setValue(dadosUsuario);

        atualizarQts(1);
    }

    private void atualizarQts(int valor){
        DatabaseReference firebaseRef = InstanciasFirebase.databaseReference();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId())
                .child("qtsCurtidas"); // id_postagem

        setQtsCurtidas(getQtsCurtidas() + valor);

        pCurtidasRef.setValue(getQtsCurtidas());
    }

    public void remover(){

        DatabaseReference firebaseRef = InstanciasFirebase.databaseReference();
        DatabaseReference pCurtidasRef = firebaseRef
                .child("postagens-curtidas")
                .child(feed.getId()) // id_postagem
                .child(usuario.getId()); // id_usuario;
        pCurtidasRef.removeValue();

        atualizarQts(-1);
    }
}
