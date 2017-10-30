package com.vcom.uploadfile.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class ThreadPollsManager {

	private static final String Tag = "ThreadPollsManager";
	
	private static final int corePoolSize = 10;//�̳߳�ά���̵߳��������� 
	private static final int maximumPoolSize = 10;//�̳߳�ά���̵߳�������� 
	private static final long keepAliveTime = 10;//�̳߳�ά���߳�������Ŀ���ʱ�� 
	public static final int QueueMaxSize = 50;
	
	private static ThreadPollsManager instance;
	//�߳���������
	public static BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(QueueMaxSize);
	//�̳߳�
	private static ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,blockingQueue);
	
	public static ThreadPollsManager getInstance(){
		if(instance == null){
			instance = new ThreadPollsManager();
		}
		return instance;
	}
	
	//ʹ���̳߳ؿ����߳�ִ��
	public void executeThread(Thread thread){
		Log.i(Tag, "��ǰ����ִ���߳���="+ThreadPool.getActiveCount());
		Log.i(Tag, "��ǰ�̶߳���size="+ThreadPool.getQueue().size());
		if(ThreadPool.getQueue().size() >= QueueMaxSize){
			Log.i(Tag, "�̶߳�������");
		}else{
			Log.i(Tag, "���һ��ִ���߳�");
			ThreadPool.execute(thread);
		}
	}
	
	//��ȡ�̳߳صȴ�����������
	public int getPoolQueNum(){
		return ThreadPool.getQueue().size();
	}
	
	//��ǰ����ִ���߳���
	public int getPoolActiveNum(){
		return ThreadPool.getActiveCount();
	}
	
	//ֹͣ�������������е��߳�
	public void shutDownRunningThread(){
		ThreadPool.shutdownNow();
		Log.i("Tag", "ֹͣ�������������е��߳�");
	}
}
