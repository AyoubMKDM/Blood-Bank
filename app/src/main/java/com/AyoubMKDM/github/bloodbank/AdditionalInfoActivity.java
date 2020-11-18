package com.AyoubMKDM.github.bloodbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class AdditionalInfoActivity extends AppCompatActivity implements OnCountryPickerListener {
    //Vars
    private final String TAG = "AdditionalInfoActivity";
    private String mFullNameText, mGender, mCityText, mPhoneNumber, mBloodType, mCountry;
    //Widgets
    private TextInputLayout mLayoutCity, mLayoutPhone, mLayoutName;
    private EditText mEdPhone, mEdCity, mEdName;
    private Spinner mSpinnerBloodGroup, mSpinnerGender;
    private ProgressBar mLoading;
    private CardView mCardView;
    private Button mButtonSubmit, mButtonCountry;
    private CheckBox mCheckBoxShareInfos;
    private SharedPreferences mUserSharedPref;
    private ImageView mImgCountryFlag;
    //Firebase
    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestoreDB;
    private DocumentReference mDocument;
    private FirebaseDatabase mDB;
    private DatabaseReference mRef;
    CountryPicker mPicker;


    @Override
    public void onSelectCountry(Country country) {
        mButtonCountry.setText(country.getName());
        mEdPhone.setText(country.getDialCode());
        mImgCountryFlag.setImageResource(country.getFlag());
        mCountry = country.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        initializeViews();
        connectToFirebaseDB();
        initializeCountryPicker();
        showWelcomeDialog();
        mButtonCountry.setOnClickListener(view -> mPicker.showBottomSheet( this));
        mButtonSubmit.setOnClickListener(l -> {
            SoftKeyboard.hide(this);
            showProgressBar();
            getData();
            if (!isDataReadyToBeStored()) {
                registerNewDonor();
            }else{
                hideProgressBar();
            }
        });

    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome");
        builder.setCancelable(true);
        builder.setMessage("Welcome! before we allow you to access our services, and let you start donating, we need this information. If you please, take the time to fill these fields.\n" +
                            "\nAll these fields are mandatory, to help the ones in need to reach you.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initializeCountryPicker() {
        mPicker = new CountryPicker.Builder().with(this).listener(this).build();
    }

    private void initializeViews() {
        mCardView = findViewById(R.id.signup_cardview);
        mSpinnerBloodGroup = findViewById(R.id.spinner_blood_groups);
        mSpinnerGender = findViewById(R.id.spinner_gender);
        mLayoutName = findViewById(R.id.signup_inputLayout_name);
        mLayoutCity = findViewById(R.id.signup_input_layout_city);
        mLayoutPhone = findViewById(R.id.signup_input_layout_phone_number);
        mEdCity = findViewById(R.id.signup_edittext_city);
        mEdPhone = findViewById(R.id.signup_edittext_phone_number);
        mEdName = findViewById(R.id.signup_edittext_name);
        mCheckBoxShareInfos = findViewById(R.id.checkBox_share_infos);
        mButtonSubmit = findViewById(R.id.button_submit);
        mButtonCountry = findViewById(R.id.button_country);
        mLoading = findViewById(R.id.singup_progress_bar);
        mImgCountryFlag = findViewById(R.id.img_country_flag);
    }

    private void connectToFirebaseDB() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        mRef = mDB.getReference().child(FirebaseUtil.USER_PATH);
    }

//    private void initializeUsersPreference() { TODO this
//        mUserSharedPref = getSharedPreferences("UserPrefs",Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mUserSharedPref.edit();
//        editor.putString(getString(R.string.shared_pref_uName), mFullNameText);
//        editor.putString(getString(R.string.shared_pref_uCity), mCityText);
//        editor.putString(getString(R.string.shared_pref_uEmail), mEmailText);
//        editor.putString(getString(R.string.shared_pref_uBloodType) , mBloodType);
//        editor.putString(getString(R.string.shared_pref_uPhoneNumber), mPhoneNumber);
//        editor.apply();
//    }

    private void getData() {
        mFullNameText = mEdName.getText().toString().trim();
        mPhoneNumber = mEdPhone.getText().toString();
        mCityText = mEdCity.getText().toString();
        mBloodType = mSpinnerBloodGroup.getSelectedItem().toString();
        mGender = mSpinnerGender.getSelectedItem().toString();
    }

    private boolean isDataReadyToBeStored() {
        boolean isAllGood = true;

        if (mCountry == null){
            mButtonCountry.setError("This filed is mandatory");
            isAllGood = false;
        }else{
//            mButtonCountry.setErrorEnabled(false);
        }

        if (mPhoneNumber.length() < 10 || mPhoneNumber.length() > 15){
            mLayoutPhone.setError("You must enter a valid phone numbers!");
            isAllGood = false;
            mEdPhone.requestFocus();
        }else{
            mLayoutPhone.setErrorEnabled(false);
        }

        if (mCityText.isEmpty()){
            mLayoutCity.setError("You must enter the name of your City! ");
            isAllGood = false;
            mEdCity.requestFocus();
        }else{
            mLayoutCity.setErrorEnabled(false);
        }

        if (!mCheckBoxShareInfos.isChecked()){
            mCheckBoxShareInfos.setError("Check this proceed");
            isAllGood = false;
            mCheckBoxShareInfos.requestFocus();
        }else{
//            TODO mCheckBoxShareInfos.setErrorEnabled(false);
        }

        if (mFullNameText.length() < 4){
            mLayoutName.setError("Invalid user name!");
            isAllGood = false;
            mEdName.requestFocus();
        }else{
            mLayoutName.setErrorEnabled(false);
        }
        return !isAllGood;
    }

//    private void setTheUserName() {
//        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
//                setDisplayName(mFullNameText).build();
//        mUser.updateProfile(profileUpdate).addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                Log.d(TAG, "setTheUserName: user profile updates");
//            }else {
//                Toast.makeText(this, "Could not add a user Name," +
//                                " maybe try again later.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void registerNewDonor(){
        addUserInfoToFirebaseDB();
        // store the data in the shared preferences
    }

    private void addUserInfoToFirebaseDB() {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                setDisplayName(mFullNameText).build();
        mUser.updateProfile(profileUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d(TAG, "setTheUserName: user profile updates");
                //TODO clean this code
                String Uid = mUser.getUid();
                UserDataModel user = new UserDataModel(Uid, mFullNameText,mCityText, mBloodType
                        ,mPhoneNumber, mCountry);
                mRef.child(Uid).setValue(user).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Intent intent = new Intent(AdditionalInfoActivity.this,
                                HomePageActivity.class);
                        startActivity(intent);
                    }
                    }).addOnFailureListener(e -> {
                        unsetUserName();
                        canNotAddUser(e);
                }).addOnFailureListener(e -> {
                    canNotAddUser(e);
                });
            }
        });
    }

    private void unsetUserName() {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                setDisplayName(null).build();
        mUser.updateProfile(profileUpdate);
    }

    private void canNotAddUser(Exception e) {
        Toast.makeText(AdditionalInfoActivity.this,
                "The account can not be created. Try again!" +
                        "\n" + e.toString(),Toast.LENGTH_SHORT).show();
        hideProgressBar();
//        mUser.delete();
//        Intent intent = new Intent(AdditionalInfoActivity.this, AdditionalInfoActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
        return;
    }

    private void showProgressBar() {
        mLoading.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        mCardView.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

}