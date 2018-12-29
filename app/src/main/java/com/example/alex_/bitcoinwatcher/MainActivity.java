package com.example.alex_.bitcoinwatcher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";

    public static final String CBR_ENDPOINT = "https://www.cbr-xml-daily.ru/daily_json.js";

    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt;
    private  TextView txtCBRrate;
    private TextView txtBTCrate;

    private double usdRate;
    private double btcRate;
    private double btcRubRate;

    private String btcTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt = findViewById(R.id.txt);
        txtCBRrate = findViewById(R.id.txtCBRrate);
        txtBTCrate = findViewById(R.id.txtBTCrate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BIP Loading");
        progressDialog.setMessage("Wait...");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        load();
        loadCBR();
    }

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
        if (id == R.id.action_load) {
            load();
            loadCBR();
        }

        if (id == R.id.action_about) {
            Intent about = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(about);
        }

        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }

    private void load() {
        Request request = new Request.Builder().url(BPI_ENDPOINT).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this,"Error during BPI loading..." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseBPIresponce(body);
                    }
                });

            }
        });
    }
    private void parseBPIresponce(String body) {
        try {
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = new JSONObject(body);
            JSONObject timeObject = jsonObject.getJSONObject("time");
            //txt.setText(timeObject.toString());
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");



            btcRate = Double.parseDouble(usdObject.getString("rate").replace(",",""));
            String formattedbtcUSDRate = new DecimalFormat("#0.00").format(btcRate);
            builder.append("1 BTC = ").append(formattedbtcUSDRate).append(" USD").append("\n");


//            Log.i("myinfo: btcRate - ", String.valueOf(btcRate));

            btcRubRate = usdRate * btcRate;
            String formattedbtcRubRate = new DecimalFormat("#0.00").format(btcRubRate);
//            Log.i("myinfo: btcRubRate -", String.valueOf(btcRubRate));
            txtBTCrate.setText("1 BTC = " + formattedbtcRubRate + " RUB" + "\n");

            //JSONObject gbpObject = bpiObject.getJSONObject("GBP");
            //builder.append(gbpObject.getString("rate")).append("GBP").append("\n");

            //JSONObject euroObject = bpiObject.getJSONObject("EUR");
            //builder.append(euroObject.getString("rate")).append("EUR").append("\n");

            txt.setText(builder.toString());


        } catch (Exception e) {
            txt.setText(e.toString());
        }
    }

    private void loadCBR() {
        // try load RUB rate from https://www.cbr-xml-daily.ru/daily_json.js
        progressDialog.setTitle("CBR rates loading");

        Request request = new Request.Builder().url(CBR_ENDPOINT).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this,"Error during CBR rates loading..." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        parseCBRresponce(body);
                    }
                });
            }
        });
    }

    private void parseCBRresponce(String data) {
        txtCBRrate.setVisibility(View.VISIBLE);
        txtBTCrate.setVisibility(View.VISIBLE);
        try {
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = new JSONObject(data);

            JSONObject valutaObjext = jsonObject.getJSONObject("Valute");
            //Log.i("parseCBRresponce: ", valutaObjext.toString());

            JSONObject usdObject = valutaObjext.getJSONObject("USD");

            usdRate = Double.parseDouble(usdObject.getString("Value"));
            String formatteUSDRate = new DecimalFormat("#0.00").format(usdRate);

            builder.append("1 USD = ").append(formatteUSDRate).append(" RUB").append("\n");

            txtCBRrate.setText(builder.toString());


//            Log.i("myinfo: usdRate - ", String.valueOf(usdRate));
            btcRubRate = usdRate * btcRate;
            String formattedbtcRubRate = new DecimalFormat("#0.00").format(btcRubRate);

//            Log.i("myinfo: btcRubRate -", String.valueOf(btcRubRate));

            builder.append("1 BTC = ").append(formattedbtcRubRate).append(" RUB").append("\n");

            txtBTCrate.setText("1 BTC = " + formattedbtcRubRate + " RUB" + "\n");




        } catch (Exception e){
            txtCBRrate.setText(e.toString());

        }
    }
}
