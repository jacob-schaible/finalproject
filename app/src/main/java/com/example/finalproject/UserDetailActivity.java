package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserDetailActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

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

        Bundle bundle = getIntent().getExtras();
        try {
            user = bundle.getParcelable("user");
            users = bundle.getParcelableArrayList("users");
        } catch (NullPointerException e) {
            user = User.EMPTY();
            users = new ArrayList<>();
        }

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
        if (hasBeenChanged()) {
            Log.d(TAG, "Changes detected");
            promptToSave();
        } else {
            Log.d(TAG, "No changes. Returning to people list");
            finish();
        }
        return true;
    }

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

    private void promptToSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.save_dialog_prompt)
                .setTitle(R.string.save_dialog_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                saveChanges();

                Intent saveAndReturnIntent = new Intent(getApplicationContext(), DisplayPeopleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("users", users);
                saveAndReturnIntent.putExtras(bundle);
                startActivity(saveAndReturnIntent);
            }
        });
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

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