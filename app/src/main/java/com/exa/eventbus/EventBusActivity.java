package com.exa.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.exa.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/eventbus/activity")
public class EventBusActivity extends AppCompatActivity {

    @BindView(R.id.tvEventShow)
    TextView tvShowMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnUpdate)
    void submit(){
        EventBus.getDefault().post(new MessageEvent("来自于EventBus的消息！"));
        EventBus.getDefault().post(new EventBean("使用EventBean传递的消息！"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(EventBean bean){
        System.out.println("EventBusActivity.EventBean ---->" + bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent){
        System.out.println("Event ----> 收到消息：" + messageEvent);
        tvShowMsg.setText(messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventEx(MessageEvent messageEvent){
        System.out.println("EventEx ----> 收到消息：" + messageEvent);
        tvShowMsg.setText(messageEvent.getMessage()+" prefix");
    }



}
