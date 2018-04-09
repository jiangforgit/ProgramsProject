package programs.publicmodule.core.interfaces;

import java.net.DatagramSocket;

import programs.publicmodule.core.entity.SendPackEntity;

/**
 * Created by caijiang.chen on 2017/11/3.
 */

public interface ISendHeartBeat {

    void sendHeartBeat(DatagramSocket datagramSocket, SendPackEntity entity);

}
