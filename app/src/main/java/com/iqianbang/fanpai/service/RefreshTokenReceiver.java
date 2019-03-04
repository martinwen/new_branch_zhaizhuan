package com.iqianbang.fanpai.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.Map;

/**
 * 刷新token的广播接收器
 */
public class RefreshTokenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String url = ConstantUtils.REFRESHTOKEN_URL;
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", "");
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(url, null, map, new HttpBackBaseListener() {
			
			@Override
			public void onSuccess(String string) {
				JSONObject json = JSON.parseObject(string);
				String code = json.getString("code");
                if ("0".equals(code)) {

                    String token = json.getString("token");
                    CacheUtils.putString("token", token);
                }
            }
			
			@Override
			public void onError(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
