package com.example.guidemap.activity;

import java.util.List;

import me.xiaopan.easy.android.util.AssetsUtils;
import me.xiaopan.easy.android.util.ToastUtils;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;

import com.example.guidemap.Area;
import com.example.guidemap.GuideMapView;
import com.example.guidemap.GuideMapView.InitialZoomMode;
import com.example.guidemap.GuideMapView.Listener;
import com.example.guidemap.PathArea;
import com.example.guidemap.R;
import com.example.guidemap.domain.Booth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	private GuideMapView guideMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		guideMapView = (GuideMapView) findViewById(R.id.guideMap);
		List<Area> booths = new Gson().fromJson(AssetsUtils.getString(getBaseContext(), "booths.txt"), new TypeToken<List<Booth>>(){}.getType()) ;
		booths.add(new PathArea() {
			@Override
			public PointF[] getCoordinates() {
//				return new Point[]{new Point(500, 500), new Point(250, 1000), new Point(750, 1000), new Point(600, 800)};
				return new PointF[]{new PointF(500, 500), new PointF(250, 1000), new PointF(750, 1000)};
			}
		});
		guideMapView.setMap(AssetsUtils.getBitmap(getBaseContext(), "44.png"), booths);
		guideMapView.setInitialZoomMode(InitialZoomMode.MIN);
		guideMapView.setListener(new Listener() {
			@Override
			public void onClickArea(Area area) {
				if(area != null){
					if(area instanceof Booth){
						ToastUtils.toastS(getBaseContext(), ((Booth) area).getCompany().getAtrName());
					}else if(area instanceof PathArea){
						ToastUtils.toastS(getBaseContext(), "多边形");
					}
				}
			}
		});
	}
}
