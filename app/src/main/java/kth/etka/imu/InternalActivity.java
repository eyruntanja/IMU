package kth.etka.imu;

import static java.lang.System.currentTimeMillis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class InternalActivity extends AppCompatActivity implements SensorEventListener{

    TextView Internal, gyroscope;
    Button recordButton;
    SensorManager sensorManager;
    private Sensor acc;
    private Sensor gyro;

    float x_acc, y_acc, z_acc;
    float EWMA = 0;
    float compFilt = 0;
    float imu = 0;
    boolean rec = true;
    private float [] internal = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        Internal = findViewById(R.id.angle1);
        gyroscope = findViewById(R.id.angle2);

        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(this::pressRecord);
    }

    @SuppressLint("SetTextI18n")
    private void pressRecord(View view) {
        long time = currentTimeMillis();

        if (rec) {
            recordButton.setText("Stop");
            rec = false;
            //TODO: Record
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {



                }
            }, 10000);
            //TODO: Stop and save

        }
        else if(!rec){
            recordButton.setText("Record");
            rec = true;

            //TODO: Stop and save


        }
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, acc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
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

            String s = "Acceleration: " + EWMA;
            //System.out.println(s);
            Internal.setText(s);
        }

        if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            compFilt = 0.9F*z_acc+(1-0.9F)*x;
            String st = "Gyro + accelerator: " + compFilt;

            gyroscope.setText(st);
        }
    }

}