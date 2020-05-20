package com.example.sensortest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VirtualJax extends AppCompatActivity implements SensorEventListener {

//    private ToggleButton toggleButton = null;//一个可选的按钮选项
    private TextView oldOne = null, nowOne = null;//两种不同的方法获得方向数据
    private SensorManager sensorManager = null;
    private Sensor accelSensor = null, compassSensor = null, orientSensor = null, rotVecSensor = null;
    //定义数组将方向传感器的数据放于数组中
    private float[] accelValues = new float[3], compassValues = new float[3], orientValues = new float[3], rotVecValues = null;
    private int mRotation;
    private LocationManager locManager = null;

    @SuppressWarnings("deprecation")//表示不检测过期的方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_jax);//跟之前的几个activity不同，这里定义了新的activity

        //返回主页
        Button bt=(Button) findViewById(R.id.Back_to_Home);
        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VirtualJax.this,MainActivity.class));
            }
        });

        oldOne = (TextView) findViewById(R.id.orientation);//采用orientation传感器
        nowOne = (TextView) findViewById(R.id.preferred);//推荐的方式
//        toggleButton = (ToggleButton) findViewById(R.id.toggle);

        //定义传感器管理器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //定义四个传感器
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//地磁传感器
        orientSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//方位传感器
        rotVecSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);//旋转角度传感器？？？


