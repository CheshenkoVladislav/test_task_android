package com.example.vlad.alphatest.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class StorageManager {
    private Context context;

    public StorageManager(Context context) {
        this.context = context;
    }

    public File saveToFiles(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        String filename = "cachedImage.jpg";
        File file = new File(context.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            outputStream.write(bitmapdata);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
