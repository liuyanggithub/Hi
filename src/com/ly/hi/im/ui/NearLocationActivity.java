package com.ly.hi.im.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

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
import com.ly.hi.CustomApplication;
import com.ly.hi.R;
import com.ly.hi.im.im.bean.User;
import com.ly.hi.lbs.biz.SendModel;
import com.ly.hi.lbs.biz.base.BaseModel;
import com.ly.hi.lbs.common.BizInterface;
import com.ly.hi.lbs.request.CreatePoiReq;
import com.ly.hi.lbs.request.UpdatePoiReq;
import com.ly.hi.lbs.response.BaseResponseParams;
import com.ly.hi.lbs.response.CreatePoiRes;
import com.ly.hi.lbs.response.UpdatePoiRes;

/**
 * ���ڷ���λ�õĽ���
 * 
 * @ClassName: LocationActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-23 ����3:17:05
 */
public class NearLocationActivity extends BaseActivity implements OnGetGeoCoderResultListener, CloudListener {

	private static final String TAG = "NearLocationActivity";
	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	private BaiduReceiver mReceiver;// ע��㲥�����������ڼ��������Լ���֤key

	GeoCoder mSearch = null; // ����ģ�飬��Ϊ�ٶȶ�λsdk�ܹ��õ���γ�ȣ�����ȴ�޷��õ��������ϸ��ַ�������Ҫ��ȡ�����뷽ʽȥ�����˾�γ�ȴ����ĵ�ַ

	static BDLocation lastLocation = null;
	static String mLastObjectId = null;

	BitmapDescriptor bdgeo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

	private SendModel mModel = null;// ��������

	private BmobUserManager userManager;
	private User user;

	private InfoWindow mInfoWindow;

	private List<CloudPoiInfo> mPoiInfos;

	private boolean mIsUpdatePoi = false;

	private Handler mCreatePoiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BaseModel.MSG_SUC:
				BaseResponseParams<CreatePoiRes> response = (BaseResponseParams<CreatePoiRes>) msg.obj;
				if (BaseModel.REQ_SUC.equals(response.getStatus())) {
					ShowToast("creat");
				}
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
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_location);
		userManager = BmobUserManager.getInstance(this);
		user = userManager.getCurrentUser(User.class);
		mLastObjectId = CustomApplication.getInstance().getSpUtil().getLastUser();
		CloudManager.getInstance().init(NearLocationActivity.this);
		initBaiduMap();
	}

	private void initBaiduMap() {
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// �������ż���
		mBaiduMap.setMaxAndMinZoomLevel(18, 13);
		// ע�� SDK �㲥������
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
		initTopBarForLeft("��������");
		mHeaderLayout.getRightImageButton().setEnabled(false);
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
							Button button = new Button(getApplicationContext());
							button.setBackgroundResource(R.drawable.popup);
							button.setText(info.title);
							button.setTextColor(Color.BLACK);
							LatLng ll = marker.getPosition();
							mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, null);
							mBaiduMap.showInfoWindow(mInfoWindow);
							/*
							 * if (!info.tags.equals(user.getObjectId())) { final ProgressDialog progress = new ProgressDialog(NearLocationActivity.this);
							 * progress.setMessage("��������..."); progress.setCanceledOnTouchOutside(false); progress.show(); // ����tag����
							 * BmobChatManager.getInstance(getApplicationContext()).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, info.tags, new PushListener() {
							 * 
							 * @Override public void onSuccess() { progress.dismiss(); ShowToast("��������ɹ����ȴ��Է���֤!"); }
							 * 
							 * @Override public void onFailure(int arg0, final String arg1) { progress.dismiss(); ShowToast("��������ʧ�ܣ�����������!"); ShowLog("��������ʧ��:" + arg1); } });
							 * }else{ ShowToast("�Լ����������Լ�"); }
							 */
						}
					}
				}
				return true;
			}
		});

	}

	// /**
	// * �ص��������
	// * @Title: gotoChatPage
	// * @Description: TODO
	// * @param
	// * @return void
	// * @throws
	// */
	// private void gotoChatPage() {
	// if(lastLocation!=null){
	// Intent intent = new Intent();
	// intent.putExtra("y", lastLocation.getLongitude());// ����
	// intent.putExtra("x", lastLocation.getLatitude());// ά��
	// intent.putExtra("address", lastLocation.getAddrStr());
	// setResult(RESULT_OK, intent);
	// this.finish();
	// }else{
	// ShowToast("��ȡ����λ����Ϣʧ��!");
	// }
	// }

	private void initLocClient() {
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL, true, null));
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// ���ò�Ʒ��
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();

		if (lastLocation != null) {
			// ��ʾ�ڵ�ͼ��
			LatLng ll = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				String latitude = lastLocation.getLatitude() + "";
				String longitude = lastLocation.getLongitude() + "";
				String addrStr = lastLocation.getAddrStr();
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					BmobLog.i("��ȡ������ͬ");// �����������ȡ���ĵ���λ����������ͬ�ģ����ٶ�λ
//					ShowToast(userManager.getCurrentUserObjectId() + "***" + mLastObjectId);
					
					// fixme createʱ�����������log
					if (userManager.getCurrentUserObjectId().equals(mLastObjectId)) {
						mLocClient.stop();
						nearbySearch(latitude, longitude);
					} else {
						createPoi(user.getUsername(), addrStr, user.getObjectId(), latitude, longitude, "1", "98950");
						nearbySearch(latitude, longitude);
						CustomApplication.getInstance().getSpUtil().setLastUser(userManager.getCurrentUserObjectId());
					}
					if(TextUtils.isEmpty(mLastObjectId)){
						CustomApplication.getInstance().getSpUtil().setLastUser(userManager.getCurrentUserObjectId());
					}

					// TODO λ����ͬ �û���ͬ λ������
					// for(CloudPoiInfo info : mPoiInfos){
					// if(info.tags.equals(user.getObjectId())){
					// updatePoi(String.valueOf(info.uid), user.getObjectId(),
					// addrStr, latitude, longitude, "1", "98950");
					// }
					// }
					return;
				}
				mIsUpdatePoi = true;
			}
			lastLocation = location;

			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = " + location.getLatitude() + ",��ַ = " + lastLocation.getAddrStr());

			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			String address = location.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// ��Geo����
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
			// ��ʾ�ڵ�ͼ��
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			// ���ð�ť�ɵ��
			mHeaderLayout.getRightImageButton().setEnabled(true);

		}
	}

	/**
	 * �ܱ�����
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
	 * ����㲥�����࣬���� SDK key ��֤�Լ������쳣�㲥
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ShowToast("key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ShowToast("�������");
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
			ShowToast("��Ǹ��δ���ҵ����");
			return;
		}
		BmobLog.i("������õ��ĵ�ַ��" + result.getAddress());
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
			// �˳�ʱ���ٶ�λ
			mLocClient.stop();
		}
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// ȡ������ SDK �㲥
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// ���� bitmap ��Դ
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
			if (mIsUpdatePoi) {
				String latitude = lastLocation.getLatitude() + "";
				String longitude = lastLocation.getLongitude() + "";
				String addrStr = lastLocation.getAddrStr();
				for (CloudPoiInfo info : mPoiInfos) {
					Map<String, Object> extras = info.extras;

					if (info.tags.equals(user.getObjectId())) {
						updatePoi(String.valueOf(info.uid), user.getUsername(), addrStr, latitude, longitude, "1", "98950");
					}
				}
				mIsUpdatePoi = false;
			}
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		}
	}

}