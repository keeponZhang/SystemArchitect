package com.example.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventbusdemo.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EventBusDemoActivity extends AppCompatActivity {
	private TextView textView;
	private Button   button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_bus_demo);
		//注册成为订阅者
		EventBus.getDefault().register(this);
		textView = (TextView) findViewById(R.id.tv_text);
		button = (Button) findViewById(R.id.secondActivityBtn);
		findViewById(R.id.thirdActivityBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventBusDemoActivity.this, EventBusThirdActivity.class);
				startActivity(intent);
			}
		});
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventBusDemoActivity.this, EventBusSecondActivity.class);
				startActivity(intent);
			}
		});
	}

	//订阅方法，当接收到事件的时候，会调用该方法
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(MessageEvent messageEvent){
		Log.d("cylog","receive it");
		textView.setText(messageEvent.getMessage());
		Toast.makeText(EventBusDemoActivity.this, messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		//解除注册
		EventBus.getDefault().unregister(this);
	}
}
