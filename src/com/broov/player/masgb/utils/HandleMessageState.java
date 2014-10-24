package com.broov.player.masgb.utils;

public class HandleMessageState {
	/**
	 * 服务器异常
	 */
	public final static int SERVERLT_ERROR = 999;
	/**
	 * 获取在学课程列表失败
	 */
	public static final int GET_COURSING_FAILURE = SERVERLT_ERROR + 1;
	/**
	 * 获取在学课程列表成功
	 */
	public static final int GET_COURSING_SUCCESS = GET_COURSING_FAILURE + 1;
	/**
	 * 登入成功
	 */
	public static final int LOGIN_SUCCESS = GET_COURSING_SUCCESS + 1;
	/**
	 * 获取课程课件列表成功
	 */
	public static final int GET_COURSE_DOC_LIST_SUCCESS = LOGIN_SUCCESS + 1;
	/**
	 * 获取已学课程失败
	 */
	public static final int GET_COURSED_FAILURE = GET_COURSE_DOC_LIST_SUCCESS + 1;
	/**
	 * 获取已学课程成功
	 */
	public static final int GET_COURSED_SUCCESS = GET_COURSED_FAILURE + 1;
	/**
	 * 获取课程列表失败（选课中心）
	 */
	public static final int GET_COURSE_LIST_FAILURE = GET_COURSED_SUCCESS + 1;
	/**
	 * 获取课程列表成功（选课中心）
	 */
	public static final int GET_COURSE_LIST_SUCCESS = GET_COURSE_LIST_FAILURE + 1;
	/**
	 * 添加课程成功
	 */
	public static final int ADD_COURSE_SUCCESS = GET_COURSE_LIST_SUCCESS + 1;
	/**
	 * 添加课程失败
	 */
	public static final int ADD_COURSE_FAILURE = ADD_COURSE_SUCCESS + 1;
	/**
	 * 删除课程成功
	 */
	public static final int DELETE_COURSE_SUCCESS = ADD_COURSE_FAILURE + 1;
	/**
	 * 删除课程失败
	 */
	public static final int DELETE_COURSE_FAILURE = DELETE_COURSE_SUCCESS + 1;
	/**
	 * 打开课程成功
	 */
	public static final int OPEN_COURSE_SUCCESS = DELETE_COURSE_FAILURE + 1;
	/**
	 * 打开课程失败
	 */
	public static final int OPEN_COURSE_FAILURE = OPEN_COURSE_SUCCESS + 1;
	/**
	 * 关闭课程成功
	 */
	public static final int CLOSE_COURSE_SUCCESS = OPEN_COURSE_FAILURE + 1;
	/**
	 * 关闭课程失败
	 */
	public static final int CLOSE_COURSE_FAILURE = CLOSE_COURSE_SUCCESS + 1;
	/**
	 * 登入失败
	 */
	public static final int LOGIN_FAILURE = CLOSE_COURSE_FAILURE + 1;
	/**
	 * 获取课程课件试卷成功
	 */
	public static final int GET_COURSE_DOC_EXERCISE_SUCCESS = LOGIN_FAILURE + 1;
	/**
	 * 添加下载文件成功
	 */
	public static final int ADD_DOWNLOAD_SUCCESS = GET_COURSE_DOC_EXERCISE_SUCCESS + 1;
	/**
	 * 获取计划列表成功
	 */
	public static final int GET_STUDENT_PLAN_LIST_SUCCESS = ADD_DOWNLOAD_SUCCESS + 1;
	/**
	 * 获取计划内容成功
	 */
	public static final int GET_STUDENT_PLAN_CONTENT_SUCCESS = GET_STUDENT_PLAN_LIST_SUCCESS + 1;
	/**
	 * 完成课程成功
	 */
	public static final int FINISH_COURSE_SUCCESS = GET_STUDENT_PLAN_CONTENT_SUCCESS + 1;
	/**
	 * 获取课程介绍成功
	 */
	public static final int GET_COURSE_DESCRI_SUCCESS = FINISH_COURSE_SUCCESS + 1;
	/**
	 * 获取在学专题成功
	 */
	public static final int GET_SPECIAL_TOPICING_SUCCESS = GET_COURSE_DESCRI_SUCCESS + 1;
	/**
	 * 获取专题课程列表成功
	 */
	public static final int GET_TOPIC_LIST_SUCCESS = GET_SPECIAL_TOPICING_SUCCESS + 1;
	/**
	 * 获得完成课程的题目成功
	 */
	public static final int GET_FINISH_COURSE_LIST_SUCCESS = GET_TOPIC_LIST_SUCCESS + 1;
	/**
	 * 获得答案成功
	 */
	public static final int GET_FINISH_DA_LIST_SUCCESS = GET_FINISH_COURSE_LIST_SUCCESS + 1;
	/**
	 * 提交答案时用到的数据
	 */
	public static final int GET_FINISH_DATA_SUCCESS = GET_FINISH_DA_LIST_SUCCESS + 1;
	/**
	 * 完成课程成功
	 */
	public static final int FINISH_SUCCESS = GET_FINISH_DATA_SUCCESS + 1;
	/**
	 * 需要重新登入
	 */
	public static final int NEED_LOGIN_AGAIN = FINISH_SUCCESS + 1;
	/**
	 * 不需要做任何事
	 */
	public static final int NO_NEED_LOGIN_AGAIN = NEED_LOGIN_AGAIN + 1;
	/**
	 * 上传时间成功
	 */
	public static final int UPDATE_TIEM_SUCCESS = NO_NEED_LOGIN_AGAIN + 1;
	/**
	 * 上传时间失败
	 */
	public static final int UPDATE_TIME_FAILURE = UPDATE_TIEM_SUCCESS + 1;
	/**
	 * 获取马鞍山发展论坛成功
	 */
	public static final int GET_BBS_TOTAL_LIST_SUCCESS = UPDATE_TIME_FAILURE + 1;
	/**
	 * 获取马鞍山发展论坛二级页面成功
	 */
	public static final int GET_BBS_SECOND_TOTAL_LIST_SUCCESS = GET_BBS_TOTAL_LIST_SUCCESS + 1;
	/**
	 * 获取原帖子
	 */
	public static final int GET_BBS_LIST_DETAIL_ORIG_SUCCESS = GET_BBS_SECOND_TOTAL_LIST_SUCCESS + 1;
	/**
	 * 获取回复成功
	 */
	public static final int GET_BBS_LIST_REPLY_SUCCESS = GET_BBS_LIST_DETAIL_ORIG_SUCCESS + 1;
	/**
	 * 添加回复成功
	 */
	public static final int ADD_REPLY_SUCCESS = GET_BBS_LIST_REPLY_SUCCESS + 1;
	/**
	 * 添加回复失败
	 */
	public static final int ADD_REPLY_FAILURE = ADD_REPLY_SUCCESS + 1;
	/**
	 * 添加新帖子成功
	 */
	public static final int ADD_NEW_BBS_SUCCESS = ADD_REPLY_FAILURE + 1;
	/**
	 * 添加新帖子失败
	 */
	public static final int ADD_NEW_BBS_FAILURE = ADD_NEW_BBS_SUCCESS + 1;
	/**
	 * 从课程界面获得BoardId成功
	 */
	public static final int GET_BBS_SECOND_TOTAL_LIST_FROM_COURSE_SUCCESS = ADD_NEW_BBS_FAILURE+1;
}
