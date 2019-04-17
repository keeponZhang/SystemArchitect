package com.darren.okhttp_source.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.darren.okhttp_source.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * GridView的适配器，负责异步从网络上下载图片展示在照片墙上。
 * 
 * @author guolin
 */
public class PhotoWallAdapter extends ArrayAdapter<String> {

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 图片硬盘缓存核心类。
	 * DiskLruCache能够正常工作的前提就是要依赖于journal文件中的内容，因此，能够读懂journal文件对于我们理解DiskLruCache的工作原理有着非常重要的作用
	 */
	private DiskLruCache mDiskLruCache;
	//图片在images https://blog.csdn.net/guolin_blog/article/details/28863651
//	由于现在只缓存了一张图片，所以journal中并没有几行日志，我们一行行进行分析。第一行是个固定的字符串“libcore.io.DiskLruCache”，标志着我们使用的是DiskLruCache技术。第二行是DiskLruCache的版本号，这个值是恒为1的。第三行是应用程序的版本号，我们在open()方法里传入的版本号是什么这里就会显示什么。第四行是valueCount，这个值也是在open()方法中传入的，通常情况下都为1。第五行是一个空行。前五行也被称为journal文件的头，这部分内容还是比较好理解的，但是接下来的部分就要稍微动点脑筋了。
//	第六行是以一个DIRTY前缀开始的，后面紧跟着缓存图片的key。通常我们看到DIRTY这个字样都不代表着什么好事情，意味着这是一条脏数据。没错，每当我们调用一次DiskLruCache的edit()方法时，都会向journal文件中写入一条DIRTY记录，表示我们正准备写入一条缓存数据，但不知结果如何。然后调用commit()方法表示写入缓存成功，这时会向journal中写入一条CLEAN记录，意味着这条“脏”数据被“洗干净了”，调用abort()方法表示写入缓存失败，这时会向journal中写入一条REMOVE记录。也就是说，每一行DIRTY的key，后面都应该有一行对应的CLEAN或者REMOVE的记录，否则这条数据就是“脏”的，会被自动删除掉。
//
	/**
	 * GridView的实例
	 */
	private GridView mPhotoWall;

	/**
	 * 记录每个子项的高度。
	 */
	private int mItemHeight = 0;

	public PhotoWallAdapter(Context context, int textViewResourceId, String[] objects,
			GridView photoWall) {
		super(context, textViewResourceId, objects);
		mPhotoWall = photoWall;
		taskCollection = new HashSet<BitmapWorkerTask>();
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		try {
			// 获取图片缓存路径
			File cacheDir = getDiskCacheDir(context, "thumb");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			mDiskLruCache = DiskLruCache
					.open(cacheDir, getAppVersion(context), 1, 30 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String url = getItem(position);
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout, null);
		} else {
			view = convertView;
		}
		final ImageView imageView = (ImageView) view.findViewById(R.id.photo);
		if (imageView.getLayoutParams().height != mItemHeight) {
			imageView.getLayoutParams().height = mItemHeight;
		}
		// 给ImageView设置一个Tag，保证异步加载图片时不会乱序
		imageView.setTag(url);
		imageView.setImageResource(R.drawable.empty_photo);
		loadBitmaps(imageView, url);
		return view;
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl) {
		try {
			Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
			if (bitmap == null) {

				//BitmapWorkerTask会先去本地磁盘中找
				BitmapWorkerTask task = new BitmapWorkerTask();
				taskCollection.add(task);
				task.execute(imageUrl);
			} else {
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前应用程序的版本号。
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 设置item子项的高度。
	 */
	public void setItemHeight(int height) {
		if (height == mItemHeight) {
			return;
		}
		mItemHeight = height;
		notifyDataSetChanged();
	}

	/**
	 * 使用MD5算法对传入的key进行加密并返回。
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}
	
	/**
	 * 将缓存记录同步到journal文件中。
	 */
	public void fluchCache() {
		if (mDiskLruCache != null) {
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;

		@Override //异步是相对于两个不同的线程来说的，doInBackground都是顺序执行的
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			DiskLruCache.Snapshot snapShot = null;
			try {
				// 生成图片URL对应的key
				final String key = hashKeyForDisk(imageUrl);
				Log.e("TAG", "doInBackground key: "+key );
				// 查找key对应的缓存
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					//写入用DiskLruCache.Editor
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
				}
				//读取用DiskLruCache.Snapshot
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitmap对象
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					// 将Bitmap对象添加到内存缓存当中
					addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
			taskCollection.remove(this);
		}

		/**
		 * 建立HTTP请求，并获取Bitmap对象。
		 * 
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
			HttpURLConnection urlConnection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;
			try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
				out = new BufferedOutputStream(outputStream, 8 * 1024);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

	}

}