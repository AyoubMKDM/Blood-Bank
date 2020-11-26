package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        initializeLocationEditText();
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
            SoftKeyboard.hide(this);
            if (mIsNew) {
                writeToDatabase();
            } else {
                DatabaseUpdatePost();
                redirectToHome();
            }
        });
    }

    private void initializeLocationEditText() {
        DatabaseReference ref = mFirebaseDatabase.getReference().child(FirebaseUtil.USER_PATH);
        ref.orderByKey().equalTo(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    UserDataModel location = data.getValue(UserDataModel.class);
                    mLocationRequest.setText(location.getUserCity());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void connectToFirebaseDB() {
        FirebaseUtil.openFBReference(this);
        mFirebaseDatabase = FirebaseUtil.sDB;
        mDatabaseReference = FirebaseUtil.sDatabaseReference.child(FirebaseUtil.PATH_POST);
        mUser = FirebaseUtil.sFirebaseAuth.getCurrentUser();
    }

    private void DatabaseUpdatePost() {
        initializePostObject();
        mDatabaseReference.child(mPost.getPostId()).setValue(mPost, (error, ref) -> {
            if (error != null) {
                Toast.makeText(this, "Couldn't Update this request, "
                        + error.toString(), Toast.LENGTH_LONG).show();
                mPostTextBody.setError(error.getMessage());
                mPostTitleHolder.setError(error.getMessage());
                redirectToHome();
            } else {
                Toast.makeText(this, "Your request has been Updated "
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializePostObject() {
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
        if (isDataReadyToBeStored()) {
            mPost.setUserName(mUser.getDisplayName());
            initializePostObject();
            @SuppressLint("SimpleDateFormat") final DateFormat df = new SimpleDateFormat(
                    "dd.MM.yyyy 'at' HH:mm z");
            mPost.setPostDateAdding(df.format(Calendar.getInstance().getTime()));
            mPost.setUserId(mUser.getUid());

            //TODO check if the body isEmpty
            mDatabaseReference.push().setValue(mPost, (error, ref) -> {
                if (error != null) {
                    Toast.makeText(this, "Couldn't Share this request, "
                            + error.toString(), Toast.LENGTH_LONG).show();
                    mPostTextBody.setError(error.getMessage());
                    mPostTitleHolder.setError(error.getMessage());
                } else {
                    Toast.makeText(this, "Your request has been shared "
                            , Toast.LENGTH_LONG).show();
                }
            });
            redirectToHome();
        }else {
            Toast.makeText(this, "Unable to share this request"
                    , Toast.LENGTH_LONG).show();
        }

    }

    private boolean isDataReadyToBeStored() {
        boolean isOk = true;
        if (mPostTitle.getText().length() < 4){
            isOk = false;
            mPostTitleHolder.setError("Title must be composed of more than 3 letters");
        }else {
            mPostTitleHolder.setErrorEnabled(false);
        }
        if (mPostTextBody.getText().length() < 10){
            isOk = false;
            mPostTextBody.setError("The body of the request has only " + mPostTextBody.getText().length()
                                        + " characters, the body needs to Have at least 10 characters");
        }else if (mPostTextBody.getText().length() > 300){
            isOk = false;
            mPostTextBody.setError("The message size is big than the allowed one\n"
                    + mPostTextBody.getText().length() + "characters while it should be less than 300 characters.");
        }else{
//            mPostTextBody.setErrorEnabled(false);
        }
        if (mLocationRequest.getText().toString().isEmpty()){

        }
        return isOk;
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