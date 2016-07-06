package com.broastRecevice;



import com.ld.Util.ApkController;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class autoStartUpReceiver extends BroadcastReceiver {

	private String packageName;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Log.i("recevice", "123");
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
             packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
             packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
            if(packageName.equals("com.example.tw")){
            	ApkController.startApp(packageName,"com.example.tw.MainActivity");
            	ComponentName componentName = new ComponentName("com.example.tw","com.example.tw.MainActivity");
						Intent a = new Intent();
						a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						a.setComponent(componentName);
						//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(a);
						
						
						
            }
        }
        Log.i("recevice", packageName);
	}

}
