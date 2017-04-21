/**   
* @Title: Pagination.java 
* @Package com.hoo.form 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年4月15日 下午9:23:47 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.util;

import javax.ws.rs.QueryParam;
import java.util.List;

/** 
 * @ClassName: Pagination 
 * @Description: TODO
 * @author 吴东雄
 * @date 2015年4月15日 下午9:23:47 
 *  
 */
public class Pagination<T> {

	/**
	 * 分页起始记录数
	 */
	@QueryParam("start")
	private int start;
	/**
	 * 分页每页记录数
	 */
	@QueryParam("limit")
	private int limit;
	/**
	 * 当前页数
	 */
	@QueryParam("pageIndex")
	private int pageIndex;
	/**
	 * 排序字段
	 */
	@QueryParam("field")
	private String field;
	/**
	 * 排序方向:ASC-升序，DESC-倒序
	 */
	@QueryParam("direction")
	private String direction = "ASC";
	/**
	 * 是否有效
	 */
	@QueryParam("isValid")
	private String isValid;


	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}


}
