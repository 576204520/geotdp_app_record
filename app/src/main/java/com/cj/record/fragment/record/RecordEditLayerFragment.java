package com.cj.record.fragment.record;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.fragment.record.layer.LayerDescCttFragment;
import com.cj.record.fragment.record.layer.LayerDescFnhcFragment;
import com.cj.record.fragment.record.layer.LayerDescFtFragment;
import com.cj.record.fragment.record.layer.LayerDescHtzFtFragment;
import com.cj.record.fragment.record.layer.LayerDescHtznxtFragment;
import com.cj.record.fragment.record.layer.LayerDescNxtFragment;
import com.cj.record.fragment.record.layer.LayerDescSstFragment;
import com.cj.record.fragment.record.layer.LayerDescStFragment;
import com.cj.record.fragment.record.layer.LayerDescTtFragment;
import com.cj.record.fragment.record.layer.LayerDescYnFragment;
import com.cj.record.fragment.record.layer.LayerDescYsFragment;
import com.cj.record.fragment.record.layer.LayerDescZdyFragment;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 岩土记录编辑
 */
public class RecordEditLayerFragment extends RecordBaseFragment {
    @BindView(R.id.sprType)
    MaterialBetterSpinner sprType;
    @BindView(R.id.sprName)
    MaterialBetterSpinner sprName;
    @BindView(R.id.fltContent)
    FrameLayout fltContent;
    @BindView(R.id.edtCauses)
    MaterialBetterSpinner edtCauses;
    @BindView(R.id.edtEra)
    MaterialBetterSpinner edtEra;

    private List<DropItemVo> layerTypeList;
    private List<DropItemVo> layerNameList;
    private List<DropItemVo> sprCausesList;
    private List<DropItemVo> eraList;

    private boolean customType;
    private boolean customName;

    private int sortNoType = 0;
    private int sortNoName = 0;

    private int sortNoCauses = 0;
    private int sortNoEra = 0;

