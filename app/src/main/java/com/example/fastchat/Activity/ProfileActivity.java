package com.example.fastchat.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.fastchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    TextView profileName;
    TextView profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileUsername);

        String uid = firebaseAuth.getCurrentUser().getUid();
        DocumentReference doc = firestore.collection("users").document(uid);
        doc.addSnapshotListener(this,(value, error) -> {
            profileName.setText(value.getString("name"));
            profileEmail.setText(value.getString("email"));
        });

    }
}