package com.jzeng.theftdetect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class BackgroundService extends Service {
    //initialize fileouput objects
    private FileInputStream accelIn;
    private FileInputStream gyroIn;
    //file names for saved gyroscope, accelerometer and thresholds
    private String accelRecordFile = "accelRecordFile";
    private String gyroRecordFile = "gyroRecordFile";
    private String thresholdFile = "thresholds";
    //list to hold record thresholds
    private List<Double> thresholds;
    //array to decide hold previous accelermeter values to detect change/movement
    private float[] accelPrev = {-1,-1,-1};
    //lists for holding current and recorded sensor values
    private List<Float> ax;
    private List<Float> ay;
    private List<Float> az;
    private List<Float> gx;
    private List<Float> gy;
    private List<Float> gz;
    private List<Float> rax;
    private List<Float> ray;
    private List<Float> raz;
    private List<Float> rgx;
    private List<Float> rgy;
    private List<Float> rgz;
    //variables for keeping track of time, currently the testing assumes it takes a user 3 seconds to pick up phone
    private long startTime = 0;
    private long currentTime = 0;
    private long recordedTime = 3000;
    //set up dtw object and sensor objects
    private DTW dtw;
    private SensorManager sensorManager;
    private SensorEventListener accelerometer;
    private SensorEventListener gyroscope;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "App will start recording in background", Toast.LENGTH_SHORT).show();
        //get thresholds from file
        thresholds = new ArrayList<>();
        try {
            FileInputStream fin = openFileInput(thresholdFile);
            Scanner scanner = new Scanner(fin);
            while (scanner.hasNext()){
                String l = scanner.nextLine();
                double v = Double.valueOf(l);
                thresholds.add(v);
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //set up sensorlisteners
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if ((sensorEvent.values[0] - accelPrev[0]) > 0.05|| (sensorEvent.values[1] - accelPrev[1]) > 0.05 || (sensorEvent.values[2] - accelPrev[2]) > 0.05 || startTime != 0) {
                    rax.add(sensorEvent.values[0]);
                    ray.add(sensorEvent.values[1]);
                    raz.add(sensorEvent.values[2]);
                    if (startTime == 0) {
                        startTime = System.currentTimeMillis();
                    }
                    currentTime = System.currentTimeMillis();
                    if ((currentTime - startTime) > recordedTime) {
                        //convert arraylists to arrays
                        float[] ax1 = toArray(ax);
                        float[] ay1 = toArray(ay);
                        float[] az1 = toArray(az);
                        float[] gx1 = toArray(gx);
                        float[] gy1 = toArray(gy);
                        float[] gz1 = toArray(gz);
                        float[] rax1 = toArray(rax);
                        float[] ray1 = toArray(ray);
                        float[] raz1 = toArray(raz);
                        float[] rgx1 = toArray(rgx);
                        float[] rgy1 = toArray(rgy);
                        float[] rgz1 = toArray(rgz);
                        //run each through dtw
                        DTW.Result dtw_ax = dtw.compute((ax1), (rax1));
                        DTW.Result dtw_ay = dtw.compute((ay1), (ray1));
                        DTW.Result dtw_az = dtw.compute((az1), (raz1));
                        DTW.Result dtw_gx = dtw.compute((gx1), (rgx1));
                        DTW.Result dtw_gy = dtw.compute((gy1), (rgy1));
                        DTW.Result dtw_gz = dtw.compute((gz1), (rgz1));
                        //get max distances of each
                        double max_ax = getPercentage(ax1, rax1, dtw_ax.getWarpingPath(), thresholds.get(0));
                        double max_ay = getPercentage(ay1, ray1, dtw_ay.getWarpingPath(), thresholds.get(1));
                        double max_az = getPercentage(az1, raz1, dtw_az.getWarpingPath(), thresholds.get(2));
                        double max_gx = getPercentage(gx1, rgx1, dtw_gx.getWarpingPath(), thresholds.get(3));
                        double max_gy = getPercentage(gy1, rgy1, dtw_gy.getWarpingPath(), thresholds.get(4));
                        double max_gz = getPercentage(gz1, rgz1, dtw_gz.getWarpingPath(), thresholds.get(5));

                        String distext = "";
                        //current tolerance value, declare is user only if each dataset has at least 97.5% of data within threshold.
                        //Purpose for this is to account for noise in training and testing data
                        double p = .975;
                        if (max_ax < p || max_ay < p || max_az < p || max_gx < p || max_gy < p || max_gz < p) {
                            distext = "Not user";
                        } else {
                            distext = "Is user";
                        }
                        Toast.makeText(BackgroundService.this, distext, Toast.LENGTH_SHORT).show();
                        //reset recording values
                        rax.clear();
                        ray.clear();
                        raz.clear();
                        rgx.clear();
                        rgy.clear();
                        rgz.clear();
                        startTime = 0;
                    }
                    accelPrev = new float[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]};
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
        };
        gyroscope = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                //gyroscope only starts recording when accelerometer values record past a certain point
                if (startTime != 0) {
                    //record gyroscope values
                    rgx.add(sensorEvent.values[0]);
                    rgy.add(sensorEvent.values[1]);
                    rgz.add(sensorEvent.values[2]);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
        };
        dtw = new DTW();
        //set up recording array lists
        rax = new ArrayList<Float>();
        ray = new ArrayList<Float>();
        raz = new ArrayList<Float>();
        rgx = new ArrayList<Float>();
        rgy = new ArrayList<Float>();
        rgz = new ArrayList<Float>();
        //set up accelerometer
        sensorManager.registerListener(accelerometer,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_GAME);
        //set up gyroscope
        sensorManager.registerListener(gyroscope,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).get(0),
                SensorManager.SENSOR_DELAY_GAME);

        //get recorded values saved in files
        ax = new ArrayList<>();
        gx = new ArrayList<>();
        ay = new ArrayList<>();
        gy = new ArrayList<>();
        az = new ArrayList<>();
        gz = new ArrayList<>();
        try {
            accelIn = openFileInput(accelRecordFile);
            gyroIn = openFileInput(gyroRecordFile);
            Scanner aScanner = new Scanner(accelIn);
            Scanner gScanner = new Scanner(gyroIn);
            while (aScanner.hasNext()) {
                String aLine = aScanner.nextLine();
                String[] aStrings = aLine.split(",");
                float[] aValues = {Float.valueOf(aStrings[0]),Float.valueOf(aStrings[1]),Float.valueOf(aStrings[2])};
                ax.add(aValues[0]);
                ay.add(aValues[1]);
                az.add(aValues[2]);
            }
            while (gScanner.hasNext()) {
                String gLine = gScanner.nextLine();
                String[] gStrings = gLine.split(",");
                float[] gValues = {Float.valueOf(gStrings[0]), Float.valueOf(gStrings[1]), Float.valueOf(gStrings[2])};
                gx.add(gValues[0]);
                gy.add(gValues[1]);
                gz.add(gValues[2]);
            }
            accelIn.close();
            gyroIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(accelerometer);
        sensorManager.unregisterListener(gyroscope);
        Toast.makeText(this, "App stopped recording in background, exit app to stop recording", Toast.LENGTH_SHORT).show();
    }

    //converts a list to an array
    public float[] toArray(List<Float> al) {
        float[] a = new float[al.size()];
        for (int i = 0; i < al.size(); i++) {
            a[i] = al.get(i);
        }
        return a;
    }

    //computes the percentage of test data that fits with threshold
    public double getPercentage(float[] training, float[] testing, int[][] path, double threshold) {
        double total = path.length;
        double valid = 0;
        for (int i = 0; i < total; i++) {
            double dist = Math.abs(training[path[i][0]] - testing[path[i][1]]);
            if (dist < threshold) {
                valid++;
            }
        }
        return valid/total;
    }
}
