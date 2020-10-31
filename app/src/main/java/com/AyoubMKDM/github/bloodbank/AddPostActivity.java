package com.AyoubMKDM.github.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddPostActivity extends AppCompatActivity {
    //Vars
    String TAG = "AddPostActivity";
    //Widgets
    Toolbar mToolbar;
    EditText mPostTitle, mPostTextBody;
    Button mButtonShare;
    //Firebase
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mToolbar = findViewById(R.id.add_post_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_go_back_dark);
        mToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(AddPostActivity.this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        mButtonShare = findViewById(R.id.button_share);
        mPostTitle = findViewById(R.id.edittext_title);
        mPostTextBody = findViewById(R.id.post_text_body);

        mButtonShare.setOnClickListener(vew -> {
            final String title = mPostTitle.getText().toString();
            final String body = mPostTextBody.getText().toString();
            final String userName = mUser.getDisplayName();

            DataModelPost post = new DataModelPost(title, body);
            DataManager dataManager = DataManager.getInstance();
            dataManager.addPost(post);
            Intent intent = new Intent(AddPostActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");
            goBackToLogin();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    private void goBackToLogin(){
        Intent intent = new Intent(AddPostActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}