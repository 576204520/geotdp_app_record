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
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 地层分类(砂土) 的片段.
 */
public class LayerDescStFragment extends RecordBaseFragment {

    @BindView(R.id.sprKwzc)
    MaterialBetterSpinner sprKwzc;
    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprKljp)
    MaterialBetterSpinner sprKljp;
    @BindView(R.id.sprMsd)
    MaterialBetterSpinner sprMsd;
    @BindView(R.id.sprKlxz)
    MaterialBetterSpinner sprKlxz;
    @BindView(R.id.sprSd)
    MaterialBetterSpinner sprSd;
    @BindView(R.id.sprJc)
    MaterialBetterSpinner sprJc;

    private List<DropItemVo> sprKwzcList;                    //矿物组成
    private List<DropItemVo> sprYsList;                    //颜色
    private List<DropItemVo> sprKljpList;                //颗粒级配
    private List<DropItemVo> sprKlxzList;                //颗粒形状
    private List<DropItemVo> sprSdList;                    //湿度
    private List<DropItemVo> sprMsdList;                //密实度
    private List<DropItemVo> sprJcList;        //夹层


    //颗粒组成
    StringBuilder kwzcStr = new StringBuilder();
    MaterialDialog kwzcDialog;
    private List<String> kwzcList;
    //颜色
    StringBuilder ysStr = new StringBuilder();
    MaterialDialog ysDialog;
    private List<String> ysList;
    //颗粒形状
    StringBuilder klxzStr = new StringBuilder();
    MaterialDialog klxzDialog;
    private List<String> klxzList;
    //湿度
    StringBuilder sdStr = new StringBuilder();
    MaterialDialog sdDialog;
    private List<String> sdList;
    //夹层
    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_st;
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = dictionaryDao.getDropItemList(getSqlString("砂土_颜色"));                    //颜色
        sprKljpList = dictionaryDao.getDropItemList(getSqlString("砂土_颗粒级配"));                //颗粒级配
        sprKlxzList = dictionaryDao.getDropItemList(getSqlString("砂土_颗粒形状"));                //颗粒形状
        sprSdList = dictionaryDao.getDropItemList(getSqlString("砂土_湿度"));                    //湿度
        sprMsdList = dictionaryDao.getDropItemList(getSqlString("砂土_密实度"));                //密实度
        sprKwzcList = dictionaryDao.getDropItemList(getSqlString("砂土_矿物组成"));             //矿物组成
        sprJcList = dictionaryDao.getDropItemList(getSqlString("砂土_夹层"));        //夹层

        kwzcList = new ArrayList<>();
        kwzcList = DropItemVo.getStrList(sprKwzcList);
//        kwzcList.add("石英");
//        kwzcList.add("云母");
//        kwzcList.add("长石");
        sprKwzc.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (kwzcDialog == null) {
                    kwzcDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_kwzc).items(kwzcList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    kwzcStr.delete(0, kwzcStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) kwzcStr.append(',');
                                        kwzcStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (kwzcStr.toString().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprKwzc.setText(kwzcStr.toString());
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
                                            kwzcList.add(input.toString());
                                            dialog.dismiss();
                                            kwzcDialog.getBuilder().items(kwzcList);
                                            kwzcDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_矿物组成", input.toString(), kwzcList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprKwzc.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                kwzcDialog.show();
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
                                    if (ysStr.toString().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprYs.setText(ysStr.toString());
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
                                            ysList.add(input.toString());
                                            dialog.dismiss();
                                            ysDialog.getBuilder().items(ysList);
                                            ysDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_颜色", input.toString(), ysList.size() + "", userID, Record.TYPE_LAYER));
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

        klxzList = new ArrayList<>();
        klxzList = DropItemVo.getStrList(sprKlxzList);
        sprKlxz.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (klxzDialog == null) {
                    klxzDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_klxz).items(klxzList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    klxzStr.delete(0, klxzStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) klxzStr.append(',');
                                        klxzStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (klxzStr.toString().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprKlxz.setText(klxzStr.toString());
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
                                            klxzList.add(input.toString());
                                            dialog.dismiss();
                                            klxzDialog.getBuilder().items(klxzList);
                                            klxzDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_颗粒形状", input.toString(), klxzList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprKlxz.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                klxzDialog.show();
            }
        });
        sdList = new ArrayList<>();
        sdList = DropItemVo.getStrList(sprSdList);
        sprSd.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (sdDialog == null) {
                    sdDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_sd).items(sdList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    sdStr.delete(0, sdStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) sdStr.append(',');
                                        sdStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (sdStr.toString().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprSd.setText(sdStr.toString());
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
                                            sdList.add(input.toString());
                                            dialog.dismiss();
                                            sdDialog.getBuilder().items(sdList);
                                            sdDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_湿度", input.toString(), sdList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprSd.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                sdDialog.show();
            }
        });
        jcList = new ArrayList<>();
        jcList = DropItemVo.getStrList(sprJcList);

        sprJc.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (jcDialog == null) {
                    jcDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_jc).items(jcList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    jcStr.delete(0, jcStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) jcStr.append(',');
                                        jcStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (jcStr.toString().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprJc.setText(jcStr.toString());
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
                                            jcList.add(input.toString());
                                            dialog.dismiss();
                                            jcDialog.getBuilder().items(jcList);
                                            jcDialog.show();
                                            dictionaryList.add(new Dictionary("1", "砂土_夹层", input.toString(), jcList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprJc.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                jcDialog.show();
            }
        });
        sprKljp.setAdapter(mActivity, sprKljpList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(mActivity, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
    }

    @Override
    public void initView() {
        super.initView();
        sprKwzc.setText(record.getKwzc());
        sprYs.setText(record.getYs());
        sprKljp.setText(record.getKljp());
        sprKlxz.setText(record.getKlxz());
        sprSd.setText(record.getSd());
        sprMsd.setText(record.getMsd());
        sprJc.setText(record.getJc());
    }

    @Override
    public Record getRecord() {
        record.setKwzc(sprKwzc.getText().toString());
        record.setYs(sprYs.getText().toString());
        record.setKljp(sprKljp.getText().toString());
        record.setKlxz(sprKlxz.getText().toString());
        record.setSd(sprSd.getText().toString());
        record.setMsd(sprMsd.getText().toString());
        record.setJc(sprJc.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}