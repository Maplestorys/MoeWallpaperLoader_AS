package com.maplestory.moewallpaperloader.fragment;

import android.app.DownloadManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.maplestory.moewallpaperloader.MainActivity;
import com.maplestory.moewallpaperloader.fragment.ImgPreviewFragment.ItemListAdapter.ViewHolder;
import com.maplestory.moewallpaperloader.utils.HttpUtils;
import com.maplestory.moewallpaperloader.utils.ImageProfile;
import com.maplestory.moewallpaperloader.utils.siteAddressGen;
import com.maplestory.moewallpaperloader.MoeWallpaperLoader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.maplestory.moewallpaperloader.R;
import com.maplestory.moewallpaperloader.view.MatrixImageView;
import com.maplestory.moewallpaperloader.view.PullDownView;
import com.maplestory.moewallpaperloader.view.PullDownView.OnPullDownListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.conn.scheme.HostNameResolver;

public class ImgPreviewFragment extends Fragment implements OnPullDownListener,
		OnItemClickListener {

	public ImgPreviewFragment() {
		// TODO Auto-generated constructor stub
	}
	private String IMAGE_PATH="/sdcard/WallpapersDownloader";
	private Parcelable listState = null;
	private ItemListAdapter itemListAdapter;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private View rootView;
	private int currentPage;
	/** Handler What加载数据完毕 **/
	private static final int WHAT_DID_LOAD_DATA = 0;
	/** Handler What更新数据完毕 **/
	private static final int WHAT_DID_REFRESH = 1;
	/** Handler What更多数据完毕 **/
	private static final int WHAT_DID_MORE = 2;
	private String[] imageUrls = {};
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private PullDownView mPullDownView;
	private List<String> mStrings = new ArrayList<String>();
	private List<ImageProfile> imageValueStrings = new ArrayList();
	private boolean firstLoad = true;
	private boolean hasTags = false;
	private String tags;
	private int maxPageNumber;
	private boolean firstStart = true;
	private Handler setPositionHandler;
	private String filterSize;
	private String filterScore;
	private String filterSortMethod;
	private String siteAddress;
	private Boolean isFilterExplicit;
	private Handler mainActivityHandler;
	public void setMainActivityHandler(Handler handler){
		this.mainActivityHandler = handler;
	}
	private HttpUtils httpUtils = new HttpUtils();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("create view ");
		View rootView = inflater.inflate(R.layout.pull_down, container, false);
		mPullDownView = (PullDownView) rootView
				.findViewById(R.id.pull_down_view);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		filterSize = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("filter_size_list","");
		filterScore = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("filter_score_list","");
		filterSortMethod = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_filter_sort_method","");
		siteAddress = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("image_site_list", "http://konachan.com/post?");
		isFilterExplicit = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("explicit_image_filter", true);


		if (!firstStart) {
			setPositionHandler.sendEmptyMessage(0);
		}else {
			firstStart =false;
		}


		System.out.println("fragment resume");
		/**
		 * 
		 * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
		 */
		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多

		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build();
		itemListAdapter = new ItemListAdapter();
		mPullDownView.setOnPullDownListener(this);
		mListView = mPullDownView.getListView();
		mListView.setOnItemClickListener(this);
		/*
		mAdapter = new
		ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,mStrings);
		mListView.setAdapter(mAdapter);
		*/
		mListView.setAdapter(itemListAdapter);
		mPullDownView.enableAutoFetchMore(true,4);

		// 隐藏 并禁用尾部
		mPullDownView.setHideFooter();
		// 显示并启用自动获取更多
		mPullDownView.setShowFooter();
		/** 隐藏并且禁用头部刷新 */
		mPullDownView.setHideHeader();
		/** 显示并且可以使用头部刷新 */
		mPullDownView.setShowHeader();

		imageLoader = ImageLoader.getInstance();

		/** 之前 网上很多代码 都会导致刷新事件 跟 上下文菜单同时弹出 这里做测试。。。已经解决 */
		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
					}
				});
		// 加载数据 本类使用
		if (firstLoad) {
			loadData();
			firstLoad = false;
		}

		((MoeWallpaperLoader) getActivity()).setUIHandler(loadDataHandler);
		setPositionHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				SharedPreferences preferences = getActivity().getSharedPreferences(
						"SCROLL", 0);
				int scroll = preferences.getInt("ScrollValue", 0);
				System.out.println("scroll...." + scroll);
				if (scroll >= mListView.getCount()-3) {
					mListView.setSelection(scroll-2);
					System.out.println("scroll  finsish ");

				} else {
					mListView.setSelection(scroll);
				}
			}
		};
	}
	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/

	@Override
	public void onRefresh() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				currentPage = 1;
				List<String> strings = new ArrayList<String>();
				;
				ArrayList<ImageProfile> al;
				tags = ((MoeWallpaperLoader) getActivity()).getTags();
				if (tags != null && tags != "") {
					hasTags = true;
				} else {
					hasTags = false;
				}
				if (hasTags) {
				//	String withTagsHtmlString = siteAddressGen.getSiteAddress(currentPage, tags);
				    String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress,currentPage,isFilterExplicit,tags,filterSize,filterScore,filterSortMethod);
					System.out.println(withTagsHtmlString);
					String htmlString = httpUtils
							.getContent(withTagsHtmlString);
					al = HttpUtils.getNewImageValues(htmlString);
					maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
				} else {
				//	String htmlString = HttpUtils.getContent(siteAddressGen.getSiteAddress(currentPage));
					String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit,"", filterSize, filterScore, filterSortMethod);
					System.out.println(withTagsHtmlString);
					String htmlString = httpUtils.getContent(withTagsHtmlString);
					al = HttpUtils.getNewImageValues(htmlString);
					maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
				}
				if (al.size() > 0) {
					for (int i = 0; i < al.size(); i++) {
						strings.add("" + al.get(i).getPreview_url());
					}
					imageValueStrings.clear();
					imageValueStrings.addAll(al);
					currentPage++;
				}
				currentPage++;

				/** 关闭 刷新完毕 ***/
				mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码

				// 这个地方有点疑问
				Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
				msg.obj = strings;
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
				System.out.println(currentPage);
				if (currentPage <= maxPageNumber) {
					List<String> strings = new ArrayList<String>();
					;
					ArrayList<ImageProfile> al;
					if (tags != null && tags != "") {
						hasTags = true;
					} else {
						hasTags = false;
					}
					if (hasTags) {
						//String withTagsHtmlString = siteAddressGen.getSiteAddress(currentPage, tags);
						//System.out.println(withTagsHtmlString);
						String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit,tags, filterSize, filterScore, filterSortMethod);
						System.out.println(withTagsHtmlString);
						String htmlString = httpUtils
								.getContent(withTagsHtmlString);
						al = HttpUtils.getNewImageValues(htmlString);

					} else {
						String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit,"", filterSize, filterScore, filterSortMethod);
						System.out.println(withTagsHtmlString);
						String htmlString = httpUtils.getContent(withTagsHtmlString);
						al = HttpUtils.getNewImageValues(htmlString);
					}
					if (al.size() > 0) {
						for (int i = 0; i < al.size(); i++) {
							strings.add("" + al.get(i).getPreview_url());
						}
						imageValueStrings.addAll(al);
						currentPage++;
						mPullDownView.notifyDidMore();
						System.out.println("notifyMore");
						Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
						msg.obj = strings;
						msg.sendToTarget();
					}
				} else {
					mPullDownView.notifyDidMore();
					System.out.println("notifyMore");
					Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
					msg.sendToTarget();
					System.out.println("no more pages");

				}
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
						imageUrls = (String[]) mStrings
								.toArray(new String[mStrings.size()]);
						// mAdapter.notifyDataSetChanged();
						itemListAdapter.notifyDataSetChanged();
						System.out.println("on load data");
					}
				}
				// Toast.makeText(getActivity(),"Loading Complete",Toast.LENGTH_LONG).show();
				// 诉它数据加载完毕;
				break;
			}
			case WHAT_DID_REFRESH: {
				mStrings.clear();
				System.out.println("REFRESH");
				List<String> strings = (List<String>) msg.obj;
				if (!strings.isEmpty()) {
					mStrings.addAll(strings);
					imageUrls = (String[]) mStrings.toArray(new String[mStrings
							.size()]);
					// mAdapter.notifyDataSetChanged();
					itemListAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), "未找到该标签的图片",
							Toast.LENGTH_LONG).show();
				}
				// Toast.makeText(getActivity(),"Refresh Complete",Toast.LENGTH_LONG).show();
				// 告诉它更新完毕
				break;
			}
			case WHAT_DID_MORE: {
				if (msg.obj != null) {
					List<String> strings = (List<String>) msg.obj;
					if (!strings.isEmpty()) {
						mStrings.addAll(strings);
						imageUrls = (String[]) mStrings
								.toArray(new String[mStrings.size()]);
						// mAdapter.notifyDataSetChanged();
						itemListAdapter.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(getActivity(), "全部图片加载完毕",
							Toast.LENGTH_SHORT).show();
				}

				//
				break;
			}
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// position指的是第几个 ，从1开始计算
		/*System.out.println(position);
		System.out.println(itemListAdapter.getCount());
		if(itemListAdapter.getCount()>0&&position<=itemListAdapter.getCount()){
			Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
			// System.out.println(imageValueStrings.get(position-1).getJpeg_url());
			ViewHolder holder = (ViewHolder) view.getTag();
			// System.out.println(holder.text.getText().toString());
			Bundle bundle = new Bundle();
			bundle.putSerializable("imageObject",
					imageValueStrings.get(position - 1));
			Intent intent = new Intent();
			intent.setClass(getActivity(), MainActivity.class);
			intent.putExtras(bundle);
			getActivity().startActivity(intent);
			System.out.println("jump_____________");
		}*/

	}

	Handler loadDataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					currentPage = 1;
					List<String> strings = new ArrayList<String>();
					ArrayList<ImageProfile> al;
					tags = ((MoeWallpaperLoader) getActivity()).getTags();
					if (tags != null && tags != "") {
						hasTags = true;
					} else {
						hasTags = false;
					}
					if (hasTags) {
						String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit, tags, filterSize, filterScore, filterSortMethod);
						System.out.println(withTagsHtmlString);
						String htmlString = httpUtils.getContent(withTagsHtmlString);
						al = HttpUtils.getNewImageValues(htmlString);
						maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
					} else {
						String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit,"", filterSize, filterScore, filterSortMethod);
						System.out.println(withTagsHtmlString);
						String htmlString = httpUtils.getContent(withTagsHtmlString);
						al = HttpUtils.getNewImageValues(htmlString);
						maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
					}
					if (al.size() > 0) {
						for (int i = 0; i < al.size(); i++) {
							strings.add("" + al.get(i).getPreview_url());
						}
						imageValueStrings.clear();
						imageValueStrings.addAll(al);
						currentPage++;
					}
					currentPage++;

					/** 关闭 刷新完毕 ***/
					mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码

					// 这个地方有点疑问
					Message nmsg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
					nmsg.obj = strings;
					nmsg.sendToTarget();
					System.out.println("TO REFRESH");
				}

			}).start();
		}
	};

	public void loadData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				currentPage = 1;
				List<String> strings = new ArrayList<String>();
				ArrayList<ImageProfile> al;
				tags = ((MoeWallpaperLoader) getActivity()).getTags();
				System.out.println(tags);
				if (tags != null && tags != "") {
					hasTags = true;
				} else {
					hasTags = false;
				}
				System.out.println(tags);
				System.out.println(hasTags);
				if (hasTags) {
					String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit, tags, filterSize, filterScore, filterSortMethod);
					System.out.println(withTagsHtmlString);
					String htmlString = httpUtils
							.getContent(withTagsHtmlString);
					al = HttpUtils.getNewImageValues(htmlString);
					maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
					System.out.println(maxPageNumber);
					System.out.println(al.size());
				} else {
					String withTagsHtmlString = siteAddressGen.getsiteAddress(siteAddress, currentPage, isFilterExplicit, "", filterSize, filterScore, filterSortMethod);
					System.out.println(withTagsHtmlString);
					String htmlString = httpUtils.getContent(withTagsHtmlString);
					al = HttpUtils.getNewImageValues(htmlString);
					maxPageNumber = HttpUtils.getMaxPageNumber(htmlString);
				}
				if (al.size() > 0) {
					for (int i = 0; i < al.size(); i++) {
						strings.add("" + al.get(i).getPreview_url());
					}
					imageValueStrings.clear();
					imageValueStrings.addAll(al);
					currentPage++;
					Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					System.out.println("mUIhandler");
					msg.obj = strings;
					msg.sendToTarget();
				}
			}
		}).start();

	}




	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("listview on saveInstantce state");
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"SCROLL", 0);
		Editor editor = preferences.edit();
		int scroll = mListView.getFirstVisiblePosition();
		System.out.println("scroll..........." + scroll);
		editor.putInt("ScrollValue", scroll);
		editor.commit();
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		System.out.println("--------------------------restore");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("on destory");
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("fragment on start......");


	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("pause");
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("on create");
	}
	class ItemListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return imageUrls[position];
		}

		public String getCurrentString(View convertView) {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			return holder.tvId.getText().toString();
		}

		public Drawable getCurrentBackground(View convertView) {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			return holder.image.getBackground();
		}

		@Override
		public View getView(final int position,  View convertView, ViewGroup parent) {
			final RotateAnimation a =  new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,
					0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			final RotateAnimation b =  new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,
					0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			a.setDuration(200);
			a.setFillAfter(true);
			b.setDuration(200);
			b.setFillAfter(true);

			ViewHolder holder = null;
			System.out.println(position + "    position imagess------");
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.item_list, parent, false);
				holder = new ViewHolder();
				holder.tvId = (TextView) convertView.findViewById(R.id.tv_id_view);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.tvScore = (TextView) convertView.findViewById(R.id.tv_score_view);
				holder.tvResolution = (TextView) convertView.findViewById(R.id.tv_resolution_view);
				holder.lvTags = (ListView) convertView.findViewById(R.id.tags_list_view);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ImageView imageView = holder.image;
			View.OnClickListener l = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()){
						case R.id.btn_download_view:
							ImageProfile ip = imageValueStrings.get(position);
							if(searchFile(ip.getId()+"")){
								System.out.println("file is exist");
								Toast.makeText(getActivity(),"图像已存在",Toast.LENGTH_SHORT).show();
							}else {
								DownloadManager.Request request = new DownloadManager.Request( Uri.parse(ip.getJpeg_url()) );
								MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
								request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(ip.getSample_url()));
								request.setDestinationInExternalPublicDir("/WallpapersDownloader/", ip.getId() + ".jpg");
								request.allowScanningByMediaScanner() ;
								request.setVisibleInDownloadsUi(true) ;
								DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE) ;
								downloadManager.enqueue(request) ;
								Toast.makeText(getActivity(),"开始下载",Toast.LENGTH_SHORT).show();
								System.out.println("download image with id:" + imageValueStrings.get(position).getJpeg_url());
							}

							break;
						case R.id.btn_share_view:
							System.out.println("share image with id:" + imageValueStrings.get(position).getId());
							Intent intent=new Intent(Intent.ACTION_SEND);
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
							intent.putExtra(Intent.EXTRA_TEXT, imageValueStrings.get(position).getFile_url());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(intent, getActivity().getTitle()));
							break;
						case R.id.btn_refresh_view:
							imageLoader.displayImage(imageUrls[position], imageView, options);
							System.out.println("refresh image with id:" + imageValueStrings.get(position).getId());
							Toast.makeText(getActivity(), "开始刷新", Toast.LENGTH_SHORT).show();
							break;

					}
				}
			};
			convertView.findViewById(R.id.btn_download_view).setOnClickListener(l);
			convertView.findViewById(R.id.btn_share_view).setOnClickListener(l);
			convertView.findViewById(R.id.btn_refresh_view).setOnClickListener(l);
			holder.lvTags.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int innerPosition, long id) {
					String selectedTag = imageValueStrings.get(position).getTags().get(innerPosition);
					((MoeWallpaperLoader) getActivity()).setTags(selectedTag);
					loadDataHandler.sendEmptyMessage(0);
					mainActivityHandler.sendEmptyMessage(0);
					System.out.println(selectedTag);
				}
			});
			final LinearLayout imageDetails = (LinearLayout)convertView.findViewById(R.id.tag_details);
			final ImageButton btnActionDetail = (ImageButton) convertView.findViewById(R.id.btn_more_detail_view);
			convertView.findViewById(R.id.btn_more_detail_view).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if(imageDetails.getVisibility()==View.GONE){
						imageDetails.setVisibility(View.VISIBLE);
						btnActionDetail.startAnimation(a);
					}else {
						imageDetails.setVisibility(View.GONE);
						btnActionDetail.startAnimation(b);
					}
					System.out.println("see detail of image with id:" + imageValueStrings.get(position).getId());

				}
			});
			holder.tvId.setText("" + imageValueStrings.get(position).getId());
			imageLoader.displayImage(imageUrls[position], holder.image, options);
			holder.tvResolution.setText("" + imageValueStrings.get(position).getJpeg_width() + "×" + imageValueStrings.get(position).getJpeg_height());
			holder.tvScore.setText(imageValueStrings.get(position).getScore() + "");
			holder.lvTags.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.single_tag_view,imageValueStrings.get(position).getTags()));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder {
			public ImageView image;
			public TextView tvId;
			public TextView tvScore;
			public TextView tvResolution;
			public ListView lvTags;
		}
	}

	public interface searchCallback {
		public void search();
	}

	private boolean searchFile(String imageID){

		File f = new File(IMAGE_PATH);
		File[] files = f.listFiles();
		if(files != null){
			int count = files.length;// 文件个数
			for (int i = 0; i < count; i++) {
				File file = files[i];
				if(getFileNameNoEx(file.getName()).equals(imageID)){
					return true;
				}
			}

		}
		return false;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
	}
