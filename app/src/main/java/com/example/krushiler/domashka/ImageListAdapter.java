package com.example.krushiler.domashka;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
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

    public ImageListAdapter(Context context, List<String> imageUrls, StorageReference storageReference1, List<String> description) {
        super(context, R.layout.imagelistmain, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;
        this.description = description;
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = storageReference1;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.imagelistmain, parent, false);
        }
        GlideApp.with(context)
                .load(storageReference.child(imageUrls.get(position)))
                .into((ImageView) convertView.findViewById(R.id.imageforlist));
        TextView tv = (TextView) convertView.findViewById(R.id.tvforlist);
        if(description!=null){tv.setText(description.get(position));}
        Log.d("callbacker", String.valueOf(storageReference.child(imageUrls.get(position)).getDownloadUrl()));

        return convertView;
    }
}
