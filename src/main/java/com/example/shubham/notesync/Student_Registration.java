package com.example.shubham.notesync;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
public class Student_Registration extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText etRollNo,etFname,etMname,etLname,etEmail,etMob,etPassword,etRePassword;
    Spinner spinnerClass;
    Button btnSubmit;
    String spinClass;
    TextView tvLogin;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String mobile="^[0-9]$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_registration);
        etRollNo=(EditText)findViewById(R.id.etRollNo);
        etFname=(EditText)findViewById(R.id.etFname);
        etMname=(EditText)findViewById(R.id.etMname);
        etLname=(EditText)findViewById(R.id.etLname);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etMob=(EditText)findViewById(R.id.etMob);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etRePassword=(EditText)findViewById(R.id.etRePassword);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        tvLogin=(TextView)findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(this);
        spinnerClass=(Spinner)findViewById(R.id.spinnerClass);
        spinnerClass.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnSubmit){
            if (isInternetOn()==true){
                if (etRollNo.getText().toString().isEmpty()){etRollNo.setError("Field is Empty");etRollNo.requestFocus();}
                if (etFname.getText().toString().isEmpty()){etFname.setError("Field is Empty");etFname.requestFocus();}
                if (etMname.getText().toString().isEmpty()){etMname.setError("Field is Empty");etMname.requestFocus();}
                if (etLname.getText().toString().isEmpty()){etLname.setError("Field is Empty");etLname.requestFocus();}
                if (spinnerClass.getSelectedItem().toString().equals("Select Your Class")){etFname.setError("Select Your Class");etFname.requestFocus();
                    Toast.makeText(Student_Registration.this, "Please Select Your Class..!", Toast.LENGTH_SHORT).show();
                }
                if (etEmail.getText().toString().isEmpty()){etEmail.setError("Field is Empty");etEmail.requestFocus();}
                if (!etEmail.getText().toString().matches(emailPattern)){
                    etEmail.setError("Invalid EmailId...!");etEmail.requestFocus();
                }
                if (etMob.getText().toString().isEmpty()){etMob.setError("Field is Empty");etMob.requestFocus();}
                  //  String number=etMob.getText().toString();
//                if (!(etMob.getText().toString().length() <10) ||number.length()>11||number.matches(mobile));{
//                    etMob.setError("Invalid Mobile No..!");etMob.requestFocus();}
                if (etPassword.getText().toString().isEmpty()){etPassword.setError("Field is Empty");etPassword.requestFocus();}
                if (etRePassword.getText().toString().isEmpty()){etRePassword.setError("Field is Empty");etRePassword.requestFocus();}
                if(!etRePassword.getText().toString().equals(etPassword.getText().toString())){
                    etRePassword.setError("Password Does Not Match");etRePassword.requestFocus();
                }
                else {
                    // String url=Config.JSON_URL + "newstudent.php?&stud_name=" + etName.getText().toString().trim() + "&stud_mob=" + etMobile.getText().toString().trim();
                    String url = Config.JSON_URL + "studentregistration.php?&stud_rollno="+etRollNo.getText().toString().trim()+"&stud_fname="+etFname.getText().toString().trim()+"&stud_mname="+etMname.getText().toString().trim()+"&stud_lname="+etLname.getText().toString().trim()+"&stud_class="+spinnerClass.getSelectedItem().toString().trim()+"&stud_email="+etEmail.getText().toString().trim()+"&stud_mob="+etMob.getText().toString().trim()+"&stud_password="+etPassword.getText().toString().trim();
                    Log.d(Config.tag, "Insert Url  : " + url);
                    RequestQueue queue = Volley.newRequestQueue(Student_Registration.this);
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
                                    // myDialog.showAlertDialog("Register", "Account Created Succesfully", IconType.SUCCESS);
                                    Toast.makeText(Student_Registration.this, "Inserted Successfully....", Toast.LENGTH_LONG).show();
                                    etRollNo.setText("");
                                    etFname.setText("");
                                    etMname.setText("");
                                    etLname.setText("");
                                    etEmail.setText("");
                                    etMob.setText("");
                                    etPassword.setText("");
                                    etRePassword.setText("");
                                    Intent intent = new Intent(Student_Registration.this,Stud_Login_Activity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d(Config.tag, "False :" + reason);
                                    Toast.makeText(Student_Registration.this, "Fill Correct Information....", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Student_Registration.this, "No Internet..! ", Toast.LENGTH_LONG).show();
            }
        }
            else if (v.getId() == R.id.tvLogin) {
            Intent intent = new Intent(this, Stud_Login_Activity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                spinClass="Select Your Class";
                break;
            case 1:
                spinClass="MCS-1";
                break;
            case 2:
                spinClass="MCS-2";
                break;
            case 3:
                spinClass="MCA-1";
                break;
            case 4:
                spinClass="MCA-2";
                break;
            case 5:
                spinClass="MCA-3";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Toast.makeText(Student_Registration.this,"Please Select the Class..!",Toast.LENGTH_SHORT).show();
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