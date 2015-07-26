package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.models.NabuBand;


public class Splash_Screen extends ActionBarActivity {
public static final String DEFAULT="N/A";
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    //Nabu SDK
    static NabuOpenSDK nabuSDK = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                SharedPreferences sharedPreferences=getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String Status = sharedPreferences.getString("LoginStatus",DEFAULT);

                if(Status.equals(DEFAULT)){
                    Intent mainIntent = new Intent(Splash_Screen.this, MainActivity.class);
                    Splash_Screen.this.startActivity(mainIntent);
                }
                if(Status.equals("LO")){
                    Intent mainIntent = new Intent(Splash_Screen.this, MainActivity.class);
                    Splash_Screen.this.startActivity(mainIntent);
                }
                if(Status.equals("LI")){
                    Intent mainIntent = new Intent(Splash_Screen.this, Profile.class);
                    Splash_Screen.this.startActivity(mainIntent);
                }
                /*String NabuStatus = sharedPreferences.getString("BandSaved",DEFAULT);

                if(NabuStatus.equals("true")){

                    //NabuBand SPband = new NabuBand();
                  String bandId = sharedPreferences.getString("BandID",DEFAULT);
                   String serialNumber = sharedPreferences.getString("BandSerialNumber",DEFAULT);
                  String model = sharedPreferences.getString("BandModel",DEFAULT);
                    String name = sharedPreferences.getString("BandName",DEFAULT);
                }*/

            }
        }, SPLASH_DISPLAY_LENGTH);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash__screen, menu);
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
