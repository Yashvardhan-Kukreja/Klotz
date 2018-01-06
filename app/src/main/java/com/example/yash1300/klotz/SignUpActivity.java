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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
EditText firstName, lastName, email, password, conpassword;
Button signup;
ProgressDialog progressDialog;
String SIGN_UP_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SIGN_UP_URL = getResources().getString(R.string.base_url) + "/user/register";
        firstName = findViewById(R.id.registerFirstName);
        lastName = findViewById(R.id.registerLastName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        conpassword = findViewById(R.id.registerConfirmPassword);
        signup = findViewById(R.id.signupButtonFinally);
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Registering you...");
        progressDialog.setCancelable(false);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (!password.getText().toString().equals(conpassword.getText().toString())){
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Password and Confirm Password doesn't match", Toast.LENGTH_LONG).show();
                    return;
                }

                //Request for registering a user
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGN_UP_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject =  new JSONObject(s);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                            if (success.equals("1")){
                                startActivity((new Intent(SignUpActivity.this, MainActivity.class)));
                            }

                        } catch (JSONException e) {
                            Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                        volleyError.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", firstName.getText().toString() + " " + lastName.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };

                Volley.newRequestQueue(SignUpActivity.this).add(stringRequest);
                //End of the request for registering the user
            }
        });

    }
}
