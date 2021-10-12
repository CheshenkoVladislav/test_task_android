package com.example.vlad.alphatest.support;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;

import timber.log.Timber;

public class IntentUtils {

    public static Intent createGalleryIntent(Context context, File file) {
        Uri contentUri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider", file);
        if (contentUri != null) {
            Timber.d("CREATE INTENT TO START GALLERY");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_VIEW);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            return shareIntent;
        }
        return null;
    }

    public static Intent createCameraIntent(Context context, File file) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(context.getPackageManager()) != null) {
            //Create a file to store the imageUrl
//            photoFile = createImageFile(context);
            if (file != null) {
                Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }
        return pictureIntent;
    }

}
