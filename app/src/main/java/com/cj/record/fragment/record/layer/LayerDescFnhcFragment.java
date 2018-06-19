package com.cj.record.fragment.record.layer;

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
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * 地层分类(粉黏互层) 的片段.
 */
public class LayerDescFnhcFragment extends RecordBaseFragment {

    private MaterialBetterSpinner sprYs;            //颜色
    private MaterialBetterSpinner sprBhw;           //包含物
    private MaterialBetterSpinner sprZt;            //状态
    private MaterialBetterSpinner sprFtfchd;        //粉土分层厚度
    private MaterialBetterSpinner sprFzntfchd;      //粉质黏土分层厚度

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprZtList;
    private List<DropItemVo> sprFtfchdList;
    private List<DropItemVo> sprFzntfchdList;

    private int sortNoYs = 0;
    private int sortNoFtfchd = 0;
    private int sortNoFzntfchd = 0;

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;

    @Override
    public int getLayoutId() {
        return super.getLayoutId();
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_颜色"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_包含物"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_状态"));
        sprFtfchdList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_粉土层厚"));
        sprFzntfchdList = dictionaryDao.getDropItemList(getSqlString("粉黏互层_粉黏层厚"));

        bhwList = new ArrayList<>();
        bhwList = DropItemVo.getStrList(sprBhwList);
        sprBhw.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (bhwDialog == null) {
                    bhwDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(bhwList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    bhwStr.delete(0, bhwStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) bhwStr.append(',');
                                        bhwStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    sprBhw.setText(bhwStr.toString());
                                    dialog.dismiss();
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
                                            bhwList.add(input.toString());
                                            dialog.dismiss();
                                            bhwDialog.getBuilder().items(bhwList);
                                            bhwDialog.show();
                                            dictionaryList.add(new Dictionary("1", "粉黏互层_包含物", input.toString(), bhwList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprBhw.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                bhwDialog.show();
            }
        });
        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprZt.setAdapter(mActivity, sprZtList, MaterialBetterSpinner.MODE_CLEAR);
        sprFtfchd.setAdapter(mActivity, sprFtfchdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprFtfchd.setOnItemClickListener(FtfchdListener);
        sprFzntfchd.setAdapter(mActivity, sprFzntfchdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprFzntfchd.setOnItemClickListener(FzntfchdListener);
    }

    @Override
    public void initView() {
        super.initView();
        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprZt.setText(record.getZt());
        sprFtfchd.setText(record.getFtfchd());
        sprFzntfchd.setText(record.getFzntfchd());
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener FtfchdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoFtfchd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener FzntfchdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoFzntfchd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        record.setZt(sprZt.getText().toString());
        record.setFtfchd(sprFtfchd.getText().toString());
        record.setFzntfchd(sprFzntfchd.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_颜色", sprYs.getText().toString(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (sortNoFtfchd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_粉土层厚", sprFtfchd.getText().toString(), "" + sortNoFtfchd, userID, Record.TYPE_LAYER));
        }
        if (sortNoFzntfchd > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "粉黏互层_粉黏层厚", sprFzntfchd.getText().toString(), "" + sortNoFzntfchd, userID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }
}