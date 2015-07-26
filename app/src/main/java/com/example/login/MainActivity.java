package com.example.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;

import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.models.Scope;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    //Register Tab Variables

    private EditText rP = null;
    private EditText rE = null;
    private EditText rCP = null;
    private Button register;
    //Get Status Variables
    private String status;

    private Button skip;

    //Login Tab Variables
    private Button login;
    private EditText lP = null;
    private EditText lE = null;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //Nabu SDK
    static NabuOpenSDK nabuSDK = null;

    //Link to XAMPP Local Host
    private static final String REGISTER_LOGIN_URL = "http://79.170.40.245/ascensionstepoff.com/register2.php";
   // private static final String REGISTER_LOGIN_URL = "http://192.168.1.5:1234/ver2/register.php";

    private static final String LOGIN_LOGIN_URL ="http://79.170.40.245/ascensionstepoff.com/login.php";
    //private static final String LOGIN_LOGIN_URL = "http://192.168.1.5:1234/ver2/login.php";

    //JSON element ids from repsonse of php script:
    private static final String REGISTER_TAG_SUCCESS = "success";
    private static final String REGISTER_TAG_MESSAGE = "message";

    private static final String LOGIN_TAG_SUCCESS="success";
    private static final String LOGIN_TAG_MESSAGE="message";


    //Progress Dialog
    private ProgressDialog RegisterDialog;
    private ProgressDialog LoginDialog;

    private JSONArray mComments = null;
    private static final String TAG_STATUS = "status";
    private ArrayList<HashMap<String, String>> mCommentList;
    private static final String TAG_POSTS = "posts";

    private String strString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //NABU SDK
        nabuSDK = NabuOpenSDK.getInstance(this);
        //Register Tab Variable Extraction)
        rCP = (EditText) findViewById(R.id.rConfirmPassword);
        rE = (EditText) findViewById(R.id.rEmail);
        rP = (EditText) findViewById(R.id.rPassword);
        register = (Button) findViewById(R.id.button1);
        skip =(Button) findViewById(R.id.skip);

        //Login Tab Variable Extraction
        lP = (EditText) findViewById(R.id.lPassword);
        lE = (EditText) findViewById(R.id.lEmail);
        login = (Button) findViewById(R.id.btnLogin);

        //TabHost Variable Extraction
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);


        /*********************************TabHost Setup*************************************/
        tabHost.setup();

        //Creation of Login TabHost
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Login");
        tabSpec.setContent(R.id.login);
        tabSpec.setIndicator("Login");
        tabHost.addTab(tabSpec);

        //Creation of Register TaHost
        tabSpec = tabHost.newTabSpec("Register");
        tabSpec.setContent(R.id.register);
        tabSpec.setIndicator("Register");
        tabHost.addTab(tabSpec);

        //Listener to detect Tab Change
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                setTabColor(tabHost);
            }
        });
        setTabColor(tabHost);
    }
    public void skip(View view) {
        Intent toLogin = new Intent(MainActivity.this, Profile.class);
        startActivity(toLogin);
    }

    /**
     * **************************************METHOD TO CHANGE TAB COLOR***********************************************************
     */
    public void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF797979")); //unselected

        if (tabhost.getCurrentTab() == 0)
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFF386B")); //1st tab selected
        else
            tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFF386B")); //2nd tab selected
    }

    /**
     * **********************************WHEN LOGIN BUTTON IS PRESSED**************************************************************
     */
    public void login(View view) {
       new AttemptLogin().execute();
   }


    /**
     * **********************************WHEN REGISTER BUTTON IS PRESSED***********************************************************
     */

    public void register(View view) {

        if (rCP.getText().toString().equals(rP.getText().toString())) {
            new CreateUser().execute();

        } else {
            rCP.setError("Make sure that passwords match");
        }
    }

    /**
     * **********************************************AUTO GENERATED CODE ON CREATION OF ACTIVITY**************************************
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ****************************************REGISTER INSERT QRY**************************************
     */
    class CreateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegisterDialog = new ProgressDialog(MainActivity.this);
            RegisterDialog.setMessage("Creating User...");
            RegisterDialog.setIndeterminate(false);
            RegisterDialog.setCancelable(true);
            RegisterDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int Registration_success;

            String Register_email = rE.getText().toString();
            String Register_password = rP.getText().toString();
            String Register_status = "first_timer";
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("status", Register_status));
                params.add(new BasicNameValuePair("username", Register_email));
                params.add(new BasicNameValuePair("email", Register_email));
                params.add(new BasicNameValuePair("password", Register_password));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_LOGIN_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                Registration_success = json.getInt(REGISTER_TAG_SUCCESS);
                if (Registration_success == 1) {
                    Log.d("User Created!", json.toString());

                   // Intent Refresh = new Intent(MainActivity.this, MainActivity.class);
                 //   finish();

                    return json.getString(REGISTER_TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(REGISTER_TAG_MESSAGE));
                    return json.getString(REGISTER_TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            RegisterDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }



    }


    /***************************************ATTEMPT LOGIN****************************************/
    class AttemptLogin extends AsyncTask<String, String, String> {


        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginDialog = new ProgressDialog(MainActivity.this);
            LoginDialog.setMessage("Attempting login...");
            LoginDialog.setIndeterminate(false);
            LoginDialog.setCancelable(true);
            LoginDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String login_email = lE.getText().toString();
            String login_password = lP.getText().toString();

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("email", login_email));
                params.add(new BasicNameValuePair("password", login_password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(LOGIN_TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());

                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("NewStatus", lE.getText().toString());
                    editor.commit();

                    //Authenticate App
                    nabuSDK.initiate(MainActivity.this, "344e04b0beb7f1b962732226f440f8169a059eb7", new String[] { Scope.COMPLETE }, new NabuAuthListener() {

                        @Override
                        public void onAuthSuccess(String arg0) {
                            Toast.makeText(getApplicationContext(), "App Auth Success",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAuthFailed(String arg0) {
                            Toast.makeText(getApplicationContext(), "App Authentication Fail",Toast.LENGTH_LONG).show();
                        }
                    });


                    Intent ToLoad = new Intent(MainActivity.this, LoadingScreen.class);
                    startActivity(ToLoad);

                    return json.getString(LOGIN_TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(LOGIN_TAG_MESSAGE));

                    return json.getString(LOGIN_TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted

            LoginDialog.dismiss();
            if (file_url != null){
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }


}
