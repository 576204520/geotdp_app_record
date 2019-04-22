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
 * 地层分类(碎石土) 的片段.
 */
public class LayerDescSstFragment extends RecordBaseFragment {

    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprMsd)
    MaterialBetterSpinner sprMsd;
    @BindView(R.id.sprKlxz)
    MaterialBetterSpinner sprKlxz;
    @BindView(R.id.sprKlpl)
    MaterialBetterSpinner sprKlpl;
    @BindView(R.id.sprYbljx)
    MaterialBetterSpinner sprYbljx;
    @BindView(R.id.sprYbljd)
    MaterialBetterSpinner sprYbljd;
    @BindView(R.id.sprJdljx)
    MaterialBetterSpinner sprJdljx;
    @BindView(R.id.sprJdljd)
    MaterialBetterSpinner sprJdljd;
    @BindView(R.id.sprZdlj)
    MaterialBetterSpinner sprZdlj;
    @BindView(R.id.sprSd)
    MaterialBetterSpinner sprSd;
    @BindView(R.id.sprFhcd)
    MaterialBetterSpinner sprFhcd;
    @BindView(R.id.sprKljp)
    MaterialBetterSpinner sprKljp;
    @BindView(R.id.sprMycf)
    MaterialBetterSpinner sprMycf;
    @BindView(R.id.sprTcw)
    MaterialBetterSpinner sprTcw;
    @BindView(R.id.sprJc)
    MaterialBetterSpinner sprJc;

    private List<DropItemVo> sprYsList;        //颜色
    private List<DropItemVo> sprMsdList;        //密实度
    private List<DropItemVo> sprTcwList;        //填充物
    private List<DropItemVo> sprKlxzList;      //颗粒形状
    private List<DropItemVo> sprKlplList;      //颗粒排列
    private List<DropItemVo> sprYbljxList;     //一般粒径小
    private List<DropItemVo> sprYbljdList;     //一般粒径大
    private List<DropItemVo> sprJdljxList;     //较大粒径小
    private List<DropItemVo> sprJdljdList;     //较大粒径大
    private List<DropItemVo> sprZdljList;      //最大粒径
    private List<DropItemVo> sprMycfList;      //岩母成份
    private List<DropItemVo> sprFhcdList;      //风化程度
    private List<DropItemVo> sprKljpList;      //颗粒级配
    private List<DropItemVo> sprSdList;        //湿度
    private List<DropItemVo> sprJcList;        //夹层


    StringBuilder mycfStr = new StringBuilder();
    MaterialDialog mycfDialog;
    private List<String> mycfList;

    StringBuilder jcStr = new StringBuilder();
    MaterialDialog jcDialog;
    private List<String> jcList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_cst;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_颜色"));        //颜色
        sprMsdList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_密实度"));        //密实度
        sprTcwList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_充填物"));        //充填物
        sprKlxzList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_颗粒形状"));      //颗粒形状
        sprKlplList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_颗粒排列"));      //颗粒排列
        sprYbljxList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_一般粒径小"));     //一般粒径小
        sprYbljdList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_一般粒径大"));     //一般粒径大
        sprJdljxList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_较大粒径小"));     //较大粒径小
        sprJdljdList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_较大粒径大"));     //较大粒径大
        sprZdljList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_最大粒径"));      //最大粒径
        sprMycfList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_母岩成份"));      //岩母成份
        sprFhcdList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_风化程度"));      //风化程度
        sprKljpList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_颗粒级配"));      //颗粒级配
        sprSdList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_湿度"));        //湿度
        sprJcList = DictionaryDao.getInstance().getDropItemList(getSqlString("碎石土_夹层"));        //夹层

        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprMsd.setAdapter(mActivity, sprMsdList, MaterialBetterSpinner.MODE_CLEAR);
        sprTcw.setAdapter(mActivity, sprTcwList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprTcw.setOnItemClickListener(tcwListener);
        sprKlxz.setAdapter(mActivity, sprKlxzList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKlxz.setOnItemClickListener(klxzListener);
        sprKlpl.setAdapter(mActivity, sprKlplList, MaterialBetterSpinner.MODE_CLEAR);
        sprYbljx.setAdapter(mActivity, sprYbljxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYbljx.setOnItemClickListener(bljxListener);
        sprYbljd.setAdapter(mActivity, sprYbljdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYbljd.setOnItemClickListener(ybljdListener);
        sprJdljx.setAdapter(mActivity, sprJdljxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprJdljx.setOnItemClickListener(jdljxListener);
        sprJdljd.setAdapter(mActivity, sprJdljdList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprJdljd.setOnItemClickListener(jdljdListener);
        sprZdlj.setAdapter(mActivity, sprZdljList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprZdlj.setOnItemClickListener(zdljListener);
        sprFhcd.setAdapter(mActivity, sprFhcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprKljp.setAdapter(mActivity, sprKljpList, MaterialBetterSpinner.MODE_CLEAR);
        sprSd.setAdapter(mActivity, sprSdList, MaterialBetterSpinner.MODE_CLEAR);


        mycfList = new ArrayList<>();
        mycfList = DropItemVo.getStrList(sprMycfList);

        sprMycf.setOnDialogListener(new MaterialBetterSpinner.OnDialogListener() {
            @Override
            public void onShow() {
                if (mycfDialog == null) {
                    mycfDialog = new MaterialDialog.Builder(getActivity()).title(R.string.hint_record_layer_mycf).items(mycfList)

                            .itemsCallbackMultiChoice(new Integer[]{}, new MaterialDialog.ListCallbackMultiChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                    mycfStr.delete(0, mycfStr.length());
                                    for (int i = 0; i < which.length; i++) {
                                        if (i > 0) mycfStr.append(',');
                                        mycfStr.append(text[i]);
                                    }
                                    return true;
                                }
                            }).callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    if (mycfStr.toString().trim().length() > 50) {
                                        ToastUtil.showToastS(mActivity, "该字段最大50字符，请重新选择");
                                    } else {
                                        sprMycf.setText(mycfStr.toString().trim());
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
                                            mycfList.add(input.toString().trim());
                                            dialog.dismiss();
                                            mycfDialog.getBuilder().items(mycfList);
                                            mycfDialog.show();
                                            dictionaryList.add(new Dictionary("1", "碎石土_母岩成份", input.toString().trim(), mycfList.size() + "", userID, Record.TYPE_LAYER));
                                        }
                                    }).show();
                                }
                            }).dismissListener(new MaterialDialog.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    sprMycf.setIsPopup(false);
                                }
                            }).alwaysCallMultiChoiceCallback().negativeText(R.string.custom).positiveText(R.string.agree).autoDismiss(false).neutralText(R.string.disagree).build();
                }
                mycfDialog.show();
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
                                            dictionaryList.add(new Dictionary("1", "碎石土_夹层", input.toString().trim(), jcList.size() + "", userID, Record.TYPE_LAYER));
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

        sprYs.setText(record.getYs());
        sprMsd.setText(record.getMsd());
        sprTcw.setText(record.getTcw());
        sprKlxz.setText(record.getKlxz());
        sprKlpl.setText(record.getKlpl());
        sprYbljx.setText(record.getYbljx());
        sprYbljd.setText(record.getYbljd());
        sprJdljx.setText(record.getJdljx());
        sprJdljd.setText(record.getJdljd());
        sprZdlj.setText(record.getZdlj());
        sprMycf.setText(record.getMycf());
        sprFhcd.setText(record.getFhcd());
        sprKljp.setText(record.getKljp());
        sprSd.setText(record.getSd());
        sprJc.setText(record.getJc());
    }


    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_颜色", sprYs.getText().toString().trim(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (sortNoTcw > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_充填物", sprTcw.getText().toString().trim(), "" + sortNoTcw, userID, Record.TYPE_LAYER));
        }
        if (sortNoKlxz > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_颗粒形状", sprKlxz.getText().toString().trim(), "" + sortNoKlxz, userID, Record.TYPE_LAYER));
        }
        if (sortNoYbljx > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_一般粒径小", sprYbljx.getText().toString().trim(), "" + sortNoYbljx, userID, Record.TYPE_LAYER));
        }
        if (sortNoYbljd > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_一般粒径大", sprYbljd.getText().toString().trim(), "" + sortNoYbljd, userID, Record.TYPE_LAYER));
        }
        if (sortNoJdljx > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_较大粒径小", sprJdljx.getText().toString().trim(), "" + sortNoJdljx, userID, Record.TYPE_LAYER));
        }
        if (sortNoJdljd > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_较大粒径大", sprJdljd.getText().toString().trim(), "" + sortNoJdljd, userID, Record.TYPE_LAYER));
        }
        if (sortNoZdlj > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "碎石土_最大粒径", sprZdlj.getText().toString().trim(), "" + sortNoZdlj, userID, Record.TYPE_LAYER));
        }
        if (dictionaryList.size() > 0) {
            DictionaryDao.getInstance().addDictionaryList(dictionaryList);
        }
        return true;
    }

    private int sortNoYs = 0;
    private int sortNoTcw = 0;
    private int sortNoKlxz = 0;
    private int sortNoYbljx = 0;
    private int sortNoYbljd = 0;
    private int sortNoJdljx = 0;
    private int sortNoJdljd = 0;
    private int sortNoZdlj = 0;
    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener tcwListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoTcw = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener klxzListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoKlxz = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener bljxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYbljx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener ybljdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYbljd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener jdljxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoJdljx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener jdljdListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoJdljd = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener zdljListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoZdlj = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString().trim());
        record.setMsd(sprMsd.getText().toString().trim());
        record.setTcw(sprTcw.getText().toString().trim());
        record.setKlxz(sprKlxz.getText().toString().trim());
        record.setKlpl(sprKlpl.getText().toString().trim());
        record.setYbljx(sprYbljx.getText().toString().trim());
        record.setYbljd(sprYbljd.getText().toString().trim());
        record.setJdljx(sprJdljx.getText().toString().trim());
        record.setJdljd(sprJdljd.getText().toString().trim());
        record.setZdlj(sprZdlj.getText().toString().trim());
        record.setMycf(sprMycf.getText().toString().trim());
        record.setFhcd(sprFhcd.getText().toString().trim());
        record.setKljp(sprKljp.getText().toString().trim());
        record.setSd(sprSd.getText().toString().trim());
        record.setJc(sprJc.getText().toString().trim());
        return record;
    }

}