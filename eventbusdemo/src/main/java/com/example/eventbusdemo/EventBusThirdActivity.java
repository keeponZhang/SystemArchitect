package com.example.eventbusdemo;

import android.util.Log;

import com.example.eventbusdemo.bean.MessageEvent;

import org.greenrobot.eventbus.Subscribe;

/**
 * createBy	 keepon
 */
public class EventBusThirdActivity extends EventBusSecondActivity {
	private static final String TAG = "EventBusThirdActivity";
	@Subscribe(sticky = true) //只会接受到最后发送的粘性事件，在此之前的事件都接收不到。
	public void onEvent2(MessageEvent messageEvent){
		Log.e(TAG,"EventBusThirdActivity onEvent2接受到了来自EventBus的事件："+messageEvent.getMessage());
	}
}
