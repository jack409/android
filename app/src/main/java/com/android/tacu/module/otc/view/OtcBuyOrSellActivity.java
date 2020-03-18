package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcBuyOrSellPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcBuyOrSellActivity extends BaseOtcHalfOrderActvity<OtcBuyOrSellPresenter> implements OtcBuyOrSellContract.IView {

    @BindView(R.id.tv_plan_buy_amount_title)
    TextView tv_plan_buy_amount_title;
    @BindView(R.id.tv_remaining_unsold_title)
    TextView tv_remaining_unsold_title;
    @BindView(R.id.tv_remaining_all_title)
    TextView tv_remaining_all_title;

    @BindView(R.id.tv_single_price)
    TextView tv_single_price;
    @BindView(R.id.tv_plan_buy_amount)
    TextView tv_plan_buy_amount;
    @BindView(R.id.tv_remaining_unsold)
    TextView tv_remaining_unsold;
    @BindView(R.id.tv_remaining_all)
    TextView tv_remaining_all;
    @BindView(R.id.tv_single_min_limit)
    TextView tv_single_min_limit;
    @BindView(R.id.tv_single_max_limit)
    TextView tv_single_max_limit;
    @BindView(R.id.tv_seller_coined_time_limit)
    TextView tv_seller_coined_time_limit;

    @BindView(R.id.img_wx)
    ImageView img_wx;
    @BindView(R.id.img_zfb)
    ImageView img_zfb;
    @BindView(R.id.img_yhk)
    ImageView img_yhk;

    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.tv_sell_number_title)
    TextView tv_sell_number_title;
    @BindView(R.id.edit_sell_number)
    EditText edit_sell_number;
    @BindView(R.id.tv_sell_allmoney_title)
    TextView tv_sell_allmoney_title;
    @BindView(R.id.edit_sell_allmoney)
    EditText edit_sell_allmoney;

    @BindView(R.id.btn_confirm)
    QMUIRoundButton btn_confirm;

    private boolean isBuy = true;
    private String orderId;
    private List<String> tabTitle = new ArrayList<>();

    private OtcMarketOrderAllModel allModel;
    //防止EditText和EditText死循环
    private boolean isInputNum = true;
    private boolean isInputAmount = true;
    private boolean isInputAll = true;

    public static Intent createActivity(Context context, boolean isBuy, String orderId) {
        Intent intent = new Intent(context, OtcBuyOrSellActivity.class);
        intent.putExtra("isBuy", isBuy);
        intent.putExtra("orderId", orderId);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_buy_sell);
    }

    @Override
    protected void initView() {
        isBuy = getIntent().getBooleanExtra("isBuy", true);
        orderId = getIntent().getStringExtra("orderId");

        if (isBuy) {
            mTopBar.setTitle(getResources().getString(R.string.otc_buy_page));
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
            tv_plan_buy_amount_title.setText(getResources().getString(R.string.all_num));
            tv_remaining_unsold_title.setText(getResources().getString(R.string.remind_num));
            tv_remaining_all_title.setText(getResources().getString(R.string.remind_all));
            tv_sell_number_title.setText(getResources().getString(R.string.buy_number));
            edit_sell_number.setHint(getResources().getString(R.string.input_buy_number));
            tv_sell_allmoney_title.setText(getResources().getString(R.string.buy_all_money));
            edit_sell_allmoney.setHint(getResources().getString(R.string.input_buy_all_money));
            btn_confirm.setText(getResources().getString(R.string.confirm_buy));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.otc_sell_page));
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
            tv_plan_buy_amount_title.setText(getResources().getString(R.string.plan_buy_amount));
            tv_remaining_unsold_title.setText(getResources().getString(R.string.remaining_unsold));
            tv_remaining_all_title.setText(getResources().getString(R.string.remaining_unsold_all));
            tv_sell_number_title.setText(getResources().getString(R.string.sell_number));
            edit_sell_number.setHint(getResources().getString(R.string.input_sell_number));
            tv_sell_allmoney_title.setText(getResources().getString(R.string.sell_all_money));
            edit_sell_allmoney.setHint(getResources().getString(R.string.input_sell_all_money));
            btn_confirm.setText(getResources().getString(R.string.confirm_sell));
        }

        tabTitle.add(getResources().getString(R.string.zhifubao));
        tabTitle.add(getResources().getString(R.string.weixin));
        tabTitle.add(getResources().getString(R.string.yinhanngka));

        edit_sell_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInputNum = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInputAmount && isInputAll && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        double value = MathHelper.mul(Double.parseDouble(s.toString()), Double.parseDouble(allModel.orderModel.price));
                        edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputNum = true;
            }
        });
        edit_sell_allmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInputAmount = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInputNum && isInputAll && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        double value = MathHelper.div(Double.parseDouble(s.toString()), Double.parseDouble(allModel.orderModel.price));
                        edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputAmount = true;
            }
        });
    }

    @Override
    protected OtcBuyOrSellPresenter createPresenter(OtcBuyOrSellPresenter mPresenter) {
        return new OtcBuyOrSellPresenter();
    }

    @Override
    protected void onPresenterCreated(OtcBuyOrSellPresenter presenter) {
        super.onPresenterCreated(presenter);

        mPresenter.otcAmount(Constant.ACU_CURRENCY_ID);
        mPresenter.orderListOne(orderId);
    }

    @OnClick(R.id.btn_all_number)
    void btnAllNumber() {
        if (allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price) && !TextUtils.isEmpty(allModel.orderModel.remainAmount) && !TextUtils.isEmpty(allModel.orderModel.remainAmount)) {
            isInputAll = false;
            double remainAmountValue = MathHelper.mul(Double.parseDouble(allModel.orderModel.remainAmount), Double.parseDouble(allModel.orderModel.price));
            double value;
            if (remainAmountValue <= Double.parseDouble(allModel.orderModel.highLimit)) {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, remainAmountValue));
                value = MathHelper.div(remainAmountValue, Double.parseDouble(allModel.orderModel.price));
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, value));
            } else {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.highLimit));
                value = MathHelper.div(Double.parseDouble(allModel.orderModel.highLimit), Double.parseDouble(allModel.orderModel.price));
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, value));
            }
            isInputAll = true;
        }
    }

    @OnClick(R.id.btn_allmoney)
    void btnAllmoney() {
        if (allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price) && !TextUtils.isEmpty(allModel.orderModel.remainAmount) && !TextUtils.isEmpty(allModel.orderModel.remainAmount)) {
            isInputAll = false;
            double remainAmountValue = MathHelper.mul(Double.parseDouble(allModel.orderModel.remainAmount), Double.parseDouble(allModel.orderModel.price));
            double value;
            if (remainAmountValue <= Double.parseDouble(allModel.orderModel.highLimit)) {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, remainAmountValue));
                value = MathHelper.div(remainAmountValue, Double.parseDouble(allModel.orderModel.price));
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, value));
            } else {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.highLimit));
                value = MathHelper.div(Double.parseDouble(allModel.orderModel.highLimit), Double.parseDouble(allModel.orderModel.price));
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, value));
            }
            isInputAll = true;
        }
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        try {
            String num = edit_sell_number.getText().toString();
            String amount = edit_sell_allmoney.getText().toString();
            if (TextUtils.isEmpty(num)) {
                showToastError(getResources().getString(R.string.input_sell_number));
                return;
            }
            if (TextUtils.isEmpty(amount)) {
                showToastError(getResources().getString(R.string.sell_all_money));
                return;
            }
            if (allModel != null && allModel.orderModel != null) {
                if (TextUtils.isEmpty(allModel.orderModel.lowLimit) || Double.parseDouble(amount) < Double.parseDouble(allModel.orderModel.lowLimit)) {
                    showToastError(getResources().getString(R.string.exceeding_min_limit));
                    return;
                }
                if (TextUtils.isEmpty(allModel.orderModel.highLimit) || Double.parseDouble(amount) > Double.parseDouble(allModel.orderModel.highLimit)) {
                    showToastError(getResources().getString(R.string.exceeding_max_limit));
                    return;
                }
            }
            jumpTo(OtcOrderCreateActivity.createActivity(this, isBuy, FormatterUtils.getFormatRoundDown(2, num), FormatterUtils.getFormatRoundDown(2, amount), allModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_return)
    void returnClick() {
        finish();
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void orderListOne(OtcMarketOrderAllModel model) {
        this.allModel = model;
        if (allModel != null && allModel.infoModel != null) {
            setInfoValue(isBuy, allModel.infoModel);
        }
        setOrderInfo();
    }

    public void setOrderInfo() {
        if (allModel != null) {
            OtcMarketOrderModel orderModel = allModel.orderModel;
            String valueWei = " " + Constant.CNY;
            if (orderModel.money != null && orderModel.money == 1) {
                valueWei = " " + Constant.CNY;
            }
            tv_single_price.setText(orderModel.price + valueWei);
            tv_plan_buy_amount.setText(orderModel.num + " " + orderModel.currencyName);
            tv_remaining_unsold.setText(orderModel.remainAmount + " " + orderModel.currencyName);
            if (!TextUtils.isEmpty(orderModel.price) && !TextUtils.isEmpty(orderModel.remainAmount)) {
                double value = MathHelper.mul(Double.parseDouble(orderModel.price), Double.parseDouble(orderModel.remainAmount));
                tv_remaining_all.setText(FormatterUtils.getFormatRoundDown(2, value) + valueWei);
            }
            tv_single_min_limit.setText(orderModel.lowLimit + valueWei);
            tv_single_max_limit.setText(orderModel.highLimit + valueWei);

            if (orderModel.payByCard != null && orderModel.payByCard == 1) {
                img_yhk.setVisibility(View.VISIBLE);
            } else {
                img_yhk.setVisibility(View.GONE);
            }
            if (orderModel.payWechat != null && orderModel.payWechat == 1) {
                img_wx.setVisibility(View.VISIBLE);
            } else {
                img_wx.setVisibility(View.GONE);
            }
            if (orderModel.payAlipay != null && orderModel.payAlipay == 1) {
                img_zfb.setVisibility(View.VISIBLE);
            } else {
                img_zfb.setVisibility(View.GONE);
            }
            tv_seller_coined_time_limit.setText(orderModel.timeOut + " min");
        }
    }
}
