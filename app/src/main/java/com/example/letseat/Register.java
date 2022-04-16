package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
This is the register class, that register user into firebase users
 */
public class Register extends AppCompatActivity {

    private static final String TAG = "TAG";
    private EditText mFullName,mEmail,mPassword,mPhone;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Binding fields to views
         */
        setContentView(R.layout.activity_register);
        mFullName   = findViewById(R.id.PersonName);
        mEmail      = findViewById(R.id.EmailAddress);
        mPassword   = findViewById(R.id.Password);
        mPhone      = findViewById(R.id.PhoneNumber);
        mRegisterBtn= findViewById(R.id.register);
        mLoginBtn   = findViewById(R.id.linktologinpage);

        /*
        Here we are going to use FirebaseAuth to communicate with firebase.
        FirebaseAuth is what we used to consume firebase API:
        https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
         */
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        //after log in, take user to the profile page which gonna locate in MainActivity
        if(fAuth.getCurrentUser() != null){
            //this checks if user has logged in
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        /*
        As its name, this button register user with the user inputs
         */
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the user inputs
                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();
                final String phone = mPhone.getText().toString();
                //make sure user input valid email address
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }
                //make sure user input password
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                //make sure password is more than 6 digits
                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                //set progressbar visible while user register account
                progressBar.setVisibility(View.VISIBLE);

                //next step, register user in firebase
                /*
                param String email
                param String password
                Note that this function could work without listener,
                but without listener we cannot tell if the registration was successful
                 */
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    /*
                    param Task<AuthResult> task, a type of server response
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if registration is good, send verification link to user's email
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            //Again, the listener is not required but we need to know the result
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not send" + e.getMessage());
                                }
                            });


                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            /*
                            Returns a user identifier as specified by the authentication provider.
                            For example, if this object corresponds to a Google user, returns a Google user ID.
                            For phone number accounts, the UID will be the normalized phone number in E.164 format.
                             */
                            //Every user will have its id generated automatically in the backend
                            userID = fAuth.getCurrentUser().getUid();
                            /*
                            A DocumentReference refers to a document location in a Cloud Firestore database
                            and can be used to write, read, or listen to the location. There may or may not
                            exist a document at the referenced location. A DocumentReference can also be used
                            to create a CollectionReference to a subcollection.
                             */
                            //Here we create a collection or enter collection if already created
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            //we use a hashmap object to store all information which will be eventually put
                            //under users.userID
                            /*
                            For example:
                            users ->
                                      xyz123abc ->
                                                fName : "Haowei Li",
                                                email : "example@gmail.com",
                                                phone : "666-666-6666"
                             */
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullName);
                            user.put("email", email);
                            user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "OnSuccess: user profile is created for" + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure:" + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }

        });
        //If the user already has an account, he/she can go to login page
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });



    }
}