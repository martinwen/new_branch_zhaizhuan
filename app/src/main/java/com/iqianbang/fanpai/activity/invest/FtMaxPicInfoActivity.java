package com.iqianbang.fanpai.activity.invest;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.BaseActivity;
import com.iqianbang.fanpai.bean.ImageInfo;
import com.iqianbang.fanpai.view.tupian.GvAdapter;
import com.iqianbang.fanpai.view.tupian.PicShowDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijinliu
 * @date 20180129
 * 标的项目图片信息
 */

public class FtMaxPicInfoActivity extends BaseActivity implements View.OnClickListener {

    private GridView mGridView;
    private List<ImageInfo> mImageList;
    private ImageView mTvback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ftmax_pic_info);

        initView();
        initData();
    }

    private void initView() {
        mTvback = (ImageView) findViewById(R.id.iv_back);// 返回
        mTvback.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gv_pic);
    }

    public void initData() {
        String[] picUrlAlls = getIntent().getStringArrayExtra("picUrlAll");
        mImageList = new ArrayList<>();
        for (int i=0;i<picUrlAlls.length;i++){
                ImageInfo imageInfo=new ImageInfo(picUrlAlls[i],200,200);
                mImageList.add(imageInfo);
        }
        mGridView.setSelector(android.R.color.transparent);
        mGridView.setAdapter(new GvAdapter(this, mImageList));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicShowDialog dialog = new PicShowDialog(FtMaxPicInfoActivity.this, mImageList, position);
                dialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
