package com.example.instagram.model;

import com.example.instagram.helper.InstanciasFirebase;
import com.google.firebase.database.DatabaseReference;

public class Cometario {

    private String idCometario;
    private String idPostagem;
    private String idUsuario;
    private String caminhoDaFoto;
    private String nomeUsuario;
    private String cometario;

        public Cometario() {
        }

        public String getIdCometario() {
                return idCometario;
        }

        public void setIdCometario(String idCometario) {
                this.idCometario = idCometario;
        }

        public String getIdPostagem() {
                return idPostagem;
        }

        public void setIdPostagem(String idPostagem) {
                this.idPostagem = idPostagem;
        }

        public String getIdUsuario() {
                return idUsuario;
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

        public String getNomeUsuario() {
                return nomeUsuario;
        }

        public void setNomeUsuario(String nomeUsuario) {
                this.nomeUsuario = nomeUsuario;
        }

        public String getCometario() {
                return cometario;
        }

        public void setCometario(String cometario) {
                this.cometario = cometario;
        }

        public boolean salvar(){

            DatabaseReference comentarioRef = InstanciasFirebase.databaseReference()
                    .child("comentarios")
                    .child(getIdPostagem());

            String chave = comentarioRef.push().getKey();
            setIdCometario(chave);
            comentarioRef.child(getIdCometario()).setValue(this);

            return true;
        }
}
