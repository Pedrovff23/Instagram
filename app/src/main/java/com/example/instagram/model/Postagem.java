package com.example.instagram.model;

import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {
    private String descricao;
    private String idUsuario;
    private String idPostagem;
    private String caminhoDaFoto;


    public Postagem() {
        DatabaseReference firebaseRef = InstanciasFirebase.databaseReference();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setIdPostagem(idPostagem);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoDaFoto() {
        return caminhoDaFoto;
    }

    public void setCaminhoDaFoto(String caminhoDaFoto) {
        this.caminhoDaFoto = caminhoDaFoto;
    }

    public Boolean salvar(DataSnapshot seguidoresSnapshot){

        Map<String, Object> objeto = new HashMap<>();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = InstanciasFirebase.databaseReference();

        //Referência para postagem
        String combinacaoId = "/" + getIdUsuario() + "/" + getIdPostagem();

        objeto.put("/postagens" + combinacaoId , this);

        //Referência para postagem
        for(DataSnapshot seguidores: seguidoresSnapshot.getChildren()){

            String idSeguidor = seguidores.getKey();

            //Monta objeto para salvar
            HashMap<String,Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("caminhoDaFoto", getCaminhoDaFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id",getIdPostagem());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoDaFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getIdPostagem();
            objeto.put("/feed" + idsAtualizacao , dadosSeguidor);

        }

        firebaseRef.updateChildren(objeto);
        return true;
    }
}
