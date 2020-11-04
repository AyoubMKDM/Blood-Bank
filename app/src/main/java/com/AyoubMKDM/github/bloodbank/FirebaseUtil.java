package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {
    public static final String PATH_POST = "Post request";
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<PostDataModel> sPosts;
    private static Context sCaller;

    private FirebaseUtil() {}
    public static void openFBReference(Context caller, String ref){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sCaller = caller;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    checkAuthentication();
                }
            };
        }
        sPosts = new ArrayList<>();
        sDatabaseReference = sFirebaseDatabase.getReference().child(ref);
    }
    public static void checkAuthentication(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            signOut();
        }else{
            Log.d("TAG", "checkAuthenticationState: user is authenticated.");
        }
        }

    private static void signOut() {
        Intent intent = new Intent(sCaller, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sCaller.startActivity(intent);
    }

    public static void attachListener() {
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }
}

