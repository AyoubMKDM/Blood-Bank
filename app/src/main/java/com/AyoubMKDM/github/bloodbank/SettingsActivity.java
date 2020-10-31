package com.AyoubMKDM.github.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.atomic.AtomicBoolean;

//TODO call the finish method once you finish
public class SettingsActivity extends AppCompatActivity {
    //Vars
    String TAG = "SettingsActivity";
    //widgets
    Toolbar mToolbar;
    //Firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mToolbar = findViewById(R.id.settings_toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_go_back_light);
        mToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        //Vars
        String TAG = "SettingsFragment";
        //widgets
        EditTextPreference mUserNamePref, mUserEmailAdressPref, mUserPhoneNumPref, mUserCityPref,
                mUserPasswordPref;
        ListPreference mBloodTypePref;
        //Firebase
        FirebaseUser mUser;
        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //Initializing objects
            mUserNamePref = findPreference("user_name_preference");
            mUserEmailAdressPref = findPreference("user_mail_preference");
            mUserPhoneNumPref = findPreference("phone_number_preference");
            mUserCityPref = findPreference("city_preference");
            mUserPasswordPref = findPreference("password_preference");
            mBloodTypePref = findPreference("blood_type_preference");
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            mSharedPreferences = getContext().getApplicationContext().getSharedPreferences(
                    "UserPrefs", Context.MODE_PRIVATE);
            //Work
            setSummaries();
            setDefaultVals();
        }

        @Override
        public void onResume() {
            super.onResume();
            checkAuthenticationState();
            mUserNamePref.setOnPreferenceChangeListener((pref, newValue) ->{
                if (updateUserSettings(pref, newValue)) {
                    firebaseEditName(newValue.toString());
                    return true;
                }else
                    sharedPrefEditString(pref.getKey(), mUser.getDisplayName());
                    return false;
            });
            mUserEmailAdressPref.setOnPreferenceChangeListener((pref, newValue) -> {
                return updateUserSettings(pref, newValue); });
            mUserPhoneNumPref.setOnPreferenceChangeListener((pref, newValue) -> {
                return updateUserSettings(pref, newValue); });
            mUserCityPref.setOnPreferenceChangeListener((pref, newValue) -> {
                return updateUserSettings(pref, newValue); });

            mBloodTypePref.setOnPreferenceChangeListener((pref, newValue) -> {
                return updateUserSettings(pref, newValue); });
        }

        //TODO add @param defaultValue
        private boolean updateUserSettings(Preference pref, Object newValue) {
            if (sharedPrefEditString(pref.getKey(), newValue.toString())) {
                setSummaryForPref(pref, pref.getKey(),
                        "");
                return true;
            }else {
                //TODO fix this in case of error
                return false;
            }
        }


        /*TODO future update extract checkAuthenticationState and other
           repetive firebase work to a new Class*/
        private void checkAuthenticationState(){
            Log.d(TAG, "checkAuthenticationState: checking authentication state.");
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            if(mUser == null){
                Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");
                goBackToLogin();
            }else{
                Log.d(TAG, "checkAuthenticationState: user is authenticated.");
            }
        }

        private void goBackToLogin() {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }

        private boolean sharedPrefEditString(String key, String newValue) {
            boolean isSuccessful = false;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(key);
            editor.putString(key,newValue);
            if (editor.commit()){
                isSuccessful = true;
            }else {
                Toast.makeText(getContext(), "Could not Change the user Name,\n" +
                                " Try Again!",
                        Toast.LENGTH_SHORT).show();
            }
            return isSuccessful;
        }

        private void setSummaries() {
            setSummaryForPref(mUserNamePref, getString(R.string.shared_pref_uName),
                    "Change your name");
            setSummaryForPref(mUserEmailAdressPref, getString(R.string.shared_pref_uEmail),
                                "Change your email");
            setSummaryForPref(mUserPhoneNumPref, getString(R.string.shared_pref_uPhoneNumber),
                    "Change your phone number");
            setSummaryForPref(mUserCityPref, getString(R.string.shared_pref_uCity),
                    "Change your location");
            setSummaryForPref(mBloodTypePref, getString(R.string.shared_pref_uBloodType),
                    "Change your blood type");
        }

        private void setDefaultVals() {
            setDefaultValForPref(mUserNamePref,getString(R.string.shared_pref_uName));
            setDefaultValForPref(mUserEmailAdressPref, getString(R.string.shared_pref_uEmail));
            setDefaultValForPref(mUserPhoneNumPref, getString(R.string.shared_pref_uPhoneNumber));
            setDefaultValForPref(mUserCityPref, getString(R.string.shared_pref_uCity));
            setDefaultValForPref(mBloodTypePref, getString(R.string.shared_pref_uBloodType));
        }

        private void setSummaryForPref(Preference preference, String spKey, String defaultValue){
            final String summary = mSharedPreferences.getString(spKey, defaultValue);
            preference.setSummary(summary);
        }

        private void setDefaultValForPref(Preference preference, String spKey){
            //TODO add defaultValue
            preference.setDefaultValue(mSharedPreferences.getString(spKey,""));
        }

        private boolean firebaseEditName(String fullName) {
            AtomicBoolean isSuccessful = new AtomicBoolean(false);
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().
                    setDisplayName(fullName).build();
            mUser.updateProfile(profileUpdate).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Log.d(TAG, "setTheUserName: user profile updates");
                    isSuccessful.set(true);
                }else {
                    Toast.makeText(getContext(), "Could not Change the user Name,\n" +
                                    " Try Again!",
                            Toast.LENGTH_SHORT).show();
                    isSuccessful.set(false);
                }
            });
        return isSuccessful.get();
        }
    }
}