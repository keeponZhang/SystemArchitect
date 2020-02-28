package com.kyleduo.digging.translucentstatusbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.digging.translucentstatusbar.widgets.OnItemClickListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.main_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.main_status_bar_stub)
    ViewStub mStatusBarStub;
    View mStatusBarOverlay;

    @BindView(R.id.main_drawer)
    LinearLayout mDrawer;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    // 5.0 CollapsingToolbarLayout true,设置了标记的子View会在StatusBar下面（under）绘制(正常绘制)
    // ，没有设置标记的子View会被挤下去（down）。


    //4.4背景色是深蓝色
    @Override
    protected int getLayoutResId() {
        // 4.4：深度遍历，第一个遇到的View是ABL，执行View的默认逻辑，设置paddingTop。所以露出了背景颜色，同时子View都被挤到了下面。
        // 5.0：因为CTL设置了true，而且子View也都设置了true，所以TB和IV都在StatusBar下面绘制。：因为CTL设置了true，而且子View也都设置了true，所以TB和IV都在StatusBar下面绘制。
        // return R.layout.act_main_all_true;

        // 4.4：深度遍历，第一个遇到的View是ABL，执行View的默认逻辑，设置paddingTop。所以露出了背景颜色，同时子View都被挤到了下面。
        // 5.0：会显示正常，status_bar是fasle，只有status_bar向下移。
        // return R.layout.act_main_status_bar_false_iv_true; //其他两个都是true

        // 4.4：深度遍历，第一个遇到的View是ABL，执行View的默认逻辑，设置paddingTop。所以露出了背景颜色，同时子View都被挤
        //5.0：会显示不正常，iv是fasle，只有iv向下移，状态栏被遮挡。
        // return R.layout.act_main_iv_false_status_bar_true;

        //4.4正常，其他都在状态栏下面绘制，只有status_bar向下移
        //5.0：会显示不正常正常，最前面的控件都是false，status_bar为true也没用，因为事件没有分发下来。(应该第一个改成true，并且iv改成true
        // (不然iv会偏移)，statar_bar改成false（只有status_bar应该偏移））
        // return R.layout.act_main_all_false_status_bar_true;
        //正确做法，根据版本适配
        return R.layout.act_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_burger));
        }

        mCollapsingToolbarLayout.setExpandedTitleColor(0x00FFFFFF);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new DummyAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    startActivity(new Intent(MainActivity.this, Demo1Activity.class));
                }  else if (position == 1) {
                    startActivity(new Intent(MainActivity.this, Demo2BugActivity.class));
                }
                else if (position == 2) {
                    startActivity(new Intent(MainActivity.this, Demo2Activity.class));
                } else if (position == 3) {
                    startActivity(new Intent(MainActivity.this, Demo3Activity.class));
                } else if (position == 4) {
                    startActivity(new Intent(MainActivity.this, Demo3BActivity.class));
                }
            }
        }));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            //4.4 避免status_bar离上面太远，加一个蒙层
            mStatusBarStub.inflate();
            mStatusBarOverlay = findViewById(R.id.main_status_bar_overlay);
            mStatusBarOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mStatusBarOverlay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = mStatusBarOverlay.getLayoutParams();
                    layoutParams.height = mToolbar.getPaddingTop();
                }
            });
        }
    }

    @OnClick(R.id.main_header)
    public void clickHeader() {
        startActivity(new Intent(this, SecondActivity.class));
    }


    static class DummyItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView tv;

        public DummyItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class DummyAdapter extends RecyclerView.Adapter<DummyItemViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        private DummyAdapter(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public DummyItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final DummyItemViewHolder holder = new DummyItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, holder, holder.getAdapterPosition());
                    }
                }
            });
            return holder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(DummyItemViewHolder holder, int position) {
            if (position == 0) {
                holder.tv.setText("CTL titleEnable == true");
            } else if (position == 1) {
                holder.tv.setText("No header. Has TabLayout Bug");
            } else if (position == 2) {
                holder.tv.setText("No header. Has TabLayout");
            }
            else if (position == 3) {
                holder.tv.setText("Use in Fragment");
            } else if (position == 4) {
                holder.tv.setText("Use in Fragment without toolbar");
            } else {
                holder.tv.setText(String.format(Locale.getDefault(), "Item: %d", position));
            }
        }

        @Override
        public int getItemCount() {
            return 40;
        }
    }
}
