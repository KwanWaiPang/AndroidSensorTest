package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SensorListActivity extends AppCompatActivity {

    private TextView tv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        //返回
        Button bt=(Button) findViewById(R.id.Back_to_Home);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SensorListActivity.this,MainActivity.class));
            }
        });

        tv=(TextView) findViewById(R.id.sensor_list_text);
        showSensorList();//自定义的函数，展示手机所带有的sensor
    }

    private HashMap<Integer, String> sensorTypes = new HashMap<Integer,String>();
    {
        sensorTypes.put(Sensor.TYPE_ACCELEROMETER, "TYPE_ACCELEROMETER");
        sensorTypes.put(Sensor.TYPE_AMBIENT_TEMPERATURE, "TYPE_AMBIENT_TEMPERATURE");
        sensorTypes.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "TYPE_GAME_ROTATION_VECTOR");
        sensorTypes.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "TYPE_GEOMAGNETIC_ROTATION_VECTOR");
        sensorTypes.put(Sensor.TYPE_GRAVITY, "TYPE_GRAVITY");
        sensorTypes.put(Sensor.TYPE_GYROSCOPE, "TYPE_GYROSCOPE");
        sensorTypes.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED,"TYPE_GYROSCOPE_UNCALIBRATED");
        sensorTypes.put(Sensor.TYPE_LIGHT, "TYPE_LIGHT");
        sensorTypes.put(Sensor.TYPE_LINEAR_ACCELERATION, "TYPE_LINEAR_ACCELERATION");
        sensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD, "TYPE_MAGNETIC_FIELD");
        sensorTypes.put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "TYPE_MAGNETIC_FIELD_UNCALIBRATED");
        sensorTypes.put(Sensor.TYPE_ORIENTATION,"TYPE_ORIENTATION (deprecated)");//use SensorManager.getOrientation() instead.
        sensorTypes.put(Sensor.TYPE_PRESSURE, "TYPE_PRESSURE");
        sensorTypes.put(Sensor.TYPE_PROXIMITY, "TYPE_PROXIMITY");
        sensorTypes.put(Sensor.TYPE_RELATIVE_HUMIDITY, "TYPE_RELATIVE_HUMIDITY");
        sensorTypes.put(Sensor.TYPE_ROTATION_VECTOR, "TYPE_ROTATION_VECTOR");
        sensorTypes.put(Sensor.TYPE_SIGNIFICANT_MOTION,"TYPE_SIGNIFICANT_MOTION");
        sensorTypes.put(Sensor.TYPE_STEP_COUNTER,"TYPE_STEP_COUNTER");
        sensorTypes.put(Sensor.TYPE_STEP_DETECTOR,"TYPE_STEP_DETECTOR");
        sensorTypes.put(Sensor.TYPE_TEMPERATURE, "TYPE_TEMPERATURE(deprecated)");//Sensor.TYPE_AMBIENT_TEMPERATURE
    }

    private void showSensorList(){
        showInfo("本设备具有以下传感器：");

        //首先获取传感器管理器
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取设备的传感器对象列表
        List<Sensor> list=mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //显示传感器对象的信息。
        // 同样的传感器类型，不同厂家不同型号会有所不同，例如解析度不同，数值范围不同，功耗对应用性能重要，
        // 但是老实说这更多是OEM应该关心的问题，因为器件是他们选择的。对于应该如果发现功耗过大，就需要尽量避免长时间使用。
        for(Sensor sensor : list){
            showInfo("名字：" + sensor.getName());
            showInfo("  type:" + sensorTypes.get(sensor.getType()) + "(" + sensor.getType() +")");
            showInfo("  vendor:" + sensor.getVendor());
            showInfo("  version:" + sensor.getVersion());
            showInfo("  resolution:" + sensor.getResolution());
            showInfo("  max range:" + sensor.getMaximumRange());
            showInfo("  power:" + sensor.getPower());
        }


    }

    private void showInfo(String info){
        tv.append("\n" + info);
        Log.d("SensorList",info);
    }
}
