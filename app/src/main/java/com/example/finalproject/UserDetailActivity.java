package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.model.Address;
import com.example.finalproject.model.Company;
import com.example.finalproject.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class UserDetailActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";
    private static final String USER = "user";
    private static final String USERS = "users";

    private SharedPreferences sharedPref;
    private ArrayList<User> users;
    private User user;

    private ImageView avatar;
    private TextView name;
    private EditText username;
    private EditText email;
    private EditText street;
    private EditText suite;
    private EditText city;
    private EditText zipcode;
    private EditText phone;
    private EditText website;
    private EditText company;

    @Override
    protected void onPause() {
        super.onPause();
        writeSharedPref();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gson gson = new Gson();
        String userJson = sharedPref.getString(USER, "");
        String usersJson = sharedPref.getString(USERS, "");
        if (!userJson.isEmpty()) {
            user = gson.fromJson(userJson, User.class);
            populateFields();
        }
        if (!usersJson.isEmpty()) {
            User[] array = gson.fromJson(usersJson, User[].class);
            users = new ArrayList<>(Arrays.asList(array));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatar = findViewById(R.id.user_avatar);
        name = findViewById(R.id.user_name);
        username = findViewById(R.id.username_field);
        email = findViewById(R.id.email_field);
        street = findViewById(R.id.street_field);
        suite = findViewById(R.id.suite_field);
        city = findViewById(R.id.city_field);
        zipcode = findViewById(R.id.zipcode_field);
        phone = findViewById(R.id.phone_field);
        website = findViewById(R.id.website_field);
        company = findViewById(R.id.company_field);

        sharedPref = getApplicationContext().getSharedPreferences("FinalProject", Context.MODE_PRIVATE);

        // Bundle bundle = getIntent().getExtras();
        // unpackBundle(bundle);
        // populateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            Log.d(TAG, "Sign out clicked");
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        returnToListView();
        return true;
    }

    @Override
    public void onBackPressed() {
        returnToListView();
    }

    private void returnToListView() {
        if (hasBeenChanged()) {
            Log.d(TAG, "Changes detected");
            promptToSave();
        } else {
            Log.d(TAG, "No changes. Returning to people list");
            Intent returnIntent = new Intent(getApplicationContext(), DisplayPeopleActivity.class);
            startActivity(returnIntent);
            finish();
        }
    }

    private void writeSharedPref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        String usersJson = gson.toJson(users);
        editor.putString(USER, userJson);
        editor.putString(USERS, usersJson);
        editor.apply();
    }

    private void unpackBundle(Bundle bundle) {
        if (bundle != null){
            user = bundle.getParcelable(USER);
            users = bundle.getParcelableArrayList(USERS);
        }
        if (user == null)
            user = User.EMPTY();
        if (users == null)
            users = new ArrayList<>();
    }

    /**
     * Fills text fields and loads avatar image from User data
     */
    private void populateFields() {
        if (user != null)
            Log.d(TAG, "Populating with user data: " + user.toString());
        else
            Log.d(TAG, "No user data to populate");
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty())
            Picasso.get().load(user.getAvatarUrl()).into(avatar);
        name.setText(user.getName());
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        street.setText(user.getAddress().getStreet());
        suite.setText(user.getAddress().getSuite());
        city.setText(user.getAddress().getCity());
        zipcode.setText(user.getAddress().getZipcode());
        phone.setText(user.getPhone());
        website.setText(user.getWebsite());
        company.setText(user.getCompany().toString());
    }

    /**
     * Saves user inputted fields
     */
    private void saveChanges() {
        int idx = users.indexOf(user);

        user.setUsername(username.getText().toString());
        user.setEmail(email.getText().toString());
        Address newAddress = new Address();
        newAddress.setStreet(street.getText().toString());
        newAddress.setSuite(suite.getText().toString());
        newAddress.setCity(city.getText().toString());
        newAddress.setZipcode(zipcode.getText().toString());
        user.setAddress(newAddress);
        user.setPhone(phone.getText().toString());
        user.setWebsite(website.getText().toString());
        Company newCompany = new Company();
        newCompany.setName(company.getText().toString());
        user.setCompany(newCompany);

        users.set(idx, user);

        Toast toast = Toast.makeText(getApplicationContext(), "Changes have been saved", Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Checks for any changes in any user input fields
     * @return true if changes detected, else false
     */
    private boolean hasBeenChanged() {
        return !(user.getUsername().equals(username.getText().toString())
                && user.getEmail().equals(email.getText().toString())
                && user.getAddress().getStreet().equals(street.getText().toString())
                && user.getAddress().getSuite().equals(suite.getText().toString())
                && user.getAddress().getCity().equals(city.getText().toString())
                && user.getAddress().getZipcode().equals(zipcode.getText().toString())
                && user.getPhone().equals(phone.getText().toString())
                && user.getWebsite().equals(website.getText().toString())
                && user.getCompany().getName().equals(company.getText().toString()));
    }


    /**
     * Displays alert dialog prompting the user to save their changes.
     * User can select "Yes", "No", or "Cancel"
     * Yes: saves changes and returns to DisplayPeopleActivity
     * No: discards changes and returns to DisplayPeopleActivity
     * Cancel: dismisses dialog and allows user to stay on UserDetailActivity
     */
    private void promptToSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_dialog_prompt)
                .setTitle(R.string.save_dialog_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User selected Yes button
                saveChanges();
                Intent returnIntent = new Intent(getApplicationContext(), DisplayPeopleActivity.class);
                startActivity(returnIntent);
                finish();
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User selected Cancel, so just dismiss the dialog
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User selected No button
                Intent returnIntent = new Intent(getApplicationContext(), DisplayPeopleActivity.class);
                startActivity(returnIntent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Signs the user out of their Google Sign In Account and returns to login activity.
     */
    private void signOut() {
        Toast toast = Toast.makeText(getApplicationContext(), "Signing you out", Toast.LENGTH_LONG);
        toast.show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }
}