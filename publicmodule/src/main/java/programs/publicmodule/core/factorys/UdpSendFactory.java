package programs.publicmodule.core.factorys;

import programs.publicmodule.core.impls.UdpSendImpl;
import programs.publicmodule.core.interfaces.IUdpSend;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class UdpSendFactory {

    public static IUdpSend udpSend(){
        return new UdpSendImpl();
    }

}
