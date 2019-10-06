package com.example.krushiler.domashka;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private List<String> imageUrls;
    private List<String> description;
    FileCache fileCache;
    Context appContext;

    public ImageListAdapter(Context context, List<String> imageUrls, StorageReference storageReference1, List<String> description, Context appContext) {
        super(context, R.layout.imagelistmain, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;
        this.description = description;
        this.appContext = appContext;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = storageReference1;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        fileCache = new FileCache(context);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.imagelistmain, parent, false);
        }
        final View finalConvertView = convertView;
        storageReference.child(imageUrls.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into((ImageView) finalConvertView.findViewById(R.id.imageforlist));
            }
        });
        saveArrayList(imageUrls, "fileList");

        TextView tv = (TextView) convertView.findViewById(R.id.tvforlist);
        if(description!=null){tv.setText(description.get(position));}
        return convertView;
    }
    public void saveArrayList(List<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<String> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}