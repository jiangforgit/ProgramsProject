package programs.publicmodule.core.services;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import programs.publicmodule.core.entity.ReceivedDataEntity;
import programs.publicmodule.core.enums.EnumDataPackType;
import programs.publicmodule.core.exceptions.UnknownReceivedDataException;
import programs.publicmodule.core.factorys.ReceivedDataFactory;
import programs.publicmodule.core.impls.ReceivedDataBusiVisitor;
import programs.publicmodule.core.impls.ReceivedDataOrderVisitor;
import programs.publicmodule.core.impls.ReceivedDataPosVisitor;
import programs.publicmodule.core.impls.ReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataSubject;
import programs.publicmodule.core.interfaces.IReceivedDataVisitor;
import programs.publicmodule.core.threadpool.PublicThreadPool;

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
            receivedData(data);
        }
    }

    private void receivedData(final String data) {
        PublicThreadPool.getPool().getSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    analyAndInsertData(data);
                } catch (UnknownReceivedDataException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void analyAndInsertData(String data) throws UnknownReceivedDataException{
        //采用模板方法模式，根据接收的数据采取对应的模板解析
        Object receivedData = ReceivedDataFactory.analysor().analyse(data);
        //采用访问者模式处理解析后的数据对象（不同的数据类型有不同的访问者）
        if(receivedData instanceof ReceivedDataEntity){
            String orderType = ((ReceivedDataEntity) receivedData).getDataType();
            IReceivedDataVisitor visitor = null;
            if(EnumDataPackType.pos.toString().equals(orderType)){
                visitor = new ReceivedDataPosVisitor();
            }else if(EnumDataPackType.order.toString().equals(orderType)){
                visitor = new ReceivedDataOrderVisitor();
            }else if(EnumDataPackType.busi.toString().equals(orderType)){
                visitor = new ReceivedDataBusiVisitor();
            }
            IReceivedDataSubject<ReceivedDataEntity> subject = new ReceivedDataSubject<ReceivedDataEntity>((ReceivedDataEntity)receivedData);
            if(null != visitor){
                subject.accept(visitor);
            }
        }else {
            throw new UnknownReceivedDataException("UnknownReceivedDataException");
        }
    }
}
