package programs.publicmodule.core.managers;

import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;

import programs.publicmodule.core.interfaces.ILocation;
import programs.publicmodule.core.interfaces.ILocationCallBack;

/**
 * Created by caijiang.chen on 2018/1/3.
 */

public class GPSLocationManager implements ILocation {

    private final String TAG = "GpsLocationManager";
    private Context context;
    private LocationManager locationManager;
    private ILocationCallBack gpsCallBack;

    public GPSLocationManager(Context cxt){
        context = cxt;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void addCallBackListener(ILocationCallBack callBack) {
        gpsCallBack = callBack;
    }

    @Override
    public void startLocation() throws SecurityException{
        locationManager.addGpsStatusListener(statusListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    @Override
    public void stopLocation() {
        if(null != locationManager){
            locationManager.removeGpsStatusListener(statusListener);
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
//            Log.i(TAG, "时间：" + location.getTime());
//            Log.i(TAG, "经度："+location.getLongitude());
//            Log.i(TAG, "纬度："+location.getLatitude());
//            Log.i(TAG, "海拔："+location.getAltitude());
            if(gpsCallBack != null){
                gpsCallBack.location(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),"");
            }
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
//                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
//                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "开启GPS");
            //Location location = locationManager.getLastKnownLocation(provider);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "禁用GPS");
        }
    };

    //状态监听
    GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) throws SecurityException{
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    Log.i(TAG, "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    Log.i(TAG, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
//                    Log.i(TAG, "搜索到：" + count + "颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
//                    Log.i(TAG, "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
//                    Log.i(TAG, "定位结束");
                    break;
            }
        };
    };

}
