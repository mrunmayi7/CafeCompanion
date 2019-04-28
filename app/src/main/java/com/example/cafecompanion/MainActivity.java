package com.example.cafecompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Py;
import io.particle.android.sdk.utils.Toaster;

import static io.particle.android.sdk.utils.Py.list;

public class MainActivity extends AppCompatActivity {

    Button button1;
    private ParticleDevice mDevice;
    public int available = 1;
    public boolean flag;
    String mycode = "123456";
    int variable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button1 = findViewById(R.id.vibrate);
        Log.d("BANANA", "logging in");

        ParticleCloudSDK.init(this);
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
                Toaster.l(MainActivity.this, "Logged in");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Toaster.l(MainActivity.this, e.getBestMessage());
                e.printStackTrace();
                Log.d("info", e.getBestMessage());
            }
        });

    }

    public void Connect(View view){

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException {
                flag = false;

                String code1 = null;
                try {
                    code1 = mDevice.getStringVariable("CodeA");
                    Log.d("BANANA", code1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("BANANA", "error1");
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    e.printStackTrace();
                    Log.d("BANANA", "error2");
                }
//                String code2 = null;
//                try {
//                    code2 = mDevice.getStringVariable("CodeB");
//                    Log.d("BANANA", code2);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ParticleDevice.VariableDoesNotExistException e) {
//                    e.printStackTrace();
//                }

                try {
                    if (code1.equals(mycode)) {

                        try {
                            available = mDevice.getIntVariable("BookA");
                        } catch (ParticleCloudException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Particle Cloud Exception!");
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException("IO Exception!");
                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Variable doesn't exist!");
                        }

//                    } else if (code2.equals(mycode)) {
//
//                        try {
//
//                            available = mDevice.getIntVariable("BookB");
//                            //            Integer obj = new Integer(available);
//                            //            String temp = obj.toString();
//                            //            Log.d("Connect", temp);
//                        } catch (ParticleCloudException e) {
//                            e.printStackTrace();
//                            throw new RuntimeException("Particle Cloud Exception!");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            throw new RuntimeException("IO Exception!");
//                        } catch (ParticleDevice.VariableDoesNotExistException e) {
//                            e.printStackTrace();
//                            throw new RuntimeException("Variable doesn't exist!");
//                        }

                    }
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }

                flag = true;
                return mDevice;
            }

            @Override
            public void onSuccess(@NonNull Object value) {
                Toaster.l(MainActivity.this, "Logged in");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Toaster.l(MainActivity.this, e.getBestMessage());
                e.printStackTrace();
                Log.d("info", e.getBestMessage());
            }

        });

        while(!flag){
            //wait
        }

        if(available == 0){

            Intent startNewActivity = new Intent(this, MainScreen.class);

            Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

                @Override
                public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {

                    //TODO: Change A to var
                    String a = "A";
                    try {
                        variable = mDevice.callFunction("bookCoaster", list(a));
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }

                    return mDevice;

                }

                @Override
                public void onSuccess(Object value) {
                    Toaster.l(MainActivity.this, "Logged in");
                }

                @Override
                public void onFailure( ParticleCloudException e) {
                    Toaster.l(MainActivity.this, e.getBestMessage());
                    e.printStackTrace();
                    Log.d("info", e.getBestMessage());
                }

            });


            startActivity(startNewActivity);
        }

        else{
            Toaster.s(this, "Cannot connect, try again!");
            Toaster.s(this, String.valueOf(available));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
