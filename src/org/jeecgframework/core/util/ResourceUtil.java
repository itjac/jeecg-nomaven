package org.jeecgframework.core.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jeecgframework.core.constant.DataBaseConstant;
import org.jeecgframework.web.cgform.common.CgAutoListConstant;
import org.jeecgframework.web.system.manager.ClientManager;
import org.jeecgframework.web.system.pojo.base.Client;
import org.jeecgframework.web.system.pojo.base.DynamicDataSourceEntity;
import org.jeecgframework.web.system.pojo.base.TSIcon;
import org.jeecgframework.web.system.pojo.base.TSRoleFunction;
import org.jeecgframework.web.system.pojo.base.TSType;
import org.jeecgframework.web.system.pojo.base.TSTypegroup;
import org.jeecgframework.web.system.pojo.base.TSUser;


/**
 * 项目参数工具类
 * 
 */
public class ResourceUtil {
	public static final String LOCAL_CLINET_USER = "LOCAL_CLINET_USER";
	/**
	 * 缓存字段分组【缓存】
	 */
	public static Map<String, TSTypegroup> allTypeGroups = new HashMap<String,TSTypegroup>();
	/**
	 * 缓存字典【缓存】
	 */
	public static Map<String, List<TSType>> allTypes = new HashMap<String,List<TSType>>();
	
	/**
	 * 国际化【缓存】
	 */
	public static Map<String, String> mutiLangMap = new HashMap<String, String>(); 
	/**
	 * 缓存系统图标【缓存】
	 */
	public static Map<String, TSIcon> allTSIcons = new HashMap<String,TSIcon>();
	/**
	 * 动态数据源参数配置【缓存】
	 */
	public static Map<String, DynamicDataSourceEntity> dynamicDataSourceMap = new HashMap<String, DynamicDataSourceEntity>(); 
	
	private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("sysConfig");
	
	/**
	 * 属性文件[resources/sysConfig.properties]
	 * #默认开启模糊查询方式 1为开启 条件无需带*就能模糊查询[暂时取消]
	 * fuzzySearch=0
	 */
	//update-begin--Author:zzl  Date:20151123 for：加入配置属性可默认进行模糊查询
//	public final static boolean fuzzySearch= ResourceUtil.isFuzzySearch();
	//update-end--Author:zzl  Date:20151123 for：加入配置属性可默认进行模糊查询

