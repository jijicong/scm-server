/**   
* @Title: Pagination.java 
* @Package com.hoo.form 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年4月15日 下午9:23:47 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.form;

import javax.ws.rs.BeanParam;
import javax.ws.rs.PathParam;
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
	 * 总记录条数
	 */
	private int count;
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
	 * 记录集
	 */
	private List<T> datas;

	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

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

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}


}
