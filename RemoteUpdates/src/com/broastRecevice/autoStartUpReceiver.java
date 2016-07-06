package com.broastRecevice;

import com.ld.Util.ApkController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class autoStartUpReceiver extends BroadcastReceiver {

	private String packageName;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		Log.i("recevice", "123");
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "��װ�ɹ�"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
             packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "ж�سɹ�"+packageName, Toast.LENGTH_LONG).show();
        }
        /**
         * ������������滻�ɹ��ķ���ֵ
         * 
         * **/
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
             packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "�滻�ɹ�"+packageName, Toast.LENGTH_LONG).show();
            if(packageName.equals("com.example.tw")){
            	ApkController.startApp(packageName,"com.example.tw.MainActivity");
            }
        }
        Log.i("recevice", packageName);
	}

}
