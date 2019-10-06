package com.example.krushiler.domashka;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;

public class FileCache
{
    private File cacheDir;
    private File                cacheDirEvent;
    private File                cacheDirPromotion;

    public FileCache(final Context context)
    {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            this.cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "TNLRadio/res/shows");
            this.cacheDirEvent = new File(android.os.Environment.getExternalStorageDirectory(), "TNLRadio/res/events");
            this.cacheDirPromotion = new File(android.os.Environment.getExternalStorageDirectory(),
                    "TNLRadio/res/promotion");
        }
        else
        {
            this.cacheDir = context.getCacheDir();
            this.cacheDirEvent = context.getCacheDir();
            this.cacheDirPromotion = context.getCacheDir();
        }
        if (!this.cacheDir.exists() && !this.cacheDirEvent.exists() && !this.cacheDirPromotion.exists())
        {
            this.cacheDir.mkdirs();
            this.cacheDirEvent.mkdir();
            this.cacheDirPromotion.mkdir();
        }
    }

    public File getFile(final Context context, final String url) throws FileNotFoundException
    {
        // retrieve filename/location from shared preferences based on the the url
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String filename = settings.getString(url, null);

        if (url == null)
        {
            throw new FileNotFoundException();
        }

        final File f = new File(this.cacheDir, filename);
        return f;
    }

    public void downloadAndCache(final Context context, final String url)
    {
        // TODO: download the file and save to the filesystem
        // TODO: generate a the filename and push into saved preferences
        String filename = "";

        // save file into the share preferences so we can get it back late
        saveFileToMap(context, url, filename);
    }

    private void saveFileToMap(final Context context, final String url, final String filename)
    {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        // save the pair into shared preferences
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(url, filename);
        editor.commit();
    }
}