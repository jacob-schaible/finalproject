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

import com.example.finalproject.model.Person;
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
    private static final String PERSON = "person";
    private static final String PEOPLE = "people";

    private SharedPreferences sharedPref;
    private Person userPerson;
    private ArrayList<Person> people;
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
        String peopleJson = sharedPref.getString(PEOPLE, "");
        if (!peopleJson.isEmpty()) {
            Person[] array = gson.fromJson(peopleJson, Person[].class);
            people = new ArrayList<>(Arrays.asList(array));
        } else {
            people = new ArrayList<>();
        }
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_people);

        handleSignedOut(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));

        recyclerView = findViewById(R.id.people_recycler);
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
     * Generates a Person object from the GoogleSignInAccount user
     * @return the Peron
     */
    private Person getUserPerson() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        Person person = Person.EMPTY();
        if (account != null) {
            int id = (people != null) ? people.size()+1 : 1;
            person.setId(id);
            person.setName(account.getDisplayName());
            person.setEmail(account.getEmail());
            if (account.getPhotoUrl() != null)
                person.setAvatarUrl(account.getPhotoUrl().toString());
        }
        return person;
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
     * Signs the user out of their GoogleSignInAccount and returns to login activity.
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

    /**
     * Makes request with web service to retrieve people list data and handles response
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
     * Parses web service response data from Json to a List of people,
     * assigns each person a randomized avatar url,
     * and sorts the List of people based on name.
     * @param data String containing Json response
     */
    private void parseData(String data) {
        if (data != null) {
            Gson gson = new Gson();
            Person[] array = gson.fromJson(data, Person[].class);
            people = new ArrayList<>(Arrays.asList(array));
            userPerson = getUserPerson();
            people.add(userPerson);
            for (Person p : people) {
                if (p.getAvatarUrl() == null || p.getAvatarUrl().isEmpty())
                    p.setAvatarUrl(generateUrl());
            }
            Collections.sort(people);
            Log.d(TAG, "Parsing " + people.size() + " people");

        } else {
            Log.d(TAG, "No data to parse");
        }
    }

    /**
     * Populates RecyclerView with Person list
     */
    private void populateRecycler() {
        Log.d(TAG, "Populating recycler view");
        PersonAdapter.setOnItemClickListener(getOnClickListener());
        recyclerView.setAdapter(new PersonAdapter(people));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
    }

    private void loadData() {
        if (people.isEmpty()) {
            Log.d(TAG, "Calling web service");
            client = new OkHttpClient();
            getWebService(getString(R.string.people_data_url));
        } else {
            populateRecycler();
        }
    }

    /**
     * Generates a randomized Robohash url to use as a Person's avatar
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
        String peopleJson = gson.toJson(people);
        editor.putString(PEOPLE, peopleJson);
        editor.apply();
    }

    private void writeSharedPref(Person person) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String personJson = gson.toJson(person);
        String peopleJson = gson.toJson(people);
        editor.putString(PERSON, personJson);
        editor.putString(PEOPLE, peopleJson);
        editor.apply();
    }

    private PersonAdapter.ClickListener getOnClickListener() {
        return new PersonAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Person person = people.get(position);
                Log.d(TAG, "Clicked " + person);

                Intent personDetailIntent = new Intent(getApplicationContext(), PersonDetailActivity.class);
                writeSharedPref(person);
                startActivity(personDetailIntent);
            }
        };
    }
}