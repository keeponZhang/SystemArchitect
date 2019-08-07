package com.example.okio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.copymoudlue.R;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class OkioActivity extends AppCompatActivity {

    private String mAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okio);
    }

    public void read(View view) {
        try {
            //FileInputStream
            Source source = null;
            BufferedSource bSource = null;
            File file = new File(mAbsolutePath);
            //读文件
            source = Okio.source(file);
            //通过source拿到 bufferedSource
            bSource = Okio.buffer(source);
            String read = bSource.readString(Charset.forName("utf-8"));
            Toast.makeText(this,"读出来的数据:"+read,Toast.LENGTH_LONG).show();
        }catch (Exception e){

        }

    }

    public void write(View view) {
        String filename = "create.txt";
        boolean isCreate = false;
        Sink sink;
        BufferedSink bSink = null;
        try {
            //判断文件是否存在，不存在，则新建！
            File externalCacheDir = getExternalCacheDir();
            File file = new File(externalCacheDir,filename);
            if (!file.exists()) {
                isCreate = file.createNewFile();
            } else {
                isCreate = true;
            }
            mAbsolutePath = file.getAbsolutePath();
            //写入操作
            if (isCreate) {
                sink = Okio.sink(file);
                bSink = Okio.buffer(sink);
                bSink.writeUtf8("1");
                bSink.writeUtf8("\n");
                bSink.writeUtf8("this is new file!");
                bSink.writeUtf8("\n");
                bSink.writeString("我是每二条", Charset.forName("utf-8"));
                bSink.flush();
                Toast.makeText(this,"写入成功:",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bSink) {
                    bSink.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
