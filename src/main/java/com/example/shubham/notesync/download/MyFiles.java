package com.example.shubham.notesync.download;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shubham on 13-04-2017.
 */
public class MyFiles {
    //file_url=" + "http://notesync.esy.es/files/"+fileName + "&file_name=" + etSubName.getText().toString().trim() + "&file_sub_code="
    public String file_url="";
    public String file_name="";
    public String file_sub_name="";
    public String file_sub_code="";
    public MyFiles(JSONObject jsonObject) {
        try {
            this.file_url=jsonObject.getString("file_url");
            this.file_name=jsonObject.getString("file_name");
            this.file_sub_name=jsonObject.getString("file_sub_name");
            this.file_sub_code=jsonObject.getString("file_sub_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
