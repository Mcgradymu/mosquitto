package com.example.tw;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.ld.Util.ApkController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button  mbtn;
	private final int INSTALLAPK = 2;
	private final int UPDATE = 1;
	private final int LOCAL_VERSION_IS_NEW = 0;
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
		 int i = msg.what;
		 switch (i) {
		case UPDATE:
			Bundle bundle = msg.getData();
			String downloadPath = bundle.getString("url");
			Log.i("version","handler的apkUrl---"+downloadPath);
			ShowUpdateDialoag(downloadPath);
			break;
		case LOCAL_VERSION_IS_NEW:
			Toast.makeText(getApplicationContext(), "当前版本是最新的", 0).show();
			break;
		case INSTALLAPK:
			File file = new File(Environment.getExternalStorageDirectory(),"RemoteUpdates.apk");
			// 静默安装
			String path = Environment.getExternalStorageDirectory()+File.separator+"RemoteUpdates.apk";
			boolean install = ApkController.install(path,getApplicationContext());
			if(install == true){
				Toast.makeText(getApplicationContext(), "安装成功", 0).show();
				
			}
			//installApk(file);
		default:
			break;
		}		 
	 }
/**
 * @author cjk
 * @category:展示对话框
 ***/
		private void ShowUpdateDialoag(final String downloadPath) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builer = new Builder(MainActivity.this);
			builer.setTitle("版本升级");
			builer.setMessage("检测到新版本，更新？");
			 //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
			builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Log.i("version", "下载apk,更新");
					try {
						new Thread(new Runnable() {							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									download(downloadPath);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Log.e("version", "error");
								}
							}
						}).start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//do sth
				}
			});
			AlertDialog dialog = builer.create();
			dialog.show();
		}
		
 };
	private void download(String downloadPath) throws Exception {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		// TODO Auto-generated method stub
		Log.i("version",downloadPath);
		Log.i("version","url_apk_local---"+Environment.getExternalStorageDirectory());
		URL url = new URL(downloadPath);
		
		HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		//获取到文件的大小 
		//pd.setMax(conn.getContentLength());
		InputStream is = conn.getInputStream();
		File file = new File(Environment.getExternalStorageDirectory(), "RemoteUpdates.apk");
		FileOutputStream fos = new FileOutputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buffer = new byte[1024];
		int len ;
		int total=0;
		while((len =bis.read(buffer))!=-1){
			fos.write(buffer, 0, len);
			total+= len;
			//获取当前下载量
			//pd.setProgress(total);
		}
		fos.close();
		bis.close();
		is.close();
		Message msg = new Message();
		msg.what = INSTALLAPK;
		handler.sendMessage(msg);
	}
	else{
		
	}
}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mbtn = (Button) findViewById(R.id.btn_version);
		checkVersionCode();
	}



	private void checkVersionCode() {
		// TODO Auto-generated method stub
		mbtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {					
					private URL url;
					@Override
					public void run() {
						// TODO Auto-generated method stub
					   try {
						url = new URL("http://192.168.1.166:8080/myservice/version.json");
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(5000);
						connection.setRequestMethod("GET");
						if (connection.getResponseCode() == 200) {
						InputStream iStream = connection.getInputStream();
						BufferedReader read = new BufferedReader(new InputStreamReader(iStream));
						String line ;
						StringBuffer json = new StringBuffer();
						while((line = read.readLine())!=null){
							json.append(line);
						}
						Log.i("inputstream", json.toString());
						JSONObject jsonObject = new JSONObject(json.toString());	
						String versionCode= jsonObject.getString("versionCode");
						String versionName = jsonObject.getString("versionName");
						String apkUrl = jsonObject.getString("apkUrl");
						Log.i("version","服务器获取的apk路径----"+apkUrl);
						String pkName = getApplication().getPackageName();
						String local_versionName = getApplication().getPackageManager().getPackageInfo(pkName, 0).versionName;
		                int local_versionCode = getApplication().getPackageManager().getPackageInfo(pkName, 0).versionCode;          
		                if(Integer.valueOf(local_versionCode)<Integer.valueOf(versionCode)){
		                	 Bundle data = new Bundle();
//		                	 data.putChar("url", apkUrl);
		                	 data.putString("url", apkUrl); 
		                	 Message message = new Message();
		                	 message.what=UPDATE;
		                	 message.setData(data);
		                	 handler.sendMessage(message);
		                }
		                else if (Integer.valueOf(local_versionCode)==Integer.valueOf(versionCode)){
		                	 Message message = new Message();
		                	 message.what=LOCAL_VERSION_IS_NEW;
		                	 handler.sendMessage(message);
		                }
						Log.i("version", "versionCode------"+versionCode+"versionName-----"+versionName);
						}
					   } catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	 catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					   catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}).start();
			}
		});
	}

	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    //执行动作  
	    intent.setAction(Intent.ACTION_VIEW);  
	    //执行的数据类型  
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
	    startActivity(intent);  
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
}
