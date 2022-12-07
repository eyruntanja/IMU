package kth.etka.imu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class InternalActivity extends AppCompatActivity {

    TextView IMU, Internal;
    //IMU.setText(String.valueOf(imu));
    //Internal.setText(String.valueOf(internal));

    int imu = 0;
    int internal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        //TODO: everything
    }
}