package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.model.User;

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
    }
}