package programs.publicmodule.core.impls;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import programs.publicmodule.core.abstracts.AbstractSendPack;
import programs.publicmodule.core.entity.HeartBeatPackEntity;
import programs.publicmodule.core.factorys.UdpSendFactory;
import programs.publicmodule.core.interfaces.ISendHeartBeat;

/**
 * Created by caijiang.chen on 2017/11/3.
 */

public class SendHeartBeat extends AbstractSendPack implements ISendHeartBeat {

    @Override
    public void sendHeartBeat(DatagramSocket datagramSocket,HeartBeatPackEntity entity) {
        try {
            UdpSendFactory.udpSend().sendUdpPack(datagramSocket,getHostIp(),getHostPort(),getHeartBeatXml(entity));
        } catch (IOException e) {
            Log.e("SendHeartBeat","sendHeartBeat error");
            e.printStackTrace();
        }
    }

    private String getHeartBeatXml(HeartBeatPackEntity entity){
        StringBuilder sb = new StringBuilder();
        sb.append("<T>" + "<T_H>" + "<requester>" + entity.getRequester()+ "</requester>" + "<version>1.4</version>"
                + "<category>" + entity.getCategory() + "</category>"+ "<mac />" + "<packetid>" + entity.getPacketid()+ "</packetid>" + "</T_H>");

        sb.append("<T_B>" + "<item cmd=" + "'"+ entity.getCmd() + "'" + " user_id=" + "'"+ entity.getUserid() + "'"
                +" mobile=" + "'"+ entity.getMobile() + "'");

//        if(isPermitLocation){//允许定位
//            sb.append(" lat=" + "'" + entity.getLat()+"'" +" lng=" + "'" + entity.getLng() + "'" + " loc="+ "'" + entity.getLoc() + "'" + " radius="+"'"+entity.getRadius()+ "'" + " flag="+"'"+entity.getFlag()+"'" );
//        }else{
        sb.append(" lat=" + "'" + ""+"'" +" lng=" + "'" + "" + "'" + " loc="+ "'" + "" + "'" + " radius="+"'"+""+ "'" + " flag="+"'"+"keep_live"+"'" );
//        }

        sb.append(" udid=" + "'"+ entity.getImei() + "'" +  " cversion=" + "'" + entity.getVersion() + "'" + " time=" + "'" + entity.getTime()+ "'" + "/>"
                + "</T_B>" + "</T>");
        return sb.toString();
    }

    private String getHeartBeatJason(HeartBeatPackEntity entity){
        return "";
    }

}
