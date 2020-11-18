package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    private String mEmailText, mPasswordText;
    //Widgets
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    private EditText mEdEmail, mEdPassword;
    private TextView mForgettePassword, mResendEmail, mRegister;
    private Button mButtonLogin;
    private ProgressBar mLoading;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ResendVerfDailog LOADING;
    private FirebaseAuth mFirebaseAuth;
    private boolean mIsNewUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectToFirebaseDB();
        mInputLayoutEmail = findViewById(R.id.login_input_layout_email);
        mInputLayoutPassword = findViewById(R.id.login_input_layout_password);
        mEdEmail = findViewById(R.id.login_edittext_email);
        mEdPassword = findViewById(R.id.login_edittext_password);
        mButtonLogin = findViewById(R.id.button_login);
        mRegister = findViewById(R.id.text_register);
        mResendEmail = findViewById(R.id.text_resend_email);
        mLoading = findViewById(R.id.signin_progress_bar);
        setupFirebaseAuth();

        mRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        mResendEmail.setOnClickListener(view -> {
            ResendVerfDailog dailog = new ResendVerfDailog();
            dailog.show(getSupportFragmentManager(), "dialog_resend_email_verification");
        });

        mButtonLogin.setOnClickListener(view -> {
            hideKeyboard(this);
            showProgressBar();
            mEmailText = mEdEmail.getText().toString();
            mPasswordText = mEdPassword.getText().toString();
            if (!isDataEmpty()){
                authenticateUser();
            }else {
                hideProgressBar();
                Toast.makeText(this,"Email or password wrong try again!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void connectToFirebaseDB() {
        FirebaseUtil.openFBReference(this);
        mFirebaseAuth = FirebaseUtil.sFirebaseAuth;
        //TODO need to be cleaned
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
//                    redirectToLogin(caller);
                }else if (user.getDisplayName() == null) {
                    redirectAuthenticatedUser(user, true);
                }else{
                    redirectAuthenticatedUser(user, false);
                    Log.d("TAG", "checkAuthenticationState: user is authenticated.");
                }
            }
        };
    }

    private void showProgressBar() {
        mLoading.setVisibility(View.VISIBLE);
    }


    private boolean isDataEmpty() {
        boolean isEmpty = false;
        if (mPasswordText.length() < 8){
            mInputLayoutPassword.setError("You must enter a valid password: 8 or more letters and numbers! ");
            isEmpty = true;
            mEdPassword.requestFocus();
        }
        if (mEmailText.isEmpty()){
            mInputLayoutEmail.setError("You forget the email field! ");
            isEmpty = true;
            mEdEmail.requestFocus();
        }
        return isEmpty;
    }

    @Override
    protected void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void addNewUsersPrefrence(String fullName, String email, String phoneNumber, String city, String bloodType) {
        SharedPreferences mUserSharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mUserSharedPref.edit();
        editor.putString(getString(R.string.shared_pref_uName), fullName);
        editor.putString(getString(R.string.shared_pref_uCity), city);
        editor.putString(getString(R.string.shared_pref_uEmail), email);
        editor.putString(getString(R.string.shared_pref_uBloodType) , bloodType);
        editor.putString(getString(R.string.shared_pref_uPhoneNumber), phoneNumber);
        editor.apply();
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
         */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");
    }


    private void authenticateUser() {
        FirebaseDatabase DB = FirebaseUtil.sDB;
        mFirebaseAuth.signInWithEmailAndPassword(mEmailText, mPasswordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                                //TODO this works but not the best solution change it next time
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
//                            if (user.getDisplayName() == null){
//                                mIsNewUser = true;
//                            }else {
//                                mIsNewUser = false;
//                            }
//
//                            redirectAuthenticatedUser(task.getResult().getUser(),
//                                    mIsNewUser);
                        }
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressBar();
                    Toast.makeText(LoginActivity.this, "Can not login," +
                            "\nmake sure you entered the right user email and password",
                            Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectAuthenticatedUser(FirebaseUser user, boolean isNewUser) {
        if (user.isEmailVerified()) {
            Intent intent;
            if (isNewUser) {
                intent = new Intent(LoginActivity.this, AdditionalInfoActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, HomePageActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this,
                    "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void hideProgressBar() {
        mLoading.setVisibility(View.GONE);

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService
                (Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}