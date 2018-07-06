package com.cj.record.activity.base;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.record.R;
import com.cj.record.views.ProgressPopupWindow;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    public BaseActivity mActivity;
    public Unbinder unbinder;
    private final String TAG = "BaseFragment";
    public static boolean isVisible;//是否对用户可见
    public LayoutInflater inflater;
    public ProgressPopupWindow progressPopupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        this.inflater = inflater;
        initMust(savedInstanceState);
        init();
        return view;
    }

    public void showPPW() {
        if (progressPopupWindow == null) {
            progressPopupWindow = new ProgressPopupWindow(mActivity);
        }
        progressPopupWindow.showPopupWindow();
    }

    public void dismissPPW() {
        if (progressPopupWindow != null) {
            progressPopupWindow.dismiss();
        }
    }

    public void initMust(Bundle savedInstanceState) {

    }

    private final void init() {
        initData();//初始化数据
        initView();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public void initData() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

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
