/**   
* @Title: ParamException.java 
* @Package com.hoo.exception 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年11月18日 下午12:35:07 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.exception;

/** 
 * @ClassName: ParamException 
 * @Description: TODO
 * @author 吴东雄
 * @date 2015年11月18日 下午12:35:07 
 *  
 */
public class ParamValidException extends RuntimeException{

	public ParamValidException(String msg) {
        super(msg);
    }
 
    public ParamValidException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
