package com.example.shubham.notesync;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubham.notesync.upload.UploadActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shubham on 23-03-2017.
 */
public class Teacher_Login_Activity extends Activity implements View.OnClickListener {
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_login_layout);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etPassword=(EditText)findViewById(R.id.etPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvNewUser=(TextView)findViewById(R.id.tvNewUser);
        tvNewUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogin) {
                 if (isInternetOn() == true) {
                    if (etEmail.getText().toString().isEmpty()) {
                    etEmail.setError("Field is Empty");
                    etEmail.requestFocus();
                }
                if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Field is Empty");
                    etPassword.requestFocus();

                } else {
                    String url = Config.JSON_URL + "teacher_login.php?&teacher_email='" + etEmail.getText().toString().trim() + "'&teacher_password='" + etPassword.getText().toString().trim()+"'";
                    Log.d(Config.tag, "Login Url  : " + url);
                    RequestQueue queue = Volley.newRequestQueue(Teacher_Login_Activity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responce) {
                            try {
                                //JSONObject jsonObject=new JSONObject(responce);
                                JSONArray jsonArray = new JSONArray(responce);
                                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                                Boolean result = jsonObject.getBoolean("result");
                                String reason = jsonObject.getString("reason");
                                if (result) {
                                    Log.d(Config.tag, "True : " + reason);
                                    Toast.makeText(Teacher_Login_Activity.this, "Login Successfully....", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Teacher_Login_Activity.this,UploadActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.d(Config.tag, "False :" + reason);
                                    Toast.makeText(Teacher_Login_Activity.this, "Please Fill Valid Information....", Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(Config.tag, "JSONException : " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d(Config.tag, "Volley Error :" + volleyError.getMessage());
                        }
                    });
                    queue.add(stringRequest);
                }
            } else {
                Toast.makeText(this, "No Internet..! ", Toast.LENGTH_LONG).show();
            }
        }
        else if (v.getId() == R.id.tvNewUser) {
            Intent intent = new Intent(this,TeacherIDActivity.class);
            startActivity(intent);
        }
    }
    private boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

           // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }
}
