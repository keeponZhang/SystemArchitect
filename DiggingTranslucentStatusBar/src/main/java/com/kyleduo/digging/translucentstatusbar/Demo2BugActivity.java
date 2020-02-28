package com.kyleduo.digging.translucentstatusbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.MenuItem;

import butterknife.BindView;

/**
 * for DiggingTranslucentStatusBar
 * Created by kyleduo on 2017/5/5.
 */

public class Demo2BugActivity extends BaseActivity {

    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;


    @Override
    protected int getLayoutResId() {
        //4.4没问题

        // 5.0
        //坑1.1  如果Toolbar的app:layout_collapseMode属性保持为pin，那么ToolBar将不会按照预期进行偏移：
        // 这个问题其实是诸多原因导致的，我列在下面，同时后面标注了这个问题正确的处理方式，请对照源码阅读
        // CTL的onMeasure没有考虑WindowInsets，所以CTL的高度变小（比期望值小-63）。（应该考虑WindowInsets）

        //2CTL的内部类OffsetUpdateListener的方法onOffsetChanged在pin分支会对子View进行偏移（pin的实现就是根据ABL
        // 的偏移量同步改变子View的偏移量从而使子View“钉在”顶部的）。
        // 而此时，通过getMaxOffsetForPinChild方法获取到的Toolbar的最大偏移量的值，为-63（计算公式自己看源码吧，其实是问题1的副作用）。（getMaxOffsetForPinChild方法的返回值应该做边界检查，maxOffset不应为负值）
        // 3MathUtils.constrain(amount, low, high)方法（看参数名就知道什么意思了）的实现有bug：
        return R.layout.act_demo2_bug;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -mToolbar.getHeight() && mToolbar.getTranslationY() == 0) {
                    mToolbar.animate().translationY(dp2px(-16)).setDuration(100).start();
                } else if (verticalOffset > -mToolbar.getHeight() + mToolbar.getTop() && mToolbar.getTranslationY() == dp2px(-16)) {
                    mToolbar.animate().translationY(0).setDuration(100).start();
                }
            }
        });

    }

    private int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
