package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        mResendEmail.setOnClickListener(view -> {
            ResendVerfDailog dailog = new ResendVerfDailog();
            dailog.show(getSupportFragmentManager(), "dialog_resend_email_verification");
        });

        mButtonLogin.setOnClickListener(view -> {
//            LOADING.startLoadingDialog(); TODO add a progressBar and controle it
            mLoading.setVisibility(View.VISIBLE);
            mEmailText = mEdEmail.getText().toString();
            mPasswordText = mEdPassword.getText().toString();
            if (!isDataEmpty()){
                //TODO delete the unnecessary Toast messages
                autnenticateUser();
            }else {
                Toast.makeText(this,"Email or password wrong try agin!",
                        Toast.LENGTH_LONG).show();
            } //TODO close softkeyboard https://medium.com/@rmirabelle/close-hide-the-soft-keyboard-in-android-db1da22b09d2
//            LOADING.dismissDialog();TODO add a progressBar and controle it
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
            mLoading.setVisibility(View.GONE);
        });
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
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
         */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    if (user.isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged:signed_in");
                        Toast.makeText(LoginActivity.this, "Authenticated with: "
                                + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,
                                HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    private void autnenticateUser() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailText, mPasswordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Can not login," +
                            " make sure that: the user email or password are right",
                            Toast.LENGTH_LONG).show();
            }
        });
    }
}