package programs.publicmodule.core.interfaces;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public interface IUdpSend {

    void sendUdpPack(DatagramSocket socket,String host,int port,String pack) throws IOException;

}
