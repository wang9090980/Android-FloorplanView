package com.example.guidemap.activity;

import java.util.List;

import me.xiaopan.easy.android.util.AssetsUtils;
import me.xiaopan.easy.android.util.BitmapDecoder;
import me.xiaopan.easy.android.util.ToastUtils;
import me.xiaopan.guide.Area;
import me.xiaopan.guide.GuideView;
import me.xiaopan.guide.GuideView.Listener;
import me.xiaopan.guide.R;
import android.app.Activity;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.widget.TextView;

import com.example.guidemap.domain.Booth;
import com.example.guidemap.domain.ZhanGuan;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {
	private GuideView guideMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		guideMapView = (GuideView) findViewById(R.id.guideMap);
		final List<Area> booths = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(AssetsUtils.getString(getBaseContext(), "booths.txt"), new TypeToken<List<Booth>>(){}.getType());
//		booths.add(new ZhanGuan(new PointF(500, 500), new PointF(0, 1000), new PointF(1000, 1000)));
		Options options = BitmapDecoder.decodeSizeFromAssets(getBaseContext(), "44.png");
		guideMapView.setMap(AssetsUtils.getBitmap(getBaseContext(), "44.png"), booths, options.outWidth, options.outHeight);
		guideMapView.setListener(new Listener() {
			@Override
			public void onClickArea(Area area) {
				guideMapView.showSingleBubble(area);
			}

			@Override
			public void onClickAreaBubble(Area area) {
				if(area != null){
					if(area instanceof Booth){
						ToastUtils.toastS(getBaseContext(), ((Booth) area).getCompany().getAtrName());
					}else if(area instanceof ZhanGuan){
						ToastUtils.toastS(getBaseContext(), ((ZhanGuan) area).getTitle());
					}
				}
			}
		});
//		guideMapView.location(booths.get(20));
		guideMapView.setTextView((TextView) findViewById(R.id.text));
	}
}