package com.example.shubham.notesync;

import android.app.Activity;
import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shubham on 23-03-2017.
 */
public class Teacher_Registration extends Activity  {
    EditText etFname, etLname, etEmail, etMob, etPassword, etRePassword;
    Button btnSubmit;
    TextView tvLogin;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String mobile="^[0-9]$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_registration);
        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMob = (EditText) findViewById(R.id.etMob);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRePassword = (EditText) findViewById(R.id.etRePassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetOn()==true){
                    if (etFname.getText().toString().isEmpty()){etFname.setError("Field is Empty");etFname.requestFocus();}
                    if (etLname.getText().toString().isEmpty()){etLname.setError("Field is Empty");etLname.requestFocus();}
                    if (etEmail.getText().toString().isEmpty()){etEmail.setError("Field is Empty");etEmail.requestFocus();}
                    if (!etEmail.getText().toString().matches(emailPattern)){
                        etEmail.setError("Invalid EmailId...!");etEmail.requestFocus();
                    }
                    if (etMob.getText().toString().isEmpty()){etMob.setError("Field is Empty");etMob.requestFocus();}
                    //   String number=etMob.getText().toString();
//                    if (!(etMob.getText().toString().length() <10) ||number.length()>13||number.matches(mobile));{
//                        etMob.setError("Invalid Mobile No..!");etMob.requestFocus();}
                    if (etPassword.getText().toString().isEmpty()){etPassword.setError("Field is Empty");etPassword.requestFocus();}
                    if (etRePassword.getText().toString().isEmpty()){etRePassword.setError("Field is Empty");etRePassword.requestFocus();}
                    if(!etRePassword.getText().toString().equals(etPassword.getText().toString())){
                        etRePassword.setError("Password Does Not Match");etRePassword.requestFocus();
                    }
                   /* if (!etPassword.equals(etRePassword.getText().toString())){
                        etRePassword.setText("");
                        Toast.makeText(Teacher_Registration.this,"Password Doesn`t Match..!",Toast.LENGTH_SHORT).show();
                    }
                  /*  if (etFname.equals("")||etLname.equals("")||etEmail.equals("")||etMob.equals("")||etPassword.equals("")||etRePassword.equals("")){
                        Toast.makeText(Teacher_Registration.this,"Field Vaccant...!",Toast.LENGTH_SHORT).show();
                        return;
                    }
               /*  if (!etPassword.equals(etRePassword)){
                    Toast.makeText(Teacher_Registration.this,"Password Does not Match..!",Toast.LENGTH_SHORT).show();
                }*/
                    else {
                        String url = Config.JSON_URL + "teacherregistration.php?&teacher_fname="+ etFname.getText().toString().trim()+"&teacher_lname="+ etLname.getText().toString().trim()+"&teacher_email="+ etEmail.getText().toString().trim()+"&teacher_mob="+ etMob.getText().toString().trim()+"&teacher_password="+etPassword.getText().toString().trim();
                        Log.d(Config.tag, "Insert Url  : " + url);
                        //ProgressDialog.show(Teacher_Registration.this, "Wait","Information inserting..!");
                        RequestQueue queue = Volley.newRequestQueue(Teacher_Registration.this);
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
                                        Toast.makeText(Teacher_Registration.this, "Inserted Successfully....", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Teacher_Registration.this, Teacher_Login_Activity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.d(Config.tag, "False :" + reason);
                                        Toast.makeText(Teacher_Registration.this, "Fill Correct Information....", Toast.LENGTH_LONG).show();
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


                }
                else {
                    Toast.makeText(Teacher_Registration.this, "No Internet..! ", Toast.LENGTH_LONG).show();

                }
            }
        });
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Teacher_Registration.this, Teacher_Login_Activity.class);
                startActivity(intent);
            }
        });
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