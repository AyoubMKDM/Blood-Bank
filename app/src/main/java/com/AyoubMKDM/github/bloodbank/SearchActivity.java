package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.AyoubMKDM.github.bloodbank.JaccardSimilarity.G;
import static com.AyoubMKDM.github.bloodbank.JaccardSimilarity.jaccard;

public class SearchActivity extends AppCompatActivity {
    //Vars
    String TAG = "SearchActivity";
    List<UserDataModel> mUsers, mAllUsers;
    private String[] mQueryQGrams;
    private float mTita;
    private final double THRESHHOLD = 0.5;
    UsersAdapter mAdapter;
    //Widgets
    Toolbar mToolbar;
    ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    Button mMoreResults;
    //Firebase
    FirebaseDatabase mDB;
    DatabaseReference mReference;
    private SearchView mSearchView;

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
        mRecyclerView = findViewById(R.id.search_result_recyclerView);
        mMoreResults = findViewById(R.id.button_get_more_results);
        RecyclerView.LayoutManager searchlayout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(searchlayout);
        mDB = FirebaseDatabase.getInstance();
        mReference = mDB.getReference().child(FirebaseUtil.USER_PATH);
        mUsers = new ArrayList();
        mAllUsers = new ArrayList<>();
        Menu menu = mToolbar.getMenu();
        MenuItem item = menu.findItem(R.id.app_bar_search);
        getAllUsers();
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
              if (!query.trim().isEmpty()){
                  searchForUser(query);
              }else{
                  mMoreResults.setVisibility(View.GONE);
                  mAdapter = new UsersAdapter(mAllUsers,SearchActivity.this);
                  mRecyclerView.setAdapter(mAdapter);
              }
              return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
              if (!newText.trim().isEmpty()){
                  mMoreResults.setVisibility(View.VISIBLE);
                  searchForUser(newText);
              }else{
                  mMoreResults.setVisibility(View.GONE);
                  mAdapter = new UsersAdapter(mAllUsers,SearchActivity.this);
                  mRecyclerView.setAdapter(mAdapter);
              }
              return false;
          }
      });

        mMoreResults.setOnClickListener(view -> {
            //@mUsers need to be reinitialize cuz the array will hold the best matches
//            mUsers = new ArrayList<>();
            search();
        });
        mProgressBar = findViewById(R.id.search_progress_bar);


    }

    private void search() {
        for (UserDataModel user : mAllUsers) {
            for (UserDataModel user2 : mUsers){
                if (!(user.getUserId() == user2.getUserId())){
                    approximateQueryToPlayer(user);
                    if (mTita >=  THRESHHOLD) {
                        // F(jaccard(query,s),weight(s))= alpha * jaccard(query,s) + beta * weight(s)
                        // TODO add to the scoringFunction the number of donation
    //                float scoringFunction =(float) (mTita /2. + player.getPlayer_rate()/200.);
                        float scoringFunction = (float) (mTita / 2.);
                        mUsers.add(user);
                    }
                }
            }
        }
        mMoreResults.setVisibility(View.GONE);
        mAdapter = new UsersAdapter(mUsers,SearchActivity.this);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void approximateQueryToPlayer(UserDataModel user) {
        String query = mSearchView.getQuery().toString().trim();
        mQueryQGrams = G(query, JaccardSimilarity.getQ());
        /* in our dataset the players might be empty in the last name column
         in case of changing the dataset TODO change the conditions to fit your dataset */
        mTita = jaccard(mQueryQGrams, G(user.getUserName(), JaccardSimilarity.getQ()));
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
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    UserDataModel user = dataSnapshot.getValue(UserDataModel.class);
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid())){
                        mAllUsers.add(user);
                    }
                    mAdapter = new UsersAdapter(mAllUsers,context);
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