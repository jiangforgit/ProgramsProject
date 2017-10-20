package programs.publicmodule.core.deals;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import programs.publicmodule.core.factorys.UdpSendFactory;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class CoreMainDeal {

    private final String Tag = "CoreMainDeal";

    private static CoreMainDeal instance;

    private CoreMainDeal(){

    }

    public static CoreMainDeal getInstance(){
        if(null == instance){
            instance = new CoreMainDeal();
        }
        return instance;
    }

    public void sendHeartBeatPack(DatagramSocket socket,String host,int port,String pack){
        try {
            UdpSendFactory.udpSend().sendUdpPack(socket,host,port,pack);
        } catch (IOException e) {
            Log.e(Tag,"sendHeartBeat error");
            e.printStackTrace();
        }
    }

    public synchronized void dealReceiveDataFromSocket(String data){
        synchronized (CoreMainDeal.class){

        }
    }
}
