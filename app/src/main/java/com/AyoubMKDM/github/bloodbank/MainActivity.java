package com.AyoubMKDM.github.bloodbank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    //Widgets
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<DataModelPost> mPosts;
    private PostAdapter mAdapter;

    //Firebase
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(MainActivity.this, "Welcome! " + mUser.getDisplayName(),
                                Toast.LENGTH_LONG).show();
        // Initializing values
        mToolbar = findViewById(R.id.main_activity_toolbar);
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
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    //TODO DELETE this method
    private void TESTpopulateRecyclerView() {
        final String lorem_ipsum = "Lorem Ipsum is simply dummy text of the printing and typesetting" +
                " industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                " when an unknown printer took a galley";
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mPosts.add(new DataModelPost("Title ZZZZZZ XXXXXXX", lorem_ipsum));
        mAdapter.notifyDataSetChanged();
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

    private void goBackToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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