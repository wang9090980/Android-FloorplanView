package me.xiaopan.android.floorplanview.sample;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.floorplanview.Area;
import me.xiaopan.android.floorplanview.FloorPlanView;
import me.xiaopan.android.floorplanview.FloorPlanView.Listener;
import me.xiaopan.android.floorplanview.R;
import me.xiaopan.easy.android.util.AssetsUtils;
import me.xiaopan.easy.android.util.ToastUtils;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

public class MainActivity extends Activity {
	private FloorPlanView guideMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		guideMapView = (FloorPlanView) findViewById(R.id.guideMap);
		
		final ExhibitionHall exhibitionHall = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(AssetsUtils.getString(getBaseContext(), "booths.txt"), ExhibitionHall.class);
		List<Area> areas = new ArrayList<Area>(exhibitionHall.getBooths().size()); 
		areas.addAll(exhibitionHall.getBooths());
		guideMapView.setMapFromAssets("exhibition_hall.png", areas);
		
		guideMapView.setListener(new Listener() {
			@Override
			public void onClickArea(Area area) {
				guideMapView.showSingleBubble(area);
			}

			@Override
			public void onClickBubble(Area area) {
				if(area != null){
					if(area instanceof Booth){
						ToastUtils.toastS(getBaseContext(), ((Booth) area).getCurrentCompany().getName());
					}else if(area instanceof ZhanGuan){
						ToastUtils.toastS(getBaseContext(), ((ZhanGuan) area).getTitle());
					}
				}
			}

			@Override
			public void onInitStart() {
//				AndroidLogger.e("初始化开始");
			}

			@Override
			public void onInitProgressUpdate(int percent) {
//				AndroidLogger.e("初始化进度："+percent + "%");
			}

			@Override
			public void onInitFinish() {
//				AndroidLogger.e("初始化完成");
			}
		});
		guideMapView.location(areas.get(10));
		guideMapView.setTextView((TextView) findViewById(R.id.text));
	}
}