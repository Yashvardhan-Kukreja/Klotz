package com.example.yash1300.klotz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainScreen extends AppCompatActivity {
ListView listView;
List<Transaction> transactions;
TextView cardholder, balance, exchangeRate;
Button send;
String receiverCode, email, balanceLeft, emailLoggedIn, senderName, userUID;
ImageView qrCode;
//FirebaseDatabase database;
ProgressDialog progressDialog;
String FETCH_TRANSACTIONS_URL;
String MAKE_TRANSACTION_URL ;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MainScreen.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        FETCH_TRANSACTIONS_URL = getResources().getString(R.string.base_url) + "/user/fetchTransactions";
        MAKE_TRANSACTION_URL = getResources().getString(R.string.base_url) + "/user/makeTransaction";

        receiverCode = getIntent().getExtras().getString("receivercode");
        emailLoggedIn = getIntent().getExtras().getString("emailLoggedIn");
        balanceLeft = getIntent().getExtras().getString("balance");
        senderName = getIntent().getExtras().getString("name");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        cardholder = findViewById(R.id.cardHolder);
        balance = findViewById(R.id.balance);
        send = findViewById(R.id.sendButton);
        qrCode = findViewById(R.id.qrCode);
        listView = findViewById(R.id.transactionList);

        cardholder.setText(senderName);
        balance.setText(balanceLeft+" Klotz");

        transactions = new ArrayList<>();
        exchangeRate = findViewById(R.id.exchangeRate);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix matrix = multiFormatWriter.encode(emailLoggedIn, BarcodeFormat.QR_CODE, 200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Request for fetching the exchange rate
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://blockchain.info/ticker", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String rate = jsonObject.getJSONObject("INR").getString("15m");
                    exchangeRate.setText(rate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                exchangeRate.setText("1010982.23");
            }
        });
        Volley.newRequestQueue(MainScreen.this).add(stringRequest);
        // End of the request for fetching the exchange rate


        // Request for fetching all the transactions of the user
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, FETCH_TRANSACTIONS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    Toast.makeText(MainScreen.this, message, Toast.LENGTH_SHORT).show();
                    if (success.equals("1")){
                        JSONArray jsonArray = jsonObject.getJSONArray("transactionSent");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("transactionReceived");
                        for (int i=0;i<jsonArray1.length();i++){
                            transactions.add((new Transaction(jsonArray1.getJSONObject(i).getString("name"), jsonArray1.getJSONObject(i).getString("date"), jsonArray1.getJSONObject(i).getString("amount")+" Klotz", 1)));
                        }
                        for (int i=0;i<jsonArray.length();i++){
                            transactions.add((new Transaction(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("date"), jsonArray.getJSONObject(i).getString("amount")+" Klotz", 0)));
                        }
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(MainScreen.this, "An error occured while fetching the transaction details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(MainScreen.this, "An error occured while fetching the transaction details", Toast.LENGTH_SHORT).show();
                volleyError.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailLoggedIn);
                return params;
            }
        };
        Volley.newRequestQueue(MainScreen.this).add(stringRequest1);
        //End of the request fetching the transactions

        CustomListAdapter customListAdapter = new CustomListAdapter(MainScreen.this, transactions);
        listView.setAdapter(customListAdapter);


        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if (!receiverCode.equals("")){
            AlertDialog.Builder  builder = new AlertDialog.Builder(MainScreen.this);
            View v = LayoutInflater.from(MainScreen.this).inflate(R.layout.amount_send_dialog, null, false);
            builder.setView(v);
            final AlertDialog sendDialog = builder.create();
            sendDialog.show();
            final EditText nameOfTransaction, amountToBeSent;
            Button sendItFinally;
            nameOfTransaction = v.findViewById(R.id.holderReceivingTheAmount);
            amountToBeSent = v.findViewById(R.id.amountToBeSent);
            sendItFinally = v.findViewById(R.id.sendItFinally);

            //receiverCodeEditText.setText(receiverCode);

            sendItFinally.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setMessage("Transacting...");
                    progressDialog.show();
                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, MAKE_TRANSACTION_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                Toast.makeText(MainScreen.this, message, Toast.LENGTH_LONG).show();
                                if (success.equals("1")){
                                    balance.setText(Integer.toString(Integer.parseInt(balanceLeft) - Integer.parseInt(amountToBeSent.getText().toString())));
                                    sendDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(MainScreen.this, "Error occured while making the transaction", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.dismiss();
                            Toast.makeText(MainScreen.this, "Error occured while making the transaction", Toast.LENGTH_SHORT).show();
                            volleyError.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("sender", emailLoggedIn);
                            params.put("receiver", receiverCode);
                            params.put("name", nameOfTransaction.getText().toString());
                            params.put("date", (new SimpleDateFormat("dd-MM-yyyy").format(new Date())));
                            params.put("amount", amountToBeSent.getText().toString());
                            return params;
                        }
                    };
                    Volley.newRequestQueue(MainScreen.this).add(stringRequest2);
                }
            });

        } else {
            //Toast.makeText(MainScreen.this, "Invalid ID of the receiver", Toast.LENGTH_SHORT).show();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainScreen.this, qrCameraActivity.class);
                i.putExtra("emailLoggedIn", emailLoggedIn);
                startActivity(i);
            }
        });
    }
}
