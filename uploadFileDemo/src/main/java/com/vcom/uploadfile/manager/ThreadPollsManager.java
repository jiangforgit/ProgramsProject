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
	
	private static final int corePoolSize = 10;//线程池维护线程的最少数量 
	private static final int maximumPoolSize = 10;//线程池维护线程的最大数量 
	private static final long keepAliveTime = 10;//线程池维护线程所允许的空闲时间 
	public static final int QueueMaxSize = 50;
	
	private static ThreadPollsManager instance;
	//线程阻塞队列
	public static BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(QueueMaxSize);
	//线程池
	private static ThreadPoolExecutor ThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,blockingQueue);
	
	public static ThreadPollsManager getInstance(){
		if(instance == null){
			instance = new ThreadPollsManager();
		}
		return instance;
	}
	
	//使用线程池开启线程执行
	public void executeThread(Thread thread){
		Log.i(Tag, "当前正在执行线程数="+ThreadPool.getActiveCount());
		Log.i(Tag, "当前线程队列size="+ThreadPool.getQueue().size());
		if(ThreadPool.getQueue().size() >= QueueMaxSize){
			Log.i(Tag, "线程队列已满");
		}else{
			Log.i(Tag, "添加一个执行线程");
			ThreadPool.execute(thread);
		}
	}
	
	//获取线程池等待队列任务数
	public int getPoolQueNum(){
		return ThreadPool.getQueue().size();
	}
	
	//当前正在执行线程数
	public int getPoolActiveNum(){
		return ThreadPool.getActiveCount();
	}
	
	//停止所有正在运行中的线程
	public void shutDownRunningThread(){
		ThreadPool.shutdownNow();
		Log.i("Tag", "停止所有正在运行中的线程");
	}
}
