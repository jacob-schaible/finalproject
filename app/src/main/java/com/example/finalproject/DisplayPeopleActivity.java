package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayPeopleActivity extends AppCompatActivity {
    private final String TAG = "DisplayPeopleActivity";

    private User currentUser;
    private List<User> users;
    private OkHttpClient client;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_people);
        recyclerView = findViewById(R.id.user_recycler);

        currentUser = getCurrentUser();
        users = new ArrayList<>();

        client = new OkHttpClient();
        Log.d(TAG, "Calling web service");
        getWebService(getString(R.string.user_data_url));
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

    private User getCurrentUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        User user = User.EMPTY();
        if (account != null) {
            user.setName(account.getDisplayName());
            user.setEmail(account.getEmail());
            if (account.getPhotoUrl() != null)
                user.setAvatarUrl(account.getPhotoUrl().toString());
        }
        return user;
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
                            populateData(response.body().string());
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

    private void populateData(String data) {
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
            Log.d(TAG, "Populating with " + users.size() + " users");

            UserAdapter.setOnItemClickListener(getOnClickListener());
            recyclerView.setAdapter(new UserAdapter(users));
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        } else {
            Log.d(TAG, "No data to populate");
        }
    }

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

    private UserAdapter.ClickListener getOnClickListener() {
        return new UserAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                User user = users.get(position);
                Log.d(TAG, "Clicked " + user);

                Intent userDetailIntent = new Intent(getApplicationContext(), UserDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                userDetailIntent.putExtras(bundle);
                startActivity(userDetailIntent);
            }
        };
    }
}