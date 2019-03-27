package com.cj.record.fragment.record.layer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.DictionaryDao;
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 地层分类(填土) 的片段.
 */
public class LayerDescTtFragment extends RecordBaseFragment {

    @BindView(R.id.sprZycf)
    MaterialBetterSpinner sprZycf;
    @BindView(R.id.sprCycf)
    MaterialBetterSpinner sprCycf;
    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprDjnd)
    MaterialBetterSpinner sprDjnd;
    @BindView(R.id.sprMsd)
    MaterialBetterSpinner sprMsd;
    @BindView(R.id.sprJyx)
    MaterialBetterSpinner sprJyx;

    private List<DropItemVo> sprDjndList;
    private List<DropItemVo> sprMsdList;
    private List<DropItemVo> sprJyxList;
    private List<DropItemVo> sprZycyList;
    private List<DropItemVo> sprCycfList;
    private List<DropItemVo> sprYsList;                    //颜色


    StringBuilder zycfStr = new StringBuilder();
    MaterialDialog zycfDialog;
    List<String> zycyList;

    StringBuilder cycfStr = new StringBuilder();
    MaterialDialog cycfDialog;
    List<String> cycfList;
    //颜色
    StringBuilder ysStr = new StringBuilder();
    MaterialDialog ysDialog;
    private List<String> ysList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_tt;
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_颜色"));
        sprDjndList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_堆积年代"));
        sprMsdList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_密实度"));
        sprJyxList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_均匀性"));
        sprZycyList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_主要成分"));                    // 主要成分
        sprCycfList = DictionaryDao.getInstance().getDropItemList(getSqlString("填土_次要成分"));                    //次要成分

        zycyList = DropItemVo.getStrList(sprZycyList);
        cycfList = DropItemVo.getStrList(sprCycfList);

        sprZycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (zycfDialog == null) {
                    zycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_zycf).items(zycyList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    zycfStr.delete(0, zycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) zycfStr.append(',');
                                        zycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (zycfStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprZycf.setText(zycfStr.toString().trim());
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
                                            zycyList.add(input.toString().trim());
                                            dialog.dismiss();
                                            zycfDialog.getBuilder().items(zycyList);
                                            zycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "填土_主要成分", input.toString().trim(), zycyList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprZycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                zycfDialog.show();
            }
        });

        sprCycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (cycfDialog == null) {
                    cycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_cycf).items(cycfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    cycfStr.delete(0, cycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) cycfStr.append(',');
                                        cycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (cycfStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprCycf.setText(cycfStr.toString().trim());
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
                                            cycfList.add(input.toString().trim());
                                            dialog.dismiss();
                                            cycfDialog.getBuilder().items(cycfList);
                                            cycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "填土_次要成分", input.toString().trim(), cycfList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();

                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprCycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                cycfDialog.show();
            }
        });
        ysList = new ArrayList<>();
        ysList = DropItemVo.getStrList(sprYsList);
        sprYs.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (ysDialog == null) {
                    ysDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_ys).items(ysList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    ysStr.delete(0, ysStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) ysStr.append(',');
                                        ysStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (ysStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprYs.setText(ysStr.toString().trim());
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
                                            ysList.add(input.toString().trim());
                                            dialog.dismiss();
                                            ysDialog.getBuilder().items(ysList);
                                            ysDialog.show();
                                            dictionaryList.add(new Dictionary("1", "填土_颜色", input.toString().trim(), ysList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprYs.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                ysDialog.show();
            }
        });
        sprDjnd.setAdapter(mActivity, sprDjndList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(mActivity, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprJyx.setAdapter(mActivity, sprJyxList, MaterialBetterSpinner.MODE_CLEAR);
    }

    @Override
    public void initView() {
        super.initView();
        sprZycf.setText(record.getZycf());
        sprCycf.setText(record.getCycf());
        sprDjnd.setText(record.getDjnd());
        sprMsd.setText(record.getMsd());
        sprJyx.setText(record.getJyx());
        sprYs.setText(record.getYs());
    }

    @Override
    public Record getRecord() {
        record.setZycf(sprZycf.getText().toString().trim());
        record.setYs(sprYs.getText().toString().trim());
        record.setCycf(sprCycf.getText().toString().trim());
        record.setDjnd(sprDjnd.getText().toString().trim());
        record.setMsd(sprMsd.getText().toString().trim());
        record.setJyx(sprJyx.getText().toString().trim());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (dictionaryList.size() > 0) {
            DictionaryDao.getInstance().addDictionaryList(dictionaryList);
        }
        return true;
    }
}