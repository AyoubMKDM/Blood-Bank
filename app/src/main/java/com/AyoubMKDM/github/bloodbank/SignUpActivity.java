package com.AyoubMKDM.github.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    //Vars
    private final String TAG = "SignUpActivity";
    private String mEmailText, mPassword, mConfirmPassword;
    //Widgets
    private TextInputLayout mLayoutName, mLayoutEmail, mLayoutPassword, mLayoutConfirmPassword;
    private EditText mEdName, mEdEmail, mEdPassword, mEdConfirmPassword;
    private ProgressBar mLoading;
    private CardView mCardView;
    private Button mButtonRegister;
    //Firebase
    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDB;
    private boolean mIsDataOK;

    @Override
    protected void onPause() {
        super.onPause();
//        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FirebaseUtil.attachListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        initializeViews();
        initializeFirebaseDatabase();
        mButtonRegister.setOnClickListener(l -> {
            mIsDataOK = true;
            hideKeyboard(this);
            showProgressBar();
//            mFullNameText = getText(mEdName).trim();
            mEmailText = getText(mEdEmail).trim();
            mPassword = getText(mEdPassword);
            mConfirmPassword = getText(mEdConfirmPassword);
            if (!isDataReadyToBeStored(mLayoutPassword, mPassword,
                    "You must enter a valid phone numbers!", mPassword.length() >= 8)){
                mEdPassword.requestFocus();
                if (!isDataReadyToBeStored(mLayoutConfirmPassword, mConfirmPassword,
                        "Password do not match!", mConfirmPassword.equals(mPassword))){
                    mEdConfirmPassword.requestFocus();
                }
            }

            if (!isDataReadyToBeStored(mLayoutEmail, mEmailText,
                    "To register you must enter an email!", null)){
                mEdEmail.requestFocus();
                isEmailValid();
            }

//            TODO if (!isDataReadyToBeStored(mLayoutName, mFullNameText, "Name must be longer!",
//                    mFullNameText.length() >= 4)){
//                mEdName.requestFocus();
//            }
            if (mIsDataOK){
                registerNewDonor();
            }else {
                hideProgressBar();
            }
        });
    }

    private void initializeViews() {
        mCardView = findViewById(R.id.register_cardView);
        mLoading = findViewById(R.id.register_progress_bar);
//        mLayoutName = findViewById(R.id.signup_inputLayout_name);
        mLayoutEmail = findViewById(R.id.signup_inputLayout_email);
        mLayoutPassword = findViewById(R.id.signup_inputLayout_password);
        mLayoutConfirmPassword = findViewById(R.id.signup_inputLayout_confirm_password);
//        mEdName = findViewById(R.id.signup_edittext_name);
        mEdEmail = findViewById(R.id.signup_editText_email);
        mEdPassword = findViewById(R.id.signup_editText_password);
        mEdConfirmPassword = findViewById(R.id.signup_editText_confirm_password);
        mButtonRegister = findViewById(R.id.button_register);
    }

    public String getText(EditText editText){
        return editText.getText().toString();
    }

    private boolean isDataReadyToBeStored(TextInputLayout layout, String content, String error,
                                          Boolean conditon){
        boolean isEmpty = false;
        if (content.isEmpty() || (conditon != null && !conditon.booleanValue())){
            layout.setError(error);
            isEmpty = true;
        }
        layout.setErrorEnabled(isEmpty);
        if (mIsDataOK)
            mIsDataOK = !isEmpty;
        return isEmpty;
    }

    private void isEmailValid() {
        boolean m_isValid = false;
        Pattern m_pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
        Matcher m_matcher = m_pattern.matcher(mEmailText.trim());
        if (!(m_matcher.matches() && mEmailText.trim().length() > 0)) {
            m_isValid = false;
            mLayoutEmail.setError("You must register with a valid email!");
        }else {
            m_isValid = true;
        }
        mEdEmail.requestFocus();
        if (mIsDataOK)
            mIsDataOK = m_isValid;
    }
//    private void addUserInfoToFirebaseDB() {
//        String Uid = mUser.getUid();
//        UserDataModel user = new UserDataModel(Uid, mFullNameText,mCityText, mBloodType
//                ,mPhoneNumber);
//        mRef.child(Uid).setValue(user).addOnFailureListener(e -> {
////            canNotAddUser(e);

////            return;

//        });

//    }

    private void initializeFirebaseDatabase() {
        FirebaseUtil.openFBReference(this);
        mDB = FirebaseUtil.sDB;
        mFirebaseAuth = FirebaseUtil.sFirebaseAuth;
    }

    private void registerNewDonor(){
        addNewUserToFirebase();
        // store the data in the shared preferences
//        initializeUsersPreference();
//        mLoading.setVisibility(View.GONE);
//        mCardView.setVisibility(View.VISIBLE);
    }

    private void addNewUserToFirebase() {
        mFirebaseAuth.createUserWithEmailAndPassword(mEmailText,mPassword)
                .addOnCompleteListener( task -> {
                    mLoading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        mUser = mFirebaseAuth.getCurrentUser();
                        // Sign in success, update UI with the signed-in user's information
//                mUser = mFirebaseAuth.getCurrentUser();
                        //TODO clean the code after this line
//                        addUserInfoToFirebaseDB();
                        sendVerificationEmail();
//                        setTheUserName();
                        mFirebaseAuth.signOut();
                        FirebaseUtil.redirectToLogin(this);
                    }
                }).addOnFailureListener(e -> {
                        canNotAddUser(e);
        });
    }


    private void canNotAddUser(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException){
            mLayoutEmail.setError(e.getMessage());
        }else {
            Toast.makeText(SignUpActivity.this, "The account can not be created. " +
                    "\nThere was an error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        hideProgressBar();
//        mUser.delete();
//        Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
        return;
    }

    private void sendVerificationEmail(){
        if(mUser != null){
            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void showProgressBar() {
        mLoading.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        mCardView.setVisibility(View.VISIBLE);
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