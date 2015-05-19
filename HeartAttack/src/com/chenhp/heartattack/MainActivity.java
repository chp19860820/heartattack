package com.chenhp.heartattack;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String username = null;
	private String password = null;

	private Button loginBtn = null;
	private Button quitBtn = null;
	private EditText eUsername = null;
	private EditText ePassword = null;

	/** 登录loading提示框 */
	private ProgressDialog proDialog;
	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	/** 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */
	private boolean isNetError;
	/** 用来操作SharePreferences的标识 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loginBtn = (Button) findViewById(R.id.BTN_login);
		loginBtn.setOnClickListener(new LoginClick());
		quitBtn = (Button) findViewById(R.id.BTN_exit);
		quitBtn.setOnClickListener(new ExitClick());
		eUsername = (EditText) findViewById(R.id.ET_username);
		ePassword = (EditText) findViewById(R.id.ET_password);
		initView(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class LoginClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			System.out.println("hello body");
			proDialog = ProgressDialog.show(MainActivity.this, "连接中..",
					"连接中..请稍后....", true, true);
			// 开一个线程进行登录验证,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
		}
	}
	private boolean login(String username,String password){
		HttpURLConnection conn = null;
		try {
			URL url = new URL("http://172.18.8.254:8080/portal/index_default.jsp");
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.connect();
			System.out.println("conn.getResponseCode():" + conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.d(this.toString(),
						"getResponseCode() not HttpURLConnection.HTTP_OK");
				isNetError = true;
				return false;
			}else{
				HttpRequest.heartAttack(username, password);
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			isNetError = true;
			Log.d(this.toString(), e.getMessage() + "  127 line");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return false;
	}
	class ExitClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			finish();
		}
	}

	/** 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面 */
	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			String retmsg = msg.getData().getString("msg");
			
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if(retmsg != null && !"".equals(retmsg)){
				Toast.makeText(MainActivity.this,
						retmsg, Toast.LENGTH_SHORT)
						.show();
			}else if (isNetError) {
				Toast.makeText(MainActivity.this,
						"登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this, "登陆失败,请输入正确的用户名和密码!",
						Toast.LENGTH_SHORT).show();
				// 清除以前的SharePreferences密码
				clearSharePassword();
			}
		}
	};

	/** 清除密码 */
	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
	}
	/**
	 * 初始化界面
	 * 
	 * @param isRememberMe
	 *            如果当时点击了RememberMe,并且登陆成功过一次,则saveSharePreferences(true,ture)后,则直接进入
	 * */
	private void initView(boolean isRememberMe) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		String password = share.getString(SHARE_LOGIN_PASSWORD, "");
		Log.d(this.toString(), "userName=" + userName + " password="
						+ password);
		if (!"".equals(userName)) {
			eUsername.setText(userName);
		}
		if (!"".equals(password)) {
			ePassword.setText(password);
		}
		share = null;
	}
	class LoginFailureHandler implements Runnable {
		@Override
		public void run() {
			username = eUsername.getText().toString();
			password = ePassword.getText().toString();
			boolean loginState = login(username, password);
			Log.d(this.toString(), "validateLogin");
			if(username==null || "".equals(username)||password==null || "".equals(password)){
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("msg", "登陆帐户或密码为空！");
				message.setData(bundle);
				loginHandler.sendMessage(message);
				return;
			}
			// 登陆成功
			if (loginState) {
				// 需要传输数据到登陆后的界面,
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, IndexPage.class);
				Bundle bundle = new Bundle();
				bundle.putString("MAP_USERNAME", username);
				intent.putExtras(bundle);
				// 转向登陆后的页面
				startActivity(intent);
				proDialog.dismiss();
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				loginHandler.sendMessage(message);
			}
		}

	}
}
