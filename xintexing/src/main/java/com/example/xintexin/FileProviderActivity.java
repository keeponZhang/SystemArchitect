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

    // 相机权限 2017/3/8 11:45
    public static final int REQUEST_CAMERA = 4;
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA};

    private static String[] PERMISSIONS_STORAGE =
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_WRITE_STORAGE = 2000;

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
            if (!file.exists()) {
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
                        .queryIntentActivities(takePictureIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);
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
        Log.e(TAG, "onActivityResult:" + resultCode);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            mIvPhoto.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }

    }

    // shouldShowRequestPermissionRationale
    // 望文生义，是否应该显示请求权限的说明,第一次没有权限时是返回false。
    // 第一次请求权限时，用户拒绝了，调用shouldShowRequestPermissionRationale()后返回true，应该显示一些为什么需要这个权限的说明。
    // 用户在第一次拒绝某个权限后，下次再次申请时，授权的dialog中将会出现“不再提醒”选项，一旦选中勾选了，那么下次申请将不会提示用户，如果没有勾选，但是拒绝了，调用shouldShowRequestPermissionRationale还是返回后返回true。
    // 第二次请求权限时，用户拒绝了，并选择了“不再提醒”的选项，调用shouldShowRequestPermissionRationale()后返回false。
    // 设备的策略禁止当前应用获取这个权限的授权：shouldShowRequestPermissionRationale()返回false 。
    // 加这个提醒的好处在于，用户拒绝过一次权限后我们再次申请时可以提醒该权限的重要性，免得再次申请时用户勾选“不再提醒”并决绝，导致下次申请权限直接失败。
    //         ————————————————
    // 版权声明：本文为CSDN博主「严振杰」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
    // 原文链接：https://blog.csdn.net/yanzhenjie1003/article/details/52503533
    public void checkWritePermission(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            boolean shouldShowRequestPermissionRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.e("TAG", "FileProviderActivity checkWritePermission " +
                    "shouldShowRequestPermissionRationale:"+shouldShowRequestPermissionRationale);
            // 没有权限，申请权限。
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_WRITE_STORAGE);
        } else {
            // 有权限了，去放肆吧。
            Toast.makeText(this, "有存储卡读写权限了", Toast.LENGTH_LONG).show();
        }

    }

    public void checkCameraPermission(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，申请权限。
            boolean shouldShowRequestPermissionRationale = ActivityCompat
                    .shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
            Log.e("TAG", "FileProviderActivity checkCameraPermission " +
                    "shouldShowRequestPermissionRationale:"+shouldShowRequestPermissionRationale);
            ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        } else {
            // 有权限了，去放肆吧。
            Toast.makeText(this, "有相机权限了", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
                    Toast.makeText(this, "获取到存储权限", Toast.LENGTH_SHORT).show();
                } else {
                    //是否去设置弹框是在这里处理的，因为第一次拒绝是返回true的，勾选了不在提示才返回false
                    boolean shouldShowRequestPermissionRationale = ActivityCompat
                            .shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    Log.w("TAG", "FileProviderActivity onRequestPermissionsResult " +
                            "shouldShowRequestPermissionRationale:"+shouldShowRequestPermissionRationale);
                    // 权限被用户拒绝了，洗洗睡吧。
                    Toast.makeText(this, "下载应用需要存储权限哦", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
                    Toast.makeText(this, "获取到相机权限", Toast.LENGTH_SHORT).show();
                } else {
                    // 权限被用户拒绝了，洗洗睡吧。
                    Toast.makeText(this, "拍照需要相机权限哦", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
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
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(fileUri,
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


}
