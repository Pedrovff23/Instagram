package com.example.instagram.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.adpter.AdapterMiniaturas;
import com.example.instagram.databinding.ActivityFiltroBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ActivityFiltroBinding binding;
    private ImageView imagemSelecionada;
    private RecyclerView filtroRecyclerView;
    private Bitmap imagemOriginal;
    private Bitmap imagemFiltroSelecionada;
    private Bitmap imagemFinalPostagem;
    private List<ThumbnailItem> listaFiltros;
    private AdapterMiniaturas adapterMiniaturas;
    private DatabaseReference usuarioRef;
    private DatabaseReference reference;
    private Postagem postagem;
    private String uid;
    private DataSnapshot seguidoresSnapshot;
    private Usuario usuarioLogado;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFiltroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //configurações inicias
        imagemSelecionada = binding.filtroImagemSelecionada;
        filtroRecyclerView = binding.filtroRecyclerView;
        uid = UsuarioFirebase.getUsuarioAtual().getUid();
        postagem = new Postagem();
        listaFiltros = new ArrayList<>();
        reference = InstanciasFirebase.databaseReference();
        usuarioRef = reference.child("usuario").child(UsuarioFirebase.getUsuarioAtual().getUid());

        //Configurar Toolbar
        setSupportActionBar(binding.filtroToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar dados para uma nova postagem
        recuperarDadosPostagem();

        //Recuperar imagem
        recuperarImagem();
    }

    private void abrirDialogCarregamento(String titulo) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();
    }

    private void recuperarDadosPostagem() {

        abrirDialogCarregamento("Carregando dados, aguarde!");

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);

                /*recuperar seguidores
                 */
                DatabaseReference seguidoresRef = reference
                        .child("seguidores")
                        .child(uid);
                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        seguidoresSnapshot = snapshot;
                        dialog.cancel();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.cancel();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void recuperarImagem() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] recuperarImagem = bundle.getByteArray("fotoEscolhida");
            imagemOriginal = BitmapFactory.decodeByteArray(recuperarImagem, 0,
                    recuperarImagem.length);
            imagemFinalPostagem = imagemOriginal;
            imagemSelecionada.setImageBitmap(imagemOriginal);

            //Configura recycler de filtros
            configurarRecyclerView();

            //Recuperar filtros
            recuperarFiltros();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void recuperarFiltros() {

        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagemOriginal;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        //Listar todos os filtros
        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filter : filters) {

            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagemOriginal;
            itemFiltro.filter = filter;
            itemFiltro.filterName = filter.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        configurarClickRecyclerView();
    }

    private void configurarRecyclerView() {

        adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        filtroRecyclerView.setLayoutManager(layoutManager);
        filtroRecyclerView.setHasFixedSize(true);

        filtroRecyclerView.setAdapter(adapterMiniaturas);
    }

    private void configurarClickRecyclerView() {

        filtroRecyclerView
                .addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                        filtroRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        ThumbnailItem filtroSelecionado = listaFiltros.get(position);

                        imagemFiltroSelecionada = imagemOriginal.copy(imagemOriginal.getConfig(),
                                true);
                        Filter filterPack = filtroSelecionado.filter;
                        imagemFinalPostagem = filterPack.processFilter(imagemFiltroSelecionada);

                        imagemSelecionada.setImageBitmap(imagemFinalPostagem);

                        //pegar imagemFiltro

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_filtros, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuFiltro) {
            salvarFotoFirebase();
        }
        return super.onOptionsItemSelected(item);
    }

    private void salvarFotoFirebase() {

        abrirDialogCarregamento("Salvando Postagem");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagemFinalPostagem.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imagemPostagem = baos.toByteArray();

        String nomeImagem = postagem.getIdPostagem() + ".jpeg";
        StorageReference reference = InstanciasFirebase.firebaseStorage();
        StorageReference imagemRef = reference
                .child("imagens")
                .child("postagem")
                .child(nomeImagem);

        UploadTask uploadTask = imagemRef.putBytes(imagemPostagem);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        salvarPostagem(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(),
                                "Erro ao fazer postagem",
                                Toast.LENGTH_SHORT)
                        .show();
                dialog.cancel();

            }
        });
    }

    private void salvarPostagem(Uri caminhoDaFoto) {

        String descricao = binding.textInputFiltro.getText().toString();
        String caminhodaFoto = caminhoDaFoto.toString();

        postagem.setCaminhoDaFoto(caminhodaFoto);
        postagem.setDescricao(descricao);
        postagem.setIdUsuario(uid);

        //Atualizar quantidade de postagem
        int qtsPostagem = usuarioLogado.getPostagem() + 1;
        usuarioLogado.setPostagem(qtsPostagem);
        usuarioLogado.atualizarQtsPostagem();

        if (postagem.salvar(seguidoresSnapshot)) {

            Toast.makeText(getApplicationContext(),
                    "Sucesso ao fazer postagem",
                    Toast.LENGTH_SHORT).show();

            dialog.cancel();
            finish();
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}