    private String causesSort = "";
    private String eraSort = "";
    private RecordBaseFragment recordBaseFragment;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_layer_edit;
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void initView() {
        super.initView();
        sprType.setText(record.getLayerType());
        sprName.setText(record.getLayerName());
        edtCauses.setText(record.getCauses());
        edtEra.setText(record.getEra());
        setFragment(record.getLayerType());
        sprType.setAdapter(mActivity, getLayerTypeList(), MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprName.setAdapter(mActivity, getLayerNameList(record.getLayerType()), MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprType.setOnItemClickListener(typeListener);
        sprName.setOnItemClickListener(nameListener);
    }

    MaterialBetterSpinner.OnItemClickListener nameListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //每次点击type后，清空数据
            clearDataByName();
            //切换name时子布局也要清空
            setFragment(sprType.getText().toString());
            //判断是否是自定义,并记录编号
            sortNoName = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener typeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //获取item对象
            DropItemVo d = layerTypeList.get(i);
            //每次点击type后，name都要重新加载
            sprName.setAdapter(mActivity, getLayerNameList(d.getName()), MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
            //每次点击type后，清空数据
            clearDataByType();
            //加载item对象的name对应的不局
            setFragment(d.getName());
            //判断是否是自定义,并记录编号
            sortNoType = i == adapterView.getCount() - 1 ? i : 0;

        }
    };


    /**
     * 每次切换type都要清空
     */
    public void clearDataByType() {
        sprName.setText(layerNameList.get(0).getValue());
        clearDataByName();
    }

    /**
     * 每次切换name都要清空
     */
    public void clearDataByName() {
        clearRecord();
//        edtCauses.setText(sprCausesList.get(0).getValue());
        edtCauses.setText("");
        edtEra.setText(eraList.get(0).getValue());
        mActivity.sendBroadcast(new Intent("receiver.clear.edtDescription"));
    }

    StringBuilder causesStr = new StringBuilder();
    MaterialDialog causesDialog;
    private List<String> bhwList;

    /**
     * 设置成因类型和地质年代.
     */
    private void setCausesAndEra(boolean isTu) {
        try {
            if (isTu) {
                causesSort = "土的成因类型";
                eraSort = "土的地质年代";
            } else {
                causesSort = "岩石的成因类型";
                eraSort = "岩石的地质年代";
            }
            sprCausesList = dictionaryDao.getDropItemList(getSqlString(causesSort));
            bhwList = new ArrayList<>();
            bhwList = DropItemVo.getStrList(sprCausesList);
            edtCauses.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
                @Override
                public void onShow() {
                    if (causesDialog == null) {
                        causesDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_wzcf).items(bhwList)

                                .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        causesStr.delete(0, causesStr.length());
                                        for (int i = 0; i < which.length; i++) {
                                            if (i > 0) causesStr.append(',');
                                            causesStr.append(text[i]);
                                        }
                                        return true;
                                    }
                                }).callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        edtCauses.setText(causesStr.toString());
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
                                                causesDialog.getBuilder().items(bhwList);
                                                causesDialog.show();
                                                dictionaryList.add(new Dictionary("1", causesSort, input.toString(), bhwList.size() + "", userID, Record.TYPE_LAYER));
                                            }
                                        }).show();
                                    }
                                }).dismissListener(new MaterialDialog.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        edtCauses.setIsPopup(false);
                                    }
                                }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                    }
                    causesDialog.show();
                }
            });
            eraList = dictionaryDao.getDropItemList(getSqlString(eraSort));
            edtEra.setAdapter(mActivity, eraList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
            edtEra.setOnItemClickListener(eraListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MaterialBetterSpinner.OnItemClickListener eraListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoEra = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    /**
     * 加载type下对应的子fragment
     *
     * @param layerType
     */
    private void setFragment(String layerType) {
        try {
            if (layerType.equals(Record.TYPE_YS) || layerType.equals(Record.TYPE_ZDY)) {
                setCausesAndEra(false);
            } else {
                setCausesAndEra(true);
            }
            if (layerType.equals(Record.TYPE_TT)) {
                setDescFragment(new LayerDescTtFragment());
            } else if (layerType.equals(Record.TYPE_CTT)) {
                setDescFragment(new LayerDescCttFragment());
            } else if (layerType.equals(Record.TYPE_NXT)) {
                setDescFragment(new LayerDescNxtFragment());
            } else if (layerType.equals(Record.TYPE_FT)) {
                setDescFragment(new LayerDescFtFragment());
            } else if (layerType.equals(Record.TYPE_FNHC)) {
                setDescFragment(new LayerDescFnhcFragment());
            } else if (layerType.equals(Record.TYPE_HTZNXT)) {
                setDescFragment(new LayerDescHtznxtFragment());
            } else if (layerType.equals(Record.TYPE_HTZFT)) {
                setDescFragment(new LayerDescHtzFtFragment());
            } else if (layerType.equals(Record.TYPE_YN)) {
                setDescFragment(new LayerDescYnFragment());
            } else if (layerType.equals(Record.TYPE_SST)) {
                setDescFragment(new LayerDescSstFragment());
            } else if (layerType.equals(Record.TYPE_ST)) {
                setDescFragment(new LayerDescStFragment());
            } else if (layerType.equals(Record.TYPE_YS)) {
                setDescFragment(new LayerDescYsFragment());
            } else {
                setDescFragment(new LayerDescZdyFragment());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DropItemVo> getLayerTypeList() {
        layerTypeList = dictionaryDao.getDropItemList(getSqlString("岩土类型"));
        if (layerTypeList.get(layerTypeList.size() - 1).getName().equals("自定义")) {
            layerTypeList.remove(layerTypeList.size() - 1);
        }
        return layerTypeList;
    }

    private List<DropItemVo> getLayerNameList(String sqlName) {
        layerNameList = dictionaryDao.getDropItemList(getSqlString(sqlName));
        return layerNameList;
    }


    private void setDescFragment(RecordBaseFragment recordBaseFragment) {
        this.recordBaseFragment = recordBaseFragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.RECORD, record);
        //向detailFragment传入参数
        recordBaseFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fltContent, recordBaseFragment, "st");
        ft.commit();
    }

    @Override
    public Record getRecord() {
        record = recordBaseFragment.getRecord();
        record.setLayerType(sprType.getText().toString());
        record.setLayerName(sprName.getText().toString());
        record.setCauses(edtCauses.getText().toString());
        record.setEra(edtEra.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprType.getText().toString() + "--" + sprName.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_LAYER;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if ("".equals(sprType.getText().toString())) {
            sprType.setError("岩土分类不能为空");
            validator = false;
        }
        if ("".equals(sprName.getText().toString())) {
            sprName.setError("岩土定名不能为空");
            validator = false;
        }
        //分类子布局获取自定义字典判断
        if (validator) {
            recordBaseFragment.layerValidator();
        }
        if (sortNoType > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1", "岩土类型", sprType.getText().toString(), "" + sortNoType, userID, Record.TYPE_LAYER));
        }
        if (sortNoName > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1", sprType.getText().toString(), sprName.getText().toString(), "" + sortNoName, userID, Record.TYPE_LAYER));
        }

        if (dictionaryList.size() > 0) {
            dictionaryDao.addDictionaryList(dictionaryList);
        }

        if (sortNoEra > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1", eraSort, edtEra.getText().toString(), "" + sortNoEra, userID, Record.TYPE_LAYER));
        }

        return validator;
    }

    public void clearRecord() {
        record.setWzcf("");
        record.setDjnd("");
        record.setMsd("");
        record.setJyx("");
        record.setSd("");
        record.setMsd("");

        record.setYs("");
        record.setBhw("");
        record.setJc("");
        record.setSd("");
        record.setMsd("");

        record.setYs("");
        record.setMsd("");
        record.setKlzc("");
        record.setKx("");
        record.setCzjl("");
        record.setBhw("");

        record.setYs("");
        record.setZt("");
        record.setKx("");
        record.setCzjl("");
        record.setBhw("");

        record.setYs("");
        record.setZt("");
        record.setBhw("");
        record.setJc("");

        record.setYs("");
        record.setMsd("");
        record.setTcw("");
        record.setKlxz("");
        record.setKlpl("");
        record.setYbljx("");
        record.setYbljd("");
        record.setJdljx("");
        record.setJdljd("");
        record.setZdlj("");
        record.setMycf("");
        record.setFhcd("");
        record.setKljp("");
        record.setSd("");
        record.setJc("");

        record.setKwzc("");
        record.setYs("");
        record.setKljp("");
        record.setKlxz("");
        record.setSd("");
        record.setMsd("");

        record.setZycf("");
        record.setCycf("");
        record.setDjnd("");
        record.setMsd("");
        record.setJyx("");

        record.setYs("");
        record.setBhw("");
        record.setHsl("");
        record.setZt("");

        record.setYs("");
        record.setJycd("");
        record.setWzcd("");
        record.setJbzldj("");
        record.setFhcd("");
        record.setKwx("");
        record.setJglx("");

        record.setFtfchd("");
        record.setFzntfchd("");

    }

}
