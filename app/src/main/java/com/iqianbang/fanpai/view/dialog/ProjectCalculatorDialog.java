package com.iqianbang.fanpai.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.view.NoDoubleClickListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 神仙有财
 * 功能描述: 验证码弹框选择器
 * 作 者:  李晓楠
 * 时 间： 2017/11/26 下午2:45
 */
public class ProjectCalculatorDialog extends Dialog {


    @BindView(R.id.tvMoney)
    TextView tvMoney;
    @BindView(R.id.tvIncome)
    TextView tvIncome;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.tv7)
    TextView tv7;
    @BindView(R.id.tv8)
    TextView tv8;
    @BindView(R.id.tv9)
    TextView tv9;
    @BindView(R.id.tvClearAll)
    TextView tvClearAll;
    @BindView(R.id.tv0)
    TextView tv0;
    @BindView(R.id.ivClearOne)
    ImageView ivClearOne;
    @BindView(R.id.ivClose)
    ImageView ivClose;

    private BigDecimal deadline;//出借期限
    private BigDecimal rate;//出借期限
    private Context mContext;


    public void setDeadline(BigDecimal deadline) {
        this.deadline = deadline;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public ProjectCalculatorDialog(@NonNull Context context) {
        super(context, R.style.BottomDialogStyle);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_project_calculator);
        ButterKnife.bind(this);
        /** 设置透明度 */
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 1.0f;// 透明度
        lp.dimAmount = 0.5f;// 黑暗度
        lp.gravity = Gravity.BOTTOM;
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        window.setAttributes(lp);

        tv1.setOnClickListener(viewclick);
        tv2.setOnClickListener(viewclick);
        tv3.setOnClickListener(viewclick);
        tv4.setOnClickListener(viewclick);
        tv5.setOnClickListener(viewclick);
        tv6.setOnClickListener(viewclick);
        tv7.setOnClickListener(viewclick);
        tv8.setOnClickListener(viewclick);
        tv9.setOnClickListener(viewclick);
        tvClearAll.setOnClickListener(viewclick);
        tv0.setOnClickListener(viewclick);
        ivClearOne.setOnClickListener(viewclick);
        ivClose.setOnClickListener(viewclick);

    }

    private NoDoubleClickListener viewclick = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            super.onNoDoubleClick(v);
           String money=tvMoney.getText().toString().trim();
            switch (v.getId()) {
                case R.id.tv1:
                    //1
                    tvMoney.setText(money+"1");
                    checkIncome(money);
                    break;
                case R.id.tv2:
                    //2
                    tvMoney.setText(money+"2");
                    checkIncome(money);
                    break;
                case R.id.tv3:
                    //3
                    tvMoney.setText(money+"3");
                    checkIncome(money);
                    break;
                case R.id.tv4:
                    //4
                    tvMoney.setText(money+"4");
                    checkIncome(money);
                    break;
                case R.id.tv5:
                    //5
                    tvMoney.setText(money+"5");
                    checkIncome(money);
                    break;
                case R.id.tv6:
                    //6
                    tvMoney.setText(money+"6");
                    checkIncome(money);
                    break;
                case R.id.tv7:
                    //7
                    tvMoney.setText(money+"7");
                    checkIncome(money);
                    break;
                case R.id.tv8:
                    //8
                    tvMoney.setText(money+"8");
                    checkIncome(money);
                    break;
                case R.id.tv9:
                    //9
                    tvMoney.setText(money+"9");
                    checkIncome(money);
                    break;
                case R.id.tvClearAll:
                    //删除all
                    tvMoney.setText("");
                    checkIncome(money);
                    break;
                case R.id.ivClearOne:
                    //删除一个
                    if(StringUtils.isNotBlank(money)){
                        try{
                            tvMoney.setText(money.substring(0,money.length()-1));
                        }catch(Exception e){
                            tvMoney.setText("");
                        }
                    }
                    checkIncome(money);
                    break;
                case R.id.tv0:
                    //0
                    if(StringUtils.isNotBlank(money)){
                        tvMoney.setText(money+"0");
                    }
                    checkIncome(money);
                    break;
                case R.id.ivClose:
                    dismiss();
                    break;
            }
        }
    };

    /**
     * 检查利息
     * @param moneystr
     */
    private void checkIncome(String moneystr){
        moneystr= tvMoney.getText().toString().trim();
        BigDecimal money;
        try {
            money=new BigDecimal(moneystr);
        } catch (Exception e) {
            e.printStackTrace();
            money=new BigDecimal("0");
        }

        BigDecimal baseinome = money.multiply(rate).multiply(deadline).divide(new BigDecimal("365"), 2, BigDecimal.ROUND_HALF_UP);
        tvIncome.setText(baseinome+"");
    }
}
