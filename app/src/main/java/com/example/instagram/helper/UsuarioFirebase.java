package com.example.instagram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual() {

        FirebaseAuth auth = InstanciasFirebase.firebaseAuth();
        return auth.getCurrentUser();
    }

    public static void atualizarNomeUsuario(String nome) {

        try {

            //Usuario Logado no App
            FirebaseUser usuarioAtual = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(nome)
                    .build();

            usuarioAtual.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.d("Perfil", "Erro ao atualizar perfil");
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void autalizarFotoUsuario(Uri foto){

        try {

            //Usuario Logado no App
            FirebaseUser usuarioAtual = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(foto)
                    .build();

            usuarioAtual.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Log.d("Perfil", "Erro ao atualizar foto perfil");
                            }
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static Usuario getDadosUsuarioLogado() {

        FirebaseUser usuarioAtual = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioAtual.getEmail());
        usuario.setNome(usuarioAtual.getDisplayName());
        usuario.setId(usuarioAtual.getUid());

        if (usuarioAtual.getPhotoUrl() == null) {
            usuario.setCaminhoDaFoto("");
        } else {
            usuario.setCaminhoDaFoto(usuarioAtual.getPhotoUrl().toString());
        }

        return usuario;
    }
}