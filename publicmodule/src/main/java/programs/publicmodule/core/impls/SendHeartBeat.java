package programs.publicmodule.core.impls;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramSocket;

import programs.publicmodule.core.abstracts.AbstractSendPack;
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
        sb.append("<p><h><pid>").append(entity.getPid()).append("</pid>");
        sb.append("<a>").append(entity.getA()).append("</a>");
        sb.append("<v>").append(entity.getV()).append("</v>");
        sb.append("<t>").append(entity.getT()).append("</t>");
        sb.append("<acount>").append(entity.getAcount()).append("</acount>");
        sb.append("<dt>").append(entity.getDt()).append("</dt>");
        sb.append("<dn>").append(entity.getDn()).append("</dn>");
        sb.append("<did>").append(entity.getDid()).append("</did>");
        sb.append("<time>").append(entity.getTime()).append("</time></h>");
        sb.append("<b><msg><![CDATA[").append(entity.getMsg()).append("]]></msg>");
        sb.append("<loc><lt>").append(entity.getLt()).append("</lt>");
        sb.append("<lat>").append(entity.getLat()).append("</lat>");
        sb.append("<lng>").append(entity.getLng()).append("</lng>");
        sb.append("<addr>").append(entity.getAddr()).append("</addr></loc></b></p>");
        return sb.toString();
    }

    private String getHeartBeatJason(SendPackEntity entity){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"packid\":"+"\""+entity.getPid()+"\"");
        sb.append("}");
        return sb.toString();
    }

}
