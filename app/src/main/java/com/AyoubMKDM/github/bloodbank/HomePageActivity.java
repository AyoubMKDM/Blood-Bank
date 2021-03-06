package com.AyoubMKDM.github.bloodbank;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    //Vars
    String TAG = "MainActivity";
    //TODO resize gridLayout https://www.youtube.com/watch?v=b6AVdCKoyiQ
    //Widgets
    private RecyclerView mRecyclerView;
    private List<PostDataModel> mPosts;
    private PostAdapter mAdapter;
    private ProgressBar loading;
    private Toolbar mToolbar;
    //Firebase
    private FirebaseDatabase mDB;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mEventListener;

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = findViewById(R.id.main_activity_toolbar);

        // Initializing values
        ConnectToFirbase();
        mEventListener = new DatabaseListener();
        mDatabaseReference.addChildEventListener(mEventListener);
        mRecyclerView = findViewById(R.id.posts_recycler_view);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mPosts = new ArrayList<PostDataModel>();
        mAdapter = new PostAdapter(mPosts,this);
        //work
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        onOptionsMenuItemSelected();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent (HomePageActivity.this, AddPostActivity.class);
            startActivity(intent);
        });

    }

    private void ConnectToFirbase() {
        FirebaseUtil.openFBReference(this);
        mDB = FirebaseUtil.sDB;
        mDatabaseReference = FirebaseUtil.sDatabaseReference.child(FirebaseUtil.PATH_POST);
    }

    private void onOptionsMenuItemSelected() {
        Menu menu = mToolbar.getMenu();
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.option_sign_out:
                    showSignOutAlert();
                    Log.d(TAG, "onCreate: signed out");
                    return true;
                case R.id.option_settings:
                    Log.d(TAG, "onCreate: Setting luanched");
                    goToSettings();
                    return true;
                case R.id.app_bar_search:
                    Intent intent = new Intent(HomePageActivity.this,SearchActivity.class);
                    startActivity(intent);
                default:
                    return false;
            }
        });
    }

    private void showSignOutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out");
        builder.setMessage("Are you sure you want to sign out");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> signout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goToSettings() {
        Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signout() {
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
    }

    public class DatabaseListener implements ChildEventListener {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            PostDataModel post = snapshot.getValue(PostDataModel.class);
            post.setPostId(snapshot.getKey());
            populateMainScreen(post);
        }

        private void populateMainScreen(PostDataModel post) {
            mPosts.add(post);
            mAdapter.notifyItemInserted(mPosts.size()-1);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            //TODO make sure this is the optimal solution
            int index = -1;
            for (int i = 0; i < mPosts.size(); i++){
                if (mPosts.get(i).getPostId().equals(snapshot.getKey())) index = i;
            }
            if (index != -1) {
                mPosts.remove(index);
                mAdapter.notifyItemRemoved(index);
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}