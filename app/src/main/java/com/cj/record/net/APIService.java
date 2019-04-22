package com.cj.record.net;


import android.util.ArrayMap;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface APIService {

    /**
     * 登陆
     *
     * @param email    账号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/compileUser/login")
    Flowable<BaseObjectBean<String>> login(@Field("email") String email,
                                           @Field("password") String password);

    /**
     * 关联项目
     *
     * @param serialNumber
     * @param userID
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/project/getProjectInfoByKey")
    Flowable<BaseObjectBean<String>> relateProject(@Field("project.serialNumber") String serialNumber,
                                                   @Field("userID") String userID,
                                                   @Field("verCode") String verCode);

    /**
     * 关联勘探点
     *
     * @param userID
     * @param relateID
     * @param holeID
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/hole/relate")
    Flowable<BaseObjectBean<String>> relateHole(@Field("userID") String userID,
                                                @Field("relateID") String relateID,
                                                @Field("holeID") String holeID,
                                                @Field("verCode") String verCode);

    /**
     * 下载勘探点
     *
     * @param userID
     * @param holeID
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/hole/download")
    Flowable<BaseObjectBean<String>> downLoadHole(@Field("userID") String userID,
                                                  @Field("holeID") String holeID,
                                                  @Field("verCode") String verCode);

    /**
     * 获取关联勘探点的列表
     *
     * @param userID
     * @param serialNumber
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/hole/getRelateList")
    Flowable<BaseObjectBean<String>> getRelateList(@Field("userID") String userID,
                                                   @Field("serialNumber") String serialNumber,
                                                   @Field("verCode") String verCode);

    /**
     * 获取下载勘探点的列表
     *
     * @param userID
     * @param serialNumber
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/hole/getHoleListWithRecord")
    Flowable<BaseObjectBean<String>> getDownLoadList(@Field("userID") String userID,
                                                     @Field("serialNumber") String serialNumber,
                                                     @Field("verCode") String verCode);

    /**
     * 校验人员信息
     *
     * @param projectID
     * @param userID
     * @param testType
     * @param holeType
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/compileUser/checkUser")
    Flowable<BaseObjectBean<String>> checkUser(@Field("projectID") String projectID,
                                               @Field("userID") String userID,
                                               @Field("testType") String testType,
                                               @Field("holeType") String holeType,
                                               @Field("verCode") String verCode);

    /**
     * 上传勘探点
     * 企业平台
     *
     * @param url
     * @return
     */
    @Multipart
    @POST
    Flowable<BaseObjectBean<Integer>> uploadHole(@Url String url, @PartMap Map<String, RequestBody> params);

    /**
     * 上传到规委平台
     *
     * @param url
     * @param requestBody
     * @return
     */
    @POST
    Flowable<BaseObjectBean<Integer>> uploadHoleGW(@Url String url, @Body RequestBody requestBody);

    /**
     * 上传字典库
     *
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/dictionary/upload")
    Flowable<BaseObjectBean<String>> uploadDictionary(@FieldMap Map<String, String> params);

    /**
     * 获取字典库列表
     *
     * @param relateID
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/dictionary/download")
    Flowable<BaseObjectBean<String>> downloadDictionary(@Field("relateID") String relateID,
                                                        @Field("verCode") String verCode);

    /**
     * 检查版本更新
     *
     * @param relateID
     * @param verCode
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/version/check")
    Flowable<BaseObjectBean<String>> versionCheck(@Field("userID") String relateID,
                                                  @Field("verCode") String verCode);

    /**
     * 修改用户信息
     *
     * @param userID
     * @param email
     * @param idCard
     * @param certificateNumber3
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/compileUser/updateInfo")
    Flowable<BaseObjectBean<String>> updateInfo(@Field("userID") String userID,
                                                @Field("email") String email,
                                                @Field("idCard") String idCard,
                                                @Field("certificateNumber3") String certificateNumber3);

    /**
     * 重置密码
     *
     * @param userID
     * @param email
     * @param oldPassword
     * @param newPassword
     * @param newPassword2
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/compileUser/resetPassword")
    Flowable<BaseObjectBean<String>> resetPassword(@Field("userID") String userID,
                                                   @Field("email") String email,
                                                   @Field("oldPassword") String oldPassword,
                                                   @Field("newPassword") String newPassword,
                                                   @Field("newPassword2") String newPassword2);

    /**
     * 校验机长
     *
     * @param userID
     * @param operatePerson
     * @param testType
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/people/checkOperate")
    Flowable<BaseObjectBean<String>> checkOperate(@Field("userID") String userID,
                                                  @Field("operatePerson") String operatePerson,
                                                  @Field("testType") String testType);

    /**
     * 未关注的其他用户
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/myUserForCompanyOrProject")
    Flowable<BaseObjectBean<String>> myUserForCompanyOrProject(@Field("userID") String userID);

    /**
     * 我的好友列表
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/myFriendList")
    Flowable<BaseObjectBean<String>> myFriendList(@Field("userID") String userID);

    /**
     * 添加关注
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/addUserFriend")
    Flowable<BaseObjectBean<String>> addUserFriend(@Field("userID") String userID,
                                                   @Field("targetUserID") String targetUserID);

    /**
     * 取消关注
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/deleteUserFriend")
    Flowable<BaseObjectBean<String>> deleteUserFriend(@Field("userID") String userID,
                                                      @Field("targetUserID") String targetUserID);

    /**
     * 发送消息
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/sendMessage")
    Flowable<BaseObjectBean<String>> sendMessage(@Field("userID") String userID,
                                                 @Field("targetUserID") String targetUserID,
                                                 @Field("message") String message);

    /**
     * 已读消息回调
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/readMessageCallBack")
    Flowable<BaseObjectBean<String>> readMessageCallBack(@Field("userID") String userID,
                                                         @Field("chatRecordId") String targetUserID);

    /**
     * 消息记录
     *
     * @param userID
     * @return
     */
    @FormUrlEncoded
    @POST("geotdp/chat/myChatRecord")
    Flowable<BaseObjectBean<String>> myChatRecord(@Field("userID") String userID,
                                                  @Field("targetUserID") String targetUserID);

}
