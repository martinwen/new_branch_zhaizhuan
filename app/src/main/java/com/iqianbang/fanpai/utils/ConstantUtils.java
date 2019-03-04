package com.iqianbang.fanpai.utils;

import java.net.URL;

public class ConstantUtils {

	/**
	 * 调试接口
	 */

	// 服务器测试地址
	public static final String BASE_URL = "http://test.zhixingzichan.com:8081/app.onigiri/app/v1";//一般接口
	public static final String BASE_WEIXIN_URL = "https://test.zhixingzichan.com/customer.onigiri";//微信页

	// 服务器正式地址
//	public static final String BASE_URL = "https://app.zhixingzichan.com/app/v1";//一般接口
//	public static final String BASE_WEIXIN_URL = "https://weixin.zhixingzichan.com/customer.onigiri";//微信页

	
	
	
	//1.刷新token
	public static final String REFRESHTOKEN_URL = BASE_URL + "/token/refresh";
	//2.新手机号注册
	public static final String REGISTER_URL = BASE_URL + "/user/register";
	//3.密码登录  
	public static final String MIMA_LOGIN_URL = BASE_URL + "/user/login";
	//4.验证码登录 
	public static final String YANZHENGMA_LOGIN_URL = BASE_URL + "/user/loginByVerifyCode";
	//5.短信或语音
	public static final String SMS_URL = BASE_URL + "/authcode/sendSms";
	//6.首页（饭盒）
	public static final String INDEX_URL = BASE_URL + "/index/index";
	//7.用户资金和个人信息
	public static final String MY_ACCOUNT_URL = BASE_URL + "/account/detail";
	//8.邀请奖励
	public static final String MY_INVITE_REWARD_URL = BASE_URL + "/invite/reward";
	//9.邀请详情
	public static final String MY_INVITE_DETAIL_URL = BASE_URL + "/invite/detail";
	//10.图形验证码
	public static final String CAPTCHA_URL = BASE_URL + "/authcode/captcha";
	//12.提现
	//12.1.获取提现用户信息(提现第一步)
		public static final String CASHVERIFYINFO_URL = BASE_URL + "/cash/cashVerifyInfo";
		//12.2.计算提现手续费（提现第二步）
		public static final String QUERYCASHFEE_URL = BASE_URL + "/cash/queryCashFee";
		//12.3.提现（提现第三步）
		public static final String SUBMITCASH_URL = BASE_URL + "/cash/submitCash";
	//13.出借的项目
	public static final String LOANS_URL = BASE_URL + "/account/loans";
	public static final String LOANDETAIL_URL = BASE_URL + "/account/loanDetail";
	//14.获取uuid
	public static final String GETUUID_URL = BASE_URL + "/account/uuid";
	//15.招商银行卡密鉴权
	public static final String GOTOAUTHCMB_URL = BASE_URL + "/invest/gotoAuthCmb";
	//17.发送买入短信验证码
	public static final String RETRIEVEMSG_URL = BASE_URL + "/invest/retrieveMsg";
	//18.银行卡列表和限额
	public static final String BANKLIST_URL = BASE_URL + "/index/banklist";
	//19.提现实名认证
	public static final String VERIFYIDNO_URL = BASE_URL + "/cash/verifyIdNo";
	//20.重置或设置提现密码
	public static final String SETCASHPWD_URL = BASE_URL + "/cash/setCashPwd";
	//21.退出登录
	public static final String LOGOUT_URL = BASE_URL + "/user/logout";
	//22.资金流水
	public static final String TRANSLIST_URL = BASE_URL + "/account/transList";
	//25.发现页
	public static final String FIND_URL = BASE_URL + "/advertise/list";
	//26.公告
	public static final String LIST_URL = BASE_URL + "/article/list";
	//27.公告详情
	public static final String LIST_DETAIL_URL = BASE_URL + "/article/detail";
	//28.找回登录密码
	public static final String FINDPWD_URL = BASE_URL + "/user/findPwd";
	//30.设置地址和邮编
	public static final String SETADDRESS_URL = BASE_URL + "/user/setAddress";
	//33.验证手机号码是否已存在
	public static final String VERIFYPHONE_URL = BASE_URL + "/user/verifyPhone";
	//34.验证身份证号是否已存在
	public static final String VERIFYUSERIDNO_URL = BASE_URL + "/user/verifyUserIdNo";
	//35.查看合同
	public static final String CONTRACT_URL = BASE_URL + "/account/contract";
	//36.检查更新
	public static final String CHECKUPDATE_URL = BASE_URL + "/index/checkUpdate";
	//37.出借记录
	public static final String INVESTRECORD_URL = BASE_URL + "/invest/investRecord";
	//38.我的银行卡
	public static final String BANKCARD_URL = BASE_URL + "/account/bankcard";
	//39.饭碗出借记录
	public static final String FW_INVEST_URL = BASE_URL + "/account/fwInvest/list";
	//40.饭盒出借界面
	public static final String FHINVESTINFO_URL = BASE_URL + "/invest/fhInvestInfo";
	//41.饭碗出借界面
	public static final String FWINVESTINFO_URL = BASE_URL + "/invest/fwInvestInfo";
	//42.饭盒赎回校验
	public static final String FHVERIFYINFO_URL = BASE_URL + "/rec/fhVerifyInfo";
	//43.饭盒赎回提交
	public static final String FHRECSUBMIT_URL = BASE_URL + "/rec/fhRecSubmit";
	//买入
	//44.买入（第一步，点击买入按钮）
	public static final String GOTOINVEST_URL = BASE_URL + "/invest/gotoInvest";
	//45.点击立即买入接口(买入第二步)
	public static final String CONFIRMINVEST_URL = BASE_URL + "/invest/confirmInvest";
	//46.确认支付(买入第三步)
	public static final String SUBMITINVEST_URL = BASE_URL + "/invest/submitInvest";
	//47.绑卡
	public static final String BINDCARD_URL = BASE_URL + "/invest/bindcard";
	//48.大额预约
	public static final String BOOK_URL = BASE_URL + "/invest/book";
	//49.修改登录密码
	public static final String UPDATEPWD_URL = BASE_URL + "/user/updatePwd";
	//50.加息票
	public static final String RATECOUPON_URL = BASE_URL + "/activity/rateCoupon/list";
	//51.打饭记录
	public static final String SIGNINLIST_URL = BASE_URL + "/activity/signIn/list";
	//52.打饭签到
	public static final String SIGNINSUBMIT_URL = BASE_URL + "/activity/signIn/submit";
	//54.使用饭盒加息票
	public static final String USEFHRATECOUPON_URL = BASE_URL + "/activity/useFhRateCoupon";
	//55.提现票
	public static final String TICKET_URL = BASE_URL + "/cash/ticket/list";
	//56.红包
	public static final String REDBAG_URL = BASE_URL + "/activity/redbag/list";
	//57.饭桶出借记录
	public static final String FT_INVEST_URL = BASE_URL + "/account/ftInvest/list";
	//58.饭桶出借界面
	public static final String FTINVESTINFO_URL = BASE_URL + "/invest/ftInvestInfo";
	//59.饭桶赎回手续费
	public static final String QUERYFTRECFEE_URL = BASE_URL + "/rec/queryFtRecFee";
	//60.饭桶赎回至卡或账户余额
	public static final String FTRECSUBMIT_URL = BASE_URL + "/rec/ftRecSubmit";
	//61.饭桶查看详情赎回手续费率
	public static final String FTRECFEERATE_URL = BASE_URL + "/rec/ftRecFeeRate";
	//62.饭桶预约校验
	public static final String FTBOOKVERIFYINFO_URL = BASE_URL + "/invest/ftBookVerifyInfo";
	//63.饭桶买入第1步
	public static final String FTINVESTSTEP1_URL = BASE_URL + "/invest/ftInvestStep1";
	//64.饭桶买入第2步
	public static final String FTINVESTSTEP2_URL = BASE_URL + "/invest/ftInvestStep2";
	//65.会员体系
	public static final String MEMBEREQUITY_URL = BASE_URL + "/member/equity";
	//66.提现进度
	public static final String CASHLIST_URL = BASE_URL + "/cash/list";
	//67.评测结果（是否已经测评,是返回相应数据）
	public static final String GETEVALUATIONINFOSTATUS_URL = BASE_URL + "/evaluation/getEvaluationInfoStatus";
	//68.加息票兑换码兑换加息票
	public static final String EXCHANGERATETICKET_URL = BASE_URL + "/activity/rateCode/exchangeRateTicket";
	//71.验证邮箱是否已存在
	public static final String ISEMAILEXIST_URL = BASE_URL + "/user/isEmailExist";
	//72.修改用户的邮箱
	public static final String UPDATEUSEREMAIL_URL = BASE_URL + "/user/updateUserEmail";
	//73.设置公告为已读
	public static final String SETHAVEREADOPER_URL = BASE_URL + "/article/setHaveReadOper";
	//74.站内信
	public static final String STATIONLETTER_URL = BASE_URL + "/stationLetter/list";
	//75.站内信详情
	public static final String STATIONLETTER_DETAIL_URL= BASE_URL + "/stationLetter/detail";
	//76.设置站内信为已读
	public static final String STATIONLETTER_HAVEREAD_URL= BASE_URL + "/stationLetter/setHaveReadOper";
	//77.优先购列表
	public static final String PREFERTICKET_URL= BASE_URL + "/activity/preferTicket/list";
	//78.优先购兑换码
	public static final String PREFERTICKET_EXCHANGE_URL= BASE_URL + "/activity/preferTicket/exchange";
	//79.小饭团转大饭团买入第一步
	public static final String FHTOFWINVESTSTEPONE_URL= BASE_URL + "/invest/fhToFwInvestStepOne";
	//80.小饭团转大饭团买入第二步
	public static final String FHTOFWINVESTSTEPTWO_URL= BASE_URL + "/invest/fhToFwInvestStepTwo";


