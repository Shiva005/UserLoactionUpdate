package com.akshcabs.userloactionupdate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    public static FirebaseAuth mAuth;
    private TextInputEditText mEmail, mPassword;
    private Button Register, Login;
    String email, password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.et_Email_id);
        mPassword = findViewById(R.id.et_password_id);
        Register = findViewById(R.id.Registr);
        Login = findViewById(R.id.logi);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterMethod();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginMethod();
            }
        });
    }

    private boolean validation() {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required !!");
            mEmail.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required !!");
            mPassword.setFocusable(true);
            return false;
        }
        return true;
    }

    private void LoginMethod() {
        if(validation()) {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Welcome, Login Success", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(RegistrationActivity.this, MapActivity.class));
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Please Check the Credentials", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void RegisterMethod() {
        if(validation()) {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegistrationActivity.this, "Registration SuccessFull", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(RegistrationActivity.this, MapActivity.class));
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Failed to Register.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            finish();
            startActivity(new Intent(RegistrationActivity.this,MapActivity.class));
        }
    }

    public void skiptoMap(View view) {
        startActivity(new Intent(RegistrationActivity.this,ViewAllUsers.class));
    }
}
