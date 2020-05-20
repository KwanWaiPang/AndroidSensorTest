package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MagneticFieldSensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private TextView tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //使用相同的layout
        setContentView(R.layout.activity_sensor_list);
        tv = (TextView)findViewById(R.id.sensor_list_text);


        //返回主页
        Button bt=(Button) findViewById(R.id.Back_to_Home);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MagneticFieldSensorActivity.this,MainActivity.class));
            }
        });


        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }


    //在onResume()注册监听器，在onPause()中注销监听器
    protected void onPause() {
        // TODO Auto-generated method stub
        sensorManager.unregisterListener(this, sensor);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }



    private int count = 1;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (count++==20){//由于不稳定，所以每20次，再输出一次
            double value = Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1]
                    +event.values[2]*event.values[2]);
            String str = String.format("X:%8.4f , Y:%8.4f , Z:%8.4f \n总值为：%8.4f", event.values[0],event.values[1],event.values[2],value);

            count = 1;
            tv.setText(str);
            Log.d("磁场感应器",str);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
