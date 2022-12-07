package kth.etka.imu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button internalButton, imuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internalButton = findViewById(R.id.intern);
        internalButton.setOnClickListener(this::internalValue);

        imuButton = findViewById(R.id.imu);
        imuButton.setOnClickListener(this::imuValue);
    }

    private void imuValue(View view) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }

    private void internalValue(View view) {
        startActivity(new Intent(this, InternalActivity.class));
    }
}