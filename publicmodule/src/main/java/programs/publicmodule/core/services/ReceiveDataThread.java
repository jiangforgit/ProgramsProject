package programs.publicmodule.core.services;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class ReceiveDataThread extends Thread {

    private final String TAG = "ReceiveDataThread";
    private DatagramSocket receiveSocket;

    public ReceiveDataThread(DatagramSocket ds){
        this.receiveSocket = ds;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            // 一次接收的内容的最大容量
            byte[] buf = new byte[1024];
            DatagramPacket datapack = new DatagramPacket(buf, buf.length);
            Log.i(TAG, "receiving-data……");
            // 接收数据包
            try {
                receiveSocket.receive(datapack);
            } catch (IOException e) {
                Log.e(TAG,"receiveSocket IO error");
                e.printStackTrace();
            }catch (Exception e){
                Log.e(TAG,"receiveSocket error");
                e.printStackTrace();
            }
            // 取得数据包里的内容
            String data = new String(datapack.getData(), 0, datapack.getLength());
            Log.i(TAG,"data="+data);
        }
    }
}
