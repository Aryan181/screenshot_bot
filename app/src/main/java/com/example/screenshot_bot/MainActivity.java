package com.example.screenshot_bot;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    int i = 0;
    private View main;
    private ImageView imageView;
    String TAG = "MainActivity";



    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyroscopeSensor == null){
            Log.e(TAG, "no sensor found");
            finish();
        }



        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                String s = "";
                if(sensorEvent.values[2]>10f || sensorEvent.values[2] < -10f) {
                    int j = 0;
                    Toast toast = Toast.makeText(getApplicationContext(), "activity detected", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.e(TAG, "SENSOR MOVEMENT DETECTED "+i);
                    //saving screenshot at motion duration
                    Date currentTime = Calendar.getInstance().getTime();
                    String uuidAsString = currentTime.toString();
                    Bitmap b = Screenshot.takescreenshotOfRootView(imageView);
                    imageView.setImageBitmap(b);
                    main.setBackgroundColor(Color.parseColor("#999999"));
                    File f = bitmapToFile(getApplicationContext(),b,"name"+uuidAsString+".png");
                    Log.e("main activity", " "+f.getAbsolutePath()+f.isFile());
                    handler.postDelayed(runnable, delay);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };


        //button, timer stuff
        main = findViewById(R.id.main);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btn = (Button) findViewById(R.id.btn);
        askForPermissions();
        runPassive();

    }


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 59*1000; //Delay for 15 seconds.  One second = 1000 milliseconds.
    protected void runPassive() {
        //start handler as activity become visible
        handler.postDelayed( runnable = new Runnable() {
            public void run() {

//                Log.e(TAG, "logging");
//                UUID uuid = UUID.randomUUID();
//                String uuidAsString = uuid.toString();
//                Bitmap b = Screenshot.takescreenshotOfRootView(imageView);
//                imageView.setImageBitmap(b);
//                main.setBackgroundColor(Color.parseColor("#999999"));
//                File f = bitmapToFile(getApplicationContext(),b,"name"+uuidAsString+".png");
//                Log.e("main activity", ""+f.getAbsolutePath()+f.isFile());
//                handler.postDelayed(runnable, delay);
            }
        }, delay);

        //super.onResume();

        //motion sensor takes screenshot




    }


    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file; // it will return null
        }
    }



    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
            //createDir();
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

}
