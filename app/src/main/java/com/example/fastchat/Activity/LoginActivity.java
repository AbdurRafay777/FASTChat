package com.example.fastchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastchat.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView signup, forgetPassword;
    EditText loginEmail, loginPassword;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    ProgressDialog progress;

    final static String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnlogin);
        forgetPassword = findViewById(R.id.forgotPassword);
        signup = findViewById(R.id.signup);

        signup.setOnClickListener((View v)->{
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            finish();
        });

        forgetPassword.setOnClickListener(v ->{
            startActivity(new Intent(this, ForgetPasswordActivity.class));
            finish();
        });

        //Login Operation on button click
        btnLogin.setOnClickListener((View v) -> {
            PerformLogin();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }
    }

    private void PerformLogin() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        //check user data on input
        if(!email.matches(emailPattern)){
            loginEmail.setError("Invalid Email");
        }else if(password.isEmpty() || password.length() < 6){
            loginPassword.setError("Invalid Password");
        }else{
            progress.setMessage("Logging In");
            progress.setTitle("Log In");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }

        //login user
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            //send user to Home Activity
            progress.dismiss();
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            progress.dismiss();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}