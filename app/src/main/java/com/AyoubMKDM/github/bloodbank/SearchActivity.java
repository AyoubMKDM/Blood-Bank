package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    //Vars
    String TAG = "SearchActivity";
    List<UserDataModel> mUsers;
    UsersAdapter mAdapter;
    //Widgets
    Toolbar mToolbar;
    ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    //Firebase
    FirebaseDatabase mDB;
    DatabaseReference mReference;

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = findViewById(R.id.search_activity_toolbar);
        mRecyclerView = findViewById(R.id.search_result);
        mDB = FirebaseDatabase.getInstance();
        mReference = mDB.getReference("Users_info");
        mUsers = new ArrayList();
        Menu menu = mToolbar.getMenu();
        MenuItem item = menu.findItem(R.id.app_bar_search);
        getAllUsers();
        android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              if (!query.trim().isEmpty()){
                  searchForUser(query);
              }else{
                  getAllUsers();
              }
              return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
              if (!newText.trim().isEmpty()){
                  searchForUser(newText);
              }else{
                  getAllUsers();
              }
              return false;
          }
      });
        mProgressBar = findViewById(R.id.search_progress_bar);


    }

    private void searchForUser(String query) {
        Context context = this;

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserDataModel user = dataSnapshot.getValue(UserDataModel.class);
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid())){
                        if (user.getUserName().toLowerCase().contains(query.toLowerCase())) {
                            mUsers.add(user);
                        }
                    }
                }
                mAdapter = new UsersAdapter(mUsers,context);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getAllUsers() {
        Context context = this;
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserDataModel user = dataSnapshot.getValue(UserDataModel.class);
                    if (true){
                        mUsers.add(user);
                    }
                    mAdapter = new UsersAdapter(mUsers,context);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public static void showProgressBar(ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
    }
    public static void hideProgressBar(ProgressBar pb){
        pb.setVisibility(View.GONE);
    }

    public static DatabaseReference getDatabaseRefence() {
        return FirebaseDatabase.getInstance().getReference();
    }

}