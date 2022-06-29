package com.example.instagram.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.databinding.ActivityLoginBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private Button logarUsuario;
    private TextView cadastrarUsuario;
    private TextView resetarSenha;
    private EditText email;
    private EditText senha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = binding.progressLogin;
        resetarSenha = binding.textLoginNovaSenha;
        isLogado();

        //Entrar com usuário já existente
        email = binding.editLoginEmail;
        senha = binding.editLoginSenha;
        logarUsuario = binding.loginEntrar;
        logarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logar();
            }
        });


        //Cadastrar um novo usuário
        cadastrarUsuario = binding.textLoginCadastrar;
        cadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void isLogado() {
        FirebaseUser user = InstanciasFirebase.firebaseAuth().getCurrentUser();
        if (user != null) {

            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            finish();
        }
    }

    private void logar() {

        String emailUser = email.getText().toString();
        String senhaUser = senha.getText().toString();

        if (!emailUser.isEmpty()) {
            if (!senhaUser.isEmpty()) {

                progressBar.setVisibility(View.VISIBLE);

                Usuario usuario = new Usuario();
                usuario.setEmail(emailUser);
                usuario.setSenha(senhaUser);

                validarLogin(usuario);

            } else {
                Toast.makeText(this, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
        }
    }

    private void validarLogin(Usuario usuario) {

        FirebaseAuth auth = InstanciasFirebase.firebaseAuth();
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            finish();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            String exception = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                exception = "Usuário não cadastrado, cadastre um novo usuário";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                restarSenha();
                                exception = "E-mail ou a senha está errado";
                            } catch (Exception e) {
                                restarSenha();
                                exception = "Erro ao logar:\n" + e.getMessage();
                            }
                            Toast.makeText(LoginActivity.this,
                                    exception, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    public void restarSenha() {

        resetarSenha.setVisibility(View.VISIBLE);
        FirebaseAuth auth = InstanciasFirebase.firebaseAuth();
        String emailUser = email.getText().toString();

        resetarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!emailUser.isEmpty()) {

                    auth.sendPasswordResetEmail(emailUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(LoginActivity.this,
                                                        "Email para recuperar " +
                                                                "senha enviado para " + emailUser,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                        resetarSenha.setVisibility(View.GONE);
                                    }
                                }
                            });

                } else {
                    Toast.makeText(LoginActivity.this,
                                    "Digite seu email, e pressione \"Definir uma nova senha\"",
                                    Toast.LENGTH_SHORT)
                            .show();
                }

            }
    });
}
}

