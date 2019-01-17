package com.example.vlad.alphatest;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.vlad.alphatest.data.Image;
import com.example.vlad.alphatest.repository.GalleryFirebaseRepository;
import com.example.vlad.alphatest.repository.GalleryRepository;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.observers.TestObserver;

@RunWith(AndroidJUnit4.class)
public class FirebaseRepositoryTest {
    private static final String TAG = "FirebaseRepositoryTest";
    private GalleryRepository repository;


    @Before
    public void setUp() {
        repository = new GalleryFirebaseRepository(FirebaseStorage.getInstance(), FirebaseDatabase.getInstance());
    }

    @Test
    public void testGetImagesFromServer() {
        TestObserver<List<Image>> testObserver = new TestObserver<>();
        repository.getImagesFromServer().subscribe(testObserver);
        while (waitResult(testObserver)) {
            continue;
        }
        Log.i(TAG, "testRepository: " + testObserver.values().get(0).get(0).getUrl());
        testObserver.assertNoErrors();
        Assert.assertNotNull(testObserver.values().get(0));
    }

    @Test
    public void testWriteNewImageToDatabase() {
        TestObserver testObserver = new TestObserver<>();
        Image image = new Image("testImageId" + System.currentTimeMillis(), "https://avatars.mds.yandex.net/get-pdb/38069/e9902ae7-d588-4c8c-b054-7ebb58899ec9/s1200");
        repository.writeNewImageToDB(image).subscribe(testObserver);
        while (waitResult(testObserver)) {
            continue;
        }
        Log.i(TAG, "testWriteNewImageToDatabase: COMPLETE");
        testObserver.assertNoErrors();
        testObserver.assertComplete();
    }

    private boolean waitResult(TestObserver<List<Image>> testObserver) {
        return testObserver.getEvents().get(0).size() == 0 &&
                testObserver.getEvents().get(1).size() == 0 &&
                testObserver.getEvents().get(2).size() == 0;
    }
}
