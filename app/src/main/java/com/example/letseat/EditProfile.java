package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.optionsPage.FavoriteFoodCuisine;
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
    private EditText profileFullName,profileEmail,profilePhone;
    private ImageView profileImageView, backBtn;
    private final int SAVE=1,RESET_PASSWORD=2,LOGOUT=3;
    private final float textSize = 30.0f;
    private Spinner spinner, preferTime, major;
    private TextView favoriteFood;
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
        favoriteFood = new TextView(this);
        //favoriteFood = new Spinner(this);

        //create a list of items for the spinner.


        major = new Spinner(this);
        String[] items_major = new String[]{"Computer Science", "Political Science", "Film/TV", "Business"};
        ArrayAdapter<String> adapter_majors = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_major);
        major.setAdapter(adapter_majors);
        for (int i = 0; i < items_major.length; i++) {
            if(items_major[i].equals(user_major)){
                Log.d("creation", items_major[i]);
                major.setSelection(i);
                break;
            }
        }
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
        for (int i = 0; i < items_time.length; i++) {
            if(items_time[i].equals(time)){
                preferTime.setSelection(i);
                break;
            }
        }
        //preferTime.setSelection(sharedPref.getInt("times",0));
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



        //This makes every text input field can escape with enter key
        EditText[] editTextManager = {profileFullName, profileEmail, profilePhone};
        for (int i = 0; i < editTextManager.length; i++) {
            editTextManager[i].setSingleLine();
            editTextManager[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        //Programmatically creating views for name, phone, email, and etc.
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);

        //Set layout parameters for the input field
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,0,20, 40);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(20,0,20, 0);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(20,0,20, 40);

        //name section
        TextView tv0 = new TextView(this);
        tv0.setText(R.string.user_name);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextColor(getResources().getColor(R.color.white));
        tv0.setLayoutParams(params1);
        linearLayout.addView(tv0);
        linearLayout.addView(profileFullName);
        profileFullName.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        profileFullName.setMaxLines(1);
        profileFullName.setLayoutParams(params);

        //Email section
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.user_email);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setLayoutParams(params1);
        linearLayout.addView(tv1);
        linearLayout.addView(profileEmail);
        profileEmail.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        profileEmail.setMaxLines(1);
        profileEmail.setLayoutParams(params);

        //Number section
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.user_phone);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(getResources().getColor(R.color.white));
        tv2.setLayoutParams(params1);
        linearLayout.addView(tv2);
        linearLayout.addView(profilePhone);
        profilePhone.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        profilePhone.setMaxLines(1);
        profilePhone.setLayoutParams(params);

        //Food section
        TextView tv3 = new TextView(this);
        tv3.setText(R.string.user_food);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(getResources().getColor(R.color.white));
        tv3.setLayoutParams(params1);
        linearLayout.addView(tv3);
        linearLayout.addView(favoriteFood);
        favoriteFood.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        favoriteFood.setText(food);
        favoriteFood.setTextSize(textSize);
        favoriteFood.setTextColor(getResources().getColor(R.color.black));
        favoriteFood.setLayoutParams(params);

        //Major section
        TextView tv4 = new TextView(this);
        tv4.setText(R.string.user_major);
        tv4.setTypeface(null, Typeface.BOLD);
        tv4.setTextColor(getResources().getColor(R.color.white));
        tv4.setLayoutParams(params1);
        linearLayout.addView(tv4);
        linearLayout.addView(major);
        major.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        major.setLayoutParams(params);

        //Time section
        TextView tv5 = new TextView(this);
        tv5.setText(R.string.user_pref_time);
        tv5.setTypeface(null, Typeface.BOLD);
        tv5.setTextColor(getResources().getColor(R.color.white));
        tv5.setLayoutParams(params1);
        linearLayout.addView(tv5);
        linearLayout.addView(preferTime);
        preferTime.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        preferTime.setLayoutParams(params);


        //add scrollable view into rootContainer
        LinearLayout linear = findViewById(R.id.rootContainer);
        linear.addView(scrollView);


        profileImageView = findViewById(R.id.profileImageView);
        backBtn = findViewById(R.id.backBtn);
        spinner = findViewById(R.id.spinner);
        spinner.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
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
        favoriteFood.setOnClickListener(view -> {
            Intent foodOptionsIntent = new Intent(view.getContext(), FavoriteFoodCuisine.class);
            foodOptionsIntent.putExtra("fullName", fullName );
            foodOptionsIntent.putExtra("email", email);
            foodOptionsIntent.putExtra("phone", phone);
            foodOptionsIntent.putExtra("favoriteFood", food);
            foodOptionsIntent.putExtra("major", user_major);
            foodOptionsIntent.putExtra("preferTime", time);
            startActivity(foodOptionsIntent);
            finish();
        });

        backBtn.setOnClickListener(view -> {
            finish();
        });
        profileEmail.setText(email);
        profileFullName.setText(fullName);
        profilePhone.setText(phone);
        //int positionFood = adapter_cuisines.getPosition(food);
       // favoriteFood.setSelection(positionFood);
        //int positionMajor = adapter_majors.getPosition(user_major);
      //  major.setSelection(positionMajor);
        //int positionTime = adapter_time.getPosition(time);
      //  preferTime.setSelection(positionTime);

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