	//大饭团买入第一步
	public static final String DFT_GOTOINVEST_URL= BASE_URL + "/invest/dft/gotoInvest";
	//大饭团买入第二步--绑卡买入
	public static final String DFT_BINDCARD_URL= BASE_URL + "/invest/dft/bindcard";
	//大饭团买入第二步--非绑卡买入
	public static final String DFT_CONFIRMINVEST_URL= BASE_URL + "/invest/dft/confirmInvest";
	//大饭团买入第三步--确认支付
	public static final String DFT_SUBMITINVEST_URL= BASE_URL + "/invest/dft/submitInvest";
	//大饭团产品-（集合标）列表
	public static final String SHOWDFTPROLISTINFO_URL= BASE_URL + "/productInfo/showDftProListInfo";
	//大饭团产品-（集合标）产品详情
	public static final String SHOWDFTPRODETAILINFO_URL= BASE_URL + "/productInfo/showDftProDetailInfo";
	//大饭团产品-（集合标）标的组成
	public static final String DFTPROMATHBORROWLIST_URL= BASE_URL + "/productInfo/dftProMathBorrowList";
	//大饭团产品-（集合标）债权详情
	public static final String SHOWBORROWDETAILINFO_URL= BASE_URL + "/productInfo/showBorrowDetailInfo";
	//大饭团产品-（我的）投资记录
	public static final String SHOWDFTINVESTRECORD_URL= BASE_URL + "/investRecord/showDftInvestRecord";
	//大饭团产品-（我的）转让赎回
	public static final String DFTRECSUBMIT_URL= BASE_URL + "/rec/dftRecSubmit";
	//饭团产品-（我的）投资详情明细信息
	public static final String DFTINVESTRECORDDETAIL_URL= BASE_URL + "/investRecord/dftInvestRecordDetail";
	//饭团产品-（我的）转让记录
	public static final String SHOWRECINFOLIST_URL= BASE_URL + "/rec/showRecInfoList";
	//大饭团-复投产品-（集合标）列表
	public static final String SHOWREPEATEDPROLISTINFO_URL= BASE_URL + "/productInfo/showRepeatedProListInfo";
	//复投买入第一步
	public static final String TRANSGOTOINVEST_URL= BASE_URL + "/invest/dft/transGotoInvest";
	//复投买入第二步
	public static final String TRANSCONFIRMINVEST_URL= BASE_URL + "/invest/dft/transConfirmInvest";
	//复投预约第一步
	public static final String BOOKGOTOINVEST_URL= BASE_URL + "/invest/dft/bookGotoInvest";
	//复投预约第二步
	public static final String BOOKCONFIRMINVEST_URL = BASE_URL + "/invest/dft/bookConfirmInvest";
	//小饭团赎回详情列表
	public static final String FHREDEEMLIST_URL = BASE_URL + "/rec/fhRedeemList";
	//小饭团赎回撤销
	public static final String RECREVOKE_URL = BASE_URL + "/rec/recRevoke";


