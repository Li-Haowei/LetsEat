package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "TAG";
    private EditText profileFullName,profileEmail,profilePhone, favoriteFood, dietaryRestriction, major, preferTime;
    private ImageView profileImageView, backBtn;
    private int SAVE=1,RESET_PASSWORD=2,LOGOUT=3;
    private Spinner spinner;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String fullName, email, phone, food, restrict, time, user_major;
    private FirebaseUser user;
    private StorageReference storageReference;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        fullName = data.getStringExtra("fullName");
        email = data.getStringExtra("email");
        phone = data.getStringExtra("phone");
        food = data.getStringExtra("favoriteFood");
        restrict = data.getStringExtra("dietaryRestriction");
        user_major = data.getStringExtra("major");
        time = data.getStringExtra("preferTime");

        /*
        FirebaseAuth is what we used to sign in users to our Firebase app
        https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
         */
        fAuth = FirebaseAuth.getInstance();
        /*
        Firestore database and is the entry point for all Cloud Firestore operations.
        https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/FirebaseFirestore#:~:text=Inherited%20Method%20Summary-,Public%20Methods,for%20all%20Cloud%20Firestore%20operations.
         */
        fStore = FirebaseFirestore.getInstance();
        /*
        get user
         */
        user = fAuth.getCurrentUser();
        /*
        Creates a new StorageReference initialized at the root Firebase Storage location.
        More details: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage#getReference()
         */
        storageReference = FirebaseStorage.getInstance().getReference();

        /*
        Bind fields to views
         */

        profileFullName = new EditText(this);
        profileEmail = new EditText(this);
        profilePhone = new EditText(this);
        favoriteFood = new EditText(this);
        major = new EditText(this);
        dietaryRestriction = new EditText(this);
        preferTime = new EditText(this);
        EditText[] editTextManager = {profileFullName, profileEmail, profilePhone, favoriteFood, major, dietaryRestriction, preferTime};
        for (int i = 0; i < editTextManager.length; i++) {
            editTextManager[i].setSingleLine();
            editTextManager[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);


        TextView tv0 = new TextView(this);
        tv0.setText("Name");
        linearLayout.addView(tv0);
        linearLayout.addView(profileFullName);
        TextView tv1 = new TextView(this);
        tv1.setText("Email");
        linearLayout.addView(tv1);
        linearLayout.addView(profileEmail);
        TextView tv2 = new TextView(this);
        tv2.setText("Phone");
        linearLayout.addView(tv2);
        linearLayout.addView(profilePhone);
        TextView tv3 = new TextView(this);
        tv3.setText("Favorite Food");
        linearLayout.addView(tv3);
        linearLayout.addView(favoriteFood);
        TextView tv4 = new TextView(this);
        tv4.setText("Major");
        linearLayout.addView(tv4);
        linearLayout.addView(major);
        TextView tv5 = new TextView(this);
        tv5.setText("Dietary Restriction");
        linearLayout.addView(tv5);
        linearLayout.addView(dietaryRestriction);
        TextView tv6 = new TextView(this);
        tv6.setText("Prefer Time to Eat");
        linearLayout.addView(tv6);
        linearLayout.addView(preferTime);

        LinearLayout linear = findViewById(R.id.rootContainer);
        linear.addView(scrollView);



        profileImageView = findViewById(R.id.profileImageView);
        backBtn = findViewById(R.id.backBtn);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.editProfileOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        /*
        StorageReference represents a reference to a Google Cloud Storage object.
        Developers can upload and download objects, get/set object metadata,
        and delete an object at a specified path.

        More detail please refer to example illustration in Register.java
         */
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        backBtn.setOnClickListener(view -> {
            finish();
        });
        profileEmail.setText(email);
        profileFullName.setText(fullName);
        profilePhone.setText(phone);
        favoriteFood.setText(food);
        major.setText(user_major);
        dietaryRestriction.setText(restrict);
        preferTime.setText(time);

        Log.d(TAG, "onCreate: " + fullName + " " + email + " " + phone);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);


            }
        }

    }
    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == LOGOUT){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        if(i == SAVE){
            if(profileFullName.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty() || profilePhone.getText().toString().isEmpty()){
                Toast.makeText(EditProfile.this, "one or many field are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(EditProfile.this, "Loading", Toast.LENGTH_SHORT).show();
            final String email = profileEmail.getText().toString();
            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String,Object> edited = new HashMap<>();
                    edited.put("email",email);
                    edited.put("fName",(Object) profileFullName.getText().toString());
                    edited.put("phone",(Object) profilePhone.getText().toString());
                    docRef.update(edited);
                    Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(i==RESET_PASSWORD){
            final EditText resetPassword = new EditText(view.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
            passwordResetDialog.setView(resetPassword);


            passwordResetDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    String newPassword = resetPassword.getText().toString();
                    user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfile.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            passwordResetDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //
                }
            });
            passwordResetDialog.create().show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}