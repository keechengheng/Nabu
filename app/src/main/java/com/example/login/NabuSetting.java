package com.example.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.BandListListener;
import com.razer.android.nabuopensdk.interfaces.LiveDataListener;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.models.NabuBand;
import com.razer.android.nabuopensdk.models.NabuFitness;
import com.razer.android.nabuopensdk.models.Scope;


public class NabuSetting extends ActionBarActivity {
    //Nabu SDK
    static NabuOpenSDK nabuSDK = null;
    private Button selectNabu;
    private Button saveChanges;
    public NabuBand selectedBand = null;
    private TextView BI;
    private TextView SN;
    private TextView N;
    private TextView M;

    String sBandID;
    String sSerialNumber;
    String sName;
    String sModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nabu_setting);
        //NABU SDK
        nabuSDK = NabuOpenSDK.getInstance(this);

        selectNabu = (Button) findViewById(R.id.btnSelectNabu);
        saveChanges = (Button) findViewById(R.id.btnSaveChanges);
        BI = (TextView) findViewById(R.id.TVnabuID);
        SN = (TextView) findViewById(R.id.TVnabuSN);
        N = (TextView) findViewById(R.id.TVnabuName);
        M = (TextView) findViewById(R.id.TVnabuModel);


        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String NabuStatus = sharedPreferences.getString("BandSaved","").toString();
        if (NabuStatus.equals("true")) {
        //    selectedBand.bandId = sharedPreferences.getString("BandID","").toString();
        //    selectedBand.serialNumber = sharedPreferences.getString("BandSerialNumber","").toString();
        //    selectedBand.model = sharedPreferences.getString("BandModel","").toString();
        //    selectedBand.name = sharedPrereferences.getString("BandName","").toString();
            BI.setText(sharedPreferences.getString("BandID",""));
            SN.setText(sharedPreferences.getString("BandSerialNumber",""));
            N.setText(sharedPreferences.getString("BandModel",""));
           M.setText(sharedPreferences.getString("BandName",""));
          //  BI.setText(selectedBand.bandId.toString());
          //  SN.setText(selectedBand.serialNumber.toString());
          //  N.setText(selectedBand.name.toString());
           // M.setText(selectedBand.model.toString());

        }
        else {
            Toast.makeText(getApplicationContext(), "No Nabu Saved",Toast.LENGTH_LONG).show();
        }

    }
    public void selectNabu(View view) {
        selectBandFromBandList();
    }
    public void saveChanges(View view) {

        //String NabuStatus = sharedPreferences.getString("BandSaved","");
       /* if (selectedBand==null) {
            Toast.makeText(getApplicationContext(), "Please Select a Nabu",Toast.LENGTH_LONG).show();
            return;
        }
        nabuSDK.enableFitness(NabuSetting.this, selectedBand, new LiveDataListener() {

            @Override
            public void onError(String errorMessage) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Enable Fitness Fail",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLiveDataReceived(NabuFitness nabuFitness) {
                //store live data
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                Toast.makeText(getApplicationContext(), "Recieve Fitness Data",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onConnectionStateChanged(NabuBand selectedBand, int status) {
                //Log.e("band", selectedBand.toString());
                //Log.e("Connection State", status == BluetoothGatt.STATE_CONNECTED ? "Connected" : "Disconnected");
                Toast.makeText(getApplicationContext(), "Bluetooth Connection Lost",Toast.LENGTH_LONG).show();
            }

        });*/
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("BandID",BI.getText().toString());
        editor.putString("BandSerialNumber", SN.getText().toString());
        editor.putString("BandName", N.getText().toString());
        editor.putString("BandModel",M.getText().toString());
        editor.putString("BandSaved","true");
        editor.commit();
        Toast.makeText(getApplicationContext(), "Nabu Saved",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nabu_setting, menu);
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

    private void selectBandFromBandList() {
        nabuSDK.getBandList(NabuSetting.this, new BandListListener() {

            @Override
            public void onReceiveFailed(String errorMessage) {
                Toast.makeText(getApplicationContext(), "No Band Detected",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onReceiveData(NabuBand[] Datas) {
                showSelectBandDialog(Datas);

            }

        });

    }

    private void showSelectBandDialog(final NabuBand[] bandList) {
        if (bandList == null || bandList.length == 0) {
            Toast.makeText(getApplicationContext(), "No Nabu Detected",Toast.LENGTH_LONG).show();
            return;
        }
        selectedBand = bandList[0];
        String[] bandNames = new String[bandList.length];
        AlertDialog.Builder builder = new AlertDialog.Builder(NabuSetting.this);
        int i = 0;

        for (int j = 0; j < bandList.length; j++) {
            bandNames[j] = (!TextUtils.isEmpty(bandList[j].name) ? bandList[j].name : bandList[j].bandId);
            if (selectedBand != null && selectedBand.bandId.equals(bandList[j].bandId)) {
                i = j;

            }
        }

        builder.setSingleChoiceItems(bandNames, i, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                selectedBand = bandList[index];
                Toast.makeText(getApplicationContext(), "Nabu Successfully Selected",Toast.LENGTH_LONG).show();
                BI.setText(selectedBand.bandId.toString());
                SN.setText(selectedBand.serialNumber.toString());
                N.setText(selectedBand.name.toString());
                M.setText(selectedBand.model.toString());

                dialog.dismiss();

            }
        });

        builder.show();

    }
}
