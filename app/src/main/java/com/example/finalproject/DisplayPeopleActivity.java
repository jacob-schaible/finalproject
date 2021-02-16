package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayPeopleActivity extends AppCompatActivity {
    private final String TAG = "DisplayPeopleActivity";
    private static final String USER = "user";
    private static final String USERS = "users";

    private SharedPreferences sharedPref;
    private User currentUser;
    private ArrayList<User> users;
    private OkHttpClient client;
    private RecyclerView recyclerView;

    @Override
    protected void onPause() {
        super.onPause();
        writeSharedPref();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Gson gson = new Gson();
        String usersJson = sharedPref.getString(USERS, "");
        if (!usersJson.isEmpty()) {
            User[] array = gson.fromJson(usersJson, User[].class);
            users = new ArrayList<>(Arrays.asList(array));
        } else {
            users = new ArrayList<>();
        }
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_people);

        currentUser = getCurrentUser();
        recyclerView = findViewById(R.id.user_recycler);
        sharedPref = getApplicationContext().getSharedPreferences("FinalProject", Context.MODE_PRIVATE);
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

    /**
     * Generates a User object from GoogleSignInAccount
     * @return the User
     */
    private User getCurrentUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        User user = User.EMPTY();
        if (account != null) {
            int id = (users != null) ? users.size()+1 : 1;
            user.setId(id);
            user.setName(account.getDisplayName());
            user.setEmail(account.getEmail());
            if (account.getPhotoUrl() != null)
                user.setAvatarUrl(account.getPhotoUrl().toString());
        }
        return user;
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

    /**
     * Makes request with web service to retrieve user list data and handles response
     * @param url web service url as a String
     */
    private void getWebService(String url) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "Got response from web service");
                            parseData(response.body().string());
                            populateRecycler();
                        } catch(IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Could not connect to web service");
                    }
                });
            }
        });
    }


    /**
     * Parses web service response data from Json to a List of Users,
     * assigns each user a randomized avatar url,
     * and sorts the List of Users based on name.
     * @param data String containing Json response
     */
    private void parseData(String data) {
        if (data != null) {
            Gson gson = new Gson();
            User[] array = gson.fromJson(data, User[].class);
            users = new ArrayList<>(Arrays.asList(array));
            users.add(currentUser);
            for (User u : users) {
                if (u.getAvatarUrl() == null || u.getAvatarUrl().isEmpty())
                    u.setAvatarUrl(generateUrl());
            }
            Collections.sort(users);
            Log.d(TAG, "Parsing " + users.size() + " users");

        } else {
            Log.d(TAG, "No data to parse");
        }
    }

    /**
     * Populates RecyclerView with User list
     */
    private void populateRecycler() {
        Log.d(TAG, "Populating recycler view");
        UserAdapter.setOnItemClickListener(getOnClickListener());
        recyclerView.setAdapter(new UserAdapter(users));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
    }

    private void loadData() {
        if (users.isEmpty()) {
            Log.d(TAG, "Calling web service");
            client = new OkHttpClient();
            getWebService(getString(R.string.user_data_url));
        } else {
            populateRecycler();
        }
    }

    /**
     * Generates a randomized Robohash url to use as a User's avatar
     * @return the randomized url as a String
     */
    private String generateUrl() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        int len = rnd.nextInt(10) + 1;
        StringBuilder sb = new StringBuilder(len);
        sb.append("https://robohash.org/");
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private void writeSharedPref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String usersJson = gson.toJson(users);
        editor.putString(USERS, usersJson);
        editor.apply();
    }

    private void writeSharedPref(User user) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        String usersJson = gson.toJson(users);
        editor.putString(USER, userJson);
        editor.putString(USERS, usersJson);
        editor.apply();
    }

    private UserAdapter.ClickListener getOnClickListener() {
        return new UserAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                User user = users.get(position);
                Log.d(TAG, "Clicked " + user);

                Intent userDetailIntent = new Intent(getApplicationContext(), UserDetailActivity.class);
                writeSharedPref(user);
                startActivity(userDetailIntent);
            }
        };
    }
}