package kth.etka.imu;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class InternalActivity extends AppCompatActivity implements SensorEventListener{

    TextView Internal, gyroscope;
    Button recordButton;
    SensorManager sensorManager;
    private Sensor acc;
    private Sensor gyro;

    String filename;
    File file;
    float x_acc, y_acc, z_acc;
    float EWMA = 0;
    float compFilt = 0;
    float imu = 0;
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

        rec = true;
        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(this::pressRecord);
    }

    private void pressRecord(View view) {
        if (rec) {
            recordButton.setText("Stop");
            rec = false;
            //TODO: Record
            saveData(s + ";" + st);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    recordButton.setText("Record");
                    rec = true;
                    System.out.println(file);
                }
            }, 10000);



        }
        else if(!rec){
            recordButton.setText("Record");
            rec = true;

            //TODO: Stop and save

        }
    }

    private void saveData(String content){
        filename = "data.csv";
        try {
            file = new File(filename);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
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
            z_acc = (float) Math.toDegrees(Math.atan((Math.sqrt(Math.pow(x,2) + Math.pow(y,2))/z)));

            internal [0]= x_acc;
            internal [1]= y_acc;
            internal [2]= z_acc;

            EWMA = 0.5F*EWMA+(1-0.5F)*z_acc;

            s = "Acceleration: " + EWMA;
            //System.out.println(s);
            Internal.setText(s);
        }

        if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            compFilt = 0.9F*z_acc+(1-0.9F)*x;
            st = "Gyro + accelerator: " + compFilt;

            gyroscope.setText(st);
        }
    }

}