package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.letseat.optionsPage.FavoriteFoodCuisine;
import com.example.letseat.tools.RealPathUtil;
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
/*
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;
*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private Uri imageUri;
    private FirebaseUser user;
    private StorageReference storageReference;
   // private TwitterLoginButton loginButton;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //initTwitter();
        setContentView(R.layout.activity_edit_profile);


       // twitterButton();


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
                major.setSelection(i);
                break;
            }
        }
        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user_major = items_major[i];
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


        profileImageView = findViewById(R.id.img_rest);
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

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("creation",e.toString());
            }
        });
        //if there is a saved profile image, load it into image view
        //if(profileImg!=null) {
            //profileImageView.setImageURI(Uri.parse(profileImg));
        //}



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
                imageUri = data.getData();
                //Log.d("creation", String.valueOf(imageUri));
                profileImageView.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
        // Pass the activity result to the login button.
        /*
        if(loginButton!=null) {
            loginButton.onActivityResult(requestCode, resultCode, data);
        } */

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
                Log.d("creation",e.toString());
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

//    init twitter
    /*
    private void initTwitter() {
        String key = getString(R.string.twitter_consumer_key);
        String secret = getString(R.string.twitter_consumer_secret);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(key, secret))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private void twitterButton () {
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                // result里面包含了用户的信息，我们可以从中取出token，tokenSecret
                // 如果我们有自己的后台服务器，发送这两个到我们自己的后台，后台再去验证）
                TwitterAuthToken authToken = result.data.getAuthToken();

                String token = authToken.token;
                String tokenSecret = authToken.secret;
                String userName = result.data.getUserName();
                String userId = result.data.getUserId()+"";

                Log.d("TEST","Token: " + token);
                Log.d("TEST","Secret: " + tokenSecret);
                Log.d("TEST","UserName: " + userName);
                Log.d("TEST","UserId: "+userId);

                storeFollowing(userId);

            }

            @Override
            public void failure(TwitterException exception) { // Do something on failure
                exception.printStackTrace();
            }
        });
    }
*/
    public void storeFollowing (String userId) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();;
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String uid = fAuth.getCurrentUser().getUid();
        ArrayList<String> followingList = new ArrayList<>();
        //TwitterAPI.searchFollowingRequestById(userId, followingList, EditProfile.this);
        String path = "https://api.twitter.com/2/users/" + userId + "/following";
        // Following request returns a list of user's following accounts given the user's ID.
        StringRequest followingRequest = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    JSONArray array = result.getJSONArray("data");
                    followingList.clear();
                    for (int i = 0; i < array.length(); i++){
                        followingList.add(array.getJSONObject(i).getString("name"));
                    }
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("Followings",followingList);
//                    Log.d("CREATION", "ARRAY: " + followingList);
//                    fStore.collection("users").document(user.getUid()).add(map).addOnSuccessListener(new OnSuccessListener() {
//                        @Override
//                        public void onSuccess(Object o) {
//                            Log.d("TAG", "OnSuccess: a new post being created" + userId);
//                        }
//                    });
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String,Object> edited = new HashMap<>();
//                    edited.put("email",email);
//                    edited.put("fName",(Object) profileFullName.getText().toString());
//                    edited.put("phone",(Object) profilePhone.getText().toString());
//                    // store other information
//                    edited.put("favoriteFood",(Object) food);
//                    edited.put("major",(Object) user_major);
//                    edited.put("preferTime",(Object) time);
                    edited.put("followings", followingList);
                    docRef.update(edited);
                } catch (JSONException e) {
                    Log.d("Response", e.getMessage()); //debug purpose
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", "No REsponding ("); //debug purpose
            }

        }){
            // Add authorization token to our request
            public Map<String, String> getHeaders(){
                HashMap<String, String> params = new HashMap<>();

                params.put("Authorization", BuildConfig.TWITTER_API_KEY);

                return params;
            }
        };
        // Put request into a request queue
        Volley.newRequestQueue(this).add(followingRequest);


    }
// fetch userinfo from Twitter
//    private void twitterUserInfo() {
//
//        final TwitterSession activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
//
//        if (activeSession == null) {
//            String message = "User haven't loged in";
//            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
//            return;
//        }
//        TwitterApiClient client = new TwitterApiClient(activeSession);
//        AccountService accountService = client.getAccountService();
//        Call<User> show = accountService.verifyCredentials(false, false, true);
//        show.enqueue(new Callback<User>() {
//            @Override
//            public void success(Result<User> result) {
//                User data = result.data;
//                String profileImageUrl = data.profileImageUrl.replace("_normal", "");
//                String idStr = data.idStr;
//                String name = data.name;
//                String email = data.email;
//                String description = data.description;
//
//                Log.i("profileImageUrl", profileImageUrl);
//                Log.i("idStr", idStr);
//                Log.i("name", name);
//
//                Log.i("email", email);
//
//                Log.i("description", description);
//
//                String message = "userName = "+name+"\t"+"idStr = "+idStr+"\t"+"email = "+email;
//
//                ArrayList<String> list = TwitterAPI.searchFollowings(name,EditProfile.this);
//                FirebaseAuth fAuth = FirebaseAuth.getInstance();;
//                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
//                String userId = fAuth.getCurrentUser().getUid();
//                fStore.collection("followings").add(list).addOnSuccessListener(new OnSuccessListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        Log.d("Post", "OnSuccess: a new post being created" + userId);
//                    }
//                });
//
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                exception.printStackTrace();
//            }
//        });
//    }
}