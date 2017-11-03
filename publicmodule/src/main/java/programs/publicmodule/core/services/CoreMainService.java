package programs.publicmodule.core.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import programs.publicmodule.AIDLProcessKeep;
import programs.publicmodule.core.entity.HeartBeatPackEntity;
import programs.publicmodule.core.enums.EnumMainServiceCmd;
import programs.publicmodule.core.factorys.UdpSendFactory;
import programs.publicmodule.core.impls.SendHeartBeat;
import programs.publicmodule.core.interfaces.ISendHeartBeat;
import programs.publicmodule.core.threadpool.PublicThreadPool;

public class CoreMainService extends Service {

    private final String Tag = "CoreMainService";
    private MainServiceBinder binder;
    private AIDLProcessKeep remoteService;
    private MainServiceConnection serviceConnection;
    private DatagramSocket datagramSocket;
    private ReceiveDataThread receiveDataThread;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        binder = new MainServiceBinder();
    }

    private void initData(){
        initDataGramSocket();
        if(null == receiveDataThread){
            initReceiveThread();
        }
    }

    private void initDataGramSocket(){
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            Log.e(Tag,"dataGramSocket init error");
            e.printStackTrace();
        }
    }

    private void initReceiveThread(){
        receiveDataThread = new ReceiveDataThread(datagramSocket);
        receiveDataThread.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if(null == serviceConnection){
            serviceConnection = new MainServiceConnection();
        }
        this.bindService(new Intent(this,RemoteProcessService.class),serviceConnection, Context.BIND_IMPORTANT);
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Tag,"onStartCommand");
        if(null != intent){
            int cmd = intent.getIntExtra("cmd",-1);
            dealByCmd(cmd);
        }else {

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void dealByCmd(int cmd){
        if(cmd == EnumMainServiceCmd.Logout.getCmd()){//退出解绑指令
            if(null != remoteService){
                try {
                    Log.i(Tag,"发送unbind到remote");
                    remoteService.unbindAndStopRemote();
                }catch (RemoteException re){
                    re.printStackTrace();
                }
            }
        }else {
            if(null == datagramSocket || null == receiveDataThread || !receiveDataThread.isAlive()){
                initDataGramSocket();
                initReceiveThread();
            }
            PublicThreadPool.getPool().getSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ISendHeartBeat sendHeartBeat = new SendHeartBeat();
                    sendHeartBeat.sendHeartBeat(datagramSocket,new HeartBeatPackEntity());
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /********************Service Binder************/
   public class MainServiceBinder extends AIDLProcessKeep.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void unbindAndStopRemote() throws RemoteException {
            Log.i(Tag,"unbindAndStopRemote");
            unbindService(serviceConnection);
            stopService(new Intent(CoreMainService.this,RemoteProcessService.class));
            stopSelf();
        }
    }

    /********************Service Connection************/
    class MainServiceConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            remoteService = AIDLProcessKeep.Stub.asInterface(iBinder);
            Log.i(Tag,"connect remote service success");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            CoreMainService.this.startService(new Intent(CoreMainService.this,RemoteProcessService.class));
            CoreMainService.this.bindService(new Intent(CoreMainService.this,RemoteProcessService.class),serviceConnection,Context.BIND_IMPORTANT);
            Log.i(Tag,"remote service is killed");
        }
    }
}
