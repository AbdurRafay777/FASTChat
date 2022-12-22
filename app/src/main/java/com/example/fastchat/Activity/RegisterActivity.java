package com.example.fastchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastchat.R;
import com.example.fastchat.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAccount;
    EditText registerUsername, registerEmail,
            registerPassword, registerConfirmPassword;
    Button btnRegister;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseDatabase database;
    ProgressDialog progress;

    final static String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        progress = new ProgressDialog(this);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        registerUsername = findViewById(R.id.registerUsername);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        alreadyHaveAccount.setOnClickListener((View v) -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        //Perform Authorization on button click
        btnRegister.setOnClickListener((View v) -> PerformAuth());
    }

    private void PerformAuth() {
        String name = registerUsername.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();
        String confirmPassword = registerConfirmPassword.getText().toString();


        //check user data on input
        if (name.isEmpty()){
            registerUsername.setError("Enter Username");
        }else if(!email.matches(emailPattern)){
            registerEmail.setError("Invalid Email");
        }else if(password.isEmpty() || password.length() < 6){
            registerPassword.setError("Invalid Password");
        }else if(!password.equals(confirmPassword)){
            registerConfirmPassword.setError("Passwords do not match");
        }else{
            progress.setMessage("Registering....");
            progress.setTitle("Registration");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }


        //register with firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            //send user to Home Activity
            progress.dismiss();
            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            saveUser(name,email);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void saveUser(String name, String email) {
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference db = firestore.collection("users").document(userID);

        Map<String, Object> user = new HashMap<>();
        user.put("name", name );
        user.put("email", email);
        user.put("uid", userID);

        db.set(user).addOnSuccessListener(unused -> Log.d("TAG", "User Profile created for" + userID));
        User dbUser = new User(name, email, userID);

        database.getReference().child("users").child(userID).setValue(dbUser);
    }
}