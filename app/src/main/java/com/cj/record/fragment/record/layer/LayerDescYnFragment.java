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
 * 地层分类(淤泥) 的片段.
 */
public class LayerDescYnFragment extends RecordBaseFragment {

    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprHsl)
    MaterialBetterSpinner sprHsl;
    @BindView(R.id.sprZt)
    MaterialBetterSpinner sprZt;
    @BindView(R.id.sprBhw)
    MaterialBetterSpinner sprBhw;

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprBhwList;
    private List<DropItemVo> sprHslList;
    private List<DropItemVo> sprZtList;

    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;

    StringBuilder str = new StringBuilder();
    MaterialDialog ztDialog;
    private List<String> ztList;

    private int sortNoYs = 0;
    private int sortNoHsl = 0;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_yn;
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("淤泥_颜色"));
        sprBhwList = DictionaryDao.getInstance().getDropItemList(getSqlString("淤泥_包含物"));
        sprHslList = DictionaryDao.getInstance().getDropItemList(getSqlString("淤泥_含水量"));
        sprZtList = DictionaryDao.getInstance().getDropItemList(getSqlString("淤泥_状态"));

        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprHsl.setAdapter(mActivity, sprHslList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprHsl.setOnItemClickListener(hslListener);

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
                                        ToastUtil.showToastS(mActivity,"该字段最大50字符，请重新选择");
                                    }else{
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
                                            dictionaryList.add(new Dictionary("1", "淤泥_包含物", input.toString().trim(), bhwList.size() + "", userID, Record.TYPE_LAYER));
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

        ztList = new ArrayList<>();
        ztList = DropItemVo.getStrList(sprZtList);
//        ztList.add("流塑");
        sprZt.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (ztDialog == null) {
                    ztDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_zt).items(ztList)

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
                                        sprZt.setText(str.toString().trim());
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
                                            ztList.add(input.toString().trim());
                                            dialog.dismiss();
                                            ztDialog.getBuilder().items(ztList);
                                            ztDialog.show();
                                            dictionaryList.add(new Dictionary("1", "淤泥_状态", input.toString().trim(), ztList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprZt.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                ztDialog.show();
            }
        });
    }

    @Override
    public void initView() {
        super.initView();
        sprYs.setText(record.getYs());
        sprBhw.setText(record.getBhw());
        sprHsl.setText(record.getHsl());
        sprZt.setText(record.getZt());
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener hslListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoHsl = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString().trim());
        record.setBhw(sprBhw.getText().toString().trim());
        record.setHsl(sprHsl.getText().toString().trim());
        record.setZt(sprZt.getText().toString().trim());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "淤泥_颜色", sprYs.getText().toString().trim(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (sortNoHsl > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "淤泥_含水量", sprHsl.getText().toString().trim(), "" + sortNoHsl, userID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            DictionaryDao.getInstance().addDictionaryList(dictionaryList);
        }
        return true;
    }
}

