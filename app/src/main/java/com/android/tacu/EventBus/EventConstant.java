package com.android.tacu.EventBus;

/**
 * Created by jiazhen on 2018/9/3.
 */
public class EventConstant {

    //切换MainActivity当前的fragment
    public static final int MainSwitchCode = 1001;
    //MarketListFragment的isVisibleToUser的值
    public static final int MarkListVisibleCode = 1002;
    //详情页跳转交易页面的买卖页面
    public static final int JumpTradeIsBuyCode = 1003;
    //设置MainActivity的drawerlayout
    public static final int MainDrawerLayoutOpenCode = 1004;
    //提币查看记录页面按钮展示
    public static final int TakeCode = 1005;
    //homgfragment通知marketfragment和selffragment刷新
    public static final int HomeNotifyCode = 1007;
    //购买vip，子fragment通知父Activity刷新
    public static final int VipBuyCode = 1009;
    //刷新订单详情
    public static final int OTCDetailCode = 1013;
    //刷新申请商户的页面
    public static final int AuthMerchant = 1014;
    //邀请列表
    public static final int Invited_Record = 1015;
}
