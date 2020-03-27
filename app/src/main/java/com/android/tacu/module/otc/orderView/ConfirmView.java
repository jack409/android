package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

//待确认
public class ConfirmView extends BaseOtcView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private UserInfoUtils spUtil;

    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private LinearLayout lin_btn;
    private QMUIRoundButton btn_cancel;
    private QMUIRoundButton btn_confirm;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_confirmed, null);
        initConfirmedView(statusView);
        return statusView;
    }

    private void initConfirmedView(View view) {
        setBaseView(view, activity);

        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        lin_btn = view.findViewById(R.id.lin_btn);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (tradeModel != null) {
                    mPresenter.confirmCancelOrder(tradeModel.id);
                }
                break;
            case R.id.btn_confirm:
                if (tradeModel != null) {
                    mPresenter.confirmOrder(tradeModel.id);
                }
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealConfirmed();
        dealTime();
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        dealTime();
    }

    private void dealConfirmed() {
        if (tradeModel != null) {
            setBaseValue(activity, tradeModel, spUtil);
            if (tradeModel.merchantId == spUtil.getUserUid()) {
                lin_btn.setVisibility(View.VISIBLE);
            } else {
                lin_btn.setVisibility(View.GONE);
            }
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.confirmEndTime)) {
            long confirmEndTime = DateUtils.string2Millis(tradeModel.confirmEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (confirmEndTime > 0) {
                startCountDownTimer(confirmEndTime);
            }
        }
    }

    private void startCountDownTimer(long valueTime) {
        if (time != null) {
            return;
        }
        isLock = false;
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    if (isLock) {
                        return;
                    }
                    if (millisUntilFinished >= 0) {
                        getCountDownTimes = DateUtils.getCountDownTime2(millisUntilFinished);
                        if (getCountDownTimes != null && getCountDownTimes.length == 3) {
                            tv_hour.setText(getCountDownTimes[0]);
                            tv_minute.setText(getCountDownTimes[1]);
                            tv_second.setText(getCountDownTimes[2]);
                        }
                    } else {
                        cancel();
                        isLock = true;
                        EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                isLock = true;
                EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
            }
        };
        time.start();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
