package org.trc.domain.config;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class WarehouseNoticeCallback implements Serializable {
	
	private static final long serialVersionUID = 4631682924038894162L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	/**
	 * 请求编号，幂等去重
	 */
	private String requestCode;
	/**
	 * 仓库编号
	 */
	private String warehouseCode;
	/**
	 * 入库单编号
	 */
	private String warehouseNoticeCode;
	/**
	 * 请求参数，json格式
	 */
	private String requestParams;
	/**
	 * 状态:1-初始状态,2-处理成功,3-处理失败
	 */
	private Integer state;
	/**
	 * 请求时间
	 */
	private Date requestTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRequestCode() {
		return requestCode;
	}
	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}
	public String getWarehouseCode() {
		return warehouseCode;
	}
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
	public String getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Date getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getWarehouseNoticeCode() {
		return warehouseNoticeCode;
	}
	public void setWarehouseNoticeCode(String warehouseNoticeCode) {
		this.warehouseNoticeCode = warehouseNoticeCode;
	}
	
}
