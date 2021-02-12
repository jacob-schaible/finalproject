package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import java.security.SecureRandom;

public class UserDetailActivity extends AppCompatActivity {
    private static final String TAG = "UserDetailActivity";

    private User user;

    private ImageView avatar;
    private TextView name;
    private EditText username;
    private EditText email;
    private EditText address;
    private EditText phone;
    private EditText website;
    private EditText company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        avatar = findViewById(R.id.user_avatar);
        name = findViewById(R.id.user_name);
        username = findViewById(R.id.username_field);
        email = findViewById(R.id.email_field);
        address = findViewById(R.id.address_field);
        phone = findViewById(R.id.phone_field);
        website = findViewById(R.id.website_field);
        company = findViewById(R.id.company_field);

        Bundle bundle = getIntent().getExtras();
        try {
            user = bundle.getParcelable("user");
        } catch (NullPointerException e) {
            user = User.DEFAULT();
        }

        Picasso.get().load(user.getAvatarUrl()).into(avatar);
        name.setText(user.getName());
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        address.setText(user.getAddress().toString());
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