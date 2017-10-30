package com.vcom.uploadfile.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import com.vcom.uploadfile.activity.XListView.IXListViewListener;
import com.vcom.uploadfile.db.WorkOrderIUDS;
import com.vcom.uploadfile.domain.FileUploadProgressInfo;
import com.vcom.uploadfile.interfaces.UploadFileService;
import com.vcom.uploadfile.manager.ThreadPollsManager;
import com.vcom.ycwl.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileUploadProgressActivity extends Activity implements IXListViewListener {

	private static final int pageCounts = 25;//每页显示的记录数
	private int loadCounts;//当前加载的记录数
	public static boolean isForeground = false;//activity的当前状态 
	
	private static final String Tag = "FileUploadProgressActivity";
	private TextView tv_file_upload_progress_tile;
	private ImageButton imgbt_file_upload_progress_back;
	private XListView lv_file_upload_progress_listview;
	
	private ListViewAdapter listViewAdapter;
	private Button reupload;
	private static List<FileUploadProgressInfo> infos;
	private static List<ViewHolder> viewHolders;
	
	public static Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_upload_progress);
		initView();
		initData();
		setListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isForeground = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isForeground = false;
	}

	//initview
	public void initView(){
		imgbt_file_upload_progress_back = (ImageButton)findViewById(R.id.imgbt_file_upload_progress_back);
		tv_file_upload_progress_tile = (TextView)findViewById(R.id.tv_file_upload_progress_tile);
		lv_file_upload_progress_listview = (XListView)findViewById(R.id.lv_file_upload_progress_listview);
		lv_file_upload_progress_listview.setPullLoadEnable(true);
		reupload = (Button)findViewById(R.id.bt_reupload);
	}
	
	public void initData(){
		loadCounts = 0;
		infos = new ArrayList<FileUploadProgressInfo>();
		viewHolders = new ArrayList<ViewHolder>();
		getInfos();
		listViewAdapter = new ListViewAdapter(this);
		lv_file_upload_progress_listview.setAdapter(listViewAdapter);
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what == 0){//接收到抛出的进度显示消息
					Bundle bundle = msg.getData();
					String fileName = bundle.getString("fileName");
					int startPosition = bundle.getInt("startPosition");
					int fileLength = bundle.getInt("fileLength");
					int result = isExistsInfo(fileName);
					if(result > -1){
						viewHolders.get(result).showLength.setText(getShowLength(startPosition,fileLength));
						viewHolders.get(result).showProgress.setText(String.valueOf(getShowProgress(startPosition,fileLength))+"%");
						viewHolders.get(result).progressBar.setProgress((int) getShowProgress(startPosition,fileLength));
						infos.get(result).setUploadedLength(startPosition);
					}else{
						loadCounts = 0;
						infos.clear();
						getInfos();
						refreshListView();
					}
				}
			}
		};
	}
	
	//get infos 
	public void getInfos(){
		WorkOrderIUDS iuds = new WorkOrderIUDS(this);
		List<FileUploadProgressInfo> list = iuds.getAllFileUploadProgressInfos(loadCounts, pageCounts);
		loadCounts += list.size();
		infos.addAll(list);
	}
	
	public void setListener(){
		imgbt_file_upload_progress_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FileUploadProgressActivity.this.finish();
			}
		});
		tv_file_upload_progress_tile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		reupload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UploadFileService.getInstance().reUploadFile(MyApplication.getInstance(),"test","com.vcom.uploadfile.UploadProgressReceiver","10086","2.0","jpg");
			}
		});
		lv_file_upload_progress_listview.setXListViewListener(this);
	}

	private void onLoad() {
		lv_file_upload_progress_listview.stopRefresh();
		lv_file_upload_progress_listview.stopLoadMore();
		lv_file_upload_progress_listview.setRefreshTime("刚刚");
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadCounts = 0;
				infos.clear();
				getInfos();
				refreshListView();
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getInfos();
				refreshListView();
				onLoad();
			}
		}, 2000);
	}
	
	//refresh listView
	public void refreshListView(){
		listViewAdapter.refresh();
	}
	
	//验证handler 接收到的文件名是否在 infos中存在 （有责刷新进度，没有责添加一条记录）
	public static int isExistsInfo(String fileName){
		int result = -1;
		for(int i=0;i<infos.size();i++){
			if(infos.get(i).getFileName().equals(fileName)){
				result = i;
				break;
			}
		}
		return result;
	}
	
	/********************************ListViewAdapter****************************/
	private class ListViewAdapter extends BaseAdapter{

		private Context context;
		
		public ListViewAdapter(Context cxt){
			context = cxt;
		}
		
		public void refresh(){
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return infos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = new ViewHolder();
			FileUploadProgressInfo info = new FileUploadProgressInfo();
			info = infos.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.file_upload_progress_listview_item, null);
				holder.showLength = (TextView)convertView.findViewById(R.id.tv_file_upload_progress_listview_item_showlength);
				holder.showProgress = (TextView)convertView.findViewById(R.id.tv_file_upload_progress_listview_item_showprogress);
				holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_file_upload_progress_listview_item_progressbar);
				holder.fileName = (TextView)convertView.findViewById(R.id.tv_file_upload_progress_listview_item_filename);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			if(info.getUploadStatus() == 2){//上传成功
				holder.showLength.setText(getShowLength(info.getFileLength(),info.getFileLength()));
				holder.showProgress.setText("100%");
				holder.progressBar.setProgress(100);
				holder.fileName.setText(info.getFileName());
				viewHolders.add(holder);
			}else{
				holder.showLength.setText(getShowLength(info.getUploadedLength(),info.getFileLength()));
				holder.showProgress.setText(String.valueOf(getShowProgress(info.getUploadedLength(), info.getFileLength()))+"%");
				holder.progressBar.setProgress((int) getShowProgress(info.getUploadedLength(),info.getFileLength()));
				holder.fileName.setText(info.getFileName());
				viewHolders.add(holder);
			}
			return convertView;
		}
	}
	
	//get showlength 
	public static String getShowLength(int loadedLen,int fileLength){
		int filePacketges = fileLength%1024==0?fileLength/1024:(fileLength/1024)+1;
		int loadedLength = loadedLen > filePacketges?filePacketges:loadedLen;
		return String.valueOf(loadedLength)+"k/"+String.valueOf(filePacketges)+"k";
	}
	
	//get showProgress
	public static float getShowProgress(int loadedLen,int fileLength){
		float filePacketges = fileLength%1024==0?fileLength/1024:(fileLength/1024)+1;
		float loadedLength = loadedLen > filePacketges?filePacketges:loadedLen;
		return (float)loadedLength/(float)filePacketges*100;
	}

	/*******************************ViewHolder*******************************/
	private final class ViewHolder{
		public TextView showLength;
		public TextView showProgress;
		public ProgressBar progressBar;
		public TextView fileName;
	}

}
