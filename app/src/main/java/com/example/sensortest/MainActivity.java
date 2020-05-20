package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //inflate() 方法接收两个参数，
        //第一个参数用于指定我们通过哪一个资源文件来创建菜单，这里当然传入R.menu.menu_main 。
        //第二个参数用于指定我们的菜单项将添加到哪一个Menu 对象当中，这里直接使用onCreateOptionsMenu() 方法中传入的menu 参数。
        return super.onCreateOptionsMenu(menu);
    }

    //然后给这些按键定义内容（不同的按菜单按键启动不一样的activity）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch(item.getItemId()){
            //通过Intent来启动别的activity
            case R.id.test_sensor_list:
                startActivity(new Intent(this,SensorListActivity.class));
                break;
            case R.id.test_light_sensor:
                startActivity(new Intent(this,LightSensorActivity.class));
                break;
            case R.id.test_proximity_sensor:
                startActivity(new Intent(this,ProximitySensorActivity.class));
                break;
            case R.id.test_gyroscope_sensor:
//                startActivity(new Intent(this,GyroscopeSensorActivity.class));
                break;
            case R.id.test_accelerometer_sensor:
//                startActivity(new Intent(this,AccelerometerSensorActivity.class));
                break;
            case R.id.test_accelerometer_2_sensor:
//                startActivity(new Intent(this,GravityActivity.class));
                break;
            case R.id.test_magnetic_sensor:
//                startActivity(new Intent(this,MagneticFieldSensorActivity.class));
                break;
            case R.id.test_orientation:
//                startActivity(new Intent(this,VirtualJax.class));
                break;
            default:
                break;
        }
        return true;
    }


}
