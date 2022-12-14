package kth.etka.imu;

import static java.lang.System.currentTimeMillis;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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

    private void pressRecord(View view) {
        if (rec) {
            recordButton.setText("Stop");
            Time = currentTimeMillis();
            recordButton.setOnClickListener(this::stopRecord);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecord();
                }
            }, 10000);
        }
    }


    private void stopRecord(View view) {
        recordButton.setText("Record");

    }

    private void stopRecord() {
        recordButton.setText("Record");

    }

    private void saveData(String content) {
        filename = "data.csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(directoryDownload, filename);
        directoryDownload.mkdirs();
        FileOutputStream outputStream = null;
        for (int i = 0; i<Time.size();i++){
            Time+=i;
            time.add(Time);
        }
        try {
            outputStream = new FileOutputStream(file, false);
            for (int i = 0; i < time.size(); i++) {
                outputStream.write((time.get(i) + ",").getBytes());
                outputStream.write((ewma.get(i) + ",").getBytes());
                outputStream.write((comple.get(i) + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void saveData(String content){
        filename = "data.csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(directoryDownload, filename);
        directoryDownload.mkdirs();
        System.out.println(directoryDownload);
        try {
            file = new File(directoryDownload, filename);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void readData (){
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(filename));
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
            z_acc = (float) Math.toDegrees(Math.atan((Math.sqrt(Math.pow(x,2) + Math.pow(y,2))/z)));

            internal [0]= x_acc;
            internal [1]= y_acc;
            internal [2]= z_acc;

            EWMA = 0.5F*EWMA+(1-0.5F)*z_acc;
            ewma.add(EWMA);
            s = "Acceleration: " + EWMA;
            Internal.setText(s);
        }

        if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            compFilt = 0.9F*z_acc+(1-0.9F)*x;
            comple.add(compFilt);
            st = "Gyro + accelerator: " + compFilt;

            gyroscope.setText(st);
        }
    }

}