package com.example.shubham.notesync.upload;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adeel.library.easyFTP;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubham.notesync.Config;
import com.example.shubham.notesync.MainActivity;
import com.example.shubham.notesync.R;
import com.example.shubham.notesync.Stud_Login_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Shubham on 07-04-2017.
 */
public class UploadActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_FILE_ACTION = 100;
    TextView tvSelectedFileName,tvSelectedSubjectCode;
    EditText etFileName;
    Spinner spinnerSubName;
    String spinSubName;
    Button btnChooseFile, btnFileUpload,btnCancel;
    long did;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_files_layout);
        verifyStoragePermissions(UploadActivity.this);
        etFileName = (EditText) findViewById(R.id.etFileName);
        tvSelectedFileName = (TextView) findViewById(R.id.tvSelectedFileName);
        spinnerSubName=(Spinner)findViewById(R.id.spinnerSubName);
        spinnerSubName.setOnItemSelectedListener(this);
        tvSelectedSubjectCode=(TextView)findViewById(R.id.tvSelectedSubjectCode);
        //default value
        tvSelectedSubjectCode.setText("CS-201");
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UploadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnChooseFile = (Button) findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        btnFileUpload = (Button) findViewById(R.id.btnFileUpload);
        btnFileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new UploadTask().execute("");
                if (!tvSelectedFileName.getText().toString().isEmpty()) {
                    new UploadTask().execute(tvSelectedFileName.getText().toString());
                } else {
                    Toast.makeText(UploadActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    PICK_FILE_ACTION);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FILE_ACTION:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("Log : ", "File Uri: " + uri.toString());
                    // Get the path
                    // String path = FileUtil.getPath(this, uri);


                    //Log.d("JERLRN", "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload

                    String realPath = getPath(UploadActivity.this, uri);
                   /* // SDK < API11
                    if (Build.VERSION.SDK_INT < 11) {
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                    }
                    else if (Build.VERSION.SDK_INT < 19) {
                        // SDK >= 11 && SDK < 19
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                    }
                    else {
                        // SDK > 19 (Android 4.4)
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                    }*/

                    tvSelectedFileName.setText(realPath);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                spinSubName="--Select Your Subject--";
                break;
            case 1:
                spinSubName="Digital_Image_Processing";
               // tvSelectedSubjectCode.setText(getString(position));
                break;
            case 2:
                spinSubName="Advanced_Operating_System";
                //tvSelectedSubjectCode.setText("CS-202");
                break;
            case 3:
                spinSubName="Data_Mining_and_Data_Warehousing";
                //tvSelectedSubjectCode.setText("CS-203");
                break;
            case 4:
                spinSubName="Programing_with_Dot_Net";
               // tvSelectedSubjectCode.setText("CS-205");
                break;
            case 5:
                spinSubName="Artificial_Intelligence";
               // tvSelectedSubjectCode.setText("CS-206");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Toast.makeText(this,"Select the Subject..!",Toast.LENGTH_SHORT).show();

    }

    class UploadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                easyFTP ftp = new easyFTP();
                ftp.connect("93.188.160.122", "u821186409", "Shubh@m18");
                boolean status = false;
                status = ftp.setWorkingDirectory("files"); // if User say provided any Destination then Set it , otherwise
                // Upload will be stored on Default /root level on server
                File file = new File(params[0]);

                Log.d("UploadTask ", "Uploading file : "+params[0]);
                if (file.exists()) {
                    InputStream fileInputStream = new FileInputStream(file);
                    //InputStream is=getResources().openRawResource(+R.drawable.nofiles);
                    ftp.uploadFile(fileInputStream, file.getName());
                    updateFileDB(file.getName());
                    return new String("Upload Successful");
                    //Todo
                    //URL
                    //File name
                    //volley call


                } else {
                    Log.d("UploadTask ", "File not exist");
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                String t = "Failure : " + e.getLocalizedMessage();
                return t;
            }
        }
    }
    private void updateFileDB(String fileName)throws UnsupportedEncodingException{
        //String url = Config.JSON_URL + "insertfile.php?&file_url=" + "http://notesync.esy.es/files/"+ URLEncoder.encode(fileName + "&file_name=" + etFileName.getText().toString().trim() + "&file_sub_name="+spinnerSubName.getSelectedItem().toString().trim()+"&file_sub_code="+tvSelectedSubjectCode.getText().toString().trim();
        String para = "insertfile.php?&file_url=" + "http://notesync.esy.es/files/"+fileName.replace(" ","_") + "&file_name=" + etFileName.getText().toString().trim().replace(" ","_") + "&file_sub_name="+spinnerSubName.getSelectedItem().toString().trim()+"&file_sub_code="+tvSelectedSubjectCode.getText().toString().trim();
        String url = Config.JSON_URL + para;
        Log.d(Config.tag, "Insert Url  : " + url);
        RequestQueue queue = Volley.newRequestQueue(UploadActivity.this);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responce) {
                try {
                    JSONArray jsonArray = new JSONArray(responce);
                    JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
                    Boolean result = jsonObject.getBoolean("result");
                    String reason = jsonObject.getString("reason");
                    if (result) {
                        Log.d(Config.tag, "True : " + reason);
                        Toast.makeText(UploadActivity.this, "File Inserted Successfully....", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UploadActivity.this,UploadActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d(Config.tag, "False :" + reason);
                        Toast.makeText(UploadActivity.this, "Missing Parameter....", Toast.LENGTH_SHORT).show();
                        tvSelectedFileName.setText("");
                        etFileName.setText("");
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


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}