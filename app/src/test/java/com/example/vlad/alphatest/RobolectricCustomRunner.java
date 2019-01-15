package com.example.vlad.alphatest;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.manifest.BroadcastReceiverData;

import java.util.ArrayList;
import java.util.List;

public class RobolectricCustomRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default. Use the {@link Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public RobolectricCustomRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest manifest = super.getAppManifest(config);
        List<BroadcastReceiverData> broadcastReceiverData = manifest.getBroadcastReceivers();
        List<BroadcastReceiverData> removeList = new ArrayList<>();

        broadcastReceiverData.removeAll(removeList);
        return  manifest;
    }
}
