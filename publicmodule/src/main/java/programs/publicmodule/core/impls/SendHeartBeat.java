package programs.publicmodule.core.impls;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import programs.publicmodule.core.abstracts.AbstractSendPack;
import programs.publicmodule.core.entity.HeartBeatPackEntity;
import programs.publicmodule.core.entity.SendPackEntity;
import programs.publicmodule.core.factorys.UdpSendFactory;
import programs.publicmodule.core.interfaces.ISendHeartBeat;

/**
 * Created by caijiang.chen on 2017/11/3.
 */

public class SendHeartBeat extends AbstractSendPack implements ISendHeartBeat {

    @Override
    public void sendHeartBeat(DatagramSocket datagramSocket,SendPackEntity entity) {
        try {
            UdpSendFactory.udpSend().sendUdpPack(datagramSocket,getHostIp(),getHostPort(),getHeartBeatXml(entity));
        } catch (IOException e) {
            Log.e("SendHeartBeat","sendHeartBeat error");
            e.printStackTrace();
        }
    }

    private String getHeartBeatXml(SendPackEntity entity){
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    private String getHeartBeatJason(SendPackEntity entity){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"packid\":"+"\""+entity.getPackid()+"\"");
        sb.append("}");
        return sb.toString();
    }

}
