package com.example.shubham.notesync;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by Shubham on 22-04-2017.
 */
public class TeacherIDActivity extends Activity{
    EditText etTeacherId;
    Button btnOk, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_id_activity_layout);
        etTeacherId = (EditText) findViewById(R.id.etTeacherId);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetOn() == true) {
                    if (etTeacherId.getText().toString().isEmpty()) {
                        etTeacherId.setError("Field is Empty");
                        etTeacherId.requestFocus();
                    }
                   else {
                        String url = Config.JSON_URL + "teacher_id.php?&teacher_reg_id='" + etTeacherId.getText().toString().trim()+"'";
                        Log.d(Config.tag, "Login Url  : " + url);
                        RequestQueue queue = Volley.newRequestQueue(TeacherIDActivity.this);
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
                                        Toast.makeText(TeacherIDActivity.this, "ID Matches..", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(TeacherIDActivity.this,Teacher_Registration.class);
                                        startActivity(intent);
                                    } else {
                                        Log.d(Config.tag, "False :" + reason);
                                        etTeacherId.setText("");
                                        etTeacherId.setError("ID Doesn't Match..");
                                       // Toast.makeText(TeacherIDActivity.this, "Please Fill Valid Information....!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TeacherIDActivity.this, "No Internet..! ", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TeacherIDActivity.this,MainActivity.class);
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
