package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    //Vars
    private final String TAG = "SignUpActivity";
    //Widgets
    private TextInputLayout mInputLayoutName, mInputLayoutEmail, mInputLayoutCity,
            mInputLayoutPassword;
    private EditText mEdFullName, mEdEmail, mEdPhoneNumber, mEdCity, mEdPassword;
    private Spinner mSpinnerBloodGroup;
    private ProgressBar mLoading;
    private CardView mCardView;
    private Button mButtonRegister;
    private String mFullNameText, mEmailText, mCityText, mPassword, mPhoneNumber, mBloodType;
    private SharedPreferences mUserSharedPref;
    //Firebase
    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestoreDB;
    private DocumentReference mDocument;
    private FirebaseDatabase mDB;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mInputLayoutName = findViewById(R.id.signup_input_layout_fullname);
        mInputLayoutEmail = findViewById(R.id.signup_input_layout_email);
        mInputLayoutCity = findViewById(R.id.signup_input_layout_city);
        mInputLayoutPassword = findViewById(R.id.signup_input_layout_password);
        mEdFullName = findViewById(R.id.signup_edittext_fullname);
        mEdEmail = findViewById(R.id.signup_edittext_email);
        mCardView = findViewById(R.id.signup_cardview);
        mEdPhoneNumber = findViewById(R.id.signup_edittext_phone_number);
        mEdCity = findViewById(R.id.signup_edittext_city);
        mEdPassword = findViewById(R.id.signup_edittext_password);
        mSpinnerBloodGroup = findViewById(R.id.spinner_blood_groups);
        mButtonRegister = findViewById(R.id.button_register);
        mLoading = findViewById(R.id.singup_progress_bar);
        //TODO add alert dialog to inform the user that the data will be public
        initializeFirebaseDatabase();
        mButtonRegister.setOnClickListener(l -> {
            //TODO close softkeyboard https://medium.com/@rmirabelle/close-hide-the-soft-keyboard-in-android-db1da22b09d2
            mCardView.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            getData();
            if (!isDataEmpty()) {
                isEmailValid();
                if (isEmailValid()){
                    registerNewDonor();
                }
            }
        });
    }

    private void registerNewDonor(){
        addNewUserToFirebase();
        // store the data in the shared preferences
        initializeUsersPreference();
    }

    private void addNewUserToFirebase() {
       mFirebaseAuth.createUserWithEmailAndPassword(mEmailText,mPassword)
        .addOnCompleteListener( task -> {
            mLoading.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                mUser = mFirebaseAuth.getCurrentUser();
                //TODO clean the code after this line
                addUserInfoToFirebaseDB();
                FirebaseAuth.getInstance().signOut();
                FirebaseUtil.redirectToLogin(this);
            }
        }).addOnFailureListener(e -> canNotAddUser(e));
    }

    private void addUserInfoToFirebaseDB() {
        String Uid = mUser.getUid();
        UserDataModel user = new UserDataModel(Uid, mFullNameText,mCityText, mBloodType
                ,mPhoneNumber);
        mRef.child(Uid).setValue(user).addOnFailureListener(e -> {
            canNotAddUser(e);
            return;
        });
        sendVerificationEmail();
        setTheUserName();
    }

    private void canNotAddUser(Exception e) {
        Toast.makeText(SignUpActivity.this,
                "The account can not be created. " +
                        "\n" + e.toString(),Toast.LENGTH_SHORT).show();
        mUser.delete();
        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return;
    }

    //TODO add this method to @SplashScreenActivity
    private void initializeFirebaseDatabase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        mRef = mDB.getReference().child(FirebaseUtil.USER_PATH);
    }

    private void initializeUsersPreference() {
        mUserSharedPref = getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        editor.putString(getString(R.string.shared_pref_uName), mFullNameText);
        editor.putString(getString(R.string.shared_pref_uCity), mCityText);
        editor.putString(getString(R.string.shared_pref_uEmail), mEmailText);
        editor.putString(getString(R.string.shared_pref_uBloodType) , mBloodType);
        editor.putString(getString(R.string.shared_pref_uPhoneNumber), mPhoneNumber);
        editor.apply();
    }

    private void setTheUserName() {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                setDisplayName(mFullNameText).build();
        mUser.updateProfile(profileUpdate).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               Log.d(TAG, "setTheUserName: user profile updates");
           }else {
               Toast.makeText(SignUpActivity.this, "Could not add a user Name," +
                               " change it later in account settings.",
                       Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void getData() {
        mFullNameText = mEdFullName.getText().toString();
        mEmailText = mEdEmail.getText().toString().toLowerCase();
        mPhoneNumber = mEdPhoneNumber.getText().toString();
        mCityText = mEdCity.getText().toString();
        mPassword = mEdPassword.getText().toString();
        mBloodType = mSpinnerBloodGroup.getSelectedItem().toString();
    }

    private boolean isEmailValid() {
        final int domainStart = mEmailText.indexOf("@");
        if (domainStart != -1) {
            String domain = mEmailText.substring(domainStart +1);
            final int tldStart = domain.indexOf(".");
            if (tldStart != -1 && !domain.substring(1,tldStart+1).isEmpty()){
                String topLevelDomain = domain.substring(tldStart + 1);
                if (!topLevelDomain.isEmpty()) {
                    return true;
                }
            }
        }
        mInputLayoutEmail.setError("You must register with a valid email! ");
        mEdEmail.requestFocus();
        return false;
    }

    private boolean isDataEmpty() {
        boolean isAllGood = true;
        if (mPassword.isEmpty() || mPassword.length() < 8){
            mInputLayoutPassword.setError("You must enter a valid password: 8 or more letters and numbers! ");
            isAllGood = false;
            mEdPassword.requestFocus();
        }else{
            mInputLayoutPassword.setErrorEnabled(false);
        }

        if (mCityText.isEmpty()){
            mInputLayoutCity.setError("You must enter the name of your City! ");
            isAllGood = false;
            mEdCity.requestFocus();
        }else{
            mInputLayoutCity.setErrorEnabled(false);
        }

        if (mEmailText.isEmpty()){
            mInputLayoutEmail.setError("You must register with an email! ");
            isAllGood = false;
            mEdEmail.requestFocus();
        }else{
            mInputLayoutEmail.setErrorEnabled(false);
        }

        if (mFullNameText.isEmpty()){
            mInputLayoutName.setError("You must enter your name! ");
            isAllGood = false;
            mEdFullName.requestFocus();
        }else{
            mInputLayoutName.setErrorEnabled(false);
        }
        return !isAllGood;
    }

    private void sendVerificationEmail(){
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,
                                "A verification email has been sent!", Toast.LENGTH_LONG)
                                .show();
                    }else {
                        Toast.makeText(SignUpActivity.this,
                                "Failed to send a verification email. try again!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}