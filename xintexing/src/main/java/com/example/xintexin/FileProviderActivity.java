package com.example.xintexin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileProviderActivity extends AppCompatActivity {
	private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
	private String mCurrentPhotoPath;
	private ImageView mIvPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_provider);
		mIvPhoto = findViewById(R.id.iv);

	}
	public static String getImagePath() {
		String path = Environment.getExternalStorageDirectory() + File.separator + "image";
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
		return path;
	}
	public void takePhotoNoCompress(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

			String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
					.format(new Date()) + ".png";
			File file = new File(getImagePath(), filename);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mCurrentPhotoPath = file.getAbsolutePath();

			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
		}
	}
	public void takePhotoNoCompress2(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

			String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
					.format(new Date()) + ".png";
			File file = new File(getImagePath(), filename);
			mCurrentPhotoPath = file.getAbsolutePath();

			Uri fileUri = FileProvider.getUriForFile(this, "com.zhy.android7.fileprovider", file);
			Log.e(TAG, "takePhotoNoCompress2:" + fileUri.getPath());
			//4.4的模拟器,会crashCaused by: java.lang.SecurityException: Permission Denial: opening provider android.support.v4.content.FileProvider from ProcessRecord{52b029b8 1670:com.android.camera/u0a36} (pid=1670, uid=10036) that is not exported from uid 10052
			//at android.os.Parcel.readException(Parcel.java:1465)
			if (Build.VERSION.SDK_INT < 24) { //对旧系统做兼容
				List<ResolveInfo> resInfoList = getPackageManager()
						.queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
				for (ResolveInfo resolveInfo : resInfoList) {
					String packageName = resolveInfo.activityInfo.packageName;
					grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
							| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				}
			}
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
		}
	}

	private static final String TAG = "FileProviderActivity";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "onActivityResult:"+resultCode);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
			mIvPhoto.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
		}

	}


	// 相机权限 2017/3/8 11:45
	public static final int REQUEST_CAMERA = 4;
	private static String[] PERMISSIONS_CAMERA = {
			Manifest.permission.CAMERA};



	public void checkSelfPermission(View view) {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			// 没有权限，申请权限。
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_WRITE_STORAGE);
		} else {
			// 有权限了，去放肆吧。
		}
		int permission = ActivityCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA,
					REQUEST_CAMERA);
		}
	}
	private static final int CODE_WRITE_STORAGE = 2000;
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case CODE_WRITE_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 权限被用户同意，可以去放肆了。

				} else {
					// 权限被用户拒绝了，洗洗睡吧。
					Toast.makeText(this, "下载应用需要存储权限哦", Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	}


	public void installApk(View view) {
		File file = new File(Environment.getExternalStorageDirectory(), "eventbusdemo.apk");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri fileUri;
		if (Build.VERSION.SDK_INT >= 24) {
			fileUri = FileProvider.getUriForFile(this, "com.zhy.android7.fileprovider", file);
		} else {
			fileUri = Uri.fromFile(file);
		}
		//7.0不加这句，会出现解析包异常
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		intent.setDataAndType(fileUri,
				"application/vnd.android.package-archive");
		startActivity(intent);
	}


}
