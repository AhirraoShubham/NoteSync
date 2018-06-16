package com.example.shubham.notesync.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shubham.notesync.Config;
import com.example.shubham.notesync.R;
import com.example.shubham.notesync.Stud_Login_Activity;
import com.example.shubham.notesync.View_Download_Files_Acticity;

import java.io.File;

public class DownloadFilesActivity extends ActionBarActivity {

    DownloadManager dManager;
    TextView tvMessage,tvFileURL,tvFileName,tvFileSubName,tvFileSubCode;
    String file_url,file_name,file_sub_name,file_sub_code;
    long did;
    Button btnBack,btnDownload;
    String mFilename;
    String mFilepath;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_files_layout);
        init();
       // Bundle bundle=getIntent().getExtras();
        file_url= getIntent().getStringExtra("file_url");
        file_name= getIntent().getStringExtra("file_name");
        file_sub_name= getIntent().getStringExtra("file_sub_name");
        file_sub_code= getIntent().getStringExtra("file_sub_code");
/*        file_name=bundle.getString("file_name");
        file_sub_name=bundle.getString("file_sub_name");
        file_sub_code=bundle.getString("file_sub_code");*/

        tvFileURL.setText(file_url);
        tvFileName.setText("File Name : "+file_name);
        tvFileSubName.setText("Sub Name : "+file_sub_name);
        tvFileSubCode.setText("Sub Code : "+file_sub_code);

        // Get DownloadManager instance
        dManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private void init() {
        tvMessage = (TextView) findViewById(R.id.txtmessage);
        tvFileURL=(TextView)findViewById(R.id.tvFileUrl);
        tvFileName=(TextView)findViewById(R.id.tvFileName);
        tvFileSubName=(TextView)findViewById(R.id.tvFileSubName);
        tvFileSubCode=(TextView)findViewById(R.id.tvFileSubCode);
        btnDownload=(Button)findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ProgressDialog.show(DownloadFilesActivity.this, "Wait", "Information inserting..!");
                downloadFile(v);
            }
        });
        btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void downloadFile(View view) {

        String urlString = tvFileURL.getText().toString();
        if (!urlString.equals("")) {
            try {

                // Get file name from the url
                String fileName = urlString.substring(urlString.lastIndexOf("/") + 1);
                mFilename = fileName;
                // Create Download Request object
                Request request = new Request(Uri.parse((urlString)));
                // Display download progress and status message in notification bar
                request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                // Set description to display in notification
                request.setDescription("Download " + fileName + " from " + urlString);
                // Set title
                request.setTitle("DownloadManager");
                mFilepath = "file://" + Environment.getExternalStorageDirectory() + "/notesync" + "/" + fileName;
                // Set destination location for the downloaded file
                request.setDestinationUri(Uri.parse(mFilepath));
                // Download the file if the Download manager is ready
                did = dManager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // BroadcastReceiver to receive intent broadcast by DownloadManager
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Query q = new Query();
            q.setFilterById(did);
            Cursor cursor = dManager.query(q);
            if (cursor.moveToFirst()) {
                String message = "";
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    message = "Download successful";
                    Intent intent=new Intent(DownloadFilesActivity.this, DownloadFilesListViewActivity.class);
                    startActivity(intent);

                } else if (status == DownloadManager.STATUS_FAILED) {
                    Log.i("handleData()", "Reason: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)));
                    message = "Download failed";
                }
                tvMessage.setText(message);
            }
        }
    };

    protected void onResume() {
        super.onResume();
        // Register the receiver to receive an intent when download complete
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // Unregister the receiver
        unregisterReceiver(downloadReceiver);
    }
}
