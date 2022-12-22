package com.example.fastchat.ui.chat;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fastchat.R;
import com.example.fastchat.User;
import com.example.fastchat.UserAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class chat extends Fragment {

    public interface MyCallback {
        void onCallback(ArrayList<User> userList);
    }

    private RecyclerView rv;
    private RecyclerView.Adapter userAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<User> users = new ArrayList<>();
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    private AdView mAdView;

    private ChatViewModel mViewModel;

    public static chat newInstance() {
        return new chat();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserItems(userList -> {
            users = userList;
            users.removeIf(user -> user.getEmail().equals(firebaseUser.getEmail()));
            users.sort(Comparator.comparing(User::getName));
            userAdapter = new UserAdapter(getActivity(),users);
            rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            rv.setAdapter(userAdapter);
        });

        rv =  view.findViewById(R.id.userList);
        rv.setHasFixedSize(true);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        return view;
    }

    private void getUserItems(MyCallback callback) {
        ArrayList<User> userList = new ArrayList<>();
        firestore.collection("users").get()
                .addOnSuccessListener(snapshot -> {
                    if(snapshot.isEmpty()){
                        Log.d("TAG", "Success but List Empty");
                        return;
                    }
                    else {
                        List<User> list = snapshot.toObjects(User.class);
                        userList.addAll(list);
                        callback.onCallback(userList);
                    }
                }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        // TODO: Use the ViewModel
    }

}