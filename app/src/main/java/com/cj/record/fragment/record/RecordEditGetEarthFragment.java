package com.cj.record.fragment.record;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.DictionaryDao;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 取样(取土) 的片段.
 */
public class RecordEditGetEarthFragment extends RecordBaseFragment {

    @BindView(R.id.sprEarthType)
    MaterialBetterSpinner sprEarthType; //土样质量等级
    @BindView(R.id.sprMode)
    MaterialBetterSpinner sprMode;//取样工具和方法
    @BindView(R.id.sprTestType)
    MaterialBetterSpinner sprTestType;//试验类型

    List<DropItemVo> earthTypeList;
    List<DropItemVo> earthModeList;
    List<DropItemVo> testTypeList;

    StringBuilder testStr = new StringBuilder();
    MaterialDialog testDialog;
    private List<String> testList;

    private int sortNoMode = 0;
    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_get_earth_edit;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        testList = new ArrayList<>();
        testList = DropItemVo.getStrList(getTestTypeList());
        sprTestType.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (testDialog == null) {
                    testDialog = new MaterialDialog.Builder(getActivity()).title("试验类型").items(testList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    testStr.delete(0, testStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) testStr.append(',');
                                        testStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (testStr.toString().trim().length() > 100) {
                                        ToastUtil.showToastS(mActivity, "该字段最大100字符，请重新选择");
                                    } else {
                                        sprTestType.setText(testStr.toString());
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                    new MaterialDialog.Builder(mActivity).title(R.string.custom).inputType(InputType.TYPE_CLASS_TEXT |
                                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                            InputType.TYPE_TEXT_FLAG_CAP_WORDS).inputMaxLength(10).input("请输入自定义内容", "", false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            testList.add(input.toString());
                                            dialog.dismiss();
                                            testDialog.getBuilder().items(testList);
                                            testDialog.show();
                                            dictionaryList.add(new Dictionary("1", "试验类型", input.toString().trim(), testList.size() + "", userID, Record.TYPE_GET_EARTH));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprTestType.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                testDialog.show();
            }
        });

        earthTypeList = getEarthTypeList();
        sprEarthType.setAdapter(mActivity, earthTypeList, MaterialBetterSpinner.MODE_NORMAL);
        sprMode.setAdapter(mActivity, getEarthModeList(), MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprMode.setOnItemClickListener(modeListener);
        //设置工具方法为可选可编辑
        //sprMode.setIsEnabled(true);
        sprEarthType.setText(record.getEarthType());
        sprMode.setText(record.getGetMode());
        sprTestType.setText(record.getTestType());
    }


    MaterialBetterSpinner.OnItemClickListener modeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoMode = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    private List<DropItemVo> getEarthTypeList() {
        earthTypeList = DictionaryDao.getInstance().getDropItemList(getSqlString("质量等级"));
        addWord();
        return earthTypeList;
    }

    private List<DropItemVo> getEarthModeList() {
        return DictionaryDao.getInstance().getDropItemList(getSqlString("取土工具和方法"));
    }

    private List<DropItemVo> getTestTypeList() {
        testTypeList = DictionaryDao.getInstance().getDropItemList(getSqlString("试验类型"));
        return testTypeList;
    }

    //本地数据库添加字段
    private void addWord() {
        boolean haveHS = false;
        for (DropItemVo dropItemVo : earthTypeList) {
            if (dropItemVo.getName().equals("Ⅰ级样")) {
                dropItemVo.setName("Ⅰ级样(不扰动)");
            }
            if (dropItemVo.getName().equals("Ⅱ级样")) {
                dropItemVo.setName("Ⅱ级样(轻微扰动)");
            }
            if (dropItemVo.getName().equals("Ⅲ级样")) {
                dropItemVo.setName("Ⅲ级样(显著扰动)");
            }
            if (dropItemVo.getName().equals("Ⅳ级样")) {
                dropItemVo.setName("Ⅳ级样(完全扰动)");
            }
            //判断是否存在‘岩石字段’
            if (!dropItemVo.getName().equals("岩石")) {
                haveHS = false;
            } else {
                haveHS = true;
                break;
            }
        }
        if (!haveHS) {
            DropItemVo dropItemVo = new DropItemVo();
            dropItemVo.setId("420");
            dropItemVo.setName("岩石");
            dropItemVo.setValue("岩石");
            earthTypeList.add(dropItemVo);
            DictionaryDao.getInstance().add(new Dictionary("0", "质量等级", "岩石", "4", "", Record.TYPE_GET_EARTH));
        }
    }

    @Override
    public Record getRecord() {
        record.setEarthType(sprEarthType.getText().toString());
        record.setGetMode(sprMode.getText().toString());
        record.setTestType(sprTestType.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprEarthType.getText().toString().trim() + "--" + sprMode.getText().toString().trim();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_GET_EARTH;
    }

    @Override
    public boolean validator() {
        boolean validator = true;

        if (sortNoMode > 0 && validator) {
            DictionaryDao.getInstance().add(new Dictionary("1", "取土工具和方法", sprMode.getText().toString().trim(), "" + sortNoMode, userID, Record.TYPE_GET_EARTH));
        }

        if (dictionaryList.size() > 0) {
            DictionaryDao.getInstance().addDictionaryList(dictionaryList);
        }
        return validator;
    }
}