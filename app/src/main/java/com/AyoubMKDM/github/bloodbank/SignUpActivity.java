package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout mInputLayoutName, mInputLayoutEmail, mInputLayoutCity,
            mInputLayoutPassword;
    private EditText mEdFullName, mEdEmail, mEdPhoneNumber, mEdCity, mEdPassword;
    private Spinner mSpinnerBloodGroup;
    private ProgressBar mLoading;
    private Button mButtonRegister;
    private String mFullNameText, mEmailText, mCityText, mPassword, mPhoneNumber, mBloodType;
    private String TAG;
    private FirebaseUser mUser;
    private SharedPreferences mUserSharedPref;


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
        mEdPhoneNumber = findViewById(R.id.signup_edittext_phone_number);
        mEdCity = findViewById(R.id.signup_edittext_city);
        mEdPassword = findViewById(R.id.signup_edittext_password);
        mSpinnerBloodGroup = findViewById(R.id.spinner_blood_groups);
        mButtonRegister = findViewById(R.id.button_register);
        mLoading = findViewById(R.id.singup_progress_bar);
        //TODO add alert dialog to inform the user that the data will be public

        mButtonRegister.setOnClickListener(l -> {
            //TODO close softkeyboard https://medium.com/@rmirabelle/close-hide-the-soft-keyboard-in-android-db1da22b09d2
            mLoading.setVisibility(View.VISIBLE);
            getData();
            if (!isDataEmpty()) {
                isEmailValid();
                if (isEmailValid()){
                    registerNewDonor(mFullNameText, mEmailText, mPhoneNumber, mCityText,
                            mPassword, mBloodType);
                }
            }
            mLoading.setVisibility(View.GONE);
        });
    }

    private void registerNewDonor(String fullName, String email, String phoneNumber, String city
            , String password, String bloodType){
//        Loading();//TODO
        addNewUserToFirebase(fullName, email, phoneNumber, city, password, bloodType);
    }

    private void addNewUserToFirebase(String fullName, String email, String phoneNumber, String city
            , String password, String bloodType) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener( task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                TAG = "SignUpActivity";
                                Log.d(TAG, "createUserWithEmail:success");
                                mUser = FirebaseAuth.getInstance()
                                        .getCurrentUser();
                                Log.d(TAG, "onComplete: user Id" + mUser.getUid());
                                sendVerificationEmail();
                                setTheUserName(fullName);
                                // store the data in the shared prefrences
                                addNewUsersPrefrence(fullName, email, phoneNumber, city, bloodType);

                                FirebaseAuth.getInstance().signOut();
                                redirectToLogin();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                });
    }

    private void addNewUsersPrefrence(String fullName, String email, String phoneNumber, String city, String bloodType) {
        mUserSharedPref = getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        editor.putString(getString(R.string.shared_pref_uName), fullName);
        editor.putString(getString(R.string.shared_pref_uCity), city);
        editor.putString(getString(R.string.shared_pref_uEmail), email);
        editor.putString(getString(R.string.shared_pref_uBloodType) , bloodType);
        editor.putString(getString(R.string.shared_pref_uPhoneNumber), phoneNumber);
        editor.apply();
    }

    private void setTheUserName(String fullName) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                setDisplayName(fullName).build();
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

    /*TODO redirectToLogin || goBackToLogin should be in an indeviduel class should have
    * @param context
    * */
    private void redirectToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

//    private void Loading() {
//        final LodingAlertDialog loading = new LodingAlertDialog(SignUpActivity.this);
//        loading.startLoadingDialog();
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loading.dismissDialog();
//            }
//        },3000);
//    }

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