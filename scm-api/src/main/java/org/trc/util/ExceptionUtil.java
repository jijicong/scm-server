package org.trc.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.*;

/** 
 * @ClassName: ExceptionUtil 
 * @Description:
 * @author 吴东雄
 * @date 2016年1月16日 上午12:53:42 
 *  
 */
public class ExceptionUtil {
	private static Logger log = LoggerFactory.getLogger(ExceptionUtil.class);
	/**
	 *
	* @Title: handlerException 
	* @Description: 异常结果处理
	* @param @param appResult 返回结果实例
	* @param @param e 异常对象
	* @param @param invokingMethodName 当前调用方法
	* @return void
	* @throws
	 */
	public static String handlerException(Exception e, Class<?> targetClass, String invokingMethodName){
		StringBuilder builder = new StringBuilder();
		ExceptionEnum exceptionEnum = null;
		CommonExceptionEnum commonExceptionEnum = null;
		String excepMsg = "";
		String errorDtl = "";
		String excepCode = "";
		try {
			Object exceptionObj = getException(e);
			if(exceptionObj instanceof CommonExceptionEnum){
				commonExceptionEnum = (CommonExceptionEnum)exceptionObj;
			}else if(exceptionObj instanceof ExceptionEnum){
				exceptionEnum = (ExceptionEnum)exceptionObj;
			}
		} catch (Exception e2) {
			excepMsg = "系统未捕获未知异常";
			errorDtl = e2.getMessage();
		}
		if(null != exceptionEnum){
			excepCode = exceptionEnum.getCode();
			excepMsg = exceptionEnum.getMessage();
			errorDtl = e.getMessage();
		}else if(null != commonExceptionEnum){
			excepCode = commonExceptionEnum.getCode();
			excepMsg = commonExceptionEnum.getMessage();
			errorDtl = e.getMessage();
		}
		builder = new StringBuilder();
		builder.append("外部系统调用");
		builder.append(targetClass.getName());
		builder.append("方法");
		builder.append(invokingMethodName);
		builder.append("发生");
		builder.append(excepMsg);
		builder.append(". 异常代码[").append(excepCode);
		builder.append("],错误明细信息：");
		builder.append(errorDtl);
		log.error(builder.toString());
		StringBuilder builder2 = new StringBuilder();
		builder2.append(excepMsg).append(",异常代码[").append(excepCode).append("],异常明细:").append(errorDtl);
		return builder2.toString();
	}

	/**
	 * 获取异常对象
	 * @param e
	 * @return
	 */
	public static Object getException(Exception e){
		ExceptionEnum exceptionEnum = null;
		CommonExceptionEnum commonExceptionEnum = null;
		String exceptionName = e.getClass().getSimpleName();
		if (StringUtils.equals(exceptionName, ParamValidException.class.getSimpleName())) {
			ParamValidException paramValidException = (ParamValidException)e;
			commonExceptionEnum = paramValidException.getExceptionEnum();
		} else if (StringUtils.equals(exceptionName, ConfigException.class.getSimpleName())) {
			ConfigException configException = (ConfigException)e;
			exceptionEnum = configException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, FileException.class.getSimpleName())) {
			FileException fileException = (FileException)e;
			exceptionEnum = fileException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, CategoryException.class.getSimpleName())) {
			CategoryException categoryException = (CategoryException)e;
			exceptionEnum = categoryException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, ChannelException.class.getSimpleName())) {
			ChannelException channelException = (ChannelException)e;
			exceptionEnum = channelException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, GoodsException.class.getSimpleName())) {
			GoodsException goodsException = (GoodsException)e;
			exceptionEnum = goodsException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, PurchaseGroupException.class.getSimpleName())) {
			PurchaseGroupException purchaseGroupException = (PurchaseGroupException)e;
			exceptionEnum = purchaseGroupException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, RoleException.class.getSimpleName())) {
			RoleException roleException = (RoleException)e;
			exceptionEnum = roleException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, SupplierException.class.getSimpleName())) {
			SupplierException supplierException = (SupplierException)e;
			exceptionEnum = supplierException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, UserAccreditInfoException.class.getSimpleName())) {
			UserAccreditInfoException userAccreditInfoException = (UserAccreditInfoException)e;
			exceptionEnum = userAccreditInfoException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, WarehouseException.class.getSimpleName())) {
			WarehouseException warehouseException = (WarehouseException)e;
			exceptionEnum = warehouseException.getExceptionEnum();
		}else if (StringUtils.equals(exceptionName, DuplicateKeyException.class.getSimpleName())) {
			exceptionEnum = ExceptionEnum.DATABASE_DUPLICATE_KEY_EXCEPTION;
		}else if (StringUtils.equals(exceptionName, PermissionDeniedDataAccessException.class.getSimpleName())) {
			exceptionEnum = ExceptionEnum.DATABASE_PERMISSION_DENIED_EXCEPTION;
		}else if (StringUtils.equals(exceptionName, QueryTimeoutException.class.getSimpleName())) {
			exceptionEnum = ExceptionEnum.DATABASE_QUERY_TIME_OUT_EXCEPTION;
		}else if (StringUtils.equals(exceptionName, DeadlockLoserDataAccessException.class.getSimpleName())) {
			exceptionEnum = ExceptionEnum.DATABASE_DEADLOCK_DATA_ACESS_EXCEPTION;
		}else if(StringUtils.equals(exceptionName, JurisdictionException.class.getSimpleName())){
			JurisdictionException jurisdictionException= (JurisdictionException) e;
			exceptionEnum=jurisdictionException.getExceptionEnum();
		}else if(StringUtils.equals(exceptionName, NullPointerException.class.getSimpleName())){
			exceptionEnum = ExceptionEnum.SYSTEM_BUSY;
		}else{
			exceptionEnum = ExceptionEnum.SYSTEM_EXCEPTION;
		}
		if(null != exceptionEnum)
			return exceptionEnum;
		if(null != commonExceptionEnum)
			return commonExceptionEnum;
		return null;
	}

	/**
	 * 获取异常错误码
	 * @param e
	 * @return
	 */
	public static String getErrorInfo(Exception e){
		Object exceptionObj = getException(e);
		String code = null;
		if(exceptionObj instanceof CommonExceptionEnum){
			CommonExceptionEnum commonExceptionEnum = (CommonExceptionEnum)exceptionObj;
			code = commonExceptionEnum.getCode();
		}else if(exceptionObj instanceof ExceptionEnum){
			ExceptionEnum exceptionEnum = (ExceptionEnum)exceptionObj;
			code = exceptionEnum.getCode();
		}
		return code;
	}
	
}
