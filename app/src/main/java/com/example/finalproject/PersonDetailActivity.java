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
import com.example.finalproject.model.Person;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class PersonDetailActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";
    private static final String PERSON = "person";
    private static final String PEOPLE = "people";

    private SharedPreferences sharedPref;
    private ArrayList<Person> people;
    private Person person;

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
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Store EditText field values in case user made a change without saving
        savedInstanceState.putString("username", username.getText().toString());
        savedInstanceState.putString("email", email.getText().toString());
        savedInstanceState.putString("street", street.getText().toString());
        savedInstanceState.putString("suite", suite.getText().toString());
        savedInstanceState.putString("city", city.getText().toString());
        savedInstanceState.putString("zipcode", zipcode.getText().toString());
        savedInstanceState.putString("phone", phone.getText().toString());
        savedInstanceState.putString("website", website.getText().toString());
        savedInstanceState.putString("company", company.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore EditText field values in case user made a change without saving
        username.setText(savedInstanceState.getString("username"));
        email.setText(savedInstanceState.getString("email"));
        street.setText(savedInstanceState.getString("street"));
        suite.setText(savedInstanceState.getString("suite"));
        city.setText(savedInstanceState.getString("city"));
        zipcode.setText(savedInstanceState.getString("zipcode"));
        phone.setText(savedInstanceState.getString("phone"));
        website.setText(savedInstanceState.getString("website"));
        company.setText(savedInstanceState.getString("company"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeSharedPref();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
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
        Gson gson = new Gson();
        String personJson = sharedPref.getString(PERSON, "");
        String peopleJson = sharedPref.getString(PEOPLE, "");
        if (!personJson.isEmpty()) {
            person = gson.fromJson(personJson, Person.class);
            populateFields();
        }
        if (!peopleJson.isEmpty()) {
            Person[] array = gson.fromJson(peopleJson, Person[].class);
            people = new ArrayList<>(Arrays.asList(array));
        }
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
        String personJson = gson.toJson(person);
        String peopleJson = gson.toJson(people);
        editor.putString(PERSON, personJson);
        editor.putString(PEOPLE, peopleJson);
        editor.apply();
    }

    /**
     * Fills text fields and loads avatar image from Person data
     */
    private void populateFields() {
        if (person != null)
            Log.d(TAG, "Populating with person data: " + person.toString());
        else
            Log.d(TAG, "No person data to populate");
        if (person.getAvatarUrl() != null && !person.getAvatarUrl().isEmpty())
            Picasso.get().load(person.getAvatarUrl()).into(avatar);
        name.setText(person.getName());
        username.setText(person.getUsername());
        email.setText(person.getEmail());
        street.setText(person.getAddress().getStreet());
        suite.setText(person.getAddress().getSuite());
        city.setText(person.getAddress().getCity());
        zipcode.setText(person.getAddress().getZipcode());
        phone.setText(person.getPhone());
        website.setText(person.getWebsite());
        company.setText(person.getCompany().toString());
    }

    /**
     * Saves user inputted fields
     */
    private void saveChanges() {
        int idx = people.indexOf(person);

        person.setUsername(username.getText().toString());
        person.setEmail(email.getText().toString());
        Address newAddress = new Address();
        newAddress.setStreet(street.getText().toString());
        newAddress.setSuite(suite.getText().toString());
        newAddress.setCity(city.getText().toString());
        newAddress.setZipcode(zipcode.getText().toString());
        person.setAddress(newAddress);
        person.setPhone(phone.getText().toString());
        person.setWebsite(website.getText().toString());
        Company newCompany = new Company();
        newCompany.setName(company.getText().toString());
        person.setCompany(newCompany);

        people.set(idx, person);

        Toast toast = Toast.makeText(getApplicationContext(), "Changes have been saved", Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Checks for any changes in any user input fields
     * @return true if changes detected, else false
     */
    private boolean hasBeenChanged() {
        return !(person.getUsername().equals(username.getText().toString())
                && person.getEmail().equals(email.getText().toString())
                && person.getAddress().getStreet().equals(street.getText().toString())
                && person.getAddress().getSuite().equals(suite.getText().toString())
                && person.getAddress().getCity().equals(city.getText().toString())
                && person.getAddress().getZipcode().equals(zipcode.getText().toString())
                && person.getPhone().equals(phone.getText().toString())
                && person.getWebsite().equals(website.getText().toString())
                && person.getCompany().getName().equals(company.getText().toString()));
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

    private void handleSignedOut(GoogleSignInAccount account) {
        if (account == null) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
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

        handleSignedOut(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
    }
}