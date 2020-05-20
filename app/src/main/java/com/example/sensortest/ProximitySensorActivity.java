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

public class ProximitySensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager=null;
    private Sensor sensor=null;
    private TextView tv=null;


    //定义一个函数在TextView仲显示信息
    private void showInfo(String info){
        tv.append("\n" + info);
        Log.d("ProximitySensor",info);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用相同的layout
        setContentView(R.layout.activity_sensor_list);
        tv=(TextView) findViewById(R.id.sensor_list_text);
        //返回
        Button bt=(Button) findViewById(R.id.Back_to_Home);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProximitySensorActivity.this,MainActivity.class));
            }
        });


        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        //检查解析度和最大值，如果两者一样，说明该近距离传感器智能给出接近和远离这两个状态。
        showInfo("resolution:"+sensor.getResolution());
        showInfo("max value:"+sensor.getMaximumRange());

    }


    //// 如果activity不在前台就不需要监听，因此在onResume()注册监听器，在onPause()中注销监听器
    @Override
    protected void onResume() {
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this,sensor);
        super.onPause();
    }



    //对于近距离传感器，有效数值存放在values[0]中的，单位为cm。
    @Override
    public void onSensorChanged(SensorEvent event) {
        showInfo("传感器事件："+event.sensor.getName()+""+event.values[0]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        showInfo(sensor.getName()+"accuracy changed:"+accuracy);

    }
}
