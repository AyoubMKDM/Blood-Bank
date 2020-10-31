package com.AyoubMKDM.github.bloodbank;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    //Vars
    String TAG = "MainActivity";
    //Widgets
    private RecyclerView mRecyclerView;
    private List<DataModelPost> mPosts;
    private PostAdapter mAdapter;
    private ProgressBar loading;
    //Firebase
    private FirebaseUser mUser;
    private Toolbar mToolbar;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mDataManager = DataManager.getInstance();
        mToolbar = findViewById(R.id.main_activity_toolbar);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(HomePageActivity.this, "Welcome! " + mUser.getDisplayName(),
                Toast.LENGTH_LONG).show();
        // Initializing values
        mRecyclerView = findViewById(R.id.posts_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        mPosts = new ArrayList<DataModelPost>();
        mAdapter = new PostAdapter(mPosts,this);
        TESTpopulateRecyclerView();
        //work
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        dealingWithMenu();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent (HomePageActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });
    }
    private void dealingWithMenu() {
        Menu menu = mToolbar.getMenu();
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.option_sign_out:
                    //TODO add dialog for confirmation
                    Log.d(TAG, "onCreate: signed out");
                    signout();
                    return true;
                case R.id.option_settings:
                    Log.d(TAG, "onCreate: Setting luanched");
                    settings();
                    return true;
                default:
                    return false;
            }
        });
    }

    private void settings() {
        Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    //TODO DELETE this method
    private void TESTpopulateRecyclerView() {
        for (DataModelPost post : mDataManager.getPosts()){
            mPosts.add(post) ;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    //TODO generlize these two methods @checkAuthenticationState @goBackToLogin
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

    private void goBackToLogin() {
        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signout() {
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
        goBackToLogin();
    }
}