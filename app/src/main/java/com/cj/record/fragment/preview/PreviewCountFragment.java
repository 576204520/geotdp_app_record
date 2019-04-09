package com.cj.record.fragment.preview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.record.R;
import com.cj.record.activity.PreviewActivity;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.db.GpsDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PreviewCountFragment extends Fragment {
    private static PreviewCountFragment instance = null;

    public static PreviewCountFragment newInstance() {
        if (instance == null) {
            instance = new PreviewCountFragment();
        }
        return instance;
    }

    public PreviewCountFragment() {
    }

    public String userName;
    private Hole hole;
    private Record person, op, oc, scene;
    private int person_num = 0;//描述员照片数
    private int op_num = 0;//机长照片数
    private int oc_num = 0;//钻机照片数
    private int scene_num = 0;//场景照片数
    private int photoNumber = 0;//点总照片数
    private Map<Integer, Integer> countMap;
    private Context context;
    private MaterialEditText preview_count_person;//描述员
    private MaterialEditText preview_count_person_num;//描述员照片数量
    private MaterialEditText preview_count_op;//机长
    private MaterialEditText preview_count_op_num;//机长照片数量
    private MaterialEditText preview_count_oc;//钻机
    private MaterialEditText preview_count_oc_num;//钻机数量
    private MaterialEditText preview_count_scene;//场景
    private MaterialEditText preview_count_scene_num;//场景数量
    private MaterialEditText preview_count_frequency;//回次
    private MaterialEditText preview_count_layer;//岩土
    private MaterialEditText preview_count_water;//水位
    private MaterialEditText preview_count_getlayer;//取土
    private MaterialEditText preview_count_getwater;//取水
    private MaterialEditText preview_count_dpt;//动探
    private MaterialEditText preview_count_spt;//标贯
    private MaterialEditText preview_count_photo;//总照片数
    private MaterialEditText preview_count_totaltime;//总时间
    private MaterialEditText preview_count_worktime;//时间分布
    //位置偏移数量与比例
    private MaterialEditText preview_count_place1, preview_count_place2, preview_count_place3, preview_count_place4;
    private String place1 = "";
    private String place2 = "";
    private String place3 = "";
    private String place4 = "";
    private String totalTime = "";
    private String wordTime = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments().containsKey(PreviewActivity.EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(PreviewActivity.EXTRA_HOLE);
        }
        userName = (String) SPUtils.get(context, Urls.SPKey.USER_REALNAME, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frt_preview_count, container, false);
        initView(view);
        loadData();
        return view;
    }

    private void initView(View view) {
        preview_count_person = (MaterialEditText) view.findViewById(R.id.preview_count_person);
        preview_count_person_num = (MaterialEditText) view.findViewById(R.id.preview_count_person_num);
        preview_count_op = (MaterialEditText) view.findViewById(R.id.preview_count_op);
        preview_count_op_num = (MaterialEditText) view.findViewById(R.id.preview_count_op_num);
        preview_count_oc = (MaterialEditText) view.findViewById(R.id.preview_count_oc);
        preview_count_oc_num = (MaterialEditText) view.findViewById(R.id.preview_count_oc_num);
        preview_count_scene = (MaterialEditText) view.findViewById(R.id.preview_count_scene);
        preview_count_scene_num = (MaterialEditText) view.findViewById(R.id.preview_count_scene_num);
        preview_count_frequency = (MaterialEditText) view.findViewById(R.id.preview_count_frequency);
        preview_count_layer = (MaterialEditText) view.findViewById(R.id.preview_count_layer);
        preview_count_water = (MaterialEditText) view.findViewById(R.id.preview_count_water);
        preview_count_getlayer = (MaterialEditText) view.findViewById(R.id.preview_count_getlayer);
        preview_count_getwater = (MaterialEditText) view.findViewById(R.id.preview_count_getwater);
        preview_count_dpt = (MaterialEditText) view.findViewById(R.id.preview_count_dpt);
        preview_count_spt = (MaterialEditText) view.findViewById(R.id.preview_count_spt);
        preview_count_photo = (MaterialEditText) view.findViewById(R.id.preview_count_photo);
        preview_count_totaltime = (MaterialEditText) view.findViewById(R.id.preview_count_totaltime);
        preview_count_worktime = (MaterialEditText) view.findViewById(R.id.preview_count_worktime);
        preview_count_place1 = (MaterialEditText) view.findViewById(R.id.preview_count_place1);
        preview_count_place2 = (MaterialEditText) view.findViewById(R.id.preview_count_place2);
        preview_count_place3 = (MaterialEditText) view.findViewById(R.id.preview_count_place3);
        preview_count_place4 = (MaterialEditText) view.findViewById(R.id.preview_count_place4);


    }

    public void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //每条记录的个数
                countMap = RecordDao.getInstance().getSortCountMap(hole.getId());
                //四条 特殊记录
                person = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_RECORDPERSON);
                op = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
                oc = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATECODE);
                scene = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_SCENE);
                //照片总数
                photoNumber = MediaDao.getInstance().getMediaCountByHoleID(hole.getId());
                //四条 特殊记录的照片数
                if (person != null) {
                    person_num = MediaDao.getInstance().getMediaCountByrdcordID(person.getId());
                }
                if (op != null) {
                    op_num = MediaDao.getInstance().getMediaCountByrdcordID(op.getId());
                }
                if (oc != null) {
                    oc_num = MediaDao.getInstance().getMediaCountByrdcordID(oc.getId());
                }
                if (scene != null) {
                    scene_num = MediaDao.getInstance().getMediaCountByrdcordID(scene.getId());
                }
                //找出所有的gps的偏移，并计算个数和所占比例
                List<Gps> gpsList = GpsDao.getInstance().getGpsListByHoleID(hole.getId());
                int p1 = 0;
                int p2 = 0;
                int p3 = 0;
                int p4 = 0;
                if (null != gpsList) {
                    for (Gps gps : gpsList) {
                        double l = Double.valueOf(gps.getDistance());
                        if (l <= 100) {
                            p1++;
                        }
                        if (100 < l && l <= 200) {
                            p2++;
                        }
                        if (200 < l && l <= 300) {
                            p3++;
                        }
                        if (300 < l) {
                            p4++;
                        }
                    }
                    int total = gpsList.size();
                    place1 = p1 + getFormat(p1, total);
                    place2 = p2 + getFormat(p2, total);
                    place3 = p3 + getFormat(p3, total);
                    place4 = p4 + getFormat(p4, total);
                }

                //找出最后一个gps的时间
                Gps lastGps = GpsDao.getInstance().getGpsByHoleID(hole.getId());
                Gps firstGps = GpsDao.getInstance().getGpsByHoleIDFrist(hole.getId());
                if (lastGps != null && firstGps != null) {
                    String lastTime = lastGps.getGpsTime();
                    totalTime = hole.getCreateTime() + " ~ " + lastTime;
                    //工作用时
                    try {
                        wordTime = stringDaysBetween(hole.getCreateTime(), lastTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendMessage(new Message());
            }
        }).start();
    }

    private String getFormat(double num, int total) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后几位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) num / (float) total * 100);
        return " (" + result + "%" + ")";
    }

    //记录第一条gps时间、钻孔创建的本地时间
    public String getFirstTime(String createTime, String firstGpsTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String firstTime = createTime;
        try {
            Date date1 = sdf.parse(createTime);
            Date date2 = sdf.parse(firstGpsTime);
            if (date2.before(date1)) {
                firstTime = firstGpsTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return firstTime;
    }

    public String stringDaysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1);
//        return Integer.parseInt(String.valueOf(between_days));
        long days = between_days / (1000 * 60 * 60 * 24);
        long hours = (between_days % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (between_days % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (between_days % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分 " + seconds + " 秒 ";

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //描述员暂时是user里的真是姓名
            preview_count_person.setText(userName);
            if (person != null) {
//                preview_count_person.setText(person.getOperatePerson());
                preview_count_person_num.setText(person_num + "");
            }
            if (op != null) {
                preview_count_op.setText(op.getOperatePerson());
                preview_count_op_num.setText(op_num + "");
            }
            if (oc != null) {
                preview_count_oc.setText(oc.getTestType());
                preview_count_oc_num.setText(oc_num + "");
            }
            if (scene != null) {
                //场景就叫场景.....
                preview_count_scene.setText("场景");
                preview_count_scene_num.setText(scene_num + "");
            }
            preview_count_frequency.setText(countMap.get(2) + "");
            preview_count_layer.setText(countMap.get(3) + "");
            preview_count_water.setText(countMap.get(4) + "");
            preview_count_dpt.setText(countMap.get(5) + "");
            preview_count_spt.setText(countMap.get(6) + "");
            preview_count_getlayer.setText(countMap.get(7) + "");
            preview_count_getwater.setText(countMap.get(8) + "");
            preview_count_photo.setText(photoNumber + "");

            preview_count_totaltime.setText(totalTime);
            //定位位置间隔
            preview_count_place1.setText(place1);
            preview_count_place2.setText(place2);
            preview_count_place3.setText(place3);
            preview_count_place4.setText(place4);
            //
            preview_count_worktime.setText(wordTime);
        }
    };


}
