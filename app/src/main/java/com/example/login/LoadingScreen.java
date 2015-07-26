package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import android.widget.TextView;


public class LoadingScreen extends ActionBarActivity {
    public static final String DEFAULT="N/A";
    private String status1;
    private ProgressDialog pDialog;
    private static final String READ_COMMENTS_URL = "http://79.170.40.245/ascensionstepoff.com/pullingstatus.php";
    //private static final String READ_COMMENTS_URL = "http://192.168.1.5:1234/ver2/pullingstatus.php";
    private TextView statusCheck;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STATUS = "status";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_MESSAGE = "message";
    private Button test;
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    private JSONArray mComments = null;
    JSONParser jsonParser = new JSONParser();

    private ArrayList<HashMap<String, String>> mCommentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                new LoadStatus().execute();

            }
        }, SPLASH_DISPLAY_LENGTH);

    }



    class LoadStatus extends AsyncTask<String, String, String> {
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected String doInBackground(String... args) {

            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String Status = sharedPreferences.getString("NewStatus",DEFAULT);

            try
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", Status));

                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(
                        READ_COMMENTS_URL, "POST", params);
                Log.d("Login attempt", json.toString());



                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    mCommentList = new ArrayList<HashMap<String, String>>();


                    try {


                        mComments = json.getJSONArray(TAG_POSTS);

                        // looping through all posts according to the json object returned
                        for (int i = 0; i < mComments.length(); i++) {
                            JSONObject c = mComments.getJSONObject(i);

                            //gets the content of each tag
                            status1 = c.getString(TAG_STATUS);


                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_STATUS, status1);

                            mCommentList.add(map);

                            if(status1.equals("first_timer")) {
                                Intent toEditProfile = new Intent(LoadingScreen.this, EditProfile.class);
                                startActivity(toEditProfile);
                            }
                            else{//if(status1.equals("registered")){
                                Intent toProfile = new Intent(LoadingScreen.this, Profile.class);
                                startActivity(toProfile);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));


                }
            }

            catch(
                    JSONException e
                    )

            {
                e.printStackTrace();
            }

            return status1;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted


            if (file_url != null){
                Toast.makeText(LoadingScreen.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
    }
}