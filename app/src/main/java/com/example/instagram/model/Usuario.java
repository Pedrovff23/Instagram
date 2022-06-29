package com.example.instagram.model;

import com.example.instagram.helper.InstanciasFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String nomePesquisa;
    private String email;
    private String senha;
    private String caminhoDaFoto;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagem = 0;

    public Usuario() {
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagem() {
        return postagem;
    }

    public void setPostagem(int postagem) {
        this.postagem = postagem;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoDaFoto() {
        return caminhoDaFoto;
    }

    public void setCaminhoDaFoto(String caminhoDaFoto) {
        this.caminhoDaFoto = caminhoDaFoto;
    }

    public void salvar() {

        try {

            DatabaseReference database = InstanciasFirebase.databaseReference()
                    .child("usuario")
                    .child(getId());

            database.setValue(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void atualizarQtsPostagem() {

        DatabaseReference reference = InstanciasFirebase.databaseReference();
        DatabaseReference databse = reference
                .child("usuario")
                .child(getId());

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("postagem", getPostagem());

        databse.updateChildren(dados);
    }


    public void atualizarDados() {

        DatabaseReference reference = InstanciasFirebase.databaseReference();
        Map<String, Object> objeto = new HashMap<>();

        objeto.put("/usuario/" + getId() + "/nome", getNome());
        objeto.put("/usuario/" + getId() + "/caminhoDaFoto", getCaminhoDaFoto());
        objeto.put("/usuario/" + getId() + "/nomePesquisa", getNome().toLowerCase());

        reference.updateChildren(objeto);

    }

    @Exclude
    public Map<String, Object> getMap() {

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("id", getId());
        usuarioMap.put("seguindo", getSeguindo());
        usuarioMap.put("seguidores", getSeguidores());
        usuarioMap.put("postagem", getPostagem());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("nomePesquisa", getNomePesquisa());
        usuarioMap.put("email", getEmail());
        usuarioMap.put("caminhoDaFoto", getCaminhoDaFoto());

        return usuarioMap;
    }
}
