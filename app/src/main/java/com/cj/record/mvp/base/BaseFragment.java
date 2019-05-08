package com.cj.record.mvp.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public abstract class BaseFragment extends Fragment {
    public BaseActivity mActivity;
    private Unbinder unBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inCreate();
        mActivity = (BaseActivity) getActivity();
        if (savedInstanceState != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (ft != null && isAdded()) {
                ft.remove(this);
                ft.commit();
            }
            if (getParentFragment() != null) {
                FragmentTransaction pft = getParentFragment().getChildFragmentManager().beginTransaction();
                if (pft != null && isAdded()) {
                    pft.remove(this);
                    pft.commit();
                }
            }
        }
    }

    public void inCreate() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getLayoutId(), container, false);
        unBinder = ButterKnife.bind(this, view);
        initMust(savedInstanceState);
        initView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unBinder.unbind();
        } catch (Exception e) {
        }
    }

    public void initMust(Bundle savedInstanceState) {

    }

    /**
     * 初始化视图
     *
     * @param view
     */
    protected abstract void initView(View view);

    protected abstract int getLayoutId();

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

}
