package com.iqianbang.fanpai.activity.home;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.bean.FindBean;
import com.iqianbang.fanpai.global.ImageLoaderOptions;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.BitmapScaleDialog;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;


public class InviteFriendActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    @BindView(R.id.ll_invite_detail)
    LinearLayout llInviteDetail;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_invest)
    TextView tvInvest;
    @BindView(R.id.money_today)
    TextView moneyToday;
    @BindView(R.id.money_total)
    TextView moneyTotal;
    @BindView(R.id.tv_invitecode)
    TextView tvInvitecode;
    @BindView(R.id.iv_saoma)
    ImageView ivSaoma;
    @BindView(R.id.iv_share_again)
    ImageView ivShareAgain;

    private CustomProgressDialog progressdialog;
    private String inviteCode;
    private String url;
    private String title;
    private String image;
    private String text;
    private Bitmap cBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);

        progressdialog = new CustomProgressDialog(this, "正在加载数据...");
        initData();
    }

    private void initData() {
        // 拿到banner图片
        requestServer();
        // 访问网络
        getDataFromServer();

    }

    private void requestServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }

        map.put("type", "5");
        map.put("pagination", "false");
        map.put("pageNum", "1");
        map.put("pageSize", "20");
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.FIND_URL, null, map,
                new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        LogUtils.i("邀请页banner图片===" + string);
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign, datastr);
                                if (isSuccess) {// 验签成功
                                    JSONObject data = JSON.parseObject(datastr);
                                    //list
                                    JSONArray getList = data.getJSONArray("list");
                                    ArrayList<FindBean> list = (ArrayList<FindBean>) JSONArray.parseArray(getList.toJSONString(), FindBean.class);

                                    String imgSrc = null;
                                    if (list.size() != 0) {
                                        imgSrc = list.get(0).getImgSrc();
                                    }
                                    //加载图片
                                    ImageLoader.getInstance().displayImage(imgSrc, ivBanner, ImageLoaderOptions.fadein_options);
                                } else {
                                    ToastUtils.toastshort("图片加载异常！");
                                }
                            }
                        } else {
                            String msg = json.getString("msg");
                            ToastUtils.toastshort(msg);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressdialog.dismiss();
                        ToastUtils.toastshort("图片加载失败！");
                    }
                });

    }

    /**
     * 字符串生成二维码
     */

    public static Bitmap Create2DCode(String str) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, 400, 400);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        final int WHITE = 0xFFFFFFFF;
        // 整体为黑色
        final int BLACK = 0xFF000000;
//		final int RED = 0xFFFF0000;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
//					pixels[y * width + x] = RED;
                    pixels[y * width + x - 2] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, WHITE);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void getDataFromServer() {
        Map<String, String> map = SortRequestData.getmap();
        String token = CacheUtils.getString("token", "");
        // 如果没有登录，直接return，不访问网络了
        if (TextUtils.isEmpty(token)) {
            return;
        }
        if (!progressdialog.isShowing()) {
            progressdialog.show();
        }
        map.put("token", token);
        String requestData = SortRequestData.sortString(map);
        String signData = SignUtil.sign(requestData);
        map.put("sign", signData);
        VolleyUtil.sendJsonRequestByPost(ConstantUtils.MY_INVITE_REWARD_URL,
                null, map, new HttpBackBaseListener() {

                    @Override
                    public void onSuccess(String string) {
                        // TODO Auto-generated method stub
                        progressdialog.dismiss();
                        JSONObject json = JSON.parseObject(string);
                        String code = json.getString("code");

                        if ("0".equals(code)) {
                            String datastr = json.getString("data");
                            if (StringUtils.isBlank(datastr)) {
                                // datastr为空不验签
                            } else {
                                String sign = json.getString("sign");
                                boolean isSuccess = SignUtil.verify(sign,
                                        datastr);
                                if (isSuccess) {// 验签成功
                                    LogUtils.i("邀请界面返回数据==" + datastr);
                                    JSONObject data = JSON.parseObject(datastr);

                                    // 邀请码
                                    inviteCode = data.getString("inviteCode");

                                    //二维码地址
                                    String qrcode = data.getString("qrcode");
                                    createEnglishQRCodeWithLogo(qrcode);


                                    // 邀请出借的人数
                                    String inviteInvestCount = data.getString("inviteInvestCount");
                                    tvInvest.setText(inviteInvestCount);

                                    // 邀请注册的人数
                                    String inviteRegCount = data.getString("inviteRegCount");
                                    tvRegister.setText(inviteRegCount);

                                    // 昨日邀请赚取
                                    String yesterdayRewardInviteIncome = data.getString("yesterdayRewardInviteIncome");
                                    moneyToday.setText(yesterdayRewardInviteIncome+" 元");

                                    // 累计邀请赚取
                                    String totalRewardInviteIncome = data.getString("totalRewardInviteIncome");
                                    moneyTotal.setText(totalRewardInviteIncome+" 元");

                                    // 分享
                                    JSONObject jsonObject = data.getJSONObject("share");
                                    url = jsonObject.getString("url");
                                    title = jsonObject.getString("title");
                                    image = jsonObject.getString("image");
                                    text = jsonObject.getString("text");

                                    // 复制邀请码方法
                                    linktext(tvInvitecode);

                                } else {
                                    ToastUtils.toastshort("加载数据异常！");
                                }
                            }
                        } else {
                            ToastUtils.toastshort("加载数据失败！");
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {
                        // TODO Auto-generated method stub

                    }

                });
    }

    private void linktext(TextView textView) {
        SpannableStringBuilder ss = new SpannableStringBuilder(inviteCode);
        ClickableSpan span = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(inviteCode);
                ToastUtils.toastshort("邀请码" + inviteCode + "已复制");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 去掉下划线
                ds.setUnderlineText(false);
                //设置文字颜色
                ds.setColor(0xff000000);
            }
        };
        // 参数2：span的开始位置（包含），参数3：span的结束位置 （不包含）
        ss.setSpan(span, 0, inviteCode.length(), 0);
        textView.setText(ss);
        // 让span 可以点击
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.iv_back, R.id.ll_invite_detail, R.id.iv_share, R.id.iv_share_again, R.id.iv_saoma})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
            case R.id.iv_share_again:
                FragmentManager fm = getSupportFragmentManager();
                ShareFrag shareFrag = new ShareFrag();
                shareFrag.show(fm, null);
                shareFrag.setData(url, title, image, text);//设置分享内容，这个方法可以根据各自需求传入不同数据进行处理。
                break;
            case R.id.ll_invite_detail:
                startActivity(new Intent(this, InviteDetailActivity.class));
                break;
            case R.id.iv_saoma:
                BitmapScaleDialog bitmapScaleDialog = new BitmapScaleDialog(this, R.style.YzmDialog, cBitmap);
                bitmapScaleDialog.show();
                break;
        }
    }

    private void createEnglishQRCodeWithLogo(final String qrcode) {
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap logoBitmap = BitmapFactory.decodeResource(InviteFriendActivity.this.getResources(), R.drawable.login_logo);
                return QRCodeEncoder.syncEncodeQRCode(qrcode, BGAQRCodeUtil.dp2px(InviteFriendActivity.this, 240), Color.BLACK, Color.WHITE, logoBitmap);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    cBitmap = bitmap;
                    ivSaoma.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(InviteFriendActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
