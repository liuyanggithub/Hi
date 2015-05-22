package com.ly.hi.im.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.ly.hi.CustomApplication;
import com.ly.hi.R;
import com.ly.hi.game.ui.GameActivity;
import com.ly.hi.im.im.bean.User;
import com.ly.hi.im.view.HeaderLayout.onRightTextViewClickListener;
import com.ly.hi.lbs.biz.SendModel;
import com.ly.hi.lbs.biz.base.BaseModel;
import com.ly.hi.lbs.common.BizInterface;
import com.ly.hi.lbs.request.CreatePoiReq;
import com.ly.hi.lbs.request.UpdatePoiReq;
import com.ly.hi.lbs.response.BaseResponseParams;
import com.ly.hi.lbs.response.CreatePoiRes;
import com.ly.hi.lbs.response.DetailTablesRes;
import com.ly.hi.lbs.response.UpdatePoiRes;

/**
 * 用于发送位置的界面
 * 
 * @ClassName: LocationActivity
 * @Description: TODO
 * @author liuy
 */
public class NearLocationActivity extends BaseActivity implements OnGetGeoCoderResultListener, CloudListener {

	private static final String TAG = "NearLocationActivity";
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

	GeoCoder mSearch = null; // 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，因此需要采取反编码方式去搜索此经纬度代表的地址

	static BDLocation lastLocation = null;
	// static String mLastObjectId = null;

	BitmapDescriptor bdgeo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

	private SendModel mModel = null;// 发送请求

	private BmobUserManager mUserManager;
	private User mUser;

	private InfoWindow mInfoWindow;

	private List<CloudPoiInfo> mPoiInfos;

	// private boolean mIsUpdatePoi = false;

