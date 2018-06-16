package com.example.shubham.notesync.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubham.notesync.Config;
import com.example.shubham.notesync.R;
import com.example.shubham.notesync.Stud_Login_Activity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Shubham on 13-04-2017.
 */
public class DownloadFilesListViewActivity extends Activity {
    ListView listViewGetAllFiles;
    ArrayList<MyFiles> arrayList;
    MyFilesAdapter myFilesAdapter;
    TextView tvLoginEmail;
    Button btnLogout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_files_listview_activity_layout);
        sharedPreferences=this.getSharedPreferences(Config.sp,this.MODE_PRIVATE);
          tvLoginEmail=(TextView)findViewById(R.id.tvLoginEmail);
          String name1=sharedPreferences.getString(Config.email,"");
          tvLoginEmail.setText(name1);
          btnLogout=(Button)findViewById(R.id.btnLogout);
          btnLogout.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("myemail");
                editor.clear();
                editor.commit();
                Intent intent = new Intent(DownloadFilesListViewActivity.this, Stud_Login_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("flag", true);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        listViewGetAllFiles = (ListView) findViewById(R.id.listViewGetAllFiles);
        fillListView();
    }

    private void fillListView() {
        String url = Config.JSON_URL + "all_files.php";
        Log.d(Config.tag, "Insert Url  : " + url);
        RequestQueue queue = Volley.newRequestQueue(DownloadFilesListViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responce) {
                try {
                    JSONArray jsonArray = new JSONArray(responce);
                    //  Boolean result = jsonArray.getJSONArray("result");
                    //  String reason = jsonArray.getJSONArray("reason");
                    //   if (result) {
                    //   Log.d(Config.tag, "True : " + reason);
                    arrayList = new ArrayList<>();
                    MyFilesAdapter myFilesAdapter=new MyFilesAdapter(DownloadFilesListViewActivity.this,arrayList);
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        MyFiles myFiles=new MyFiles(jsonObject);
                        myFilesAdapter.add(myFiles);
                    }
                    //    }else {
                    //  Log.d(Config.tag, "False :" + reason);
                    listViewGetAllFiles.setAdapter(myFilesAdapter);
                    //   }
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

    private class MyFilesAdapter extends ArrayAdapter {
        Context context;
        public MyFilesAdapter(Context context, ArrayList<MyFiles> arrayList) {
            super(context,0,arrayList);
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyFiles myfiles=(MyFiles)getItem(position);
            ViewHolder viewHolder;
            if (convertView==null){
                viewHolder=new ViewHolder();
                LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                convertView=layoutInflater.inflate(R.layout.listview_file_row_layout,parent,false);
                viewHolder.tvFileUrl=(TextView)convertView.findViewById(R.id.tvFileUrl);
                viewHolder.tvFileName=(TextView)convertView.findViewById(R.id.tvFileName);
                viewHolder.tvSubName=(TextView)convertView.findViewById(R.id.tvSubName);
                viewHolder.tvSubCode=(TextView)convertView.findViewById(R.id.tvSubCode);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.tvFileUrl.setText(""+myfiles.file_url);
            viewHolder.tvFileName.setText(""+myfiles.file_name);
            viewHolder.tvFileName.setText(""+myfiles.file_sub_name);
            viewHolder.tvFileName.setText(""+myfiles.file_sub_code);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,DownloadFilesActivity.class);
                   /* bundle.putString("file_name",String.valueOf(myfiles.file_name));
                    bundle.putString("file_sub_name",String.valueOf(myfiles.file_sub_name));
                    bundle.putString("file_sub_code",String.valueOf(myfiles.file_sub_code));*/
                    intent.putExtra("file_url",String.valueOf(myfiles.file_url));
                    intent.putExtra("file_name",String.valueOf(myfiles.file_name));
                    intent.putExtra("file_sub_name",String.valueOf(myfiles.file_sub_name));
                    intent.putExtra("file_sub_code",String.valueOf(myfiles.file_sub_code));
                    startActivity(intent);
                }
            });
            return convertView;
        }
        public class ViewHolder{
            TextView tvFileUrl,tvFileName,tvSubName,tvSubCode;
        }
    }
}
