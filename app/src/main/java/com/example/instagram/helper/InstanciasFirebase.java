package com.example.instagram.helper;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InstanciasFirebase {
    private static DatabaseReference reference;
    private static FirebaseAuth auth;
    private static StorageReference storage;

     public static DatabaseReference databaseReference(){
        if(reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }
       return reference;
    }

    public static FirebaseAuth firebaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference firebaseStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
