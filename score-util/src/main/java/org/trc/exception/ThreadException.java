/**   
* @Title: ThreadException.java 
* @Package com.hoo.exception 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年11月19日 下午3:30:47 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.exception;

/** 
 * @ClassName: ThreadException 
 * @Description: TODO
 * @author 吴东雄
 * @date 2015年11月19日 下午3:30:47 
 *  
 */
public class ThreadException  extends RuntimeException{

	public ThreadException(String msg) {
        super(msg);
    }
 
    public ThreadException(String msg, Throwable cause) {
        super(msg, cause);
    }
	
}
