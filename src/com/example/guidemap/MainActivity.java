package com.example.guidemap;

import java.util.List;

import me.xiaopan.easy.android.util.AssetsUtils;
import android.app.Activity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	private GuideMapView guideMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		guideMapView = (GuideMapView) findViewById(R.id.guideMap);
		List<Booth> booths = new Gson().fromJson(AssetsUtils.getString(getBaseContext(), "booths.txt"), new TypeToken<List<Booth>>(){}.getType()) ;
		guideMapView.setMap(AssetsUtils.getBitmap(getBaseContext(), "44.png"), booths);
	}
}
