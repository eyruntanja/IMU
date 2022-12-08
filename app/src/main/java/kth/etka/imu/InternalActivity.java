package kth.etka.imu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorPrivacyManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class InternalActivity extends AppCompatActivity {

    TextView IMU, Internal;
    SensorManager sensorManager;


    //IMU.setText(String.valueOf(imu)); //To change the value of the textview in the HTML
    //Internal.setText(String.valueOf(internal));

    int imu = 0;
    int internal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> gyroscope = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

        //TODO: First method, ONLY accelerometer
        //TODO: Second method, accelerometer and gyroscope
    }
}