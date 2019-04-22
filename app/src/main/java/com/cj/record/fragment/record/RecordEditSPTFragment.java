package com.cj.record.fragment.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.cj.record.views.MaterialEditTextElevation;
import com.cj.record.views.MaterialEditTextInt;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 标灌记录
 */
public class RecordEditSPTFragment extends RecordBaseFragment {
    @BindView(R.id.edtDrillLength)
    MaterialEditTextElevation edtDrillLength;
    @BindView(R.id.edtBegin1)
    MaterialEditTextElevation edtBegin1;
    @BindView(R.id.edtEnd1)
    MaterialEditTextElevation edtEnd1;
    @BindView(R.id.edtBlow1)
    MaterialEditTextInt edtBlow1;
    @BindView(R.id.edtBegin2)
    MaterialEditTextElevation edtBegin2;
    @BindView(R.id.edtEnd2)
    MaterialEditTextElevation edtEnd2;
    @BindView(R.id.edtBlow2)
    MaterialEditTextInt edtBlow2;
    @BindView(R.id.edtBegin3)
    MaterialEditTextElevation edtBegin3;
    @BindView(R.id.edtEnd3)
    MaterialEditTextElevation edtEnd3;
    @BindView(R.id.lltRow3)
    LinearLayout lltRow3;
    @BindView(R.id.edtBlow3)
    MaterialEditTextInt edtBlow3;
    @BindView(R.id.edtBegin4)
    MaterialEditTextElevation edtBegin4;
    @BindView(R.id.edtEnd4)
    MaterialEditTextElevation edtEnd4;
    @BindView(R.id.lltRow4)
    LinearLayout lltRow4;
    @BindView(R.id.edtBlow4)
    MaterialEditTextInt edtBlow4;
    @BindView(R.id.edtEndCount)
    MaterialEditTextElevation edtEndCount;
    @BindView(R.id.edtBlowCount)
    MaterialEditTextInt edtBlowCount;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_spt_edit;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        setValue();
        edtBegin1.addTextChangedListener(beginTextWatcher);
        edtEnd1.addTextChangedListener(endCountTextWatcher1);
        edtEnd2.addTextChangedListener(endCountTextWatcher2);
        edtEnd3.addTextChangedListener(endCountTextWatcher3);
        edtEnd4.addTextChangedListener(endCountTextWatcher4);
        edtBlow2.addTextChangedListener(blowCountTextWatcher);
        edtBlow3.addTextChangedListener(blowCountTextWatcher);
        edtBlow4.addTextChangedListener(blowCountTextWatcher);
    }

    private void setValue() {
        try {
            edtDrillLength.setText(record.getDrillLength());
            edtBegin1.setText(record.getBegin1());
            edtEnd1.setText(record.getEnd1());
            edtBlow1.setText(record.getBlow1());
            edtBegin2.setText(record.getBegin2());
            edtEnd2.setText(record.getEnd2());
            edtBlow2.setText(record.getBlow2());
            edtBegin3.setText(record.getBegin3());
            edtEnd3.setText(record.getEnd3());
            edtBlow3.setText(record.getBlow3());
            edtBegin4.setText(record.getBegin4());
            edtEnd4.setText(record.getEnd4());
            edtBlow4.setText(record.getBlow4());
            if ("0.00".equals(edtBegin3.getText().toString())) lltRow3.setVisibility(View.GONE);
            if ("0.00".equals(edtBegin4.getText().toString())) lltRow4.setVisibility(View.GONE);
            updateEndCount();
            updateBlowCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher beginTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int selectionStart;
        private int selectionEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                record.setBeginBySPT(s.toString());
                edtEnd1.setText(record.getEnd1());
                edtBegin2.setText(record.getBegin2());
                edtEnd2.setText(record.getEnd2());
                edtBegin3.setText(record.getBegin3());
                edtEnd3.setText(record.getEnd3());
                edtBegin4.setText(record.getBegin4());
                edtEnd4.setText(record.getEnd4());
            }
        }
    };


    TextWatcher endCountTextWatcher1 = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                if (validator(edtBegin1, edtEnd1)) {
                    BigDecimal end1 = new BigDecimal(s.toString());
                    record.setSPTBegin2(end1.toString());
                    edtBegin2.setText(record.getBegin2());
                    edtEnd2.setText(record.getEnd2());
                    edtBegin3.setText(record.getBegin3());
                    edtEnd3.setText(record.getEnd3());
                    edtBegin4.setText(record.getBegin4());
                    edtEnd4.setText(record.getEnd4());
                }
            }
        }
    };
    TextWatcher endCountTextWatcher2 = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                if (validator(edtBegin2, edtEnd2)) {
                    BigDecimal end2 = new BigDecimal(s.toString());
                    BigDecimal begin2 = new BigDecimal(edtBegin2.getText().toString());
                    double difference = end2.subtract(begin2).doubleValue();
                    if (difference > 0.1) {
                        edtEnd2.setError("每次惯探深度不能超过10cm");
                    } else {
                        if (difference < 0.1) {
                            lltRow3.setVisibility(View.GONE);
                            edtBegin3.setText("0");
                            edtEnd3.setText("0");
                            edtBlow3.setText("0");
                            lltRow4.setVisibility(View.GONE);
                            edtBegin4.setText("0");
                            edtEnd4.setText("0");
                            edtBlow4.setText("0");
                        } else {
                            record.setSPTBegin3(end2.toString());
                            lltRow3.setVisibility(View.VISIBLE);
                            edtBegin3.setText(record.getBegin3());
                            edtEnd3.setText(record.getEnd3());
                            lltRow4.setVisibility(View.VISIBLE);
                            edtBegin4.setText(record.getBegin4());
                            edtEnd4.setText(record.getEnd4());
                        }
                        updateEndCount();
                    }
                }
            }
        }
    };
    TextWatcher endCountTextWatcher3 = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                if (validator(edtBegin3, edtEnd3)) {
                    if (lltRow3.getVisibility() == View.VISIBLE) {
                        BigDecimal end3 = new BigDecimal(s.toString());
                        BigDecimal begin3 = new BigDecimal(edtBegin3.getText().toString());
                        double difference = end3.subtract(begin3).doubleValue();
                        if (difference > 0.1) {
                            edtEnd3.setError("每次惯探深度不能超过10cm");
                        } else {

                            if (difference < 0.1) {
                                lltRow4.setVisibility(View.GONE);
                                edtBegin4.setText("0");
                                edtEnd4.setText("0");
                                edtBlow4.setText("0");
                            } else {
                                record.setSPTBegin4(end3.toString());
                                lltRow4.setVisibility(View.VISIBLE);
                                edtBegin4.setText(record.getBegin4());
                                edtEnd4.setText(record.getEnd4());
                            }

                            updateEndCount();
                        }
                    }
                }
            }
        }
    };


    TextWatcher endCountTextWatcher4 = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                if (validator(edtBegin4, edtEnd4)) {
                    if (lltRow3.getVisibility() == View.VISIBLE) {
                        BigDecimal end4 = new BigDecimal(s.toString());
                        BigDecimal begin4 = new BigDecimal(edtBegin4.getText().toString());
                        double difference = end4.subtract(begin4).doubleValue();
                        if (difference > 0.1) {
                            edtEnd4.setError("每次惯探深度不能超过10cm");
                        } else {
                            updateEndCount();
                        }
                    }
                }
            }
        }
    };

    public boolean validator(MaterialEditTextElevation edtBegin, MaterialEditTextElevation edtEnd) {
        boolean validator = true;
        try {
            BigDecimal end = new BigDecimal(edtEnd.getText().toString());
            BigDecimal begin = new BigDecimal(edtBegin.getText().toString());
            double difference = end.subtract(begin).doubleValue();
            if (difference <= 0) {
                edtEnd.setError("终止深度不能小于起始深度");
                validator = false;
            }
        } catch (Exception e) {
            validator = false;
            e.printStackTrace();
        }
        return validator;
    }

    TextWatcher blowCountTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {
                updateBlowCount();
            }
        }
    };


    public void updateEndCount() {
        double endCount = 0.0;
        double begin2 = Double.valueOf(edtBegin2.getText().toString());
        double end2 = Double.valueOf(edtEnd2.getText().toString());
        double end3 = Double.valueOf(edtEnd3.getText().toString());
        double end4 = Double.valueOf(edtEnd4.getText().toString());
        if (end4 != 0) {
            endCount = end4 - begin2;
        } else if (end3 != 0) {
            endCount = end3 - begin2;
        } else if (end2 != 0) {
            endCount = end2 - begin2;
        }

        edtEndCount.setText(String.valueOf(endCount));
    }

    public void updateBlowCount() {
        int blowCount = Integer.valueOf(edtBlow2.getText().toString()) + Integer.valueOf(edtBlow3.getText().toString()) + Integer.valueOf(edtBlow4.getText().toString());
        edtBlowCount.setText(String.valueOf(blowCount));
    }


    @Override
    public Record getRecord() {
        record.setDrillLength(edtDrillLength.getText().toString());
        record.setBegin1(edtBegin1.getText().toString());
        record.setEnd1(edtEnd1.getText().toString());
        record.setBlow1(edtBlow1.getText().toString());
        record.setBegin2(edtBegin2.getText().toString());
        record.setEnd2(edtEnd2.getText().toString());
        record.setBlow2(edtBlow2.getText().toString());
        record.setBegin3(edtBegin3.getText().toString());
        record.setEnd3(edtEnd3.getText().toString());
        record.setBlow3(edtBlow3.getText().toString());
        record.setBegin4(edtBegin4.getText().toString());
        record.setEnd4(edtEnd4.getText().toString());
        record.setBlow4(edtBlow4.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = edtDrillLength.getText().toString().trim();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SPT;
    }

    @Override
    public String getBegin() {
        return edtBegin1.getText().toString().trim();
    }

    @Override
    public String getEnd() {
        if (lltRow4.getVisibility() == View.VISIBLE) {
            return edtEnd4.getText().toString().trim();
        } else if (lltRow3.getVisibility() == View.VISIBLE) {
            return edtEnd3.getText().toString().trim();
        } else {
            return edtEnd2.getText().toString().trim();
        }
    }


    @Override
    public boolean validator() {
        boolean validator = true;
        validator = validator(edtBegin1, edtEnd1) || validator(edtBegin2, edtEnd2) || validator(edtBegin3, edtEnd3) || validator(edtBegin4, edtEnd4);
        if (RecordDao.getInstance().validatorBeginDepth(record, Record.TYPE_DPT, edtBegin1.getText().toString())) {
            edtBegin1.setError("与其他记录重叠");
            validator = false;
        }
        MaterialEditTextElevation edtEnd;
        if (lltRow4.getVisibility() == View.VISIBLE) {
            edtEnd = edtEnd4;
        } else if (lltRow3.getVisibility() == View.VISIBLE) {
            edtEnd = edtEnd3;
        } else {
            edtEnd = edtEnd2;
        }
        if (RecordDao.getInstance().validatorEndDepth(record, Record.TYPE_DPT, edtEnd.getText().toString())) {
            edtEnd.setError("与其他记录重叠");
            validator = false;
        }

        if ("".equals(edtDrillLength.getText().toString()) || Double.valueOf(edtDrillLength.getText().toString()) == 0) {
            edtDrillLength.setError("钻杆长度不能为零");
            validator = false;
        }
        if ("".equals(edtBlowCount.getText().toString()) || Double.valueOf(edtBlowCount.getText().toString()) == 0) {
            edtBlowCount.setError("击数不能为零");
            validator = false;
        }
        return validator;
    }
}
