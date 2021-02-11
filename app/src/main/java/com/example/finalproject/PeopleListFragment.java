package com.example.finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class PeopleListFragment extends Fragment {
    private static final String TAG = "PeopleListFragment";
    private static final String USERS = "users";

    private RecyclerView peopleRecycler;
    private ArrayList<User> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            users = savedInstanceState.getParcelableArrayList(USERS);
        } catch (NullPointerException e) {
            users = new ArrayList<>();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        peopleRecycler = view.findViewById(R.id.people_recycler);
        peopleRecycler.setAdapter(new UserAdapter(users));
        peopleRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }
}