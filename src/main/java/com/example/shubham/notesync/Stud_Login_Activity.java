package com.example.shubham.notesync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.shubham.notesync.download.DownloadFilesActivity;
import com.example.shubham.notesync.download.DownloadFilesListViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shubham on 23-03-2017.
 */
public class Stud_Login_Activity extends Activity {
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvNewUser;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stud_login_layout);
        initialization();
    }
    private void initialization() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        sharedPreferences = this.getSharedPreferences(Config.sp, this.MODE_PRIVATE);
        Boolean flag = false;
        try {
            Bundle bundle = getIntent().getExtras();
            flag = bundle.getBoolean("flag", false);
        } catch (Exception ex) {
            if (!flag)
                if (sharedPreferences.contains(Config.email)) {
                    Intent intent = new Intent(Stud_Login_Activity.this, DownloadFilesListViewActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    btnLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isInternetOn() == true) {
                                if (etEmail.getText().toString().isEmpty()) {
                                    etEmail.setError("Field is Empty");
                                    etEmail.requestFocus();
                                }
                                if (etPassword.getText().toString().isEmpty()) {
                                    etPassword.setError("Field is Empty");
                                    etPassword.requestFocus();
                                } else {
                                    String email = etEmail.getText().toString();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("myemail", email);
                                    editor.putString(Config.email, email);
                                    editor.commit();
                                    String url = Config.JSON_URL + "student_login.php?&stud_email='" + etEmail.getText().toString().trim() + "'&stud_password='" + etPassword.getText().toString().trim()+"'";
                                    Log.d(Config.tag, "Insert Url  : " + url);
                                    RequestQueue queue = Volley.newRequestQueue(Stud_Login_Activity.this);
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String responce) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(responce);
                                                JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                                                Boolean result = jsonObject.getBoolean("result");
                                                String reason = jsonObject.getString("reason");

                                                if (result) {
                                                    Log.d(Config.tag, "True : " + reason);
                                                    Toast.makeText(Stud_Login_Activity.this, "Login Successfully....", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Stud_Login_Activity.this, DownloadFilesListViewActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.d(Config.tag, "False :" + reason);
                                                    Toast.makeText(Stud_Login_Activity.this, "Please Fill Valid Information....", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
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
                                Toast.makeText(Stud_Login_Activity.this, "No Internet..! ", Toast.LENGTH_LONG).show();
                                //  myDialog.showAlertDialog("Error", "No Internet Connection..!", IconType.ERROR);
                                // MyDialog.showProgress(Stud_Login_Activity.this, "Done", "Account Created Succesfully", true);
                            }
                        }
                    });

                    tvNewUser = (TextView) findViewById(R.id.tvNewUser);
                    tvNewUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Stud_Login_Activity.this, Student_Registration.class);
                            startActivity(intent);
                        }
                    });
                }
        }
    }
    public boolean isInternetOn() {
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