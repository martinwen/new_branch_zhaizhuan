package com.iqianbang.fanpai.fragment.zhanneixin;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iqianbang.fanpai.R;
import com.iqianbang.fanpai.activity.user.ZhanNeiXinDetailActivity;
import com.iqianbang.fanpai.adapter.zhanneixin.ZhanNeiXinAdapter;
import com.iqianbang.fanpai.bean.ZhanNeiXinBean;
import com.iqianbang.fanpai.fragment.BaseFragment;
import com.iqianbang.fanpai.sign.SignUtil;
import com.iqianbang.fanpai.sign.SortRequestData;
import com.iqianbang.fanpai.utils.CacheUtils;
import com.iqianbang.fanpai.utils.ConstantUtils;
import com.iqianbang.fanpai.utils.LogUtils;
import com.iqianbang.fanpai.utils.StringUtils;
import com.iqianbang.fanpai.utils.ToastUtils;
import com.iqianbang.fanpai.view.dialog.CustomProgressDialog;
import com.iqianbang.fanpai.volley.HttpBackBaseListener;
import com.iqianbang.fanpai.volley.VolleyUtil;

import java.util.ArrayList;
import java.util.Map;


public class QuanBuFragment extends BaseFragment implements View.OnClickListener {

	private RelativeLayout ll_sign;
	private TextView tv_sign;
	private TextView tv_all;
	private TextView tv_cancel;
	private PullToRefreshListView refreshListView;
	private ListView listView;
	private CustomProgressDialog progressdialog;
	private ArrayList<ZhanNeiXinBean> list = new ArrayList<ZhanNeiXinBean>();
	private ZhanNeiXinAdapter adapter;
	private int pagenum=1;
	private int pagesize=20;
	private int pages;
	// 是否全选
	private boolean selectAll;


	private int num;//记录点击的条目位置

	@Override
	protected View initView() {
		LogUtils.i("initView走了==");
		progressdialog = new CustomProgressDialog(mActivity, "正在获取数据...");
		View view = View.inflate(mActivity, R.layout.fragment_guanfang, null);

		ll_sign = (RelativeLayout) view.findViewById(R.id.ll_sign);
		tv_sign = (TextView) view.findViewById(R.id.tv_sign);
		tv_sign.setOnClickListener(QuanBuFragment.this);
		tv_all = (TextView) view.findViewById(R.id.tv_all);
		tv_all.setOnClickListener(QuanBuFragment.this);
		refreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);

