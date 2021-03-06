package com.gaotianpu.ftodo.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.gaotianpu.ftodo.MyApplication;
import com.gaotianpu.ftodo.R; 
import com.gaotianpu.ftodo.da.FTDClient;
import com.gaotianpu.ftodo.da.UserDa;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import android.os.Build;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginFragment extends Fragment {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_MOBILE = "com.gaotianpu.ftodo.ui.EMAIL";

	// Values for email and password at the time of the login attempt.
	private String mMobile;
	private String mPassword;

	// UI references.
	private EditText mMobileView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// private Context ctx;
	private View rootView;
	private Activity act;
	private MyApplication app;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_login, container, false);

		act = this.getActivity();
		app = (MyApplication) act.getApplicationContext();

		// ui
		mMobileView = (EditText) rootView.findViewById(R.id.mobile);
		mPasswordView = (EditText) rootView.findViewById(R.id.password);
		mLoginFormView = rootView.findViewById(R.id.login_form);
		mLoginStatusView = rootView.findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) rootView
				.findViewById(R.id.login_status_message);

		init();

		getActivity().setTitle(R.string.login_or_register); 
		 
		return rootView;
	}

	private void init() {
		// Set up the login form.

		mMobile = act.getIntent().getStringExtra(EXTRA_MOBILE);
		mMobileView.setText(mMobile);
		mMobileView.setInputType(EditorInfo.TYPE_CLASS_PHONE);  

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		rootView.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		//
	}

	public void attemptLogin() {
		// 检测网络条件
		if(!app.network_available()){
			Toast.makeText(act, R.string.network_failed ,
				     Toast.LENGTH_SHORT).show();
			return ;
		} 
		
		// Reset errors.
		mMobileView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mMobile = mMobileView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mMobile)) {
			mMobileView.setError(getString(R.string.error_field_required));
			focusView = mMobileView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			TelephonyManager tm = (TelephonyManager) this.getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = tm.getDeviceId();
			String device_type = android.os.Build.MODEL;
			String os_type = "android." + android.os.Build.VERSION.RELEASE;

			FTDClient client = new FTDClient(act);
			client.login_or_register(mMobile, mPassword, deviceId, device_type,
					os_type, app.get_channel_no(), app.get_version_no(),
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject result) {
							try {
								int code = result.getInt("code");
								if (code != 1) {
									Log.e("login", "code is not 1");
									login_failed();
									return;
								}

								JSONObject data = result.getJSONObject("data"); 
								 
								(new UserDa(act)).login(data.getLong("user_id"),
										data.getString("name"),
										data.getString("access_token"),
										data.getString("email"),
										data.getString("password")); 
								app.changeUser();

								showProgress(false);

								// 跳转至list activity?
								int position = 1;
								Fragment fragment = new ListFragment();
								FragmentManager fragmentManager = getFragmentManager();
								fragmentManager.beginTransaction()
										.replace(R.id.content_frame, fragment)
										.commit();
								String[] mPlanetTitles = getResources()
										.getStringArray(
												R.array.drawer_menu_items);
								ListView mDrawerList = (ListView) act
										.findViewById(R.id.left_drawer);

								mPlanetTitles[0] = data.getString("name");
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										act, R.layout.drawer_list_item,
										mPlanetTitles);
								mDrawerList.setAdapter(adapter);

								mDrawerList.setItemChecked(position, true);

								// 登录后立刻改变左侧菜单的选项？
								// mDrawerList.getItemAtPosition(0);
								// mDrawerList.getAdapter().notifyDataSetChanged();

								// act.setTitle(mPlanetTitles[position]);

								// ListView lvDefault = (ListView)
								// act.findViewById(R.id.lvDefault);

							} catch (JSONException e) {
								Log.e("login", e.toString());
								login_failed();
							}

						}

						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONObject errorResponse) {
							login_failed();
							Log.e("login", e.toString());

						}

					});

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private void login_failed() {
		showProgress(false);

		mPasswordView.setError(getString(R.string.error_incorrect_password));
		mPasswordView.requestFocus();
	}

}
