package com.example.ubitransport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    TextView show;
    TextView text;
    ArrayList speeds = new ArrayList();
    double avspeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show=(TextView) findViewById(R.id.show);
        text=(TextView) findViewById(R.id.textView);
        text.setText("Average Speed");
        show.setText("233");//no use, hahaha
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //startActivity(new Intent(this,MainActivity.class));
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //从GPS获取最近的定位信息
        Location location=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.i("LocationActivity", "location="+location);
        updateView(location);
        //不断获取GPS的定位信息
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) throws SecurityException{
                // 当GPS LocationProvider可用时，更新位置
                updateView(lm.getLastKnownLocation(provider));
            }
            @Override
            public void onProviderDisabled(String provider) throws SecurityException {
                updateView(lm.getLastKnownLocation(null));
            }
            @Override
            public void onLocationChanged(Location location) {
                //GPS定位信息发生改变时，更新位置
                updateView(location);
            }
        });
    // Example of a call to a native method
    //TextView tv = (TextView) findViewById(R.id.sample_text);
    //tv.setText(stringFromJNI());
    }
    public void updateView(Location newLocation){
        if(newLocation!=null){
            StringBuilder sb=new StringBuilder();
            sb.append("speed: ");
            double speed = newLocation.getSpeed();
            speed=(double)(Math.round(speed*100)/100.0);
            if(speed < 0.1){
                text.setText("Average Speed");
                if(speeds.isEmpty())
                    speed = avspeed;
                else{
                    avspeed = average();
                    avspeed=(double)(Math.round(avspeed*100)/100.0);
                    speed = avspeed;
                    speeds.clear();
                }
            }
            else{
                text.setText("Instant Speed");
                speeds.add(speed);
            }
            sb.append(speed);
            show.setText(sb.toString());
        }
        else{
            show.setText("fail");
        }
    }
    public double average(){
        double sum=0.0;
        for(int i=0;i<speeds.size();i++){
            double number=(double)speeds.get(i);
            sum = sum + number;
        }
        double res = sum/speeds.size();
        return res;
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
