package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    //Vars
    private final String TAG = "ProfileActivity";
    //Widgets
    TextView mUserName, mUserBloodType, mUserCity, mUserEmail, mUserPhoneNumber;
    ImageView mUserProfilePic;
    Toolbar mToolbar;
    //Firebase
    FirebaseDatabase mDB;
    FirebaseAuth mAuth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mUserName = findViewById(R.id.profile_textview_name);
        mUserBloodType = findViewById(R.id.proeil_textview_blood);
        mUserCity = findViewById(R.id.profile_textview_city);
        mUserEmail = findViewById(R.id.profile_textview_email);
        mUserPhoneNumber = findViewById(R.id.profile_textview_phone);
        mUserProfilePic = findViewById(R.id.profile_pic);
        mToolbar = findViewById(R.id.profile_toolbar);
       //Firebase
        mDB = FirebaseDatabase.getInstance();
        ref = mDB.getReference(FirebaseUtil.USER_PATH);
        mAuth = FirebaseAuth.getInstance();

        mToolbar.setNavigationIcon(R.drawable.ic_go_back_light);
        mToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        retriveUserFromDB();
        mUserProfilePic.setOnClickListener(view -> Toast.makeText(ProfileActivity.this,
                "Soon you'll be able to add your profile picture.\nWait for the update",
                Toast.LENGTH_LONG).show());
    }

    private void retriveUserFromDB() {
        String userID = getIntent().getStringExtra(PostAdapter.USER_ID);
        ref.orderByKey().equalTo(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot datas: snapshot.getChildren()){
                                populateProfileFields(datas);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                );
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

    private void populateProfileFields(DataSnapshot datas) {
        mUserName.setText(datas.child("userName").getValue().toString());
        mUserEmail.setText(mAuth.getCurrentUser().getEmail());
        mUserBloodType.setText("Blood group: " + datas.child("userBloodType").getValue().toString());
        mUserCity.setText(datas.child("userCity").getValue().toString());
        mUserPhoneNumber.setText(datas.child("userPhoneNumber").getValue().toString());
    }
}