package programs.publicmodule.core.impls;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import programs.publicmodule.core.interfaces.IUdpSend;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class UdpSendImpl implements IUdpSend {
    @Override
    public void sendUdpPack(DatagramSocket socket, String host, int port, String pack) throws IOException {
        InetAddress serverAddress = InetAddress.getByName(host);
        byte[] bytesToSend = pack.getBytes();
        // sendSocket.setSoTimeout(TIME_OUT); // 设置阻塞时间
        DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length, serverAddress, port);
        socket.send(sendPacket);
        Log.i("UdpSendImpl","sendpack="+pack);
    }
}
