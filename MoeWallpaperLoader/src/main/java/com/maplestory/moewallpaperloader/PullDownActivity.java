package com.maplestory.moewallpaperloader;

import java.util.ArrayList;
import java.util.List;

import com.maplestory.moewallpaperloader.view.PullDownView;
import com.maplestory.moewallpaperloader.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PullDownActivity extends Activity implements OnPullDownListener,

OnItemClickListener {



/** Handler What加载数据完毕 **/

private static final int WHAT_DID_LOAD_DATA = 0;

/** Handler What更新数据完毕 **/

private static final int WHAT_DID_REFRESH = 1;

/** Handler What更多数据完毕 **/

private static final int WHAT_DID_MORE = 2;



private ListView mListView;

private ArrayAdapter<String> mAdapter;

private PullDownView mPullDownView;

private List<String> mStrings = new ArrayList<String>();



@Override

protected void onCreate(Bundle savedInstanceState) {

super.onCreate(savedInstanceState);

setContentView(R.layout.pull_down);



/**

 * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView

 */

mPullDownView = (PullDownView) findViewById(R.id.pull_down_view);



mPullDownView.setOnPullDownListener(this);



mListView = mPullDownView.getListView();

mListView.setOnItemClickListener(this);



mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,

mStrings);

mListView.setAdapter(mAdapter);



// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多

mPullDownView.enableAutoFetchMore(true, 1);

// 隐藏 并禁用尾部

mPullDownView.setHideFooter();

// 显示并启用自动获取更多

mPullDownView.setShowFooter();

/** 隐藏并且禁用头部刷新*/

mPullDownView.setHideHeader();

/** 显示并且可以使用头部刷新*/

mPullDownView.setShowHeader();

/** 之前 网上很多代码 都会导致刷新事件 跟 上下文菜单同时弹出 这里做测试。。。已经解决 */

mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {



@Override
public void onCreateContextMenu(ContextMenu menu, View v,
		ContextMenuInfo menuInfo) {
	// TODO Auto-generated method stub
	
}

});



// 加载数据 本类使用

loadData();



}



/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/

@Override

public void onRefresh() {



new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(2000);

} catch (InterruptedException e) {

e.printStackTrace();

}



/** 关闭 刷新完毕 ***/

mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码



// 这个地方有点疑问

Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);

msg.obj = "After refresh " + System.currentTimeMillis();

msg.sendToTarget();

}

}).start();



}



/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/

@Override

public void onMore() {

new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(2000);

} catch (InterruptedException e) {

e.printStackTrace();

}



// 告诉它获取更多完毕 这个事线程安全的 可看源代码

mPullDownView.notifyDidMore();

Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);

msg.obj = "After more " + System.currentTimeMillis();

msg.sendToTarget();

}

}).start();

}



private Handler mUIHandler = new Handler() {



@Override

public void handleMessage(Message msg) {

switch (msg.what) {

case WHAT_DID_LOAD_DATA: {

if (msg.obj != null) {

List<String> strings = (List<String>) msg.obj;

if (!strings.isEmpty()) {

mStrings.addAll(strings);

mAdapter.notifyDataSetChanged();

}

}

Toast.makeText(PullDownActivity.this,

"" + mListView.getAdapter().getCount(),

Toast.LENGTH_LONG).show();

// 诉它数据加载完毕;

break;

}

case WHAT_DID_REFRESH: {

String body = (String) msg.obj;

mStrings.add(0, body);

mAdapter.notifyDataSetChanged();

// 告诉它更新完毕

break;

}



case WHAT_DID_MORE: {

String body = (String) msg.obj;

mStrings.add(body);

mAdapter.notifyDataSetChanged();



break;

}

}



}



};



@Override

public void onItemClick(AdapterView<?> parent, View view, int position,

long id) {

Toast.makeText(this, "啊，你点中我了 " + position, Toast.LENGTH_SHORT).show();

}



// 模拟数据

private String[] mStringArray = { "Abbaye de Belloc"/*

 * ,

 * "Abbaye du Mont des Cats"

 * , "Abertam",

 * "Abondance",

 * "Ackawi", "Acorn",

 * "Adelost",

 * "Affidelice au Chablis"

 * , "Afuega'l Pitu",

 * "Airag", "Airedale",

 * "Aisy Cendre",

 * "Allgauer Emmentaler"

 * , "Alverca",

 * "Ambert",

 * "American Cheese"

 */};



private void loadData() {

new Thread(new Runnable() {



@Override

public void run() {

try {

Thread.sleep(0000);

} catch (InterruptedException e) {

e.printStackTrace();

}

List<String> strings = new ArrayList<String>();

for (String body : mStringArray) {

strings.add(body);

}

Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);

msg.obj = strings;

msg.sendToTarget();

}

}).start();

}

}
