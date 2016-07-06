package com.ld.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * ����: app��װ����
 * @author �⴫�� Email:andywuchuanlong@sina.cn QQ: 3026862225
 * @version ����ʱ��: 2015��3��6�� ����3:51:14
 * @version ����޸�ʱ��:2015��3��6�� ����3:51:14 �޸���:�⴫��
 */
public class ApkController {
	/**
	 * ����: ��װ �޸���: �¼̿� ����޸�ʱ��:2016-7-5
	 */
	public static boolean install(String apkPath, Context context) {
		// ���ж��ֻ��Ƿ���rootȨ��
		if (hasRootPerssion()) {
			// ��rootȨ�ޣ����þ�Ĭ��װʵ��
			return clientInstall(apkPath);
		} else {
			// û��rootȨ�ޣ�������ͼ���а�װ
			File file = new File(apkPath);
			if (!file.exists())
				return false;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
	}

	/**
	 * ����: ж�� �޸���: �⴫�� ����޸�ʱ��:2015��3��8�� ����9:07:50
	 */
	public static boolean uninstall(String packageName, Context context) {
		if (hasRootPerssion()) {
			// ��rootȨ�ޣ����þ�Ĭж��ʵ��
			return clientUninstall(packageName);
		} else {
			Uri packageURI = Uri.parse("package:" + packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					packageURI);
			uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(uninstallIntent);
			return true;
		}
	}

	/**
	 * �ж��ֻ��Ƿ���rootȨ��
	 */
	private static boolean hasRootPerssion() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * ��Ĭ��װ
	 */
	private static boolean clientInstall(String apkPath) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("chmod 777 " + apkPath);
			PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			PrintWriter.println("pm install -r " + apkPath);
			startApp("com.example.tw", "com.example.tw.MainActivity");
			// PrintWriter.println("exit");
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();

			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * ��Ĭж��
	 */
	private static boolean clientUninstall(String packageName) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
			PrintWriter.println("pm uninstall " + packageName);
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * ����app com.exmaple.client/.MainActivity
	 * com.exmaple.client/com.exmaple.client.MainActivity
	 */
	public static boolean startApp(String packageName, String activityName) {
		boolean isSuccess = false;
		String cmd = "am start -n " + packageName + "/" + activityName;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return isSuccess;
	}

	private static boolean returnResult(int value) {
		// ����ɹ�
		if (value == 0) {
			return true;
		} else if (value == 1) { // ʧ��
			return false;
		} else { // δ֪���
			return false;
		}
	}
}