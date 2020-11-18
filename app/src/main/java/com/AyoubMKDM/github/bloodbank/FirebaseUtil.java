package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirebaseUtil {
    public static final String PATH_POST = "Post request";
    public static final String USER_PATH = "Users_info";
    public static FirebaseDatabase sDB;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<PostDataModel> sPosts   ;
    private static Context sCaller;

    private FirebaseUtil() {}
    public static void openFBReference(Context caller){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            sDB = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sCaller = caller;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user == null){
                        redirectToLogin(caller);
                    }else{
                        Log.d("TAG", "checkAuthenticationState: user is authenticated.");
                    }
                }
            };
        }
        sPosts = new ArrayList<>();
        sDatabaseReference = sDB.getReference();
    }

    public static void redirectToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void attachListener() {
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

}