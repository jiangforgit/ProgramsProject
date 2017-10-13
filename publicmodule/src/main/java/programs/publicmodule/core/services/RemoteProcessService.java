package programs.publicmodule.core.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import programs.publicmodule.AIDLProcessKeep;

public class RemoteProcessService extends Service {
    private final String Tag = "RemoteProcessService";
    private RemoteServiceBinder binder;
    private AIDLProcessKeep coreMainService;
    private RemoteServiceConnection serviceConnection;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new RemoteServiceBinder();
        repeatingMainService();
    }

    private void repeatingMainService(){
        PendingIntent sender = PendingIntent.getService(this, 0, new Intent(this, CoreMainService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(), 60 * 1000, sender);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(null == serviceConnection){
            serviceConnection = new RemoteServiceConnection();
        }
        this.bindService(new Intent(this,CoreMainService.class),serviceConnection,Context.BIND_IMPORTANT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /********************Service Binder************/
    class RemoteServiceBinder extends AIDLProcessKeep.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void unbindAndStopRemote() throws RemoteException {
            Log.i(Tag,"unbindAndStopRemote");
            unbindService(serviceConnection);
            PendingIntent sender = PendingIntent.getService(RemoteProcessService.this, 0, new Intent(RemoteProcessService.this, CoreMainService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) RemoteProcessService.this.getSystemService(Activity.ALARM_SERVICE);
            am.cancel(sender);
            coreMainService.unbindAndStopRemote();
        }
    }

    /********************Service Connection************/
    class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            coreMainService = AIDLProcessKeep.Stub.asInterface(iBinder);
            Log.i(Tag,"connect CoreMain service success");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            RemoteProcessService.this.startService(new Intent(RemoteProcessService.this,CoreMainService.class));
            RemoteProcessService.this.bindService(new Intent(RemoteProcessService.this,CoreMainService.class),serviceConnection, Context.BIND_IMPORTANT);
            Log.i(Tag,"Coremain service is killed");
        }
    }
}
