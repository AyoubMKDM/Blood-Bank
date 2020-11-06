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
    public static final String USERS_COLLECTION = "Users";
    public static FirebaseDatabase sDB;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseUser sUser;
    public static ArrayList<PostDataModel> sPosts   ;
    private static Context sCaller;
    //Firebase Cloud
    public static FirebaseFirestore sFirestoreDB;
    public static DocumentReference sDocumentReference;

    private FirebaseUtil() {}
    public static void openFBReference(Context caller, String ref){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            sDB = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sUser = sFirebaseAuth.getCurrentUser();
            sCaller = caller;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    checkAuthentication(caller);
                }
            };
        }
        sPosts = new ArrayList<>();
        sDatabaseReference = sDB.getReference().child(ref);
    }
    public static void checkAuthentication(Context context){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            redirectToLogin(context);
        }else{
            Log.d("TAG", "checkAuthenticationState: user is authenticated.");
        }
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

    public static void openUsersCollection(String uid){
        if (sFirestoreDB == null){
            sFirestoreDB = FirebaseFirestore.getInstance();
            sDocumentReference = sFirestoreDB.collection(USERS_COLLECTION).document(uid);
        }

    }
}