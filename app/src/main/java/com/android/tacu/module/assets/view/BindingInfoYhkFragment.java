package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.PayInfoEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class BindingInfoYhkFragment extends BaseFragment<BindingPayInfoPresenter> implements BindingPayInfoContract.IYhkView {

    @BindView(R.id.lin_edit)
    LinearLayout lin_edit;
    @BindView(R.id.tv_cardholder_name)
    TextView tv_cardholder_name;
    @BindView(R.id.edit_bank_name)
    EditText edit_bank_name;
    @BindView(R.id.edit_open_bank_name)
    EditText edit_open_bank_name;
    @BindView(R.id.edit_open_bank_address)
    EditText edit_open_bank_address;
    @BindView(R.id.edit_bank_id)
    EditText edit_bank_id;
    @BindView(R.id.lin_trade_pwd)
    LinearLayout lin_trade_pwd;
    @BindView(R.id.edit_trade_password)
    EditText edit_trade_password;

    @BindView(R.id.lin_list)
    LinearLayout lin_list;
    @BindView(R.id.tv_cardholder_name1)
    TextView tv_cardholder_name1;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.tv_open_bank_name)
    TextView tv_open_bank_name;
    @BindView(R.id.tv_open_bank_address)
    TextView tv_open_bank_address;
    @BindView(R.id.tv_bank_id)
    TextView tv_bank_id;

    private PayInfoModel payInfoModel;
    private final int MY_SCAN_REQUEST_CODE = 1001;

    public static BindingInfoYhkFragment newInstance() {
        Bundle bundle = new Bundle();
        BindingInfoYhkFragment fragment = new BindingInfoYhkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_yhk;
    }

    @Override
    protected void initData(View view) {
        if (spUtil.getPwdVisibility()) {
            lin_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            lin_trade_pwd.setVisibility(View.GONE);
        }
        tv_cardholder_name.setText(spUtil.getKYCName());
        tv_cardholder_name1.setText(spUtil.getKYCName());
    }

    @OnClick(R.id.img_saomiao)
    void saomiaoClick() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                Intent scanIntent = new Intent(getContext(), CardIOActivity.class);
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                        .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)//去除水印
                        .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)//去除键盘
                        .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, "zh-Hans");//设置提示为中文
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.CAMERA);
    }

    @OnClick(R.id.btn_bindinng)
    void bindingClick() {
        String bankName = edit_bank_name.getText().toString().trim();
        String openBankName = edit_open_bank_name.getText().toString().trim();
        String openBankAdress = edit_open_bank_address.getText().toString().trim();
        String bankCard = edit_bank_id.getText().toString().trim();
        String pwdString = edit_trade_password.getText().toString().trim();
        if (TextUtils.isEmpty(bankName)) {
            showToastError(getResources().getString(R.string.please_input_bank_name));
            return;
        }
        if (TextUtils.isEmpty(openBankName)) {
            showToastError(getResources().getString(R.string.please_input_open_bank_name));
            return;
        }
        if (TextUtils.isEmpty(openBankAdress)) {
            showToastError(getResources().getString(R.string.please_input_open_bank_address));
            return;
        }
        if (TextUtils.isEmpty(bankCard)) {
            showToastError(getResources().getString(R.string.please_input_bank_id));
            return;
        }
        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
            showToastError(getResources().getString(R.string.please_input_trade_password));
            return;
        }
        mPresenter.insertBank(1, bankName, openBankName, openBankAdress, bankCard, null, null, null, null, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        getHostActivity().finish();
    }

    @OnClick(R.id.btn_delete)
    void deleteClick() {
        mPresenter.deleteBank(1, payInfoModel.id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                edit_bank_id.setText(scanResult.cardNumber);
            }
        }
    }

    @Override
    public void insertBankSuccess() {
        sendRefresh();
    }

    @Override
    public void deleteBankSuccess() {
        sendRefresh();
    }

    private void sendRefresh() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.PayInfoCode, new PayInfoEvent(true)));
    }

    public void setValue(PayInfoModel model) {
        this.payInfoModel = model;
        if (model != null) {
            lin_edit.setVisibility(View.GONE);
            lin_list.setVisibility(View.VISIBLE);

            tv_bank_name.setText(model.bankName);
            tv_open_bank_name.setText(model.openBankName);
            tv_open_bank_address.setText(model.openBankAdress);
            tv_bank_id.setText(model.bankCard);
        } else {
            lin_edit.setVisibility(View.VISIBLE);
            lin_list.setVisibility(View.GONE);
        }
    }
}
