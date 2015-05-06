package com.ly.hi.im.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import com.ly.hi.CustomApplication;
import com.ly.hi.R;
import com.ly.hi.im.common.MyMessageReceiver;
import com.ly.hi.im.ui.fragment.ContactFragment;
import com.ly.hi.im.ui.fragment.RecentFragment;
import com.ly.hi.im.ui.fragment.SettingsFragment;
import com.ly.hi.im.view.TabShadeView;

/**
 * 登陆
 * 
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 下午2:45:35
 */
public class MainActivity extends ActivityBase implements EventListener, ViewPager.OnPageChangeListener, OnClickListener {

	private ViewPager mViewPager;
	private List<Fragment> mTabs = new ArrayList<Fragment>();
	private FragmentPagerAdapter mAdapter;
	private List<TabShadeView> mTabIndicator = new ArrayList<TabShadeView>();

	// private Button[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private SettingsFragment settingFragment;
	// private Fragment[] fragments;
	// private int index;
	private int currentTabIndex = 0;

	private ImageView iv_recent_tips, iv_contact_tips;// 消息提示

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setOverflowShowingAlways();
//		getActionBar().setDisplayShowHomeEnabled(false);;
//		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		// BmobChat.getInstance(this).startPollService(30);
		// 开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
		// initTab();
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
	private void initView() {
		// mTabs = new Button[3];
		// mTabs[0] = (Button) findViewById(R.id.btn_message);
		// mTabs[1] = (Button) findViewById(R.id.btn_contract);
		// mTabs[2] = (Button) findViewById(R.id.btn_set);
		iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);
		// 把第一个tab设为选中状态
		// mTabs[0].setSelected(true);

		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		initDatas();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);

	}

	private void initDatas() {
		recentFragment = new RecentFragment();
		contactFragment = new ContactFragment();
		settingFragment = new SettingsFragment();
		mTabs.add(recentFragment);
		mTabs.add(contactFragment);
		mTabs.add(settingFragment);

		// for (String title : mTitles) {
		// TabFragment tabFragment = new TabFragment();
		// Bundle args = new Bundle();
		// args.putString("title", title);
		// tabFragment.setArguments(args);
		//
		// }
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mTabs.get(arg0);
			}
		};
		initTabIndicator();

	}

	private void initTabIndicator() {
		TabShadeView one = (TabShadeView) findViewById(R.id.id_indicator_one);
		TabShadeView two = (TabShadeView) findViewById(R.id.id_indicator_two);
		TabShadeView three = (TabShadeView) findViewById(R.id.id_indicator_three);

		mTabIndicator.add(one);
		mTabIndicator.add(two);
		mTabIndicator.add(three);

		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);

		one.setImageView(1.0f);
		one.setTextView(1.0f);
	}

	// private void initTab(){
	// contactFragment = new ContactFragment();
	// recentFragment = new RecentFragment();
	// settingFragment = new SettingsFragment();
	// fragments = new Fragment[] {recentFragment, contactFragment, settingFragment };
	// // 添加显示第一个fragment
	// getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment).
	// add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment).commit();
	// }

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	// public void onTabSelect(View view) {
	// switch (view.getId()) {
	// case R.id.btn_message:
	// index = 0;
	// break;
	// case R.id.btn_contract:
	// index = 1;
	// break;
	// case R.id.btn_set:
	// index = 2;
	// break;
	// }
	// if (currentTabIndex != index) {
	// FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
	// trx.hide(fragments[currentTabIndex]);
	// if (!fragments[index].isAdded()) {
	// trx.add(R.id.fragment_container, fragments[index]);
	// }
	// trx.show(fragments[index]).commit();
	// }
	// mTabs[currentTabIndex].setSelected(false);
	// //把当前tab设为选中状态
	// mTabs[index].setSelected(true);
	// currentTabIndex = index;
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 小圆点提示
		if (BmobDB.create(this).hasUnReadMsg()) {
			iv_recent_tips.setVisibility(View.VISIBLE);
		} else {
			iv_recent_tips.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tips.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		MyMessageReceiver.mNewNum = 0;

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}

	/**
	 * 刷新界面
	 * 
	 * @Title: refreshNewMsg
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
		if (isAllow) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true, message);
		}
		if (currentTabIndex == 0) {
			// 当前页面如果为会话页面，刷新此页面
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}
	}

	NewBroadcastReceiver newReceiver;

	private void initNewMessageBroadCast() {
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	TagBroadcastReceiver userReceiver;

	private void initTagMessageBroadCast() {
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}

	/**
	 * 刷新好友请求
	 * 
	 * @Title: notifyAddUser
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
		if (isAllow) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				contactFragment.refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}

	private static long firstTime;

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		// 取消定时检测服务
		// BmobChat.getInstance(this).stopPollService();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (positionOffset > 0) {
			TabShadeView left = mTabIndicator.get(position);
			TabShadeView right = mTabIndicator.get(position + 1);
			left.setImageView(1 - positionOffset);
			left.setTextView(1 - positionOffset);
			right.setImageView(positionOffset);
			right.setTextView(positionOffset);
		}
		currentTabIndex = position;
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		resetOtherTabs();
		switch (v.getId()) {
		case R.id.id_indicator_one:
			mTabIndicator.get(0).setImageView(1.0f);
			mTabIndicator.get(0).setTextView(1.0f);
			mViewPager.setCurrentItem(0, false);
			currentTabIndex = 0;
			break;
		case R.id.id_indicator_two:
			mTabIndicator.get(1).setImageView(1.0f);
			mTabIndicator.get(1).setTextView(1.0f);
			mViewPager.setCurrentItem(1, false);
			currentTabIndex = 1;
			break;
		case R.id.id_indicator_three:
			mTabIndicator.get(2).setImageView(1.0f);
			mTabIndicator.get(2).setTextView(1.0f);
			mViewPager.setCurrentItem(2, false);
			currentTabIndex = 2;
			break;
		}

	}

	/**
	 * 重置其他的Tab
	 */
	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicator.size(); i++) {
			mTabIndicator.get(i).setImageView(0);
			mTabIndicator.get(i).setTextView(0);
		}
	}

	private void setOverflowShowingAlways() {
		try {
			// true if a permanent menu key is present, false otherwise.
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
