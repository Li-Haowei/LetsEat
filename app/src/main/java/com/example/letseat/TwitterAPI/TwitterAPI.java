package com.example.letseat.TwitterAPI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.letseat.BuildConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TwitterAPI {


    // Use Volley to make network request and retrieve data
    public static ArrayList<String> searchFollowings(String userName, Context context){
        // Generate URL based on the input username.
        String path = "https://api.twitter.com/2/users/by/username/" + userName;
        ArrayList<String> followingList = new ArrayList<>();
        // Following request returns user information given the user name, we only need ID here.
        StringRequest followingRequest = new StringRequest(Request.Method.GET, path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    String id = result.getJSONObject("data").getString("id");
                    // Use ID to retrieve user's following list
                    searchFollowingRequestById(id, followingList, context);
                    // Catch for the JSON parsing error
                } catch (JSONException e) {
                    Log.d("Response", e.getMessage()); //debug purpose
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", "No Responding ("); //debug purpose
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
        Volley.newRequestQueue(context).add(followingRequest);
        return followingList;
    }

    public static void searchFollowingRequestById(String Id, ArrayList<String> followingList, Context context){
        // Generate URL based on the user's ID.
        String path = "https://api.twitter.com/2/users/" + Id + "/following";
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
                } catch (JSONException e) {
                    Log.d("Response", e.getMessage()); //debug purpose
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", "No Responding ("); //debug purpose
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
        Volley.newRequestQueue(context).add(followingRequest);
    }

    // Compare the two list and return common followings
    public ArrayList<String> followingCompare(ArrayList<String> list1, ArrayList<String> list2){
        ArrayList<String> common = new ArrayList<>();
        for (int i = 0; i < list1.size(); i ++) {
            if (list2.contains(list1.get(i))) {
                common.add(list1.get(i));
            }
        }
        return common;
    }
}
