package com.cj.record.fragment.record;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.cj.record.R;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.contract.UserContract;
import com.cj.record.db.RecordDao;
import com.cj.record.presenter.UserPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.cj.record.views.ProgressDialog;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * 机长 改成司钻员
 */
public class RecordOperatePersionFragment extends RecordBaseFragment<UserPresenter> implements UserContract.View, ObsUtils.ObsLinstener {
    @BindView(R.id.operateperson_name)
    MaterialBetterSpinner operatepersonName;
    @BindView(R.id.operateperson_code)
    MaterialEditTextNoEmoji operatepersonCode;
    @BindView(R.id.operateperson_check)
    Button operatepersonCheck;

    private List<Record> recordList;
    private ObsUtils obsUtils;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_operateperson;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);

        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
        operatepersonCheck.setOnClickListener(personCheckListener);
    }


    View.OnClickListener personCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doCheck();
        }
    };

    private void doCheck() {
        if (!validator()) {
            return;
        }
        mPresenter.checkOperate(mActivity, userID, operatepersonName.getText().toString().trim(), operatepersonCode.getText().toString().trim());
    }

    @Override
    public void onSubscribe(int type) {
        recordList = RecordDao.getInstance().getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_OPERATEPERSON);
    }


    @Override
    public void onComplete(int type) {
        if (recordList != null) {
            //新建的记录机长信息是空的，删除掉
            Iterator<Record> ir = recordList.iterator();
            while (ir.hasNext()) {
                Record record = ir.next();
                if ("".equals(record.getOperatePerson())) {
                    ir.remove();
                }
            }
            //去掉机长和机长编号相同的数据
            for (int i = 0; i < recordList.size(); i++) {
                for (int j = i + 1; j < recordList.size(); j++) {
                    if (recordList.get(i).getOperatePerson().equals(recordList.get(j).getOperatePerson()) && recordList.get(i).getTestType().equals(recordList.get(j).getTestType())) {
                        recordList.remove(j);
                        j--;
                    }
                }
            }
            final List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName("姓名:" + recordList.get(i - 1).getOperatePerson() + "  编号:" + recordList.get(i - 1).getTestType());
                dropItemVo.setValue(recordList.get(i - 1).getOperatePerson() + "  " + recordList.get(i - 1).getTestType());
                list.add(dropItemVo);
            }
            operatepersonName.setText(record.getOperatePerson());
            operatepersonCode.setText(record.getTestType());
            operatepersonName.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
            operatepersonName.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i + 1 < list.size()) {
                        operatepersonName.setText(recordList.get(i).getOperatePerson());
                        operatepersonCode.setText(recordList.get(i).getTestType());
                    }
                }
            });
        }
    }


    @Override
    public Record getRecord() {
        record.setOperatePerson(operatepersonName.getText().toString().trim());
        record.setTestType(operatepersonCode.getText().toString().trim());
        return record;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if (TextUtils.isEmpty(operatepersonName.getText().toString().trim())) {
            operatepersonName.setError(getString(R.string.record_op_p1));
            validator = false;
        }
        if (operatepersonName.getText().toString().length() > 16) {
            operatepersonName.setError(getString(R.string.record_op_p2));
            validator = false;
        }
        if (operatepersonCode.getText().toString().length() > 50) {
            operatepersonCode.setError(getString(R.string.record_op_p3));
            validator = false;
        }
        if (TextUtils.isEmpty(operatepersonCode.getText().toString().trim())) {
            operatepersonCode.setError(getString(R.string.record_op_p4));
            validator = false;
        }
        return validator;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_OPERATEPERSON;
    }

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(mActivity);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtil.showToastS(mActivity, throwable.toString());
    }

    @Override
    public void onSuccess(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessUpdateVersion(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessUpdateInfo(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessResetPassword(BaseObjectBean<String> bean) {

    }


    @Override
    public void onSuccessCheckOperate(BaseObjectBean<String> bean) {
        Common.showMessage(mActivity, bean.getMessage());
    }

    @Override
    public void onSuccessInitDB() {

    }
}
