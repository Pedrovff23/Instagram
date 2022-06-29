package com.example.instagram.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.fragment.FeedFragment;
import com.example.instagram.fragment.PerfilFragment;
import com.example.instagram.fragment.PesquisaFragment;
import com.example.instagram.fragment.PostagemFragment;
import com.example.instagram.helper.InstanciasFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configurando toolbar
        setSupportActionBar(binding.toolbarMenu.toolbar);

        //Configurar Botton navigation view
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPege, new FeedFragment()).commit();
        configuraBottonNavigationView();


    }

    private void configuraBottonNavigationView() {

        BottomNavigationViewEx bottomNavigationViewEx = binding.bottonNavigation.bottonNavigation;

        //configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //Habilitar navegação
        habilitarNavegacao(bottomNavigationViewEx);


    }

    // Método para tratar evento de click na BottonNavigationView
    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.ic_home:
                        fragmentTransaction.replace(R.id.viewPege, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa:
                        fragmentTransaction.replace(R.id.viewPege, new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem:
                        fragmentTransaction.replace(R.id.viewPege, new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPege, new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuSair) {
            deslogar();
        }
        return super.onOptionsItemSelected(item);
    }


    public void deslogar() {
        try {

            FirebaseAuth auth = InstanciasFirebase.firebaseAuth();
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}