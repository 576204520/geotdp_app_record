package com.cj.record.fragment.record;

import com.cj.record.activity.MainActivity;
import com.cj.record.activity.base.BaseFragment;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.db.DictionaryDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/7.
 */

public class RecordBaseFragment extends BaseFragment {
    public DictionaryDao dictionaryDao;
    public Record record;
    public String userID;
    public String userName;
    /**
     * 存放自定义的字典的集合
     */
    protected List<Dictionary> dictionaryList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        userID = (String) SPUtils.get(mActivity, Urls.SPKey.USER_ID, "");
        userName = (String) SPUtils.get(mActivity, Urls.SPKey.USER_REALNAME, "");
        dictionaryDao = new DictionaryDao(mActivity);
        if (getArguments().containsKey(MainActivity.RECORD)) {
            record = (Record) getArguments().getSerializable(MainActivity.RECORD);
        }
    }

    @Override
    public void initView() {

    }

    public Record getRecord() {
        return null;
    }

    public boolean validator() {
        return true;
    }

    public String getTitle() {
        return "";
    }

    public String getTypeName() {
        return null;
    }

    public String getBegin() {
        return null;
    }

    public String getEnd() {
        return null;
    }

    /**
     * 根据sort查询字典库的sql
     *
     * @param sqlName
     * @return
     */
    public String getSqlString(String sqlName) {
        return "select rowid as _id,name  from dictionary where sort='" + sqlName + "' order by cast(sortNo as int)";
    }

    public boolean layerValidator() {
        return true;
    }
}
