package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class EditProfile extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    private Spinner spinner;
    private Toolbar toolbar;
    private Button next;
    private ToggleButton ocMale;
    private ToggleButton ocFemale;
    private String genderStatus;
    //Update Strings
    private String strCountry;
    private String strGender;
    private String strName;
    private String age;
    private String description;

    private EditText etDescription;
    private EditText etAge;
    private EditText etDisplayName;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String LOGIN_LOGIN_URL ="http://79.170.40.245/ascensionstepoff.com/addingdetails2.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public static final String DEFAULT="N/A";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        next = (Button)findViewById(R.id.button);

        ocMale = (ToggleButton) findViewById(R.id.btnMale);
        ocFemale = (ToggleButton)findViewById(R.id.btnFemale);
        spinner = (Spinner) findViewById(R.id.spinner);

        etDescription = (EditText) findViewById(R.id.description);
        etAge = (EditText) findViewById(R.id.age);
        etDisplayName = (EditText) findViewById(R.id.displayName);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
    public void next (View view){
        //Intent toProfile = new Intent(EditProfile.this, Profile.class);
        //startActivity(toProfile);

       // Toast.makeText(this,Status, Toast.LENGTH_SHORT).show();
        new AttemptInsert().execute();
    }

        public void ocMale (View view){
            ocFemale.setChecked(false);

            strGender = "Male";
        }

        public void ocFemale (View view){
            ocMale.setChecked(false);

            strGender = "Female";
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        TextView myText = (TextView) view;
        Toast.makeText(this,"You Selected " + myText.getText(), Toast.LENGTH_SHORT).show();
        strCountry = myText.getText().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class AttemptInsert extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfile.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String strStatus = "registered";
            String strName = etDisplayName.getText().toString();
            String strDescription = etDescription.getText().toString();
            String strAge = etAge.getText().toString();
            SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String strEmail = sharedPreferences.getString("NewStatus",DEFAULT);


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("status",strStatus ));
                params.add(new BasicNameValuePair("name", strName));
                params.add(new BasicNameValuePair("description", strDescription));
                params.add(new BasicNameValuePair("country", strCountry));
                params.add(new BasicNameValuePair("age", strAge));
                params.add(new BasicNameValuePair("gender", strGender));
                params.add(new BasicNameValuePair("email", strEmail));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_LOGIN_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    Intent toProfile = new Intent (EditProfile.this, Profile.class);
                    startActivity(toProfile);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

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
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(EditProfile.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
}
