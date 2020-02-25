package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcManagePresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcManageFragment extends BaseFragment<OtcManagePresenter> implements OtcManageContract.IChildView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ManageAdapter manageAdapter;
    //0=全部 1买 2卖
    private int buyorsell = 0;
    private int start = 1;

    private List<OtcMarketOrderModel> orderList = new ArrayList<>();

    public static OtcManageFragment newInstance(int buyorsell) {
        Bundle bundle = new Bundle();
        bundle.putInt("buyorsell", buyorsell);
        OtcManageFragment fragment = new OtcManageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            buyorsell = bundle.getInt("buyorsell", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage;
    }

    @Override
    protected void initData(View view) {
        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                start = 1;
                upload(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload(false);
            }
        });

        manageAdapter = new ManageAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(manageAdapter);
    }

    @Override
    protected OtcManagePresenter createPresenter(OtcManagePresenter mPresenter) {
        return new OtcManagePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            upload(true);
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshManage != null && (refreshManage.isRefreshing() || refreshManage.isLoading())) {
            refreshManage.finishRefresh();
            refreshManage.finishLoadmore();
        }
    }

    @Override
    public void orderListOwn(OtcMarketOrderListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                if (orderList != null && orderList.size() > 0) {
                    orderList.addAll(model.list);
                } else {
                    orderList = model.list;
                }
                if (orderList != null && orderList.size() > 0) {
                    manageAdapter.setNewData(orderList);
                    if (orderList.size() >= model.total) {
                        refreshManage.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshManage.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                manageAdapter.setNewData(null);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            manageAdapter.setNewData(null);
            refreshManage.setEnableLoadmore(false);
        }
    }

    private void upload(boolean isShowView) {
        if (start == 1 && orderList != null && orderList.size() > 0) {
            orderList.clear();
        }
        mPresenter.orderListOwn(isShowView, start, 10, buyorsell);
    }

    public class ManageAdapter extends BaseQuickAdapter<OtcMarketOrderModel, BaseViewHolder> {

        public ManageAdapter() {
            super(R.layout.item_otc_manage);
        }

        @Override
        protected void convert(BaseViewHolder holder, OtcMarketOrderModel item) {
            holder.setText(R.id.tv_coin_name, item.currencyName);
            if (item.buyorsell != null && item.buyorsell == 1) {
                holder.setText(R.id.tv_status, getResources().getString(R.string.buy));
            } else if (item.buyorsell != null && item.buyorsell == 2) {
                holder.setText(R.id.tv_status, getResources().getString(R.string.sell));
            }
            holder.setText(R.id.tv_time, item.createTime);
            if (item.payByCard != null && item.payByCard == 1) {
                holder.setGone(R.id.img_yhk, true);
            } else {
                holder.setGone(R.id.img_yhk, false);
            }
            if (item.payWechat != null && item.payWechat == 1) {
                holder.setGone(R.id.img_wx, true);
            } else {
                holder.setGone(R.id.img_wx, false);
            }
            if (item.payAlipay != null && item.payAlipay == 1) {
                holder.setGone(R.id.img_zfb, true);
            } else {
                holder.setGone(R.id.img_zfb, false);
            }

            holder.setText(R.id.tv_dealed_title, getResources().getString(R.string.dealed_status) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_dealed, item.tradeAmount);
            holder.setText(R.id.tv_finish, item.finishNum);
            holder.setText(R.id.tv_wait_deal, item.waitNum);

            holder.setText(R.id.tv_num_title, getResources().getString(R.string.biao_num) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_num, item.num);
            holder.setText(R.id.tv_single_price, item.price);
            holder.setText(R.id.tv_surplus_amount_title, getResources().getString(R.string.biao_surplus_num) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_surplus_amount, item.remainAmount);

            String valueWei = "";
            if (item.money == 1) {
                valueWei = "CNY";
            }
            holder.setText(R.id.tv_total_value_title, getResources().getString(R.string.total_value) + "(" + valueWei + ")");
            holder.setText(R.id.tv_total_value, item.amount);
            holder.setText(R.id.tv_trade_limit, item.lowLimit + "~" + item.highLimit);
        }
    }
}
