package com.maplestory.moewallpaperloader.fragment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.maplestory.moewallpaperloader.LargeImageView;
import com.maplestory.moewallpaperloader.MoeWallpaperLoader;
import com.maplestory.moewallpaperloader.view.PopupMenuCompat;
import com.maplestory.moewallpaperloader.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
public class WallpaperSelectFragment extends Fragment{

	public WallpaperSelectFragment() {
		// TODO Auto-generated constructor stub

	}
	

	public void updateUI() {
		fileHandler.sendEmptyMessage(0);
	}
	private ImageAdapter ia;
	private static Handler fileHandler ;
	private ImageLoader imageLoader;
	private String[] imageUrls;					// 图片Url
	private List<String> images;
	private List<String> imagePaths;
	private DisplayImageOptions options;		// 显示图片的设置
	private GridView listView;
	private String IMAGE_PATH="/sdcard/WallpapersDownloader";
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.ac_image_grid, container, false);
		 images = new ArrayList();
		 imagePaths = new ArrayList();
		 options = new DisplayImageOptions.Builder()
			.cacheInMemory(false)
			.cacheOnDisc(false)
			.bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
			.build();
		initFile();
		listView = (GridView) rootView.findViewById(R.id.gridview);
		final ImageAdapter ia = new ImageAdapter();
		((GridView) listView).setAdapter(ia);			// 填充数据
		fileHandler = new Handler()
		{	
			public void handleMessage(android.os.Message msg) {
			System.out.println("refresh local images");
			initFile();	
			ia.notifyDataSetChanged();
			};
		};
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				System.out.println("on long click");
/*				new AlertDialog.Builder(getActivity())
				.setTitle("确认删除图片")
				.setMessage("是否删除该图片")
				.setPositiveButton("是", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0,int arg1) {
						File deleteImage = new File(imagePaths.get(position));
						deleteImage.delete();
						fileHandler.sendEmptyMessage(0);
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
				}
				})
				.setNegativeButton("否", null)
				.show();*/
				return true;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {


/*				final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
				Drawable wallpaperDrawable = wallpaperManager.getDrawable();
				final Bitmap previousWallpaper = ((BitmapDrawable) wallpaperDrawable).getBitmap();
				Bitmap nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(position));

				try {
					wallpaperManager.setBitmap(nextWallpaper);
					Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();

					new AlertDialog.Builder(getActivity())
							.setTitle("确认")
							.setMessage("返回原有壁纸")
							.setPositiveButton("是", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0,int arg1) {
									// TODO Auto-generated method stub
									try {
										wallpaperManager.setBitmap(previousWallpaper);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Toast.makeText(getActivity(), "返回原有壁纸", Toast.LENGTH_SHORT).show();
								}
							})
							.setNegativeButton("否", null)

							.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				PopupMenuCompat menu = PopupMenuCompat.newInstance( getActivity(), view );
				menu.inflate( R.menu.main );
				menu.setOnMenuItemClickListener( new PopupMenuCompat.OnMenuItemClickListener()
				{

					Uri uri = Uri.parse(images.get(position));
					@Override
					public boolean onMenuItemClick( MenuItem item )
					{
						System.out.println(images.get(position));
						switch (item.getItemId()){
							case(R.id.set_wallpaper):
								final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
								Bitmap nextWallpaper = BitmapFactory.decodeFile(imagePaths.get(position));
								try {
									wallpaperManager.setBitmap(nextWallpaper);
									Toast.makeText(getActivity(), "设置成功", Toast.LENGTH_SHORT).show();}
								catch (IOException e){
									e.printStackTrace();
								}
								break;
							case (R.id.delete_images):
								new AlertDialog.Builder(getActivity())
										.setTitle("确认删除图片")
										.setMessage("是否删除该图片")
										.setPositiveButton("是", new DialogInterface.OnClickListener(){
											@Override
											public void onClick(DialogInterface arg0,int arg1) {
												File deleteImage = new File(imagePaths.get(position));
												deleteImage.delete();
												fileHandler.sendEmptyMessage(0);
												Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
											}
										})
										.setNegativeButton("否", null)
										.show();

								break;
							case(R.id.share_image):
								String path = imagePaths.get(position);
								System.out.println("share image ....." + path);
								Intent intent=new Intent(Intent.ACTION_SEND);
								if(path == null || path.equals("")){
									intent.setType("text/plain");
								} else {
									File f = new File(path);
									if((f != null)&&(f.exists())&&(f.isFile())){
										intent.setType("image/jpeg");
										Uri u = Uri.fromFile(f);
										intent.putExtra(Intent.EXTRA_STREAM,u);
									}
								}
								intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
								intent.putExtra(Intent.EXTRA_TEXT,"Share Images");
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(Intent.createChooser(intent, getActivity().getTitle()));
								break;
							case(R.id.resize_image):
								cropImageUri(uri,1);
								break;
							case (R.id.view_image):
								Intent intenta = new Intent(getActivity(),LargeImageView.class);
								intenta.putExtra("position",position);
								intenta.putExtra("imagePaths",imageUrls);
								startActivity(intenta);
								break;
						}
						return true;
					}
				} );

				menu.show();
				
				
				
				
			}
		});
		BroadcastReceiver receiver = new BroadcastReceiver() {    
		        @Override     
		        public void onReceive(Context context, Intent intent) { 
		            String action = intent.getAction() ;
		            if( action.equals( DownloadManager.ACTION_DOWNLOAD_COMPLETE  )){
		                System.out.println("complete.........................");
		                fileHandler.sendEmptyMessage(0);
		            }

		            if( action.equals( DownloadManager.ACTION_NOTIFICATION_CLICKED )){
		               
		            }
		        }

			
		    };
		  IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) ;   
	      getActivity().registerReceiver( receiver , filter ) ; 
		
		
	     ((MoeWallpaperLoader) getActivity()).setImagePreviewHandler(fileHandler);
		 return rootView;
	 }


	 
	 
	 
		public class ImageAdapter extends BaseAdapter {
			@Override
			public int getCount() {
				return imageUrls.length;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ImageView imageView;
				if (convertView == null) {
					imageView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				} else {
					imageView = (ImageView) convertView;
				}
				imageLoader = ImageLoader.getInstance();
				// 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
				imageLoader.displayImage(imageUrls[position], imageView, options);

				return imageView;
			}
		}
		
		
	    public static String getExtensionName(String filename) {    
	        if ((filename != null) && (filename.length() > 0)) {    
	            int dot = filename.lastIndexOf('.');    
	            if ((dot >-1) && (dot < (filename.length() - 1))) {    
	                return filename.substring(dot + 1);    
	            }    
	        }    
	        return filename;    
	    }    
	    

	    
	    private void initFile(){
	    	 images.clear();
	    	 imagePaths.clear();
			 File f = new File(IMAGE_PATH);  
	         File[] files = f.listFiles();
	         if(files != null){
	             int count = files.length;// 文件个数  
	             for (int i = 0; i < count; i++) {  
	                 File file = files[i];  
	                 if(getExtensionName(file.getName()).equals("jpg")){
	                	 String path = "file:///mnt/sdcard/WallpapersDownloader" + "/" + file.getName();
	                	 String paths = "/sdcard/WallpapersDownloader/"+file.getName();
	                	 images.add(path);
	                	 imagePaths.add(paths);
	                 }
	             }  
	             
	             imageUrls = (String[])images.toArray(new String[images.size()]);  
	         }  
	    }

	private void cropImageUri(Uri uri, int requestCode){
		WindowManager wm = (WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX",14);
		intent.putExtra("aspectY",16);
		intent.putExtra("scale", false);
		intent.putExtra("outputX", height*1.25);// 输出图片大小
		intent.putExtra("outputY", height);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		this.startActivityForResult(intent, 100);
	}




	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("on activity result" + requestCode+" result code"+resultCode);

	}

	@Override
	public void onResume() {
		super.onResume();
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}
}
