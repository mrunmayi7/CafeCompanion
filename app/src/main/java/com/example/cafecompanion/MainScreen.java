package com.example.cafecompanion;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainScreen extends AppCompatActivity {

    private ParticleDevice mDevice;
    private int available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException {
                sparkCloud.logIn("nimsnaik23@gmail.com", "pottedplants");
                Log.d("BANANA", "logging in");
                sparkCloud.getDevices();


                try {
                    mDevice = sparkCloud.getDevices().get(0);
                    Log.d("BANANA", "device gotten");
                } catch (IndexOutOfBoundsException iobEx) {
                    throw new RuntimeException("Your account must have at least one device for this example app to work");
                }
                return mDevice;
            }

            @Override
            public void onSuccess(@NonNull Object value) {
                Toaster.l(MainScreen.this, "Logged in");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Toaster.l(MainScreen.this, e.getBestMessage());
                e.printStackTrace();
                Log.d("info", e.getBestMessage());
            }
        });
        //setContentView(TextView);
    }


}
