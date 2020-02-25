package com.android.tacu.module.otc.view;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import butterknife.BindView;

public abstract class BaseOtcHalfOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

    @BindView(R.id.img_people)
    ImageView img_people;
    @BindView(R.id.tv_people_nickname)
    TextView tv_people_nickname;
    @BindView(R.id.img_people_vip)
    ImageView img_people_vip;
    @BindView(R.id.img_people_phone_auth)
    ImageView img_people_phone_auth;
    @BindView(R.id.img_people_truenname_auth)
    ImageView img_people_truenname_auth;
    @BindView(R.id.img_people_email_auth)
    ImageView img_people_email_auth;
    @BindView(R.id.img_people_video_auth)
    ImageView img_people_video_auth;
    @BindView(R.id.tv_people_phone_auth)
    TextView tv_people_phone_auth;
    @BindView(R.id.tv_people_truenname_auth)
    TextView tv_people_truenname_auth;
    @BindView(R.id.tv_people_email_auth)
    TextView tv_people_email_auth;
    @BindView(R.id.tv_people_video_auth)
    TextView tv_people_video_auth;
    @BindView(R.id.tv_people_pay)
    TextView tv_people_pay;

    @BindView(R.id.tv_bond)
    TextView tv_bond;
    @BindView(R.id.tv_all_deal_amount)
    TextView tv_all_deal_amount;
    @BindView(R.id.tv_buy_amount)
    TextView tv_buy_amount;
    @BindView(R.id.tv_sell_amount)
    TextView tv_sell_amount;
    @BindView(R.id.tv_deal_rate)
    TextView tv_deal_rate;

    @BindView(R.id.tv_dispute_amount)
    TextView tv_dispute_amount;
    @BindView(R.id.tv_be_arbitrated_amount)
    TextView tv_be_arbitrated_amount;
    @BindView(R.id.tv_arbitration_amount)
    TextView tv_arbitration_amount;
    @BindView(R.id.tv_appeal_amount)
    TextView tv_appeal_amount;
    @BindView(R.id.tv_win_lawsuit_amount)
    TextView tv_win_lawsuit_amount;

    @BindView(R.id.tv_average_payment_time)
    TextView tv_average_payment_time;

    @BindView(R.id.rl_people_bg)
    QMUIRoundRelativeLayout rl_people_bg;

    protected void setInfoValue(OtcMarketOrderAllModel allModel) {
        if (allModel != null) {
            OtcMarketInfoModel infoModel = allModel.infoModel;
            if (infoModel != null) {
                GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people);
                tv_people_nickname.setText(infoModel.nickname + "(" + infoModel.secondName + ")");
                if (infoModel.vip != null && infoModel.vip != 0) {
                    img_people_vip.setImageResource(R.mipmap.img_vip_green);
                } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                    img_people_vip.setImageResource(R.mipmap.img_vip_grey);
                } else if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                    img_people_vip.setImageResource(R.drawable.icon_vip);
                }
                if (infoModel.isValidatePhone != null && infoModel.isValidatePhone == 1) {
                    img_people_phone_auth.setImageResource(R.drawable.icon_auth_success);
                } else {
                    img_people_phone_auth.setImageResource(R.drawable.icon_auth_failure);
                }
                if (infoModel.isAuthSenior != null && infoModel.isAuthSenior >= 2) {
                    img_people_truenname_auth.setImageResource(R.drawable.icon_auth_success);
                } else {
                    img_people_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
                }
                if (infoModel.isValidateEmail != null && infoModel.isValidateEmail == 1) {
                    img_people_email_auth.setImageResource(R.drawable.icon_auth_success);
                } else {
                    img_people_email_auth.setImageResource(R.drawable.icon_auth_failure);
                }
                if ((infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) || (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2)) {
                    img_people_video_auth.setImageResource(R.drawable.icon_auth_success);
                } else {
                    img_people_video_auth.setImageResource(R.drawable.icon_auth_failure);
                }
                tv_bond.setText(FormatterUtils.getFormatValue(infoModel.bondMoney) + " " + Constant.OTC_ACU);
                tv_all_deal_amount.setText(FormatterUtils.getFormatValue(infoModel.total) + getResources().getString(R.string.dan));
                tv_buy_amount.setText(FormatterUtils.getFormatValue(infoModel.buy) + getResources().getString(R.string.dan));
                tv_sell_amount.setText(FormatterUtils.getFormatValue(infoModel.sell) + getResources().getString(R.string.dan));
                if (!TextUtils.isEmpty(infoModel.success) && !TextUtils.isEmpty(infoModel.total) && !TextUtils.equals(infoModel.success, "0") && !TextUtils.equals(infoModel.total, "0")) {
                    tv_deal_rate.setText((Double.parseDouble(infoModel.success) / Double.valueOf(infoModel.total)) * 100 + " %");
                } else {
                    tv_deal_rate.setText("0%");
                }
                tv_dispute_amount.setText(infoModel.disputeTotal);
                tv_be_arbitrated_amount.setText(infoModel.disputeByOther);
                tv_arbitration_amount.setText(infoModel.disputeBySelf);
                tv_appeal_amount.setText(infoModel.otherDispute);
                tv_win_lawsuit_amount.setText(infoModel.winDispute);

                if (!TextUtils.isEmpty(infoModel.allTime) && !TextUtils.isEmpty(infoModel.success) && !TextUtils.equals(infoModel.allTime, "0") && !TextUtils.equals(infoModel.success, "0")) {
                    String value = Double.valueOf(infoModel.allTime) / Double.valueOf(infoModel.success) + " min";
                    Spanned text = Html.fromHtml(getResources().getString(R.string.average_payment_time) + "<font color=" + ContextCompat.getColor(this, R.color.text_default) + ">" + value + " min</font>");
                    tv_average_payment_time.setText(text);
                } else {
                    Spanned text = Html.fromHtml(getResources().getString(R.string.average_payment_time) + "<font color=" + ContextCompat.getColor(this, R.color.text_default) + ">0 min</font>");
                    tv_average_payment_time.setText(text);
                }
            }
        }
    }
}