	//切底层--我的
	public static final String NEWASSETSPAGE_URL= BASE_URL + "/account/newAssetsPage";
	//切底层--我的--出借记录
	public static final String SHOWSCATTERINVESTRECORDLIST_URL= BASE_URL + "/investRecord/showScatterInvestRecordList";
	//切底层--我的--出借记录明细
	public static final String SHOWSCATTERINVESTRECORDINFODETAIL_URL= BASE_URL + "/investRecord/showScatterInvestRecordInfoDetail";
	//切底层--散标--转让记录
	public static final String GETPROTRANINFOLIST_URL= BASE_URL + "/productInfo/getProTranInfoList";
	//切底层--散标--转让界面
	public static final String TRANSFERPAGEINFO_URL= BASE_URL + "/transfer/transferPageInfo";
	//切底层--散标--转让买入
	public static final String PROTRANSOPER_URL= BASE_URL + "/transfer/proTransOper";
	//切底层--散标--撤销转让
	public static final String CANCELTRANSFEROPERSCATTERPRO_URL= BASE_URL + "/investRecord/cancelTransferOperScatterPro";


	//转让专区-产品列表
	public static final String SHOWTRANSFERPROLISTINFO_URL= BASE_URL + "/productInfo/showTransferProListInfo";
	//转让专区-产品详情
	public static final String SHOWTRANSFERPRODETAILINFO_URL= BASE_URL + "/productInfo/showTransferProDetailInfo";
	//转让专区-买入第一步
	public static final String TRANSFER_GOTOINVEST_URL= BASE_URL + "/invest/transfer/gotoInvest";
	//转让专区-买入第二步
	public static final String TRANSFER_CONFIRMINVEST_URL= BASE_URL + "/invest/transfer/confirmInvest";
	//转让专区-买入第二步
	public static final String TRANSFER_BINDCARD_URL= BASE_URL + "/invest/transfer/bindcard";
	//转让专区-投资记录列表
	public static final String SHOWTRANSFERINVESTRECORDLIST_URL= BASE_URL + "/investRecord/showTransferInvestRecordList";
	//转让专区- 投资记录明细信息
	public static final String SHOWTRANSFERINVESTRECORDINFODETAIL_URL= BASE_URL + "/investRecord/showTransferInvestRecordInfoDetail";
	//120.转让专区-撤销转让操作
	public static final String CANCELTRANSFEROPERTRANSFERPRO_URL= BASE_URL + "/investRecord/cancelTransferOperTransferPro";



