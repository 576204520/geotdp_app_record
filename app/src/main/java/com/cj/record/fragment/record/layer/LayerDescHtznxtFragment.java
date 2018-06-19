package com.cj.record.fragment.record.layer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 地层分类(黄土状黏性土) 的片段.
 */
public class LayerDescHtznxtFragment extends RecordBaseFragment {

    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprZt)
    MaterialBetterSpinner sprZt;
    @BindView(R.id.sprKx)
    MaterialBetterSpinner sprKx;
    @BindView(R.id.sprCzjl)
    MaterialBetterSpinner sprCzjl;
    @BindView(R.id.sprBhw)
    MaterialBetterSpinner sprBhw;

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprZtList;
    private List<DropItemVo> sprKxList;
    private List<DropItemVo> sprCzjlList;
    private List<DropItemVo> sprBhwList;


    private int sortNoYs = 0;
    private int sortNoKx = 0;
    StringBuilder bhwStr = new StringBuilder();
    MaterialDialog bhwDialog;
    private List<String> bhwList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_htznxt;
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = dictionaryDao.getDropItemList(getSqlString("黄土_颜色"));
        sprZtList = dictionaryDao.getDropItemList(getSqlString("黄土_状态"));
        sprKxList = dictionaryDao.getDropItemList(getSqlString("黄土_孔隙"));
        sprCzjlList = dictionaryDao.getDropItemList(getSqlString("黄土_垂直节理"));
        sprBhwList = dictionaryDao.getDropItemList(getSqlString("黄土_包含物"));

        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprZt.setAdapter(mActivity, sprZtList, MaterialBetterSpinner.MODE_CLEAR);
        sprKx.setAdapter(mActivity, sprKxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKx.setOnItemClickListener(kxListener);
        sprCzjl.setAdapter(mActivity, sprCzjlList, MaterialBetterSpinner.MODE_CLEAR);

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
                                            dictionaryList.add(new Dictionary("1", "黄土_包含物", input.toString(), bhwList.size() + "", userID, Record.TYPE_LAYER));
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
    }

    @Override
    public void initView() {
        super.initView();
        sprYs.setText(record.getYs());
        sprZt.setText(record.getZt());
        sprKx.setText(record.getKx());
        sprCzjl.setText(record.getCzjl());
        sprBhw.setText(record.getBhw());
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener kxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoKx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString());
        record.setZt(sprZt.getText().toString());
        record.setKx(sprKx.getText().toString());
        record.setCzjl(sprCzjl.getText().toString());
        record.setBhw(sprBhw.getText().toString());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土_颜色", sprYs.getText().toString(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (sortNoKx > 0) {
            dictionaryDao.addDictionary(new Dictionary("1", "黄土_孔隙", sprKx.getText().toString(), "" + sortNoKx, userID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }
        return true;
    }

}