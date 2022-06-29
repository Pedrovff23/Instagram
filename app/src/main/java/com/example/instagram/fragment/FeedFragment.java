package com.example.instagram.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.adpter.AdpterFeed;
import com.example.instagram.databinding.FragmentFeedBinding;
import com.example.instagram.helper.InstanciasFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private RecyclerView recyclerFeed;
    private AdpterFeed adpterFeed;
    private Context context;
    private final List<Feed> listFeed = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private DatabaseReference feedRef;


    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        recyclerFeed = binding.recyclerFeed;

        //Configurações inicias
        feedRef = InstanciasFirebase.databaseReference()
                .child("feed")
                .child(UsuarioFirebase.getUsuarioAtual().getUid());


        //Configurar RecyclerView
        adpterFeed = new AdpterFeed(listFeed, context);
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setAdapter(adpterFeed);

        return binding.getRoot();
    }

    private void listarFeed() {
        valueEventListener = feedRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Feed feed = ds.getValue(Feed.class);
                    listFeed.add(feed);
                }
                Collections.reverse(listFeed);
                adpterFeed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}