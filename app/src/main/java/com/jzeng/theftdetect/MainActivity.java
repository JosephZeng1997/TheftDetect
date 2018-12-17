package com.jzeng.theftdetect;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    //names of save files
    private String accelRecordFile = "accelRecordFile";
    private String gyroRecordFile = "gyroRecordFile";
    private String testingFile = "testingFile";
    //buttons needed
    //start and end button are for detecting behaviors
    private Button start;
    private Button end;
    //all record and finish buttons are for obtaining training data about user
    private Button record;
    private Button record2;
    private Button record3;
    private Button finish;
    private Button finish2;
    private Button finish3;
    //button for training
    private Button train;
    //private Button test;
    //create DTW object
    private DTW dtw;
    //create file output objects
    private FileOutputStream accelOut;
    private FileOutputStream gyroOut;
    //private FileOutputStream testOut;
    //create sensor objects
    private SensorManager sensorManager;
    private SensorEventListener accelerometer;
    private SensorEventListener gyroscope;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set up intent
        intent = new Intent(this, BackgroundService.class);
        //set up sensor manager and sensors
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //save accelerometer values to file
                String accelValues = System.currentTimeMillis() + "," + Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]) + "\n";
                try {
                    accelOut = openFileOutput(accelRecordFile, Context.MODE_APPEND);
                    accelOut.write(accelValues.getBytes());
                    accelOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        gyroscope = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //save gyroscope values to file
                String gyroValues = System.currentTimeMillis() + "," + Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]) + "\n";
                try {
                    gyroOut = openFileOutput(gyroRecordFile, Context.MODE_APPEND);
                    gyroOut.write(gyroValues.getBytes());
                    gyroOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        //set start button
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
            }
        });
        //set end button
        end = (Button) findViewById(R.id.end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });
        //set record button 1
        record = (Button) findViewById(R.id.record1);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set which files to write
                gyroRecordFile = "gyroRecordFile1";
                accelRecordFile = "accelRecordFile1";
                //reset and clear files
                try {
                    gyroOut = openFileOutput(gyroRecordFile, Context.MODE_PRIVATE);
                    gyroOut.write("".getBytes());
                    gyroOut.close();
                    accelOut = openFileOutput(accelRecordFile, Context.MODE_PRIVATE);
                    accelOut.write("".getBytes());
                    accelOut.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                //start accelerometer for recording
                sensorManager.registerListener(accelerometer,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_GAME);
                //start gyroscope for recording
                sensorManager.registerListener(gyroscope,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).get(0),SensorManager.SENSOR_DELAY_GAME);
                Toast.makeText(v.getContext(),"Start recording user behavior. 1", Toast.LENGTH_SHORT).show();
            }
        });
        //set finish button 1
        finish = (Button) findViewById(R.id.finish1);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(accelerometer);
                sensorManager.unregisterListener(gyroscope);
                Toast.makeText(v.getContext(),"Stop recording user behavior. 1", Toast.LENGTH_SHORT).show();
            }
        });
        //set record button 2
        record2 = (Button) findViewById(R.id.record2);
        record2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set which files to write
                gyroRecordFile = "gyroRecordFile2";
                accelRecordFile = "accelRecordFile2";
                //reset and clear files
                try {
                    gyroOut = openFileOutput(gyroRecordFile, Context.MODE_PRIVATE);
                    gyroOut.write("".getBytes());
                    gyroOut.close();
                    accelOut = openFileOutput(accelRecordFile, Context.MODE_PRIVATE);
                    accelOut.write("".getBytes());
                    accelOut.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                //start accelerometer for recording
                sensorManager.registerListener(accelerometer,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_GAME);
                //start gyroscope for recording
                sensorManager.registerListener(gyroscope,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).get(0),SensorManager.SENSOR_DELAY_GAME);
                Toast.makeText(v.getContext(),"Start recording user behavior. 2", Toast.LENGTH_SHORT).show();
            }
        });
        //set finish button 2
        finish2 = (Button) findViewById(R.id.finish2);
        finish2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(accelerometer);
                sensorManager.unregisterListener(gyroscope);
                Toast.makeText(v.getContext(),"Stop recording user behavior. 2", Toast.LENGTH_SHORT).show();
            }
        });
        //set record button 1
        record3 = (Button) findViewById(R.id.record3);
        record3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set which files to write
                gyroRecordFile = "gyroRecordFile3";
                accelRecordFile = "accelRecordFile3";
                //reset and clear files
                try {
                    gyroOut = openFileOutput(gyroRecordFile, Context.MODE_PRIVATE);
                    gyroOut.write("".getBytes());
                    gyroOut.close();
                    accelOut = openFileOutput(accelRecordFile, Context.MODE_PRIVATE);
                    accelOut.write("".getBytes());
                    accelOut.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                //start accelerometer for recording
                sensorManager.registerListener(accelerometer,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),SensorManager.SENSOR_DELAY_GAME);
                //start gyroscope for recording
                sensorManager.registerListener(gyroscope,((SensorManager)getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_GYROSCOPE).get(0),SensorManager.SENSOR_DELAY_GAME);
                Toast.makeText(v.getContext(),"Start recording user behavior. 3", Toast.LENGTH_SHORT).show();
            }
        });
        //set finish button 1
        finish3 = (Button) findViewById(R.id.finish3);
        finish3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(accelerometer);
                sensorManager.unregisterListener(gyroscope);
                Toast.makeText(v.getContext(),"Stop recording user behavior. 3", Toast.LENGTH_SHORT).show();
            }
        });
        //set train button
        train = (Button) findViewById(R.id.train);
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data data1 = new Data();
                Data data2 = new Data();
                Data data3 = new Data();
                data1.GetData("gyroRecordFile1", "accelRecordFile1");
                data2.GetData("gyroRecordFile2", "accelRecordFile2");
                data3.GetData("gyroRecordFile3", "accelRecordFile3");
                //get train to get new dataset
                Data common = averageData(data1, data2);
                common = averageData(common, data3);
                //get threshold by comparing common dataset to all three datasets
                DTWResults dtw1 = new DTWResults();
                dtw1.ComputeDTW(common, data1);
                List<Double> t1 = dtw1.getMaxDist();
                DTWResults dtw2 = new DTWResults();
                dtw2.ComputeDTW(common, data2);
                List<Double> t2 = dtw2.getMaxDist();
                DTWResults dtw3 = new DTWResults();
                dtw3.ComputeDTW(common, data3);
                List<Double> t3 = dtw3.getMaxDist();
                List<Double> thresholds = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    thresholds.add(maxValue(t1.get(i),maxValue(t2.get(i),t3.get(i))));
                }
                //add common dataset and thresholds to files
                setRecordFiles(common,thresholds);
            }
        });
    }

    //object for holding everything about one dataset
    public class Data {
        String gyroRecordFile;
        String accelRecordFile;
        //values
        public List<Float> ax = new ArrayList<>();
        public List<Float> gx = new ArrayList<>();
        public List<Float> ay = new ArrayList<>();
        public List<Float> gy = new ArrayList<>();
        public List<Float> az = new ArrayList<>();
        public List<Float> gz = new ArrayList<>();

        int minA = 0;
        int minG = 0;

        //get data
        public void GetData(String gyroFile, String accelFile) {
            accelRecordFile = accelFile;
            gyroRecordFile = gyroFile;
            try{
                FileInputStream accelIn = openFileInput(accelFile);
                FileInputStream gyroIn = openFileInput(gyroFile);
                Scanner aScanner = new Scanner(accelIn);
                Scanner gScanner = new Scanner(gyroIn);
                while (aScanner.hasNext()) {
                    String aLine = aScanner.nextLine();
                    String[] aStrings = aLine.split(",");
                    float[] aValues = {Float.valueOf(aStrings[0]),Float.valueOf(aStrings[1]),Float.valueOf(aStrings[2]),Float.valueOf(aStrings[3])};
                    ax.add(aValues[1]);
                    ay.add(aValues[2]);
                    az.add(aValues[3]);
                }
                while (gScanner.hasNext()) {
                    String gLine = gScanner.nextLine();
                    String[] gStrings = gLine.split(",");
                    float[] gValues = {Float.valueOf(gStrings[0]), Float.valueOf(gStrings[1]), Float.valueOf(gStrings[2]), Float.valueOf(gStrings[3])};
                    gx.add(gValues[1]);
                    gy.add(gValues[2]);
                    gz.add(gValues[3]);
                }
                accelIn.close();
                gyroIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //holds the all dtw results for two datasets
    public class DTWResults {
        public DTW.Result ax;
        public DTW.Result ay;
        public DTW.Result az;
        public DTW.Result gx;
        public DTW.Result gy;
        public DTW.Result gz;
        public Data data_1;
        public Data data_2;
        public double averageTotalDistance;

        public void ComputeDTW(Data data1, Data data2) {
            data_1 = data1;
            data_2 = data2;
            DTW dtw = new DTW();
            ax = dtw.compute((toArray(data1.ax)), (toArray(data2.ax)));
            ay = dtw.compute((toArray(data1.ay)), (toArray(data2.ay)));
            az = dtw.compute((toArray(data1.az)), (toArray(data2.az)));
            gx = dtw.compute((toArray(data1.gx)), (toArray(data2.gx)));
            gy = dtw.compute((toArray(data1.gy)), (toArray(data2.gy)));
            gz = dtw.compute((toArray(data1.gz)), (toArray(data2.gz)));
            averageTotalDistance = (ax.getDistance() + ay.getDistance() + az.getDistance() + gx.getDistance() + gy.getDistance() + gz.getDistance()) / 6.0;
        }
        public List<Double> getMaxDist() {
            List<Double> thresholds = new ArrayList<>();
            thresholds.add(getMax(data_1.ax, data_2.ax, ax.getWarpingPath()));
            thresholds.add(getMax(data_1.ay, data_2.ay, ay.getWarpingPath()));
            thresholds.add(getMax(data_1.az, data_2.az, az.getWarpingPath()));
            thresholds.add(getMax(data_1.gx, data_2.gx, gx.getWarpingPath()));
            thresholds.add(getMax(data_1.gy, data_2.gy, gy.getWarpingPath()));
            thresholds.add(getMax(data_1.gz, data_2.gz, gz.getWarpingPath()));
            return thresholds;
        }
    }

    public float[] toArray(List<Float> al) {
        float[] a = new float[al.size()];
        for (int i = 0; i < al.size(); i++) {
            a[i] = al.get(i);
        }
        return a;
    }

    public void setRecordFiles(Data common, List<Double> thresholds) {
        try{
            FileOutputStream aOut = openFileOutput("accelRecordFile", Context.MODE_PRIVATE);
            FileOutputStream gOut = openFileOutput("gyroRecordFile", Context.MODE_PRIVATE);
            //write each list in common to files
            for (int i = 0; i < common.minA; i++) {
                String aLine = common.ax.get(i) + "," + common.ay.get(i) + "," + common.az.get(i) + "\n";
                aOut.write(aLine.getBytes());
            }
            for (int i = 0; i < common.minG; i++) {
                String gLine = common.gx.get(i) + "," + common.gy.get(i) + "," + common.gz.get(i) + "\n";
                gOut.write(gLine.getBytes());
            }
            //close files
            aOut.close();
            gOut.close();
            //add thresholds to new file
            FileOutputStream fout = openFileOutput("thresholds", Context.MODE_PRIVATE);
            for (int i = 0; i < thresholds.size(); i++) {
                String temp = thresholds.get(i) + "\n";
                fout.write(temp.getBytes());
            }
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getMax(List<Float> training, List<Float> testing, int[][] path) {
        double max = 0;
        for (int i = 0; i < path.length; i++) {
            double dist = Math.abs((double)training.get(path[i][0]) - (double)testing.get(path[i][1]));
            if (dist > max) {
                max = dist;
            }
        }
        return max;
    }

    public double maxValue(double a, double b){
        if (a > b) {
            return a;
        }
        else {
            return b;
        }
    }

    //computs the new data between the two given data
    public Data averageData(Data a, Data b) {
        //compute dtw between a & b
        DTWResults dtw = new DTWResults();
        dtw.ComputeDTW(a,b);
        //create new dataset and set file names to original
        Data newData = new Data();
        newData.gyroRecordFile = "gyroRecordfile";
        newData.accelRecordFile = "accelRecordFile";
        //for sensor values, calc average
        newData.ax = calcNewList(a.ax, b.ax, dtw.ax.getWarpingPath());
        newData.ay = calcNewList(a.ay, b.ay, dtw.ay.getWarpingPath());
        newData.az = calcNewList(a.az, b.az, dtw.az.getWarpingPath());
        newData.gx = calcNewList(a.gx, b.gx, dtw.gx.getWarpingPath());
        newData.gy = calcNewList(a.gy, b.gy, dtw.gy.getWarpingPath());
        newData.gz = calcNewList(a.gz, b.gz, dtw.gz.getWarpingPath());
        if (newData.ax.size() < newData.ay.size()) {
            if (newData.ax.size() < newData.az.size()) {
                newData.minA = newData.ax.size();
            }else {
                newData.minA = newData.az.size();
            }
        }else {
            if (newData.ay.size() < newData.az.size()) {
                newData.minA = newData.ay.size();
            }else {
                newData.minA = newData.az.size();
            }
        }
        if (newData.gx.size() < newData.gy.size()) {
            if (newData.gx.size() < newData.gz.size()) {
                newData.minG = newData.gx.size();
            }else {
                newData.minG = newData.gz.size();
            }
        }else {
            if (newData.gy.size() < newData.gz.size()) {
                newData.minG = newData.gy.size();
            }else {
                newData.minG = newData.gz.size();
            }
        }
        return newData;
    }

    //creates new dataset by averaging two lists by the warp path
    public List<Float> calcNewList(List<Float> a, List<Float> b, int[][] path) {
        List<Float> c = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            double sum = (double)a.get(path[i][0]) + (double)b.get(path[i][1]);
            double avg = sum/2;
            c.add((float)avg);
        }
        return c;
    }
}