//        //当横屏的时候应该对坐标进行修订
//        WindowManager window = (WindowManager) this.getSystemService(WINDOW_SERVICE);
//        //检验当前的版本号
//        if (Build.VERSION.SDK_INT < 8)
//            mRotation = window.getDefaultDisplay().getOrientation();
//        else
//            mRotation = window.getDefaultDisplay().getRotation();
//
//        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }


    //在onResume()注册监听器，在onPause()中注销监听器
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
//        isAllowRemap = toggleButton.isChecked();
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, orientSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rotVecSensor, SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        sensorManager.unregisterListener(this, accelSensor);
        sensorManager.unregisterListener(this, compassSensor);
        sensorManager.unregisterListener(this, orientSensor);
        sensorManager.unregisterListener(this, rotVecSensor);
        super.onPause();
    }




    ///////////////////////////////////////////////////////********************************//////////////////////////////
    private boolean ready = false; //检查是否同时具有加速度传感器和磁场传感器
    private float[] inR = new float[9], outR = new float[9];
    private float[] inclineMatrix = new float[9];
    private float[] prefValues = new float[3];
    private double mInclination;
    private int count = 1;
    private float[] rotvecR = new float[9], rotQ = new float[4];
    private float[] rotvecOrientValues = new float[3];

    @SuppressWarnings("deprecation")//表示不检测过期的方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //将相关传感器的数值分别读入accelValues，compassValues（磁力感应器的数值）和orientValues和rotVecValues数组中
        switch (event.sensor.getType()) {//获取传感器的类型
            case Sensor.TYPE_ACCELEROMETER://当时加速度传感器时
                for (int i = 0; i < 3; i++) {
                    accelValues[i] = event.values[i];//将三个值分别放于accelValues中
                }
                if (compassValues[0] != 0) //即accelerator和magnetic传感器都有数值
                    ready = true;//此时检测同时具有加速度传感器与地磁传感器
                break;

            case Sensor.TYPE_MAGNETIC_FIELD://获取地磁传感器的值
                for (int i = 0; i < 3; i++) {
                    compassValues[i] = event.values[i];//将三个值分别放于compassValues中
                }
                if (accelValues[2] != 0) //即accelerator和magnetic传感器都有数值，换一个轴向检查
                    ready = true;//此时检测同时具有加速度传感器与地磁传感器
                break;

            case Sensor.TYPE_ORIENTATION://如果是方向传感器
                for (int i = 0; i < 3; i++) {
                    orientValues[i] = event.values[i];//将三个值分别放于orientValues中
                }
                break;

            case Sensor.TYPE_ROTATION_VECTOR://对于旋转传感器
                if (rotVecValues == null) {
                    rotVecValues = new float[event.values.length];
                }
                for (int i = 0; i < rotVecValues.length; i++) {
                    rotVecValues[i] = event.values[i];
                }
                break;
        }

        if (!ready)//此时如果没有有加速度与地磁传感器，则退出返回
            return;

        //计算:inclination matrix 倾角矩阵 I(inclineMatrix) 以及 the rotation matrix 旋转矩阵 R(inR)
        //根据加速传感器的数值accelValues[3]和磁力感应器的数值compassValues[3]，进行矩阵计算，获得方位
        if (SensorManager.getRotationMatrix(inR, inclineMatrix, accelValues, compassValues)) {

            //下面是旋转屏幕的情况，此处不用
//            if (isAllowRemap && mRotation == Surface.ROTATION_90) {
//                //参数二表示设备X轴成为新坐标的Y轴，参数三表示设备的Y轴成为新坐标-x轴（方向相反）
//                SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
//                SensorManager.getOrientation(outR, prefValues);
//            } else {

				/* Computes the device's orientation based on the rotation matrix.
				 * 	When it returns, the array values is filled with the result:
				 * 根据rotation matrix计算设备的方位。，范围数组：
				values[0]: azimuth, rotation around the Z axis.
				values[1]: pitch, rotation around the X axis.
				values[2]: roll, rotation around the Y axis.*/
                SensorManager.getOrientation(inR, prefValues);//根据rotation matrix计算设备的方位
//            }
            //根据inclination matrix计算磁仰角。
            //计算磁仰角：地球表面任一点的地磁场总强度的矢量方向与水平面的夹角。
            mInclination = SensorManager.getInclination(inclineMatrix);

            //显示测量值
            if (count++ % 100 == 0) {
                doUpdate(null);
                count = 1;
            }

        } else {
            Toast.makeText(this, "无法获得矩阵（SensorManager.getRotationMatrix）", Toast.LENGTH_LONG);
            finish();
        }

        if (rotVecValues != null) {
            SensorManager.getQuaternionFromVector(rotQ, rotVecValues);
            SensorManager.getRotationMatrixFromVector(rotvecR, rotVecValues);
            SensorManager.getOrientation(rotvecR, rotvecOrientValues);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    //定义一个函数来显示测量值
    public void doUpdate(View v) {
        if (!ready)
            return;

        //preValues[0]是方位角，范围是-pi到pi，通过Math.toDegrees转换为角度
        float mAzimuth = (float) Math.toDegrees(prefValues[0]);//方角位，地平经度
		/*//纠正为orientation的数值。
		 * if(mAzimuth < 0)
			mAzimuth += 360.0;*/


        String msg = String.format("Acceleration sensor + magnetic sensor：\nazimuth：%7.3f\npitch: %7.3f\nroll: %7.3f\n地磁仰角：%7.3f\n重适配坐标=%s\n%s\n",
                mAzimuth, Math.toDegrees(prefValues[1]), Math.toDegrees(prefValues[2]),
                Math.toDegrees(mInclination),
                (isAllowRemap && mRotation == Surface.ROTATION_90) ? "true" : "false", info);

        if (rotvecOrientValues != null && mRotation == Surface.ROTATION_0) {
            msg += String.format("Rotation Vector Sensor:\nazimuth %7.3f\npitch %7.3f\nroll %7.3f\nw,x,y,z %6.2f,%6.2f,%6.2f,%6.2f\n",
                    Math.toDegrees(rotvecOrientValues[0]),
                    Math.toDegrees(rotvecOrientValues[1]),
                    Math.toDegrees(rotvecOrientValues[2]),
                    rotQ[0], rotQ[1], rotQ[2], rotQ[3]);
            //Log.d("WEI","Quaternion w,x,y,z=" + rotQ[0] + "," + rotQ[1] + "," + rotQ[2] + "," + rotQ[3]);
        }
        nowOne.setText(msg);

        msg = String.format("Orientation Sensor：\nazimuth：%7.3f\npitch: %7.3f\nroll: %7.3f",
                orientValues[0], orientValues[1], orientValues[2]);
        oldOne.setText(msg);

    }

    private boolean isAllowRemap = false;

    public void doToggle(View v) {
        isAllowRemap = ((ToggleButton) v).isChecked();
    }

    private String info = "";

    public void doGeoNorth(View v) {
        if (!ready)
            return;

        String providerName = locManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = locManager.getLastKnownLocation(providerName);


        if(loc == null && locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //请注意，设备要打开网络定位的选项。在室内，由于不容易搜索到GPS，建议采用network方式。
            //否则，locManager.isProviderEnabled("network")为false，不能使用网络方式，而GPS在室内搜半天卫星都不一定有
            providerName = LocationManager.NETWORK_PROVIDER;
            loc = locManager.getLastKnownLocation(providerName);
        }
        if(loc == null)
            return;

        info = "定位："+ providerName+ "\n"+ String.format(" %9.5f,%9.5f",(float)loc.getLongitude(),(float)loc.getLatitude())+"\n";
        Log.d("WEI","" + loc);


        GeomagneticField geo = new GeomagneticField((float)loc.getLatitude(),(float)loc.getLongitude(),
                (float)loc.getAltitude(),System.currentTimeMillis());

        float declination = geo.getDeclination();
        info += String.format("磁偏角：%7.3f\n", declination);
    }


}

