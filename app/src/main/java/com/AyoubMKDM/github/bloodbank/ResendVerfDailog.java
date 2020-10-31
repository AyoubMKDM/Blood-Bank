package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendVerfDailog extends DialogFragment {
    private static final String TAG = "ResendVerificationDialo";

    //widgets
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    private EditText mEdConfirmPassword, mEdConfirmEmail;

    //vars
    private Context mContext;
    private String mEmailText, mPasswordText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailog_resend_verf_email, container, false);
        mEdConfirmPassword =  view.findViewById(R.id.confirm_password);
        mEdConfirmEmail = view.findViewById(R.id.confirm_email);
        mContext = getActivity();
        mInputLayoutEmail = view.findViewById(R.id.confirm_input_layout_email);
        mInputLayoutPassword = view.findViewById(R.id.confirm_input_layout_password);


        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO close softkeyboard https://medium.com/@rmirabelle/close-hide-the-soft-keyboard-in-android-db1da22b09d2
                Log.d(TAG, "onClick: attempting to resend verification email.");
                mEmailText = mEdConfirmEmail.getText().toString();
                mPasswordText = mEdConfirmPassword.getText().toString();
                if (!isDataEmpty()){
                        //temporarily authenticate and resend verification email
                        authenticateAndResendEmail(mEmailText, mPasswordText);
                    }else{
                        Toast.makeText(mContext, "all fields must be filled out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button for closing the dialog
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }


    /**
     * reauthenticate so we can send a verification email again
     * @param email
     * @param password
     */
    private void authenticateAndResendEmail(String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: reauthenticate success.");
                            sendVerificationEmail();
                            FirebaseAuth.getInstance().signOut();
                            getDialog().dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "User email or password is wrong, try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * sends an email verification link to the user
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mContext, "couldn't send email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private boolean isDataEmpty() {
        boolean isEmpty = false;
        if (mPasswordText.length() < 8){
            mInputLayoutPassword.setError("You must enter a valid password: 8 or more letters and numbers! ");
            isEmpty = true;
            mEdConfirmPassword.requestFocus();
        }
        if (mEmailText.isEmpty()){
            mInputLayoutEmail.setError("You forget the email field! ");
            isEmpty = true;
            mEdConfirmEmail.requestFocus();
        }
        return isEmpty;
    }
}
