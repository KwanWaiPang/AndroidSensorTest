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

public class GyroscopeSensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private TextView tv = null;

    //在华为P6的机器上，陀螺仪非常敏感，平放在桌面，由于电脑照成的轻微震动在不断地刷屏，为了避免写UI造成的性能问题，可以只写Log。
    private void showInfo(String info){
        tv.append("\n" + info);
        Log.d("陀螺仪",info);
    }

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
                startActivity(new Intent(GyroscopeSensorActivity.this,MainActivity.class));
            }
        });

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        showInfo("resolution is " + sensor.getResolution());

    }


    //在onResume()注册监听器，在onPause()中注销监听器
    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this,sensor);
        super.onPause();
    }


    //重写的方法

    //对于陀螺仪，测量的是x、y、z三个轴向的角速度，分别从values[0]、values[1]、values[2]中读取，单位为弧度/秒。
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            showInfo("传感器测量结果：" + " x:" + event.values[0] + " y:" + event.values[1]
                    + " z:" + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        showInfo(sensor.getName() + " accuracy changed: " + accuracy);
    }
}