		// 1.设置刷新模式,上拉和下拉刷新都有
		refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		// 2.设置刷新监听器
		refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			// 下拉刷新和加载更多都会走这个方法
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// 直接请求
				pagenum++;
				if(pagenum>pages){
					refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
				}
				getDataFromServer();

			}
		});
		// 3.获取refreshableView,其实就是ListView
		listView = refreshListView.getRefreshableView();
		// 5.设置adapter
		adapter=new ZhanNeiXinAdapter(mActivity, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new NoticeListener());

		if (refreshListView.getMode()!= PullToRefreshBase.Mode.DISABLED) {
			//当切换到此页时会走initdata方法，会导致数据重复添加而显示，所以需要清楚数据，又因为当下拉后pagenum自增，请求第二页数据，数据为空，
			//这时不能把之前的list清除掉，所以才这样判断
			list.clear();
			pagenum = 1;
		}
		//初始化数据
		getDataFromServer();
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_sign:
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < list.size(); i++) {
					if (ZhanNeiXinAdapter.getIsSelected().get(i)) {
						sb.append(list.get(i).getId()).append(",");
					}
				}
				String articleIds = sb.toString();
				articleIds = articleIds.substring(0,articleIds.length()-1);
				sign(articleIds);
				break;
			case R.id.tv_all:
				selectAll = !selectAll;
				if(selectAll){
					tv_all.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.checkbox_checked),null,null,null);
					for (int i = 0; i < list.size(); i++) {// 遍历list的长度，全选
						ZhanNeiXinAdapter.getIsSelected().put(i, true);
					}
				}else{
					ll_sign.setVisibility(View.GONE);
					tv_all.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.checkbox_normal),null,null,null);
					for (int i = 0; i < list.size(); i++) {// 遍历list的长度，全不选
						ZhanNeiXinAdapter.getIsSelected().put(i, false);
					}
				}
				adapter.notifyDataSetChanged();
				break;
		}
	}

	private void sign(String articleIds) {
		progressdialog.show();
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", null);
		map.put("token", token);
		map.put("letterIds", articleIds);
		map.put("selectAll", selectAll?"1":"0");
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.STATIONLETTER_HAVEREAD_URL, null, map,
				new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						progressdialog.dismiss();

						JSONObject json = JSON.parseObject(string);
						String  code= json.getString("code");
						if("0".equals(code)){
							for (int i = 0; i < list.size(); i++) {
								if (ZhanNeiXinAdapter.getIsSelected().get(i)) {
									list.get(i).setStatus("2");
									ZhanNeiXinAdapter.getIsSelected().put(i, false);
								}
							}
							adapter.notifyDataSetChanged();
							ll_sign.setVisibility(View.GONE);

							selectAll = false;
						}else{
							String msg = json.getString("msg");
							ToastUtils.toastshort(msg);
						}
					}

					@Override
					public void onError(VolleyError error) {
						progressdialog.dismiss();
						ToastUtils.toastshort("网络异常");
					}
				});
	}

	class NoticeListener implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			num=position - 1;
			ZhanNeiXinBean zhanNeiXinBean = list.get(position - 1);
			//公告详情页面
			String id_items = zhanNeiXinBean.getId();

			Intent intent = new Intent(mActivity,ZhanNeiXinDetailActivity.class);
			intent.putExtra("id_items", id_items);
			startActivityForResult(intent,1);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			if(list.size()>0){
				list.get(num).setStatus("2");
				adapter.notifyDataSetChanged();
				listView.setSelection(num);
			}
		}
	}

	private void getDataFromServer() {
		Map<String, String> map = SortRequestData.getmap();
		String token = CacheUtils.getString("token", null);
		map.put("pageNum", pagenum+"");
		map.put("pageSize", pagesize+"");
		map.put("type", "0");
		map.put("token", token);
		String requestData = SortRequestData.sortString(map);
		String signData = SignUtil.sign(requestData);
		map.put("sign", signData);
		VolleyUtil.sendJsonRequestByPost(ConstantUtils.STATIONLETTER_URL,
				null, map, new HttpBackBaseListener() {

					@Override
					public void onSuccess(String string) {
						LogUtils.i("string=="+string);
						refreshListView.onRefreshComplete();
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
									JSONObject data = JSON.parseObject(datastr);
									//当前页码
									String pageNum = data.getString("pageNum");
									//每页条数
									String pageSize = data.getString("pageSize");
									//总页数
									pages = data.getInteger("pages");
									// 总条数
									int total = data.getInteger("total");
									//list
									JSONArray getList=data.getJSONArray("list");
									ArrayList<ZhanNeiXinBean> listadd = (ArrayList<ZhanNeiXinBean>) JSONArray.parseArray(getList.toJSONString(), ZhanNeiXinBean.class);
									list.addAll(listadd);
									adapter.setList(list,selectAll);
									adapter.setsubClickListener(new ZhanNeiXinAdapter.SubClickListener() {
										@Override
										public void OntopicClickListener() {
											for (int i = 0; i < ZhanNeiXinAdapter.getIsSelected().size(); i++) {
												if (ZhanNeiXinAdapter.getIsSelected().get(i)) {
													ll_sign.setVisibility(View.VISIBLE);
													return;
												}
											}
											ll_sign.setVisibility(View.GONE);
										}
									});

									adapter.notifyDataSetChanged();

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
						refreshListView.onRefreshComplete();
						ToastUtils.toastshort("加载数据失败！");
					}
				});
	}
}
