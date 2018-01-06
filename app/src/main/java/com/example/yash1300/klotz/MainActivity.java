package com.example.yash1300.klotz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button login, signup;
    String usernameString, passString;
    FirebaseAuth mAuth;
    String userUID;
    ProgressDialog progressDialog;
    String LOGIN_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LOGIN_URL = getResources().getString(R.string.base_url) + "/user/authenticate";
        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                usernameString = username.getText().toString();
                passString = password.getText().toString();
                if (usernameString.isEmpty() || passString.isEmpty()){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_LONG).show();
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                progressDialog.dismiss();
                                JSONObject jsonObject = new JSONObject(s);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                if (success.equals("1")){
                                    Intent i = new Intent(MainActivity.this, MainScreen.class);
                                    i.putExtra("receivercode", "");
                                    i.putExtra("emailLoggedIn", usernameString);
                                    i.putExtra("name", jsonObject.getString("name"));
                                    i.putExtra("balance", jsonObject.getString("balance"));
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.dismiss();
                            volleyError.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email",usernameString);
                            params.put("password", passString);
                            return params;
                        }
                    };

                    Volley.newRequestQueue(MainActivity.this).add(stringRequest);
                    // End of the request
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(MainActivity.this, SignUpActivity.class)));
            }
        });
    }
}
