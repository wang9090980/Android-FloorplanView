# ![Logo](https://github.com/xiaopansky/Android-FloorplanView/raw/master/res/drawable-mdpi/ic_launcher.png) Android-FloorplanView

这是一个用在Android上的简单的平面图组件，最低兼容Android2.2
![smaple](https://github.com/xiaopansky/Android-FloorplanView/raw/master/docs/sample.png)

##Usage Guide
###1.在布局中使用FloorplanView
```xml
<me.xiaopan.android.floorplanview.FloorplanView 
    android:layout_width="match_parent" 
    android:layout_height="match_parent" 
    android:id="@+id/guideMap"/>
```

###2.设置监听器
调用FloorplanView.setListener(FloorplanView.Listener listener)方法设置监听器并处理相关事件，如下所示：
```java
floorplanView.setListener(new Listener() {
	@Override
	public void onClickArea(Area area) {
		floorplanView.showSingleBubble(area);   //在当前区域上显示气泡
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
        //初始化开始
	}

	@Override
	public void onInitProgressUpdate(int percent) {
        //初始化进度更新
	}

	@Override
	public void onInitFinish() {
        //初始化完成
	}

	@Override
	public void onInitFailure() {
        //初始化失败
	}
});
```

###3.在代码中设置底图和区域
你可以调用以下四种方法的任意一种来设置底图和区域
>* FloorplanView.setMap(final Bitmap baseMapBitmap, final List<Area> newAreas, final int suggestMapWidth, final int suggestMapHeight)
>* FloorplanView.setMap(Bitmap mapBitmap, List<Area> newAreas)
>* FloorplanView.setMap(final String filePath, final List<Area> newAreas)
>* FloorplanView.setMapFromAssets(final String fileName, final List<Area> newAreas)

##Downloads
**[android-floorplan-view-1.2.2.jar](https://github.com/xiaopansky/Android-FloorplanView/raw/master/releases/android-floorplan-view-1.2.2)**

**[android-floorplan-view-1.2.2-with-src.jar](https://github.com/xiaopansky/Android-FloorplanView/raw/master/releases/android-floorplan-view-1.2.2-with-src.jar)**

##License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
