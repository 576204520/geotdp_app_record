package com.cj.record.mvp.model;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.mvp.contract.ProjectContract;
import com.cj.record.db.ProjectDao;
import com.cj.record.mvp.net.RetrofitClient;

import io.reactivex.Flowable;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class ProjectModel implements ProjectContract.Model {

    @Override
    public Flowable<BaseObjectBean<String>> relate(String serialNumber, String userID, String verCode) {
        return RetrofitClient.getInstance().getApi().relateProject(serialNumber, userID, verCode);
    }

    @Override
    public Flowable<PageBean<Project>> loadData(String userID, int page, int size, String search) {
        return ProjectDao.getInstance().loadData(userID,page,size,search);
    }

    @Override
    public Flowable<Project> addOrUpdate(Project project, boolean updateState) {
        return ProjectDao.getInstance().addOrUpdate(project,updateState);
    }

    @Override
    public Flowable<Boolean> delete(Project project) {
        return ProjectDao.getInstance().deleteProject(project);
    }
}
