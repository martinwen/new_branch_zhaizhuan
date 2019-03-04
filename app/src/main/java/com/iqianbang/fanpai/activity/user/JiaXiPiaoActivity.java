package com.iqianbang.fanpai.activity.user;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMaxUseablefragment;
import com.iqianbang.fanpai.fragment.jiaxipiaoFragment.FtMinfragment;
import com.iqianbang.fanpai.view.FloatDragView;
import com.iqianbang.fanpai.view.dialog.JiaXiPiaoDuiHuanDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JiaXiPiaoActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_min)
    TextView tvTitleMin;
    @BindView(R.id.tv_title_max)
    TextView tvTitleMax;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.switch_fragments)
    FrameLayout switchFragments;
    @BindView(R.id.tv_shixiao)
    TextView tvShixiao;

    private RelativeLayout rl;
    private Fragment[] mPages = new Fragment[2];
    private PopupWindow pop;
    private String from = "min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jia_xi_piao);
        ButterKnife.bind(this);
        rl = (RelativeLayout) findViewById(R.id.rl);
        initData();
    }

    private void initData() {
        // 准备数据
        FtMinfragment ftMinfragment = new FtMinfragment();
        mPages[0] = ftMinfragment;

        FtMaxUseablefragment ftMaxUseablefragment = new FtMaxUseablefragment();
        mPages[1] = ftMaxUseablefragment;

        //兑换加息票
//        FloatDragView.addFloatDragView(this, rl, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JiaXiPiaoDuiHuanDialog jiaXiPiaoDuiHuanDialog = new JiaXiPiaoDuiHuanDialog(JiaXiPiaoActivity.this, R.style.YzmDialog);
//                jiaXiPiaoDuiHuanDialog.show();
//            }
//        });

        //初始化界面
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.switch_fragments, mPages[0])
                .commit();
    }

    @OnClick({R.id.iv_back, R.id.tv_title, R.id.tv_title_min, R.id.tv_title_max, R.id.tv_shixiao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_shixiao:
                Intent intent = new Intent(JiaXiPiaoActivity.this, JiaXiPiaoShiXiaoActivity.class);
                intent.putExtra("from",from);
                startActivity(intent);
                break;
            case R.id.tv_title_min:
                from = "min";
                llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_min);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.switch_fragments, mPages[0])
                        .commit();
                break;
            case R.id.tv_title_max:
                from = "max";
                llTitle.setBackgroundResource(R.drawable.jiaxipiao_title_max);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.switch_fragments, mPages[1])
                        .commit();
                break;
            case R.id.tv_title:
                View inflate = View.inflate(this, R.layout.activity_jiaxipiao_pop, null);
                TextView tv_up = (TextView) inflate.findViewById(R.id.tv_up);
                tv_up.setText("提现票");
                tv_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(JiaXiPiaoActivity.this, TiXianPiaoActivity.class));
                        finish();
                    }
                });
                TextView tv_middle = (TextView) inflate.findViewById(R.id.tv_middle);
                tv_middle.setText("优先购");
                tv_middle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(JiaXiPiaoActivity.this, YouXianGouActivity.class));
                        finish();
                    }
                });
                TextView tv_down = (TextView) inflate.findViewById(R.id.tv_down);
                tv_down.setText("红包");
                tv_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        startActivity(new Intent(JiaXiPiaoActivity.this, HongBaoActivity.class));
                        finish();
                    }
                });
                pop = new PopupWindow(inflate, dip2px(100), dip2px(125));
                // 设置焦点
                pop.setFocusable(true);
                // 设置背景，为了点击外面时，把Popupwindow消失
                pop.setBackgroundDrawable(new BitmapDrawable());
                // 展示Popupwindow,显示在输入框的下面
                pop.showAsDropDown(tvTitle, dip2px(-20), 0);
                break;
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
