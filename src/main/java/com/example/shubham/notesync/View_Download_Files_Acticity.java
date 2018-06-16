package com.example.shubham.notesync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Vector;

/**
 * Created by Shubham on 24-03-2017.
 */
public class View_Download_Files_Acticity extends Activity implements AdapterView.OnItemClickListener {
    Vector<GridIcon> gridIconVector;
    GridView gridViewSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_downloaded_files_layout);
        gridIconVector = new Vector<GridIcon>();
        gridViewSubject = (GridView) findViewById(R.id.gridViewSubject);


        gridViewSubject.setOnItemClickListener(this);


         File folder = new File(Environment.getExternalStorageDirectory() + "/notesync");
        listFilesForFolder(folder);
    }
    public void listFilesForFolder(final File folder) {
        if(folder.exists()){
            for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                gridIconVector.add(new GridIcon(fileEntry.getName(),"Sub Name :", R.drawable.gridviewlogo));
                System.out.println(fileEntry.getName());
            }
        }
            gridViewSubject.setAdapter(new GridIconAdapter(this));
        }
        else {
            Toast.makeText(this,"No Notes Available..!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory() + "/notesync/"+gridIconVector.get(position).title);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        intent.setDataAndType(Uri.fromFile(file), type);
        if (intent.resolveActivity(getPackageManager())!=null){
            Toast.makeText(View_Download_Files_Acticity.this, +position+"Click", Toast.LENGTH_SHORT).show();
            Log.d("Path:",Environment.getExternalStorageDirectory() + "/notesync/" + gridIconVector.get(position).title);
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"Doument Viewer UnAvailable..!",Toast.LENGTH_SHORT).show();
        }
    }

    private class GridIconAdapter extends BaseAdapter {
        Context context;
        public GridIconAdapter(Context context) {
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myview;
            GridIcon gridIcon=gridIconVector.elementAt(position);
            if(convertView==null){
                LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                myview=layoutInflater.inflate(R.layout.grid_view_row_layout,null);

                TextView tvTitle=(TextView)myview.findViewById(R.id.tvTitle);
                tvTitle.setText(gridIcon.title);

//                TextView tvSubTitle=(TextView)myview.findViewById(R.id.tvSubTitle);
//                tvSubTitle.setText(gridIcon.subtitle);

                ImageView imageViewFileLogo=(ImageView)myview.findViewById(R.id.imageViewFileLogo);
                imageViewFileLogo.setImageResource(gridIcon.icon_resource);
            }
            else
                myview = (View) convertView;
            return myview;
        }

        @Override
        public int getCount() {
            return gridIconVector.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
