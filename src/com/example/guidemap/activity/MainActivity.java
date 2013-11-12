package com.example.guidemap.activity;

import java.util.List;

import me.xiaopan.easy.android.util.AssetsUtils;
import me.xiaopan.easy.android.util.ToastUtils;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.guidemap.Area;
import com.example.guidemap.GuideMapView;
import com.example.guidemap.GuideMapView.InitialZoomMode;
import com.example.guidemap.GuideMapView.Listener;
import com.example.guidemap.R;
import com.example.guidemap.domain.Booth;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	private GuideMapView guideMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		guideMapView = (GuideMapView) findViewById(R.id.guideMap);
		final List<Area> booths = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(AssetsUtils.getString(getBaseContext(), "booths.txt"), new TypeToken<List<Booth>>(){}.getType()) ;
		guideMapView.setMap(AssetsUtils.getBitmap(getBaseContext(), "44.png"), booths);
		guideMapView.setInitialZoomMode(InitialZoomMode.MIN);
		guideMapView.setListener(new Listener() {
			@Override
			public void onClickArea(Area area) {
				guideMapView.showSingleBubble(area);
			}

			@Override
			public void onClickAreaBubble(Area area) {
				if(area != null && area instanceof Booth){
					ToastUtils.toastS(getBaseContext(), ((Booth) area).getCompany().getAtrName());
				}
			}
		});
		guideMapView.location(booths.get(20));
		guideMapView.setTextView((TextView) findViewById(R.id.text));
	}
}