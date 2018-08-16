package com.darren.architect_day11.simple3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darren.architect_day11.R;
import com.darren.architect_day11.simple2.PersonEat;
import com.darren.architect_day11.simple2.TeacherEat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WrapRecyclerView mRecyclerView;
    private List<Integer> mItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mItems = new ArrayList<>();
        for (int i=0;i<100;i++){
            mItems.add(i);
        }

        mRecyclerView = (WrapRecyclerView) findViewById(R.id.recycler_view);
        // 一般的写法new对象调用方法
        PersonEat eat = new PersonEat();
        TeacherEat teacherEat = new TeacherEat(eat);
        teacherEat.eat();
        // 装饰设计模式怎么写，一般情况都是把类对象作为构造参数传递
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 采用装饰设计模式，让其支持添加头部和底部
        /*RecyclerAdapter mRealAdapter = new RecyclerAdapter();
        WrapRecyclerAdapter wrapRecyclerAdapter = new WrapRecyclerAdapter(mRealAdapter);
        // setAdapter
        mRecyclerView.setAdapter(wrapRecyclerAdapter);*/
        mRecyclerView.setAdapter(new RecyclerAdapter());
        // 添加头部 有没有问题？
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_view,mRecyclerView,false);
        mRecyclerView.addHeaderView(headerView);
        View footerView = LayoutInflater.from(this).inflate(R.layout.layout_header_view,mRecyclerView,false);
        mRecyclerView.addFooterView(headerView);
        // 面向对象的六大基本原则在哪里？最少知识原则又在哪里呢？必须要像ListView让其支持
        // 不要把代码过度封装，在我看来，业务逻辑能分开就分开，底层和中间层不含业务逻辑的能封装就封装不用纠结过度封装
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_rv,parent,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.text.setText("position = "+mItems.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItems.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView text;
            public ViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
