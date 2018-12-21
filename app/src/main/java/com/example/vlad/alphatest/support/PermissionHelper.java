package com.example.vlad.alphatest.support;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.vlad.alphatest.view.activity.BaseActivity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.vlad.alphatest.support.Constants.REQUEST_PERMISSION_CAMERA;
import static com.example.vlad.alphatest.support.Constants.REQUEST_READ_WRITE_STORAGE;

public class PermissionHelper {
    private static final String TAG = "PermissionHelper";

    public static boolean checkGranted(int[] grantResults) {
        for (int grantResult : grantResults)
            if (grantResult == PackageManager.PERMISSION_DENIED) return false;
        return true;
    }

    public static boolean mayRequestCamera(BaseActivity activity) {
        Log.d(TAG, "mayRequestCamera: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            if (activity.shouldShowRequestPermissionRationale(CAMERA)) {
                requestCameraPermission(activity);
            } else {
                requestCameraPermission(activity);
            }
        } else return true;
        return false;
    }



    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean mayRequestStorage(BaseActivity activity) {
        Log.d(TAG, "mayRequestStorage: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
            if (activity.shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) &&
                    activity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                requestReadWriteExternalStoragePermission(activity);
            } else {
                requestReadWriteExternalStoragePermission(activity);
            }
        }else return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestReadWriteExternalStoragePermission(BaseActivity activity) {
        Log.d(TAG, "requestReadWriteExternalStoragePermission: ");
        activity.requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_READ_WRITE_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestCameraPermission(BaseActivity activity) {
        Log.d(TAG, "requestCameraPermission: ");
        activity.requestPermissions(new String[]{CAMERA}, REQUEST_PERMISSION_CAMERA);
    }
}
