package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AccelerometerSensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor sensor = null,gravitySensor = null,linearAcceleSensor = null;
    private TextView tv = null;
    private WindowManager window = null;

//    private void showInfo(String info){
//        tv.append("\n" + info);
//        Log.d("加速度仪",info);
//    }
    private void showInfo1(String info){
        tv.setText(info + "\n" + tv.getText());
        tv.append("\n" + info);
        Log.d("加速度仪",info);
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
                startActivity(new Intent(AccelerometerSensorActivity.this,MainActivity.class));
            }
        });

        //定义传感器管理器
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);//重力传感器（获取重力传感器的值的目标是减去重力）
        linearAcceleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);//线性加速度

        showInfo1("resolution is " + sensor.getResolution());
        showInfo1("API为" + Build.VERSION.SDK_INT);
        window = (WindowManager)getSystemService(WINDOW_SERVICE);
        //返回值为Surface.ROTATION_0（0）、Surface.ROTATION_90（1）、Surface.ROTATION_180（2）和Surface.ROTATION_270（3），
        //可以用来确定屏幕UI的旋转方向。
        // 注意：需要开启“自动旋转”才能有效检查，否则均为Surface.ROTATION_0（手机以竖屏为主，一般都会0，但不保证都如此）。
        // 不是所以的手机都能检测到这4个值，例如P6，没有Surface.ROTATION_180，即UI不支持倒过来，如果有某个数值不支持，通过getRotation()获取的数值可能并不准确，仍以P6为例，如果我们顺时针转90°，得到Surface.ROTATION_90，继续顺时针转至180°，无检测新数值，仍未Surface.ROTATION_90，再继续顺时针转90°（至270°），仍显示为Surface.ROTATION_90，而非Surface.ROTATION_270。
        showRotation();//展示旋转角

    }


    //在onResume()注册监听器，在onPause()中注销监听器
    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, linearAcceleSensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }


    private int count = 1;
    // 对于加速器，测量的是x、y、z三个轴向的加速度，分别从values[0]、values[1]、values[2]中读取。
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (count++%40==0){//还不到40个计数时
            int type = event.sensor.getType();
            if(type == Sensor.TYPE_ACCELEROMETER){
                showInfo1("加速器：" + " x:" + event.values[0] + " y:" + event.values[1]+ " z:" + event.values[2]);
            }else if(type == Sensor.TYPE_GRAVITY){
                showInfo1("重力仪：" + " x:" + event.values[0] + " y:" + event.values[1]+ " z:" + event.values[2]);
            }else if(type == Sensor.TYPE_LINEAR_ACCELERATION){
                showInfo1("线性加速仪" + " x:" + event.values[0] + " y:" + event.values[1] + " z:" + event.values[2]);
            }
            count = 1;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        showInfo1(sensor.getName() + " accuracy changed: " + accuracy);

    }

    //将角度显示出来
    private void showRotation(){
        int rotation = window.getDefaultDisplay().getRotation();//获取旋转的方向
        switch(rotation){
            case Surface.ROTATION_0:
                showInfo1("方向：ROTATION 0(" + rotation + ")");
                break;
            case Surface.ROTATION_90:
                showInfo1("方向：ROTATION 90(" + rotation + ")");
                break;
            case Surface.ROTATION_180:
                showInfo1("方向：ROTATION 180(" + rotation + ")");
                break;
            case Surface.ROTATION_270:
                showInfo1("方向：ROTATION 270(" + rotation + ")");
                break;
            default:
                showInfo1("方向：(" + rotation + ")");
                break;
        }

    }
}
