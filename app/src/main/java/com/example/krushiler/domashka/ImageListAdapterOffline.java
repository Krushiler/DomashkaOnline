package com.example.krushiler.domashka;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ImageListAdapterOffline extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> imageUrls;
    private List<String> description;
    FileCache fileCache;
    String storageReference;

    public ImageListAdapterOffline(Context context, List<String> imageUrls, List<String> description, String storageReference1) {
        super(context, R.layout.imagelistmain, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;
        this.description = description;
        inflater = LayoutInflater.from(context);
        storageReference = storageReference1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        fileCache = new FileCache(context);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.imagelistmain, parent, false);
        }
        RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Uri.parse(imageUrls.get(position)))
                .onlyRetrieveFromCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(requestOptions)
                .error(R.drawable.rubbish_bin)
                .into((ImageView) convertView.findViewById(R.id.imageforlist));
        Log.d("strg", imageUrls.get(position));
        TextView tv = (TextView) convertView.findViewById(R.id.tvforlist);
        if(description!=null){tv.setText(description.get(position));}
        return convertView;
    }
}
