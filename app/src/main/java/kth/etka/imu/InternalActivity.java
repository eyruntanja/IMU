package kth.etka.imu;

import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class InternalActivity extends AppCompatActivity implements SensorEventListener{

    TextView Internal, gyroscope;
    Button recordButton;
    SensorManager sensorManager;
    private Sensor acc;
    private Sensor gyro;
    List<Long> time;
    List<Float> ewma;
    List<Float> comple;

    String filename;
    File file;
    float x_acc, y_acc, z_acc;
    float EWMA = 0;
    float compFilt = 0;
    float imu = 0;
    long Time = 0;
    final float [] internal = new float[3];
    boolean rec;
    String s, st;


    public List<Long> getTime() {
        return time;
    }

    public List<Float> getEwma() {
        return ewma;
    }

    public List<Float> getComple() {
        return comple;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Internal = findViewById(R.id.angle1);
        gyroscope = findViewById(R.id.angle2);

        acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        time = new ArrayList<>();
        ewma = new ArrayList<>();
        comple = new ArrayList<>();

        rec = true;
        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(this::pressRecord);
    }

    @SuppressLint("SetTextI18n")
    private void pressRecord(View view) {
        if (rec) {
            recordButton.setText("Stop");
            Time = currentTimeMillis();
            ewma.add(EWMA);
            comple.add(compFilt);
            recordButton.setOnClickListener(this::stopRecord);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i<ewma.size();i++){
                        Time += i;
                        time.add(Time);
                    }
                    stopRecord();
                }
            }, 10000);
        }
    }


    private void stopRecord(View view) {
        recordButton.setText("Record");
        saveData();
    }

    private void stopRecord() {
        recordButton.setText("Record");
        saveData();
    }

    /*
    private void function(long timeVal,float Ewma,float Compliment){
        for (int i = 0; i<ewma.size();i++){
            timeVal += i;
            time.add(timeVal);
        }
        ewma.add(Ewma);
        comple.add(Compliment);
        saveData();

    }*/

    private void saveData(){
        filename = "data.csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File logDir = new File (directoryDownload, "data"); //Creates a new folder in DOWNLOAD directory
        logDir.mkdirs();
        File file = new File(logDir, filename);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            for (int i = 0; i < ewma.size(); i++) {
                outputStream.write((time.get(i) + ",").getBytes());
                outputStream.write((ewma.get(i) + ",").getBytes());
                outputStream.write((comple.get(i) + "\n").getBytes());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(getApplicationContext(), "Sensor accuracy changed", Toast.LENGTH_SHORT).show();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            x_acc = (float) Math.toDegrees(Math.atan(y/(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)))));
            y_acc = (float) Math.toDegrees(Math.atan(x/(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)))));
            z_acc = (float) Math.toDegrees(Math.atan((z/Math.sqrt(Math.pow(x,2) + Math.pow(y,2)))));

            internal [0]= x_acc;
            internal [1]= y_acc;
            internal [2]= z_acc;

            EWMA = 0.5F*EWMA+(1-0.5F)*z_acc;
            EWMA = Math.round(EWMA);
            ewma.add(EWMA);
            s = "Acceleration: " + EWMA;
            Internal.setText(s);
        }

        if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            compFilt = 0.9F*z_acc+(1-0.9F)*x;
            compFilt = Math.round(compFilt);
            comple.add(compFilt);
            st = "Gyro + accelerator: " + compFilt;

            gyroscope.setText(st);
        }
    }

}