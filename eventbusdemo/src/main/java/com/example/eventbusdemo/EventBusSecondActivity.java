package com.example.eventbusdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.eventbusdemo.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EventBusSecondActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = "EventBusSecondActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_bus_second);
		Button button = (Button) findViewById(R.id.sendMessageBtn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new MessageEvent("Hello !....."));
			}
		});

		Button button1 = (Button) findViewById(R.id.sendStickyMessageBtn1);
		Button button2 = (Button) findViewById(R.id.sendStickyMessageBtn2);
		Button button3 = (Button) findViewById(R.id.sendStickyMessageBtn3);
		Button button4 = (Button) findViewById(R.id.sendRegisterBtn);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.sendStickyMessageBtn1:
				EventBus.getDefault().postSticky(new MessageEvent("粘性事件1"));
				Log.e(TAG,"发送粘性事件1...");
				break;
			case R.id.sendStickyMessageBtn2:
				EventBus.getDefault().postSticky(new MessageEvent("粘性事件2"));
				Log.e(TAG, "发送粘性事件2...");
				break;
			case R.id.sendStickyMessageBtn3:
				EventBus.getDefault().postSticky(new MessageEvent("粘性事件3"));
				Log.e(TAG, "发送粘性事件3...");
				break;
			case R.id.sendRegisterBtn:
				Log.e(TAG, "注册成为订阅者...");
				EventBus.getDefault().register(this);

				break;
		}
	}

	@Subscribe(sticky = true) //只会接受到最后发送的粘性事件，在此之前的事件都接收不到。
	public void onEvent(MessageEvent messageEvent){
		Log.e(TAG,"EventBusSecondActivity onEvent 接受到了来自EventBus的事件："+messageEvent.getMessage());
	}
	@Subscribe(sticky = true) //只会接受到最后发送的粘性事件，在此之前的事件都接收不到。
	public void onEvent2(MessageEvent messageEvent){
		Log.e(TAG,"EventBusSecondActivity onEvent2接受到了来自EventBus的事件："+messageEvent.getMessage());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
