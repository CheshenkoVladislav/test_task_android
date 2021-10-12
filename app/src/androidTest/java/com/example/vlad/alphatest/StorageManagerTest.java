package com.example.vlad.alphatest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.vlad.alphatest.managers.StorageManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class StorageManagerTest {
    private Context context;
    private StorageManager storageManager;
    private String tempFileName = "cachedImage.jpg";
    private static final String TAG = "StorageManagerTest";

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
        storageManager = new StorageManager(context);
    }

    @After
    public void after() {
        File file = new File(context.getFilesDir(), tempFileName);
        if (file.exists())
            file.delete();
    }

    @Test
    public void testSaveToFiles() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf); // thi
        Drawable drawable = new BitmapDrawable(bmp);
        File file = storageManager.saveToFiles(drawable);
        Assert.assertEquals(file.getName(), tempFileName);
    }
}
