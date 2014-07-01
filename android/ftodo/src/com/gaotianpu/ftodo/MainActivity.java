package com.gaotianpu.ftodo;

import com.gaotianpu.ftodo.R;
import com.gaotianpu.ftodo.bean.UserBean;
import com.gaotianpu.ftodo.ui.DashboardFragment;
import com.gaotianpu.ftodo.ui.ListFragment;
import com.gaotianpu.ftodo.ui.ListTodoFragment;
import com.gaotianpu.ftodo.ui.LoginFragment;
import com.gaotianpu.ftodo.ui.SettingFragment;

import java.util.Locale;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private int drawer_menu_selected_item = 1;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerItems;

	private UserBean user;
	private ArrayAdapter adapter;
	private MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 1. 全局
		app = (MyApplication) getApplicationContext();

		// 2. UI控件
		//mTitle = mDrawerTitle = getTitle();
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// 3. 数据加载
		user = app.getUser();
		mDrawerItems = getResources().getStringArray(
				R.array.drawer_menu_items);
		if (user.getUserId() != 0 && user.getTokenStatus() != 0) {
			mDrawerItems[0] = String.valueOf( user.getMobile() ) ;
		}
		
		 
		adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item,
				mDrawerItems);
		mDrawerList.setAdapter(adapter);

		// 4. 事件绑定
		mDrawerLayout_setDrawerListener();
		mDrawerList_setOnItemClickListener();

		if (savedInstanceState == null) {
			selectItem(1);
		}

		// 启动 AsyncService
		Intent startIntent = new Intent(this, AsyncService.class);
		this.startService(startIntent); // service如何获得当前用户
	}

	private void mDrawerLayout_setDrawerListener() {

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				Log.i("title",mTitle.toString());
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				Log.i("title",mTitle.toString());
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	private void updateMenu() {
		user = app.getUser(); // UserDa.load_current_user(this);
		if ( user.getUserId() != 0) {
			// 菜单显示 未登录...
			mDrawerItems[0] = String.valueOf( user.getMobile() );
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);  
		
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		//menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				return super.onSearchRequested();
				//
				// Toast.makeText(this, R.string.app_not_available,
				// Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void mDrawerList_setOnItemClickListener() {
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectItem(position);
				drawer_menu_selected_item = position;
			}
		});
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment;

		switch (position) {
		case 0:
			user = app.getUser();
			if ( user.getUserId() > 0 && user.getTokenStatus() > 0) {
				fragment = new DashboardFragment();
			} else {
				fragment = new LoginFragment();
			}

			break;
		case 1: // 全部
			fragment = new ListFragment();
			Bundle args0 = new Bundle();			
			args0.putInt(ListFragment.LIST_SORT, position);
			fragment.setArguments(args0);
			break;
		case 2: // 待办
			fragment = new ListTodoFragment();
//			Bundle args1 = new Bundle();			
//			args1.putInt(ListFragment.LIST_SORT, position);
//			fragment.setArguments(args0);
			break;
//		case 3: // 提醒
			
		case 3:
		default:
			fragment = new SettingFragment();
			break;
		} 
		 
		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// 使用后退按键？
		// android.app.FragmentTransaction transaction =
		// getFragmentManager().beginTransaction();
		// transaction.replace(R.id.content_frame,fragment);
		// transaction.addToBackStack(null);
		// transaction.commit();

		updateMenu();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = mDrawerTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

}