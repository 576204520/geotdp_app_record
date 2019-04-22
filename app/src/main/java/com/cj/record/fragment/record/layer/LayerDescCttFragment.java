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
 * 地层分类(冲填土) 的片段.
 */
public class LayerDescCttFragment extends RecordBaseFragment {

    @BindView(R.id.sprWzcf)
    MaterialBetterSpinner sprWzcf;
    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprDjnd)
    MaterialBetterSpinner sprDjnd;
    @BindView(R.id.sprMsd)
    MaterialBetterSpinner sprMsd;
    @BindView(R.id.sprJyx)
    MaterialBetterSpinner sprJyx;

    private List<DropItemVo> sprWzcfList;
    private List<DropItemVo> sprDjndList;
    private List<DropItemVo> sprMsdList;
    private List<DropItemVo> sprJyxList;
    private List<DropItemVo> sprYsList;

    StringBuilder str = new StringBuilder();
    MaterialDialog wzcfDialog;
    private List<String> wzcfList;
    StringBuilder ysStr = new StringBuilder();
    MaterialDialog ysDialog;
    private List<String> ysList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_ctt;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("冲填土_颜色"));
        sprDjndList = DictionaryDao.getInstance().getDropItemList(getSqlString("冲填土_堆积年代"));                //颗粒级配
        sprMsdList = DictionaryDao.getInstance().getDropItemList(getSqlString("冲填土_密实度"));                //颗粒形状
        sprJyxList = DictionaryDao.getInstance().getDropItemList(getSqlString("冲填土_均匀性"));                    //湿度
        sprWzcfList = DictionaryDao.getInstance().getDropItemList(getSqlString("冲填土_状态"));
        wzcfList = new ArrayList<>();
        wzcfList = DropItemVo.getStrList(sprWzcfList);
//        wzcfList.add("以泥沙为主");
        sprWzcf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (wzcfDialog == null) {
                    wzcfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(wzcfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    str.delete(0, str.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) str.append(',');
                                        str.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (str.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
                                        sprWzcf.setText(str.toString().trim());
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
                                            wzcfList.add(input.toString().trim());
                                            dialog.dismiss();
                                            wzcfDialog.getBuilder().items(wzcfList);
                                            wzcfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "冲填土_状态", input.toString().trim(), wzcfList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprWzcf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                wzcfDialog.show();
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
                                            dictionaryList.add(new Dictionary("1", "冲填土_颜色", input.toString().trim(), ysList.size() + "", userID, Record.TYPE_LAYER));
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

        sprWzcf.setText(record.getWzcf());
        sprDjnd.setText(record.getDjnd());
        sprMsd.setText(record.getMsd());
        sprJyx.setText(record.getJyx());
        sprYs.setText(record.getYs());
    }


    @Override
    public Record getRecord() {
        record.setWzcf(sprWzcf.getText().toString().trim());
        record.setDjnd(sprDjnd.getText().toString().trim());
        record.setMsd(sprMsd.getText().toString().trim());
        record.setJyx(sprJyx.getText().toString().trim());
        record.setYs(sprYs.getText().toString().trim());
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