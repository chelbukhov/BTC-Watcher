package com.example.alex_.bitcoinwatcher;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ReportActivity extends AppCompatActivity {

    public static final String INFO_ENDPOINT = "https://api.blockchain.info/stats";

    private TextView info;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        info = findViewById(R.id.txtInfo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("INFO loading");
        progressDialog.setMessage("Wait...");

        loadInfo();
    }

    private void loadInfo() {
        progressDialog.setTitle("CBR rates loading");

        Request request = new Request.Builder().url(INFO_ENDPOINT).build();
        progressDialog.show();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ReportActivity.this,"Error during CBR rates loading..." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        parseINFOresponce(body);
                    }
                });
            }
        });

    }

    private void parseINFOresponce(String data) {
        try {
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = new JSONObject(data);

            String formattedString = new DecimalFormat("#0.00").format(jsonObject.getDouble("market_price_usd"));
            builder.append("BTC price: ").append(formattedString).append(" USD").append("\n\n");

            builder.append("N blocks mined: ").append(jsonObject.getInt("n_blocks_mined")).append("\n\n");

            formattedString = new DecimalFormat("#0.00").format(jsonObject.getDouble("minutes_between_blocks"));
            builder.append("Minutes between blocks: ").append(formattedString).append("\n\n");

            formattedString = new DecimalFormat("#0.00").format(jsonObject.getDouble("n_btc_mined")/100000000);
            builder.append("N BTC mined: ").append(formattedString).append("\n\n");

            builder.append("Difficulty: ").append(jsonObject.getLong("difficulty")).append("\n\n");

            builder.append("Hash rate: ").append(jsonObject.getLong("hash_rate")).append(" GH/s").append("\n\n");

            builder.append("Trade volume BTC: ").append(jsonObject.getInt("trade_volume_btc")).append("\n\n");

            info.setText(builder.toString());

        } catch (Exception e){
            info.setText(e.toString());
        }

    }
}
