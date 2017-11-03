package programs.publicmodule.core.impls;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import programs.publicmodule.core.abstracts.AbstractSendHeartBeat;
import programs.publicmodule.core.entity.HeartBeatPackEntity;
import programs.publicmodule.core.factorys.UdpSendFactory;
import programs.publicmodule.core.interfaces.ISendHeartBeat;

/**
 * Created by caijiang.chen on 2017/11/3.
 */

public class SendHeartBeat extends AbstractSendHeartBeat implements ISendHeartBeat {

    @Override
    public void sendHeartBeat(DatagramSocket datagramSocket,HeartBeatPackEntity entity) {
        try {
            UdpSendFactory.udpSend().sendUdpPack(datagramSocket,getHostIp(),getHostPort(),getHeartBeatXml(entity));
        } catch (IOException e) {
            Log.e("SendHeartBeat","sendHeartBeat error");
            e.printStackTrace();
        }
    }

}
