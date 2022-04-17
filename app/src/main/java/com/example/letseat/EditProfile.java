package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private EditText profileFullName,profileEmail,profilePhone;
    private ImageView profileImageView, backBtn;
    private int SAVE=1,RESET_PASSWORD=2,LOGOUT=3;
    private Spinner spinner, favoriteFood, preferTime, major;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String fullName, email, phone, food, time, user_major;
    private FirebaseUser user;
    private StorageReference storageReference;
    private SharedPreferences sharedPref;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Intent data = getIntent();
        fullName = data.getStringExtra("fullName");
        email = data.getStringExtra("email");
        phone = data.getStringExtra("phone");
        food = data.getStringExtra("favoriteFood");
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
        favoriteFood = new Spinner(this);
        //create a list of items for the spinner.
        String[] items_cuisines = new String[]{"American", "Korean", "Chinese", "Thai", "Japanese", "Italian", "French"};
        ArrayAdapter<String> adapter_cuisines = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_cuisines);
        favoriteFood.setAdapter(adapter_cuisines);
        favoriteFood.setSelection(sharedPref.getInt("foods",0));
        favoriteFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                food = items_cuisines[i];
                editor.putInt("foods", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        major = new Spinner(this);
        String[] items_major = new String[]{"Computer Science", "Political Science", "Film/TV", "Business"};
        ArrayAdapter<String> adapter_majors = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_major);
        major.setAdapter(adapter_majors);
        major.setSelection(sharedPref.getInt("majors",0));
        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user_major = items_major[i];
                editor.putInt("majors", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        preferTime = new Spinner(this);
        String[] items_time = new String[]{"Breakfast", "Lunch", "Dinner"};
        ArrayAdapter<String> adapter_time = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_time);
        preferTime.setAdapter(adapter_time);
        preferTime.setSelection(sharedPref.getInt("times",0));
        preferTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                time = items_time[i];
                editor.putInt("times", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EditText[] editTextManager = {profileFullName, profileEmail, profilePhone};
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
        tv3.setText("Favorite Food Cuisine");
        linearLayout.addView(tv3);
        linearLayout.addView(favoriteFood);
        TextView tv4 = new TextView(this);
        tv4.setText("Major");
        linearLayout.addView(tv4);
        linearLayout.addView(major);
        TextView tv5 = new TextView(this);
        tv5.setText("Preferred Meal to Eat");
        linearLayout.addView(tv5);
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
        int positionFood = adapter_cuisines.getPosition(food);
       // favoriteFood.setSelection(positionFood);
        int positionMajor = adapter_majors.getPosition(user_major);
      //  major.setSelection(positionMajor);
        int positionTime = adapter_time.getPosition(time);
      //  preferTime.setSelection(positionTime);

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
        // upload image to firebase storage
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
                    // store other information
                    edited.put("favoriteFood",(Object) food);
                    edited.put("major",(Object) user_major);
                    edited.put("preferTime",(Object) time);
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