	private Handler mCreatePoiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseModel.MSG_SUC:
				BaseResponseParams<CreatePoiRes> response = (BaseResponseParams<CreatePoiRes>) msg.obj;
				if (BaseModel.REQ_SUC.equals(response.getStatus())) {
					ShowToast("creat");
				}
				break;
			}

		}
	};

	private Handler mUpdatePoiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseModel.MSG_SUC:
				BaseResponseParams<UpdatePoiRes> response = (BaseResponseParams<UpdatePoiRes>) msg.obj;
				if (BaseModel.REQ_SUC.equals(response.getStatus())) {
					ShowToast("update");
				}
				break;
			}

		}
	};

	private Handler mDetailTableHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseModel.MSG_SUC:
				BaseResponseParams<DetailTablesRes> response = (BaseResponseParams<DetailTablesRes>) msg.obj;
				if (BaseModel.REQ_SUC.equals(response.getStatus())) {
					String latitude = lastLocation.getLatitude() + "";
					String longitude = lastLocation.getLongitude() + "";
					String addrStr = lastLocation.getAddrStr();
					if (response.getObj().getPois() != null && response.getObj().getPois().size() > 0) {
						String geoId = response.getObj().getPois().get(0).getId();
						updatePoi(geoId, mUser.getUsername(), addrStr, latitude, longitude, "1", BizInterface.BAIDU_LBS_GEOTABLE_ID);
					} else {
						createPoi(mUser.getUsername(), addrStr, mUser.getObjectId(), latitude, longitude, "1", BizInterface.BAIDU_LBS_GEOTABLE_ID);
					}
				}
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_location);
		mUserManager = BmobUserManager.getInstance(this);
		mUser = mUserManager.getCurrentUser(User.class);
		// mLastObjectId = CustomApplication.getInstance().getSpUtil().getLastUser();
		CloudManager.getInstance().init(NearLocationActivity.this);
		initBaiduMap();
	}

	private void initBaiduMap() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 设置缩放级别
		mBaiduMap.setMaxAndMinZoomLevel(18, 13);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
		// initTopBarForLeft("附近的人");
		initTopBarForBoth("附近的人", "列表查看", new onRightTextViewClickListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NearLocationActivity.this, NearPeopleActivity.class);
				startAnimActivity(intent);
				finish();
			}
		});
		mHeaderLayout.getRightTextView().setEnabled(true);

		// mHeaderLayout.getRightImageButton().setEnabled(false);
		initLocClient();

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				if (!mPoiInfos.isEmpty()) {
					// DecimalFormat df = new DecimalFormat("#0.000000");
					String infoPosition, markerPosition;
					for (CloudPoiInfo info : mPoiInfos) {
						infoPosition = String.valueOf(info.latitude).substring(0, 5) + String.valueOf(info.longitude).substring(0, 5);
						markerPosition = String.valueOf(marker.getPosition().latitude).substring(0, 5) + String.valueOf(marker.getPosition().longitude).substring(0, 5);

						if (infoPosition.equals(markerPosition)) {
							// Button button = new Button(getApplicationContext());
							// button.setBackgroundResource(R.drawable.popup);
							// button.setText(info.title);
							// button.setTextColor(Color.BLACK);
							// LatLng ll = marker.getPosition();
							// mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, null);
							// mBaiduMap.showInfoWindow(mInfoWindow);

							if (!info.title.equals(mUser.getUsername())) {
								// final ProgressDialog progress = new ProgressDialog(NearLocationActivity.this);
								// progress.setMessage("正在添加...");
								// progress.setCanceledOnTouchOutside(false);
								// progress.show(); // 发送tag请求
								// BmobChatManager.getInstance(getApplicationContext()).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, info.tags, new PushListener() {
								//
								// @Override
								// public void onSuccess() {
								// progress.dismiss();
								// ShowToast("发送请求成功，等待对方验证!");
								// }
								//
								// @Override
								// public void onFailure(int arg0, final String arg1) {
								// progress.dismiss();
								// ShowToast("发送请求失败，请重新添加!");
								// ShowLog("发送请求失败:" + arg1);
								// }
								// });

								Intent intent = new Intent(NearLocationActivity.this, GameActivity.class);
								intent.putExtra("from", "add");
								intent.putExtra("username", info.title);
								startAnimActivity(intent);
								finish();
							} else {
								ShowToast("自己不能添加自己");
							}

						}
					}
				}
				return true;
			}
		});

	}

	// /**
	// * 回到聊天界面
	// * @Title: gotoChatPage
	// * @Description: TODO
	// * @param
	// * @return void
	// * @throws
	// */
	// private void gotoChatPage() {
	// if(lastLocation!=null){
	// Intent intent = new Intent();
	// intent.putExtra("y", lastLocation.getLongitude());// 经度
	// intent.putExtra("x", lastLocation.getLatitude());// 维度
	// intent.putExtra("address", lastLocation.getAddrStr());
	// setResult(RESULT_OK, intent);
	// this.finish();
	// }else{
	// ShowToast("获取地理位置信息失败!");
	// }
	// }

	private void initLocClient() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// 设置产品线
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();

		if (lastLocation != null) {
			// 显示在地图上
			LatLng ll = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				String latitude = lastLocation.getLatitude() + "";
				String longitude = lastLocation.getLongitude() + "";
				String addrStr = lastLocation.getAddrStr();
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocClient.stop();
					// if (mUserManager.getCurrentUserObjectId().equals(mLastObjectId)) {
					// nearbySearch(latitude, longitude);
					// } else {
					// createPoi(mUser.getUsername(), addrStr, mUser.getObjectId(), latitude, longitude, "1", BizInterface.BAIDU_LBS_GEOTABLE_ID);
					getDetailTableByName(mUser.getUsername());
					nearbySearch(latitude, longitude);
					// CustomApplication.getInstance().getSpUtil().setLastUser(mUserManager.getCurrentUserObjectId());
					// }
					// if (TextUtils.isEmpty(mLastObjectId)) {
					// CustomApplication.getInstance().getSpUtil().setLastUser(mUserManager.getCurrentUserObjectId());
					// }
					return;
				} else {
					LatLng last = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
					LatLng now = new LatLng(location.getLatitude(), location.getLongitude());
					if (DistanceUtil.getDistance(last, now) > 100) {// 移动距离超过100米
						// mIsUpdatePoi = true;
						getDetailTableByName(mUser.getUsername());
					}
				}

			}
			lastLocation = location;

			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = " + location.getLatitude() + ",地址 = " + lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// 反Geo搜索
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// 显示在地图上
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			// 设置按钮可点击
			// mHeaderLayout.getRightImageButton().setEnabled(true);

		}
	}

	/**
	 * 周边搜索
	 * 
	 * @param latitude
	 * @param longitude
	 */
	private void nearbySearch(String latitude, String longitude) {
		NearbySearchInfo info = new NearbySearchInfo();
		info.ak = BizInterface.BAIDU_LBS_AK;
		info.geoTableId = Integer.parseInt(BizInterface.BAIDU_LBS_GEOTABLE_ID);
		info.radius = 1000;
		info.location = longitude + "," + latitude;
		CloudManager.getInstance().nearbySearch(info);
	}

	/**
	 * 获取列表详细
	 */
	protected void getDetailTableByName(String name) {
		mModel = new SendModel(mDetailTableHandler, getApplicationContext(), getTag(), getRequestQueue());
		mModel.detailGeotable(name);
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("网络出错");
			}
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ShowToast("抱歉，未能找到结果");
			return;
		}
		BmobLog.i("反编码得到的地址：" + result.getAddress());
		lastLocation.setAddrStr(result.getAddress());
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null && mLocClient.isStarted()) {
			// 退出时销毁定位
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// 回收 bitmap 资源
		bdgeo.recycle();

		CloudManager.getInstance().destroy();
	}

	private void createPoi(String title, String address, String tags, String latitude, String longitude, String coord_type, String geotable_id) {
		mModel = new SendModel(mCreatePoiHandler, getApplicationContext(), getTag(), getRequestQueue());
		CreatePoiReq req = new CreatePoiReq(title, address, tags, latitude, longitude, coord_type, geotable_id);
		mModel.createPoi(req);

	}

	private void updatePoi(String id, String title, String address, String latitude, String longitude, String coord_type, String geotable_id) {
		mModel = new SendModel(mUpdatePoiHandler, getApplicationContext(), getTag(), getRequestQueue());
		UpdatePoiReq req = new UpdatePoiReq(id, title, address, latitude, longitude, coord_type, geotable_id);
		mModel.updatePoi(req);

	}

	@Override
	public String setTag() {
		return "near_location_activity";
	}

	@Override
	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(NearLocationActivity.this, result.poiInfo.title, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NearLocationActivity.this, "status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {
		if (result != null && result.poiList != null && result.poiList.size() > 0) {
			Log.d(TAG, "onGetSearchResult, result length: " + result.poiList.size());
			mPoiInfos = new ArrayList<CloudPoiInfo>();
			mPoiInfos.addAll(result.poiList);
			// if (mIsUpdatePoi) {
			// String latitude = lastLocation.getLatitude() + "";
			// String longitude = lastLocation.getLongitude() + "";
			// String addrStr = lastLocation.getAddrStr();
			// for (CloudPoiInfo info : mPoiInfos) {
			// Map<String, Object> extras = info.extras;
			//
			// if (info.tags.equals(mUser.getObjectId())) {
			// updatePoi(String.valueOf(info.uid), mUser.getUsername(), addrStr, latitude, longitude, "1", "98950");
			// }
			// }
			// mIsUpdatePoi = false;
			// }
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : mPoiInfos) {
				if (info.title.equals(mUser.getUsername())) {
					continue;
				}
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).title(info.title);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
				LatLngBounds bounds = builder.build();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

}
