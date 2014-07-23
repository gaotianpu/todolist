package com.gaotianpu.ftodo.ui;

import java.io.IOException;

import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R;

import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.ui.ItemDetailActivity.DetailReadFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingDetailActivity extends Activity {

	public static final String SETTING_ITEM_ID = "SETTING_ITEM_ID";
	public static final String SETTING_ITEM_TITLE = "SETTING_ITEM_TITLE";

	private static Activity act;
	private static MyApplication app;
	private static UserBean user;
	private static FTDClient ftd;

	private String setting_item_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_detail);

		// 0.外部传入参数
		Intent intent = getIntent();
		setting_item_id = intent.getStringExtra(SETTING_ITEM_ID);

		this.setTitle(intent.getStringExtra(SETTING_ITEM_TITLE));

		// 1 全局
		act = this;
		app = (MyApplication) getApplicationContext();
		user = app.getUser();
		ftd = new FTDClient(act);

		if (savedInstanceState == null) {
			Fragment f;
			if (setting_item_id.equals("mobile")) {
				f = new MobileFragment();
			} else if (setting_item_id.equals("password")) {
				f = new PasswordFragment();
			} else if (setting_item_id.equals("email")) {
				f = new EmailFragment();
			} else { // about
				f = new AboutFragment();
			}

			getFragmentManager().beginTransaction().replace(R.id.container, f)
					.commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// //goback?
			Runtime runtime = Runtime.getRuntime();
			try {
				// Log.i("back", String.valueOf(item.getItemId()));
				runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("goback", e.toString());
				e.printStackTrace();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public static class MobileFragment extends Fragment {
		private EditText txtMobile;
		private EditText txtSmsCode;
		private Button btnGetSmsCode;
		private Button btnPost;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.setting_mobile,
					container, false);

			txtMobile = (EditText) rootView.findViewById(R.id.txtMobile);
			txtMobile.setInputType(EditorInfo.TYPE_CLASS_PHONE);  
			
			txtSmsCode = (EditText) rootView.findViewById(R.id.txtSmsCode);
			btnGetSmsCode = (Button) rootView.findViewById(R.id.btnGetSmsCode);
			btnPost = (Button) rootView.findViewById(R.id.btnPost);

			load_data();

			btn_bindding();

			return rootView;
		}

		private void load_data() {
			String mobile = String.valueOf(user.getMobile());
			txtMobile.setText(mobile);
			txtMobile.setSelection(mobile.length());
		}

		private void btn_bindding() {
			btnGetSmsCode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//1.validate txtMobile format, int, 11
					
					//60s 禁用期

					ftd.load_sms_code(user.getUserId(), txtMobile.getText()
							.toString(), new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try {
								int code = result.getInt("code");
							} catch (Exception ex) {
								Log.i("setting", ex.toString());
							}
							
						}

						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONObject errorResponse) {

							if (statusCode == 401) {
								app.set_token_failure();
							}

						}
					});

				}
			});

			btnPost.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//1.validate txtMobile format, int, 11
					
					ftd.validate_mobile(user.getUserId(), txtMobile.getText()
							.toString(), txtSmsCode.getText().toString(),
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(JSONObject result) {
									try {
										int code = result.getInt("code");
										
										//修改成功后跳转至setting list ？
										
									} catch (Exception ex) {
										Log.i("setting", ex.toString());
									}
								}

								@Override
								public void onFailure(int statusCode,
										Throwable e, JSONObject errorResponse) {
									if (statusCode == 401) {
										app.set_token_failure();
									}
								}
							});
				}
			});
		}

	}

	public static class PasswordFragment extends Fragment {
		private TextView tvMobile;
		private EditText txtOldPassword;
		private EditText txtNewPassword;
		private Button btnPost;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.setting_password,
					container, false);

			tvMobile = (TextView) rootView.findViewById(R.id.tvMobile);
			txtOldPassword = (EditText) rootView
					.findViewById(R.id.txtOldPassword);
			txtNewPassword = (EditText) rootView
					.findViewById(R.id.txtNewPassword);
			btnPost = (Button) rootView.findViewById(R.id.btnPost);

			load_data();
			btn_bindding();

			return rootView;
		}

		private void load_data() {
			String mobile = String.valueOf(user.getMobile());
			tvMobile.setText(mobile);

		}

		private void btn_bindding() {

			btnPost.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ftd.change_password(user.getUserId(), txtOldPassword
							.getText().toString(), txtNewPassword.getText()
							.toString(), new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try {
								int code = result.getInt("code");
							} catch (Exception ex) {
								Log.i("setting", ex.toString());
							}
						}

						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONObject errorResponse) {
							if (statusCode == 401) {
								app.set_token_failure();
							}
						}
					});
				}
			});
		}
	}

	public static class EmailFragment extends Fragment {
		private EditText txtEmail;
		private EditText txtOldPassword;
		private Button btnPost;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.setting_email, container,
					false);

			txtEmail = (EditText) rootView.findViewById(R.id.txtEmail);
			txtOldPassword = (EditText) rootView
					.findViewById(R.id.txtOldPassword);
			btnPost = (Button) rootView.findViewById(R.id.btnPost);

			load_data();
			btn_bindding();

			return rootView;
		}

		private void load_data() {
			txtEmail.setText(user.getEmail());
			txtEmail.setSelection(user.getEmail().length());
		}

		private void btn_bindding() {
			btnPost.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ftd.update_email(user.getUserId(), txtEmail.getText()
							.toString(), txtOldPassword.getText().toString(),
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(JSONObject result) {
									try {
										int code = result.getInt("code");
									} catch (Exception ex) {
										Log.i("setting", ex.toString());
									}
								}

								@Override
								public void onFailure(int statusCode,
										Throwable e, JSONObject errorResponse) {
									if (statusCode == 401) {
										app.set_token_failure();
									}
								}
							});
				}
			});
		}
	}

	public static class AboutFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			WebView webview = new WebView(act);
			webview.loadUrl("http://ftodo.sinaapp.com/about");
			return webview;
		}

	}
}
