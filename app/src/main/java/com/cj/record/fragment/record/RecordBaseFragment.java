package com.cj.record.fragment.record;

import android.arch.lifecycle.Lifecycle;
import android.view.View;

import com.cj.record.activity.MainActivity;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.base.BaseActivity;
import com.cj.record.base.BaseFragment;
import com.cj.record.base.BaseMvpFragment;
import com.cj.record.base.BasePresenter;
import com.cj.record.base.BaseView;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/7.
 */

public abstract class RecordBaseFragment<T extends BasePresenter> extends BaseFragment implements BaseView {
    public Record record;
    public String userID;
    public String userName;
    /**
     * 存放自定义的字典的集合
     */
    protected List<Dictionary> dictionaryList = new ArrayList<>();
    protected T mPresenter;

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroyView();
    }

    /**
     * 绑定生命周期 防止MVP内存泄漏
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> AutoDisposeConverter<T> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mActivity, Lifecycle.Event.ON_DESTROY));
    }

    @Override
    protected void initView(View view) {
        userID = (String) SPUtils.get(mActivity, Urls.SPKey.USER_ID, "");
        userName = (String) SPUtils.get(mActivity, Urls.SPKey.USER_REALNAME, "");
        if (getArguments().containsKey(MainActivity.RECORD)) {
            record = (Record) getArguments().getSerializable(MainActivity.RECORD);
        }
    }

    @Override
    public int getLayoutId() {
        return 0;
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
