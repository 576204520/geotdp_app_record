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
import com.cj.record.adapter.DropListAdapter;
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
 * 地层分类(粉土) 的片段.
 */
public class LayerDescFtFragment extends RecordBaseFragment {

    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprSd)
    MaterialBetterSpinner sprSd;
    @BindView(R.id.sprBhw)
    MaterialBetterSpinner sprBhw;
    @BindView(R.id.sprMsd)
    MaterialBetterSpinner sprMsd;
    @BindView(R.id.sprJc)
    MaterialBetterSpinner sprJc;

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprJcList;
    private List<DropItemVo> sprSdList;
    private List<DropItemVo> sprMsdList;

    private int sortNoYs = 0;

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;


    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_ft;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("粉土_颜色"));
        sprBhwList = DictionaryDao.getInstance().getDropItemList(getSqlString("粉土_包含物"));
        sprJcList = DictionaryDao.getInstance().getDropItemList(getSqlString("粉土_夹层"));
        sprSdList = DictionaryDao.getInstance().getDropItemList(getSqlString("粉土_湿度"));
        sprMsdList = DictionaryDao.getInstance().getDropItemList(getSqlString("粉土_密实度"));

        bhwList = new ArrayList<>();
        bhwList = DropItemVo.getStrList(sprBhwList);

        sprBhw.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (bhwDialog == null) {
                    bhwDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_bhw).items(bhwList)

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
                                    if (bhwStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity, "该字段最大50字符，请重新选择");
                                    } else {
                                        sprBhw.setText(bhwStr.toString().trim());
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
                                            bhwList.add(input.toString().trim());
                                            dialog.dismiss();
                                            bhwDialog.getBuilder().items(bhwList);
                                            bhwDialog.show();
                                            dictionaryList.add(new Dictionary("1", "粉土_包含物", input.toString().trim(), bhwList.size() + "", userID, Record.TYPE_LAYER));
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
                                    if (jcStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity, "该字段最大50字符，请重新选择");
                                    } else {
                                        sprJc.setText(jcStr.toString().trim());
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
                                            jcList.add(input.toString().trim());
                                            dialog.dismiss();
                                            jcDialog.getBuilder().items(jcList);
                                            jcDialog.show();
                                            dictionaryList.add(new Dictionary("1", "粉土_夹层", input.toString().trim(), jcList.size() + "", userID, Record.TYPE_LAYER));
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

        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprSd.setAdapter(mActivity, sprSdList, MaterialBetterSpinner.MODE_CLEAR);
        sprMsd.setAdapter(mActivity, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);

        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprJc.setText(record.getJc());
        sprSd.setText(record.getSd());
        sprMsd.setText(record.getMsd());
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString().trim());
        record.setBhw(sprBhw.getText().toString().trim());
        record.setJc(sprJc.getText().toString().trim());
        record.setSd(sprSd.getText().toString().trim());
        record.setMsd(sprMsd.getText().toString().trim());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "粉土_颜色", sprYs.getText().toString().trim(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            DictionaryDao.getInstance().addDictionaryList(dictionaryList);
        }
        return true;
    }

}