package com.AyoubMKDM.github.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPostActivity extends AppCompatActivity {
    //Vars
    String TAG = "AddPostActivity";
    PostDataModel mPost;
    //Widgets
    Toolbar mToolbar;
    EditText mPostTitle, mPostTextBody, mLocationRequest;
    Spinner mBloodTypeRequested;
    TextInputLayout mPostTitleHolder;
    Button mButtonShare;
    //Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mUser;
    private boolean mIsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        //Initializing
        mToolbar = findViewById(R.id.add_post_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_go_back_dark);
        mToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(AddPostActivity.this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        connectToFirebaseDB();
        mButtonShare = findViewById(R.id.button_share);
        mPostTitle = findViewById(R.id.edittext_title);
        mPostTitleHolder = findViewById(R.id.add_post_title_holder);
        mPostTextBody = findViewById(R.id.post_text_body);
        mLocationRequest = findViewById(R.id.edittext_location);
        mBloodTypeRequested = findViewById(R.id.spinner_blood_groups);
        //Work
        Intent intent = getIntent();
        mPost = intent.getParcelableExtra("Post");
        mIsNew = false;
        if (mPost == null) {
            mIsNew = true;
            mPost = new PostDataModel();
        } else {
            updateActivityFields();
        }
        mButtonShare.setOnClickListener(vew -> {
            if (mIsNew) {
                writeToDatabase();
                redirectToHome();
            } else {
                DatabaseUpdatePost();
                redirectToHome();
            }
        });
    }

    private void connectToFirebaseDB() {
        FirebaseUtil.openFBReference(this);
        mFirebaseDatabase = FirebaseUtil.sDB;
        mDatabaseReference = FirebaseUtil.sDatabaseReference.child(FirebaseUtil.USER_PATH);
        mUser = FirebaseUtil.sFirebaseAuth.getCurrentUser();
    }

    private void DatabaseUpdatePost() {
        setDatabasePostClass();

        //TODO test the update
        mDatabaseReference.child(mPost.getPostId()).setValue(mPost, (error, ref) -> {
            if (error != null) {
                Toast.makeText(this, "Couldn't Update this request, "
                        + error.toString(), Toast.LENGTH_LONG).show();
                mPostTextBody.setError(error.getMessage());
                mPostTitleHolder.setError(error.getMessage());
            } else {
                Toast.makeText(this, "Your request has been Updated "
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDatabasePostClass() {
        mPost.setUserId(mUser.getUid());
        mPost.setUserLocation(mLocationRequest.getText().toString());
        mPost.setPostRequestedBloodType(mBloodTypeRequested.getSelectedItem().toString());
        mPost.setPostTitle(mPostTitle.getText().toString());
        mPost.setPostTextBody(mPostTextBody.getText().toString());
    }

    private void updateActivityFields() {
        mLocationRequest.setText(mPost.getUserLocation());
        String[] list = getResources().getStringArray(R.array.blood_entries);
        int index;
        for (index = 0; index < list.length; index++){
            if (list[index].equals(mPost.getPostRequestedBloodType()))
                break;
        }
        mBloodTypeRequested.setSelection(index);
        mPostTitle.setText(mPost.getPostTitle());
        mPostTextBody.setText(mPost.getPostTextBody());
    }

    private void redirectToHome() {
        Intent intent = new Intent(AddPostActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void writeToDatabase() {
        mPost.setUserName(mUser.getDisplayName());
        setDatabasePostClass();
        @SuppressLint("SimpleDateFormat") final DateFormat df = new SimpleDateFormat(
                "dd.MM.yyyy 'at' HH:mm z");
        mPost.setPostDateAdding(df.format(Calendar.getInstance().getTime()));
        mPost.setUserId(mUser.getUid());

        //TODO check if the body isEmpty
        mDatabaseReference.push().setValue(mPost, (error, ref) -> {
            if (error != null){
                Toast.makeText(this, "Couldn't Share this request, "
                        + error.toString(), Toast.LENGTH_LONG).show();
                mPostTextBody.setError(error.getMessage());
                mPostTitleHolder.setError(error.getMessage());
            }else {
                Toast.makeText(this, "Your request has been shared "
                        , Toast.LENGTH_LONG).show();
            }
            //TODO close softkeyboard
        });

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
}