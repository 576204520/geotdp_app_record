package com.cj.record.presenter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.cj.record.BuildConfig;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.Media;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.base.BasePresenter;
import com.cj.record.contract.HoleContract;
import com.cj.record.db.GpsDao;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.RecordDao;
import com.cj.record.model.HoleModel;
import com.cj.record.net.RetrofitClient;
import com.cj.record.net.RxScheduler;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.RxPartMapUtils;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/3/19.
 */

public class HolePresenter extends BasePresenter<HoleContract.View> implements HoleContract.Presenter {
    private HoleContract.Model model;

    public HolePresenter() {
        model = new HoleModel();
    }

    @Override
    public void addOrUpdate(Hole hole) {

    }

    @Override
    public void loadData(String projectID, int page, int size, String search, String sort) {
//View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<PageBean<Hole>>() {
            @Override
            public void subscribe(ObservableEmitter<PageBean<Hole>> e) throws Exception {
                List<Hole> list = HoleDao.getInstance().getAll(projectID, page, size, search, sort);
                //查询每一个勘探点下记录的最大深度，并赋值
                if (list != null && list.size() > 0) {
                    for (Hole hole : list) {
                        //获取每一个hole 的最大深度
                        Record record = RecordDao.getInstance().getCurrentDepthByHoleID(hole.getId());
                        if (record != null && !TextUtils.isEmpty(record.getEndDepth())) {
                            hole.setCurrentDepth(record.getEndDepth());
                        } else {
                            hole.setCurrentDepth("0");
                        }
                        //获取record 不包含场景等信息
                        List<Record> recordList = RecordDao.getInstance().getNotUploadListByHoleID(hole.getId());
                        //获取record 只有场景等信息 该信息不包含历史记录
                        List<Record> recordListScene = RecordDao.getInstance().getNotUploadListByHoleIDScene(hole.getId());
                        hole.setNotUploadCount(recordList.size() + recordListScene.size());
                    }
                }
                int totleSize = HoleDao.getInstance().getAllCount(projectID);
                PageBean<Hole> pageBean = new PageBean<>();
                pageBean.setTotleSize(totleSize);
                pageBean.setPage(page);
                pageBean.setSize(size);
                pageBean.setList(list);
                e.onNext(pageBean);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PageBean<Hole>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PageBean<Hole> pageBean) {
                        mView.onSuccessList(pageBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onError(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void delete(Context context, Hole hole) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                hole.delete(context);
                e.onNext("");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onError(e);
                        mView.hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                        mView.onSuccessDelete();
                    }
                });
    }

