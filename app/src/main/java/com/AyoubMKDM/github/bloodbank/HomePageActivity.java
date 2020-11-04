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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    //Vars
    String TAG = "MainActivity";
    //TODO define it in some contract class
    //TODO resize gridLayout https://www.youtube.com/watch?v=b6AVdCKoyiQ
    //Widgets
    private RecyclerView mRecyclerView;
    private List<PostDataModel> mPosts;
    private PostAdapter mAdapter;
    private ProgressBar loading;
    private Toolbar mToolbar;
    //Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mEventListener;
    private FirebaseUser mUser;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mDataManager = DataManager.getInstance();
        mToolbar = findViewById(R.id.main_activity_toolbar);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(HomePageActivity.this, "Welcome! " + mUser.getDisplayName(),
                Toast.LENGTH_SHORT).show();
        // Initializing values
        FirebaseUtil.openFBReference(this, FirebaseUtil.PATH_POST);
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mEventListener = new DatabaseListener();
        mDatabaseReference.addChildEventListener(mEventListener);
        mRecyclerView = findViewById(R.id.posts_recycler_view);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        mPosts = new ArrayList<PostDataModel>();
        mAdapter = new PostAdapter(mPosts,this);
        //work
//        TESTpopulateRecyclerView();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        onOptionsMenuItemSelected();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener( view -> {
            Intent intent = new Intent (HomePageActivity.this, AddPostActivity.class);
            startActivity(intent);
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        });

    }
    private void onOptionsMenuItemSelected() {
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
        for (PostDataModel post : mDataManager.getPosts()){
            mPosts.add(post) ;
        }
        mAdapter.notifyDataSetChanged();
    }

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

    private void signout() {
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
        FirebaseUtil.attachListener();
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
//            mAdapter = new PostAdapter(mPosts,this);
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