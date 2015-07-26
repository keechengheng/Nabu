package com.example.login;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.FitnessListener;
import com.razer.android.nabuopensdk.interfaces.LiveDataListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.NabuFitness;


public class Profile extends ActionBarActivity {
    private Toolbar toolbar;
    private Button logout;
    private Button setting;
    private Button beginGame;
    private TextView TT;
    private TextView LiveFitness;
    private TextView TVTest;
    public NabuBand selectedBand = null;
    long startTime;
    long endTime;
    int timeA = 60000;
    final String saveStatus = null;
    private int saveSteps = 0;

    static NabuOpenSDK nabuSDK = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        //NABU SDK
        nabuSDK = NabuOpenSDK.getInstance(this);

        logout = (Button) findViewById(R.id.btnLogOut);
        setting = (Button) findViewById(R.id.btnSetting);
        beginGame = (Button) findViewById(R.id.btnBeginGame);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        TT = (TextView) findViewById(R.id.TVtimer);
        TVTest = (TextView) findViewById(R.id.TVtest);
        LiveFitness = (TextView) findViewById(R.id.TVfitness);

        setSupportActionBar(toolbar);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);

        //Retrieve Nabu from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String NabuStatus = sharedPreferences.getString("BandSaved","").toString();
        if (NabuStatus.equals("true")) {
            selectedBand.bandId = sharedPreferences.getString("BandID","").toString();
            selectedBand.serialNumber = sharedPreferences.getString("BandSerialNumber","").toString();
            selectedBand.model = sharedPreferences.getString("BandModel","").toString();
            selectedBand.name = sharedPreferences.getString("BandName","").toString();
        }
        else {
            Toast.makeText(getApplicationContext(), "No Nabu Saved", Toast.LENGTH_LONG).show();
        }
    }

    public void logout(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("LoginStatus", "LO");
        editor.commit();

        Intent toLogin = new Intent(Profile.this, MainActivity.class);
        startActivity(toLogin);
    }
    public void setting(View view){

        Intent toSetting = new Intent(Profile.this, NabuSetting.class);
        startActivity(toSetting);
    }
    public void beginGame(View view){
        if (selectedBand == null) {
            Toast.makeText(getApplicationContext(), "Please Select a Nabu", Toast.LENGTH_LONG).show();
            return;
        }

        long startTime = System.currentTimeMillis() / 1000L;
        long endTime = startTime + timeA;
        nabuSDK.enableFitness(Profile.this, selectedBand, new LiveDataListener() {

            @Override
            public void onError(String errorMessage) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLiveDataReceived(NabuFitness nabuFitness) {
                if(saveStatus.equals(saveStatus)){
                    saveSteps = nabuFitness.fitness.steps;
                    TVTest.setText(saveSteps);
                }
                LiveFitness.setText(nabuFitness.toString());


            }

            @Override
            public void onConnectionStateChanged(NabuBand selectedBand, int status) {
                Toast.makeText(getApplicationContext(), "Nabu Disconnected", Toast.LENGTH_LONG).show();
            }

        });
        new CountDownTimer(timeA, 1000) {

            public void onTick(long millisUntilFinished) {
                TT.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                TT.setText("done!");
            }
        }.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
}
