package com.example.instagram.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.instagram.activity.FiltroActivity;
import com.example.instagram.databinding.FragmentPostagemBinding;
import com.example.instagram.helper.Permissao;

import java.io.ByteArrayOutputStream;


public class PostagemFragment extends Fragment {
    private final String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final int REQUESTE_CODE = 1;
    private FragmentPostagemBinding binding;
    private Button abrirGaleria;
    private Button abrirCamera;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostagemBinding.inflate(inflater, container, false);

        //inicializar componentes
        abrirCamera = binding.abrirCamera;
        abrirGaleria = binding.abrirGaleria;

        //Pedi Permissões
        Permissao.validarPermissoes(permissoes, getActivity(), REQUESTE_CODE);

        //iniciar o forResult
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                            Bitmap imagem = null;

                            try {

                                if (result.getData().getData() != null) {
                                    Uri imagemSelecionada = result.getData().getData();
                                    imagem = MediaStore.Images.Media
                                            .getBitmap(getActivity().getContentResolver(),
                                                    imagemSelecionada);
                                } else {
                                    imagem = (Bitmap) result.getData().getExtras().get("data");
                                }

                                if (imagem != null) {

                                    //Converter imagem em bytes arry
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    imagem.compress(Bitmap.CompressFormat.JPEG,75,baos);
                                    byte[] imagemBytes = baos.toByteArray();

                                    //Envia imagem escolhida para aplicação de filtro
                                    Intent i = new Intent(getActivity(),
                                            FiltroActivity.class);

                                    i.putExtra("fotoEscolhida",imagemBytes);

                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(i);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

        //Adicionar evento de click para o botão galeria
        abrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                }
            }
        });

        //Adicionar evento de click para os Camera
        abrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                }
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}