	/**
	 * 获取session定义名称
	 * 
	 * @return
	 */
	public static final String getSessionattachmenttitle(String sessionName) {
		return bundle.getString(sessionName);
	}
	public static final TSUser getSessionUser() {
		HttpSession session = ContextHolderUtils.getSession();
		if(ClientManager.getInstance().getClient(session.getId())!=null){
			return ClientManager.getInstance().getClient(session.getId()).getUser();
		//update-begin--update---author:scott-----------date:20151218-------for:解决分布式登录问题-------
		}else{
			TSUser u = (TSUser) session.getAttribute(ResourceUtil.LOCAL_CLINET_USER);
			Client client = new Client();
	        client.setIp("");
	        client.setLogindatetime(new Date());
	        client.setUser(u);
	        ClientManager.getInstance().addClinet(session.getId(), client);
		}
		//update-end--update---author:scott-----------date:20151218-------for:解决分布式登录问题-------
		return null;
	}
	@Deprecated
	public static final List<TSRoleFunction> getSessionTSRoleFunction(String roleId) {
		HttpSession session = ContextHolderUtils.getSession();
		if (session.getAttributeNames().hasMoreElements()) {
			List<TSRoleFunction> TSRoleFunctionList = (List<TSRoleFunction>)session.getAttribute(roleId);
			if (TSRoleFunctionList != null) {
				return TSRoleFunctionList;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * 获得请求路径【注意： 不通用】
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request) {
//	    update-begin--Author:zhoujf  Date:20170331 for：BUG请求中没有参数时 会给加上?null
//		String requestPath = request.getRequestURI() + "?" + request.getQueryString();
		String queryString = request.getQueryString();
		String requestPath = request.getRequestURI();
		if(StringUtils.isNotEmpty(queryString)){
			requestPath += "?" + queryString;
		}
//	    update-begin--Author:zhoujf  Date:20170331 for：BUG请求中没有参数时 会给加上?null
		if (requestPath.indexOf("&") > -1) {// 去掉其他参数(保留一个参数) 例如：loginController.do?login
			requestPath = requestPath.substring(0, requestPath.indexOf("&"));
		}
		//update-begin--Author:scott  Date:20170906 for：TASK #2298 【权限请求带参问题】权限拦截，请求带参问题
		if(requestPath.indexOf("=")!=-1){
			//update-begin--Author:scott  Date:20171108 for：restful请求报错--------
			if(requestPath.indexOf(".do")!=-1){
				requestPath = requestPath.substring(0,requestPath.indexOf(".do")+3);
			}else{
				requestPath = requestPath.substring(0,requestPath.indexOf("?"));
			}
			//update-end--Author:scott  Date:20171108 for：restful请求报错--------
		}
		//update-end--Author:scott  Date:20170906 for：TASK #2298 【权限请求带参问题】权限拦截，请求带参问题
		requestPath = requestPath.substring(request.getContextPath().length() + 1);// 去掉项目路径
		return requestPath;
	}

	/**
	 * 获取配置文件参数
	 * 
	 * @param name
	 * @return
	 */
	public static final String getConfigByName(String name) {
		return bundle.getString(name);
	}

	/**
	 * 获取配置文件参数
	 * 
	 * @param name
	 * @return
	 */
	public static final Map<Object, Object> getConfigMap(String path) {
		ResourceBundle bundle = ResourceBundle.getBundle(path);
		Set set = bundle.keySet();
		return oConvertUtils.SetToMap(set);
	}

	
	
	public static String getSysPath() {
		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
		String temp = path.replaceFirst("file:/", "").replaceFirst("WEB-INF/classes/", "");
		String separator = System.getProperty("file.separator");
		String resultPath = temp.replaceAll("/", separator + separator).replaceAll("%20", " ");
		return resultPath;
	}

	/**
	 * 获取项目根目录
	 * 
	 * @return
	 */
	public static String getPorjectPath() {
		String nowpath; // 当前tomcat的bin目录的路径 如
						// D:\java\software\apache-tomcat-6.0.14\bin
		String tempdir;
		nowpath = System.getProperty("user.dir");
		tempdir = nowpath.replace("bin", "webapps"); // 把bin 文件夹变到 webapps文件里面
		tempdir += "\\"; // 拼成D:\java\software\apache-tomcat-6.0.14\webapps\sz_pro
		return tempdir;
	}

	public static String getClassPath() {
		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
		String temp = path.replaceFirst("file:/", "");
		String separator = System.getProperty("file.separator");
		String resultPath = temp.replaceAll("/", separator + separator);
		return resultPath;
	}

	public static String getSystempPath() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String getSeparator() {
		return System.getProperty("file.separator");
	}

	public static String getParameter(String field) {
		HttpServletRequest request = ContextHolderUtils.getRequest();
		return request.getParameter(field);
	}

	/**
	 * 获取数据库类型
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static final String getJdbcUrl() {
		return DBTypeUtil.getDBType().toLowerCase();
	}

//    update-begin--Author:zhangguoming  Date:20140226 for：添加验证码
    /**
     * 获取随机码的长度
     *
     * @return 随机码的长度
     */
    public static String getRandCodeLength() {
        return bundle.getString("randCodeLength");
    }

    /**
     * 获取随机码的类型
     *
     * @return 随机码的类型
     */
    public static String getRandCodeType() {
        return bundle.getString("randCodeType");
    }
//    update-end--Author:zhangguoming  Date:20140226 for：添加验证码

    /**
     * 获取组织机构编码长度的类型
     *
     * @return 组织机构编码长度的类型
     */
    public static String getOrgCodeLengthType() {
        return bundle.getString("orgCodeLengthType");
    }
    /**
     * 获取用户系统变量
     * @param key
     * 			DataBaseConstant 中的值
     * @return
     */
	public static String getUserSystemData(String key) {
		//#{sys_user_code}%
		String moshi = "";
		if(key.indexOf("}")!=-1){
			 moshi = key.substring(key.indexOf("}")+1);
		}
		String returnValue = null;
		//针对特殊标示处理#{sysOrgCode}，判断替换
		if (key.contains("#{")) {
			key = key.substring(2,key.indexOf("}"));
		} else {
			key = key;
		}
		
		 //----------------------------------------------------------------
		//update-begin--Author:zhangdaihao  Date:20140913 for：获取系统上下文变量
	
		//替换为系统的登录用户账号
//		if (key.equals(DataBaseConstant.CREATE_BY)
//				|| key.equals(DataBaseConstant.CREATE_BY_TABLE)
//				|| key.equals(DataBaseConstant.UPDATE_BY)
//				|| key.equals(DataBaseConstant.UPDATE_BY_TABLE)
//				|| 
		if (key.equals(DataBaseConstant.SYS_USER_CODE)
				|| key.equals(DataBaseConstant.SYS_USER_CODE_TABLE)) {
			returnValue = getSessionUser().getUserName();
		}
		//替换为系统登录用户真实名字
//		if (key.equals(DataBaseConstant.CREATE_NAME)
//				|| key.equals(DataBaseConstant.CREATE_NAME_TABLE)
//				|| key.equals(DataBaseConstant.UPDATE_NAME_TABLE)
//				|| key.equals(DataBaseConstant.UPDATE_NAME)
		if (key.equals(DataBaseConstant.SYS_USER_NAME)
				|| key.equals(DataBaseConstant.SYS_USER_NAME_TABLE)
			) {
			returnValue =  getSessionUser().getRealName();
		}
		
		//update-end--Author:zhangdaihao  Date:20140913 for：获取系统上下文变量
		//---------------------------------------------------------------- 
		//替换为系统登录用户的公司编码
		if (key.equals(DataBaseConstant.SYS_COMPANY_CODE)|| key.equals(DataBaseConstant.SYS_COMPANY_CODE_TABLE)) {
			//update-begin--author:zhangjiaqiang Date:20170112 for:业务数据当中的公司编码错误
			returnValue = getSessionUser().getCurrentDepart().getOrgCode()
					.substring(0, Integer.valueOf(getOrgCodeLengthType()) + 1);
			//update-end--author:zhangjiaqiang Date:20170112 for:业务数据当中的公司编码错误
		}
		//替换为系统用户登录所使用的机构编码
		if (key.equals(DataBaseConstant.SYS_ORG_CODE)|| key.equals(DataBaseConstant.SYS_ORG_CODE_TABLE)) {
			returnValue = getSessionUser().getCurrentDepart().getOrgCode();
		}
		//替换为当前系统时间(年月日)
		if (key.equals(DataBaseConstant.SYS_DATE)|| key.equals(DataBaseConstant.SYS_DATE_TABLE)) {
			returnValue = DateUtils.formatDate();
		}
		//替换为当前系统时间（年月日时分秒）
		if (key.equals(DataBaseConstant.SYS_TIME)|| key.equals(DataBaseConstant.SYS_TIME_TABLE)) {
			returnValue = DateUtils.formatTime();
		}
		//流程状态默认值（默认未发起）
		if (key.equals(DataBaseConstant.BPM_STATUS_TABLE)|| key.equals(DataBaseConstant.BPM_STATUS_TABLE)) {
			returnValue = "1";
		}
		if(returnValue!=null){returnValue = returnValue + moshi;}
		return returnValue;
	}
	//---author:jg_xugj----start-----date:20151226--------for：#814 【数据权限】扩展支持写表达式，通过session取值
    /**
     * 获取用户session 中的变量
     * @param key
     * 			Session 中的值
     * @return
     */
	private static String getSessionData(String key) {
		//${myVar}%
		//得到${} 后面的值
		String moshi = "";
		if(key.indexOf("}")!=-1){
			 moshi = key.substring(key.indexOf("}")+1);
		}
		String returnValue = null;
//---author:jg_xugj----start-----date:20151226--------for：修改bug 1、key.contains("${")  应改为 key.contains("#{") 2、StringUtil.isEmpty(key) 判断 不为空
		if (key.contains("#{")) {
			key = key.substring(2,key.indexOf("}"));
		}
		//从session中取得值
		if (!StringUtil.isEmpty(key)) {
			HttpSession session = ContextHolderUtils.getSession();
			returnValue = (String) session.getAttribute(key);
		}
//---author:jg_xugj----end-----date:20151226--------for：修改bug 1、key.contains("${")  应改为 key.contains("#{") 2、StringUtil.isEmpty(key) 判断 不为空

		//结果加上${} 后面的值
		if(returnValue!=null){returnValue = returnValue + moshi;}
		return returnValue;
	}
	
    /**
     * 处理数据权限规则变量
     * 以用户变量为准  先得到用户变量，如果用户没有设置，则获到 系统变量
     * @param key
     * 			Session 中的值
     * @return
     */
	public static String converRuleValue(String ruleValue) {
		String value = ResourceUtil.getSessionData(ruleValue);
		if(StringUtil.isEmpty(value))
			value = ResourceUtil.getUserSystemData(ruleValue);
		return value!= null ? value : ruleValue;
	}
	//---author:jg_xugj----end-----date:20151226--------for：#814 【数据权限】扩展支持写表达式，通过session取值

	public static void main(String[] args) {
		org.jeecgframework.core.util.LogUtil.info(getPorjectPath());
		org.jeecgframework.core.util.LogUtil.info(getSysPath());
	}
	//update-begin--Author:zzl  Date:20151123 for：加入配置属性可默认进行模糊查询
//	public static boolean isFuzzySearch(){
//		return "1".equals(bundle.getString("fuzzySearch"));
//	}
	//update-end--Author:zzl  Date:20151123 for：加入配置属性可默认进行模糊查询
	
	//update--begin--author:zhoujf date:20180413 for:TASK #2623 【bug】生成代码sql 不支持表达式--
	/**
	 * 【Minidao写法】
	 * 将Sql增强中的系统变量替换掉
	 * @param sql
	 * @return
	 */
	public static Map<String, Object> minidaoReplaceExtendSqlSysVar(Map<String, Object> data){
		data.put("sys."+DataBaseConstant.SYS_USER_CODE_TABLE, getUserSystemData(DataBaseConstant.SYS_USER_CODE));
		data.put("sys."+DataBaseConstant.SYS_USER_NAME_TABLE, getUserSystemData(DataBaseConstant.SYS_USER_NAME));
		data.put("sys."+DataBaseConstant.SYS_ORG_CODE_TABLE, getUserSystemData(DataBaseConstant.SYS_ORG_CODE));
		data.put("sys."+DataBaseConstant.SYS_COMPANY_CODE_TABLE, getUserSystemData(DataBaseConstant.SYS_COMPANY_CODE));
		data.put("sys."+DataBaseConstant.SYS_DATE_TABLE, DateUtils.formatDate());
		data.put("sys."+DataBaseConstant.SYS_TIME_TABLE, DateUtils.formatTime());
		return data;
	}
	
	/**
	 * sql值替换
	 * @param sql
	 * @param params
	 * @return
	 */
	public static String formateSQl(String sql, Map<String, Object> params) {
		sql = replaceExtendSqlSysVar(sql);
		if (params == null) {
			return sql;
		}
		if(sql.toLowerCase().indexOf(CgAutoListConstant.SQL_INSERT)!=-1){
			sql = sql.replace("#{UUID}", UUIDGenerator.generate());
		}
		for (String key : params.keySet()) {
			sql = sql.replace("#{" + key + "}",String.valueOf(params.get(key)));
		}
		return sql;
	}
	
	/**
	 * 【老写法】
	 * 将Sql增强中的系统变量替换掉
	 * @param sql
	 * @return
	 */
	private static String replaceExtendSqlSysVar(String sql){
		sql = sql.replace("#{sys."+DataBaseConstant.SYS_USER_CODE_TABLE+"}", ResourceUtil.getUserSystemData(DataBaseConstant.SYS_USER_CODE))
				.replace("#{sys."+DataBaseConstant.SYS_USER_NAME_TABLE+"}", ResourceUtil.getUserSystemData(DataBaseConstant.SYS_USER_NAME))
				.replace("#{sys."+DataBaseConstant.SYS_ORG_CODE_TABLE+"}", ResourceUtil.getUserSystemData(DataBaseConstant.SYS_ORG_CODE))
				.replace("#{sys."+DataBaseConstant.SYS_COMPANY_CODE_TABLE+"}", ResourceUtil.getUserSystemData(DataBaseConstant.SYS_COMPANY_CODE))
				.replace("#{sys."+DataBaseConstant.SYS_DATE_TABLE+"}",  DateUtils.formatDate())
				.replace("#{sys."+DataBaseConstant.SYS_TIME_TABLE+"}",  DateUtils.formatTime());
		return sql;
	}
	//update--end--author:zhoujf date:20180413 for:TASK #2623 【bug】生成代码sql 不支持表达式--
}