    @Override
    public void relate(String userID, String relateID, String holeID, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.relate(userID, relateID, holeID, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessRelate(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void relateMore(List<Hole> checkList, Context context, HoleDao holeDao, Project project, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        //本地已经存在的关联点
        List<Hole> noRelateList = new ArrayList<>();
        for (Hole relateHole : checkList) {
            if (holeDao.checkRelated(relateHole.getId(), project.getId())) {
                noRelateList.add(relateHole);
            } else {
                //每次都新建一个新的勘察点
                Hole newHole = new Hole(context, project.getId());
                newHole.setRelateCode(relateHole.getCode());
                newHole.setRelateID(relateHole.getId());
                newHole.setUploadID(relateHole.getUploadID());
                newHole.setType(relateHole.getType());
                newHole.setDepth(relateHole.getDepth());
                newHole.setDescription(relateHole.getDescription());
                newHole.setElevation(relateHole.getElevation());
                newHole.setState("1");
                newHole.setStateGW("1");
                model.relate(App.userID, relateHole.getId(), newHole.getId(), verCode)
                        .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                        .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                        .subscribe(new Consumer<BaseObjectBean<String>>() {
                            @Override
                            public void accept(BaseObjectBean<String> bean) throws Exception {
                                mView.onSuccessRelateMore(bean, newHole);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mView.onError(throwable);
                            }
                        });
            }
        }
        if (noRelateList.size() > 0) {
            mView.onSuccessNoRelateList(noRelateList);
        }

    }


    @Override
    public void downLoadHole(List<LocalUser> localUserList, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        for (LocalUser localUser : localUserList) {
            model.downLoadHole(App.userID, localUser.getId(), verCode)
                    .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                    .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                    .subscribe(new Consumer<BaseObjectBean<String>>() {
                        @Override
                        public void accept(BaseObjectBean<String> bean) throws Exception {
                            mView.hideLoading();
                            mView.onSuccessDownloadHole(bean, localUser);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mView.hideLoading();
                            mView.onError(throwable);
                        }
                    });
        }
    }


    @Override
    public void getRelateList(String userID, String serialNumber, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.getRelateList(userID, serialNumber, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessRelateList(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void getDownLoadList(String userID, String serialNumber, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.getDownLoadList(userID, serialNumber, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessDownloadList(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void checkUser(String projectID, String userID, String testType, String holeType, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.checkUser(projectID, userID, testType, holeType, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessCheckUser(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void getSceneRecord(String holeID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        Observable.create(new ObservableOnSubscribe<List<Record>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Record>> e) throws Exception {
                e.onNext(RecordDao.getInstance().getSceneRecord(holeID));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Record>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Record> recordList) {
                        mView.onSuccessGetScene(recordList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onError(e);
                        mView.hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        mView.hideLoading();
                    }
                });
    }


    @Override
    public void uploadHole(Context context, Project project, Hole uploadHole) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();

        Flowable flowable = null;
        Flowable upload_company = null;
        Flowable upload_gw = null;

        //获取record 不包含场景等信息
        List<Record> recordList = RecordDao.getInstance().getNotUploadListByHoleID(uploadHole.getId());
        //获取record 只有场景等信息 该信息不包含历史记录
        List<Record> recordListScene = RecordDao.getInstance().getNotUploadListByHoleIDScene(uploadHole.getId());
        if (recordListScene != null && recordListScene.size() > 0) {
            recordList.addAll(recordListScene);
        }
        //所有真是存在的媒体数据集合
        List<Media> realMediaList = new LinkedList<>();
        if (recordList != null && recordList.size() > 0) {
            for (Record record : recordList) {
                //查出记录下所有媒体数据
                List<Media> mediaList = MediaDao.getInstance().getNotUploadListByHoleIDToZF(uploadHole.getId(), record.getId());
                if (mediaList != null && mediaList.size() > 0) {
                    for (Media media : mediaList) {
                        //mp4的localPath是目录
                        File file = new File(media.getLocalPath());
                        String localPath;
                        if (file.isDirectory()) {
                            localPath = Common.getVideoByDir(media.getLocalPath());
                        } else {
                            localPath = media.getLocalPath();
                        }
                        //判断媒体文件是否存在
                        File f = new File(localPath);
                        if (f.exists()) {
                            //添加媒体参数
                            realMediaList.add(media);
                        } else {
                            media.delete(context);
                        }
                    }
                }
            }

        }
        /**
         * 上传企业平台的数据
         */
        if ("1".equals(uploadHole.getState())) {
            //上传企业平台要提交的数据
            ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();
            //添加hole到表单
            params.putAll(uploadHole.getNameValuePairMap(project.getSerialNumber()));
            //url
            String strParams = "/" + project.getSerialNumber();
            //所有的gps的集合
            List<Gps> resultGpsList = new LinkedList<>();
            if (recordList != null && recordList.size() > 0) {
                //添加record集合到表单
                params.putAll(Record.getMap(recordList, project.getSerialNumber()));
                //获取record对应的gps
                for (Record record : recordList) {
                    Gps gps = GpsDao.getInstance().getGpsByRecord(record.getId());
                    if (gps != null) {
                        resultGpsList.add(gps);
                    }
                }
            }
            //所有媒体数据
            if (realMediaList != null && realMediaList.size() > 0) {
                //添加media数据到表单
                params.putAll(Media.getMap(realMediaList, project.getSerialNumber()));
                //获取media对应的gps
                for (Media media : realMediaList) {
                    Gps gps = GpsDao.getInstance().getGpsByMedia(media.getId());
                    resultGpsList.add(gps);
                }
            }
            //所有的gps数据
            if (resultGpsList != null && resultGpsList.size() > 0) {
                params.putAll(Gps.getMap(resultGpsList, project.getSerialNumber()));
            }

            RequestBody requestBody;
            //media file
            if (realMediaList != null && realMediaList.size() > 0) {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                //map数据添加到builder中
                builder = RxPartMapUtils.addTextPart(builder, params);
                for (Media media : realMediaList) {
                    File file = new File(media.getLocalPath());
                    if (file.isDirectory()) {
                        File video = new File(Common.getVideoByDir(media.getLocalPath()));
                        builder = RxPartMapUtils.addFilePart(builder, media.getId() + ".mp4", video);
                    } else {
                        builder = RxPartMapUtils.addFilePart(builder, media.getId() + ".jpg", file);
                    }
                }
                requestBody = builder.build();
                //url
                strParams += "-file";
            } else {
                strParams += "-noFile";
                requestBody = RxPartMapUtils.getRequestBody(params);
            }
            //最终url
            strParams += "?verCode=" + UpdateUtil.getVerCode(context) + "&userID=" + App.userID + "&relateID=" + uploadHole.getRelateID();
            upload_company = RetrofitClient.getInstance().getApi().uploadHole(BuildConfig.URL + "geotdp/hole/uploadNew" + strParams, requestBody);
        }
        /**
         * 上传规委的数据
         */
        if ("1".equals(uploadHole.getStateGW()) && project.isUpload()) {
            //修改projectID为序列号
            Hole uploadHoleZF = (Hole) uploadHole.clone();
            uploadHoleZF.setProjectID(project.getSerialNumber());
            uploadHoleZF.setSecretKey(BuildConfig.SECRET_KEY);
            uploadHoleZF.setRelateID(uploadHole.getUploadID());

            if (recordList != null && recordList.size() > 0) {
                //获取gps,赋值给recordList
                for (Record record : recordList) {
                    record.setIds(record.getId());
                    //title这个字段没用，清空
                    record.setTitle("");
                    record.setProjectID(project.getProjectID());
                    List<Gps> gpsList = GpsDao.getInstance().getListGpsByRecord(record.getId());//
                    if (gpsList != null && gpsList.size() > 0) {
                        record.setLongitude(gpsList.get(0).getLongitude());
                        record.setLatitude(gpsList.get(0).getLatitude());
                        record.setGpsTime(gpsList.get(0).getGpsTime());
                    }
                    //查出记录下所有媒体数据
                    final List<Media> mediaList = MediaDao.getInstance().getNotUploadListByHoleIDToZF(uploadHole.getId(), record.getId());
                    if (mediaList != null && mediaList.size() > 0) {
                        for (Media media : mediaList) {
                            Gps gps = GpsDao.getInstance().getGpsByMedia(media.getId());
                            if (null != gps) {
                                media.setProjectID(project.getProjectID());
                                media.setLongitude(gps.getLongitude());
                                media.setLatitude(gps.getLatitude());
                                media.setGpsTime(gps.getGpsTime());
                                File file = new File(media.getLocalPath());
                                String suffix;
                                if (file.isDirectory()) {
                                    suffix = ".mp4";
                                } else {
                                    suffix = ".jpg";
                                }
                                media.setInternetPath(BuildConfig.URL + "upload/" + project.getCompanyID() + "/" + project.getProjectID() + "/db/" + media.getId() + suffix);
                            }
                        }
                        record.setMediaListStr(mediaList);
                    }
                }
            }
            uploadHoleZF.setRecordListStr(recordList);
            String uploadHoleJson = JsonUtils.getInstance().toJson(uploadHoleZF);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), uploadHoleJson);

            upload_gw = RetrofitClient.getInstance().getApi().uploadHoleGW(BuildConfig.UPLOAD_GW, requestBody);
        }

        if (upload_company != null && upload_gw != null) {
            flowable = Flowable.concat(upload_company, upload_gw);
        } else if (upload_company != null && upload_gw == null) {
            flowable = upload_company;
        } else if (upload_company == null && upload_gw != null) {
            flowable = upload_gw;
        }
        //上传数据
        flowable.compose(RxScheduler.<BaseObjectBean<Integer>>Flo_io_main())
                .subscribe(new Consumer<BaseObjectBean<Integer>>() {
                    @Override
                    public void accept(BaseObjectBean<Integer> bean) throws Exception {
                        if (bean.isStatus()) {
                            switch (bean.getResult()) {
                                case 1:
                                    uploadHole.setState("2");
                                    break;
                                case 2:
                                    uploadHole.setStateGW("2");
                                    break;
                            }
                            HoleDao.getInstance().addOrUpdate(uploadHole);
                            if (project.isUpload()) {
                                if ("2".equals(uploadHole.getState()) && "2".equals(uploadHole.getStateGW())) {
                                    if (recordList != null && recordList.size() > 0) {
                                        for (Record record : recordList) {
                                            record.setState("2");
                                            RecordDao.getInstance().addOrUpdate(record);
                                        }
                                    }
                                    if (realMediaList != null && realMediaList.size() > 0) {
                                        for (Media media : realMediaList) {
                                            media.setState("2");
                                            MediaDao.getInstance().addOrUpdate(media);
                                        }
                                    }
                                }
                            } else {
                                if ("2".equals(uploadHole.getState())) {
                                    if (recordList != null && recordList.size() > 0) {
                                        for (Record record : recordList) {
                                            record.setState("2");
                                            RecordDao.getInstance().addOrUpdate(record);
                                        }
                                    }
                                    if (realMediaList != null && realMediaList.size() > 0) {
                                        for (Media media : realMediaList) {
                                            media.setState("2");
                                            MediaDao.getInstance().addOrUpdate(media);
                                        }
                                    }
                                }
                            }
                        }
                        mView.hideLoading();
                        mView.onSuccessUpload(bean);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });

    }

}