	//还款日历-日历数据
	public static final String REPAYCALENDARDATA_URL= BASE_URL + "/repayCalendar/repayCalendarData";
	//还款日历-X天列表数据
	public static final String GETONEDAYREPAYLIST_URL= BASE_URL + "/repayCalendar/getOneDayRepayList";
	//还款日历-汇总数据
	public static final String REPAYCALENDARSUMMARYDATA_URL= BASE_URL + "/repayCalendar/repayCalendarSummaryData";
	//还款日历-X月明细列表数据
	public static final String GETONEMONTHREPAYLIST_URL= BASE_URL + "/repayCalendar/getOneMonthRepayList";




	//出借页平台介绍
	public static final String PLATFORMINTRO = BASE_WEIXIN_URL + "/index/platformIntro.do";
	//出借页项目详情
	public static final String PROJECTDETAILS = BASE_WEIXIN_URL + "/index/goProjectDeatil.do";
	//出借页常见问题
	public static final String PROBLEMS = BASE_WEIXIN_URL + "/index/goCommonProblem.do";
	//帮助中心
	public static final String HELP = BASE_WEIXIN_URL + "/account/help.do";
	//更换银行卡
	public static final String BANKCHANGED = BASE_WEIXIN_URL + "/account/intcpt-changeCard1.do";
	//更换手机号
	public static final String PHONECHANGED = BASE_WEIXIN_URL + "/account/intcpt-myphonechange.do";
	//注册协议静态页
	public static final String REGISTRATIONAGREEMENT = BASE_WEIXIN_URL + "/userLR/goToServiceAgreement.do";
	//出借委托协议静态页
	public static final String AGREEMENTORIGHT = BASE_WEIXIN_URL + "/invest/goBondContract.do";
	//饭盒饭碗饭桶收益详情静态页
	public static final String FHRATE_URL = BASE_WEIXIN_URL + "/account/intcpt-gainCalculator.do";
	public static final String FWRATE_URL = BASE_WEIXIN_URL + "/account/intcpt-gainDftCalculator.do";
	public static final String FTRATE_URL = BASE_WEIXIN_URL + "/account/intcpt-gainFtCalculator.do";
	//饭桶预约答题静态页
	public static final String TOANSWERPAGE_URL = BASE_WEIXIN_URL + "/invest/toAnswerPage.do";
	//测评页
	public static final String EVALUATION_URL = BASE_WEIXIN_URL + "/userSetting/intcpt-evaluation.do";
	//天天饭局
	public static final String DAYDINNERINDEX_URL = BASE_WEIXIN_URL + "/dayDinnerActivity/intcpt-dayDinnerIndex.do";
	//大饭团产品-项目介绍
	public static final String TODFTPRODUCTINTROPAGE_URL = BASE_WEIXIN_URL + "/staticPageAction/toDftProductIntroPage.do";
	//大饭团产品-服务协议
	public static final String TODFTSERVICEPROTOCOLPAGE_URL = BASE_WEIXIN_URL + "/staticPageAction/toDftServiceProtocolPage.do";
	//80.大饭团产品-（我的）出借协议
	public static final String GOAGREEMENTORIGHT_URL= BASE_WEIXIN_URL + "/borrowinfo/intcpt-goAgreementOright.do";
	//80.大饭团产品-（我的）出借协议
	public static final String GOBONDCONTRACT_URL= BASE_WEIXIN_URL + "/invest/dft/goBondContract.do";
	//80.大饭团产品-（我的）服务协议
	public static final String TODFTSERVICEAGREEPAGE_URL= BASE_WEIXIN_URL + "/investRecord/intcpt-toDftServiceAgreePage.do";
	//80.网络借贷风险和禁止性行为提示书
	public static final String TOAGREEFORBID_URL= BASE_WEIXIN_URL + "/staticPageAction/toAgreeForbid.do";



}