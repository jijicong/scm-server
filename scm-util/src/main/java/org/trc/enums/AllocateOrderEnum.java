package org.trc.enums;

public class AllocateOrderEnum {
	
	/**
	 * @author admin
	 * 调拨单状态
	 */
	public enum AllocateOrderStatusEnum {
		
		INIT("0","暂存"),
		AUDIT("1","提交审核"),
		PASS("2","审核通过"),
		REJECT("3","审核驳回"),
		WAREHOUSE_NOTICE("4","通知仓库"),
		DROP("5","作废");

		private String code;
		private String name;

		AllocateOrderStatusEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	/**
	 * @author admin
	 * 调拨单出入库状态
	 */
	public enum AllocateOrderInOutStatusEnum {
		
		INIT("0","初始"),
		WAIT("1","等待出入库"),
		OUT_NORMAL("2","出库完成"),
		OUT_EXCEPTION("3","出库异常"),
		IN_NORMAL("4","入库完成"),
		IN_EXCEPTION("5","入库异常");
		
		private String code;
		private String name;
		
		AllocateOrderInOutStatusEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	/**
	 * @author admin
	 * 调拨单商品入库状态
	 */
	public enum AllocateOrderSkuInStatusEnum {
		
		INIT("0","初始"),
		WAIT_IN("1","等待入库"),
		IN_NORMAL("2","入库完成"),
		IN_EXCEPTION("3","入库异常");
		
		private String code;
		private String name;
		
		AllocateOrderSkuInStatusEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	/**
	 * @author admin
	 * 调拨单商品出库状态
	 */
	public enum AllocateOrderSkuOutStatusEnum {
		
		INIT("0","初始"),
		WAIT_OUT("1","等待出库"),
		OUT_NORMAL("2","出库完成"),
		OUT_EXCEPTION("3","出库异常");

		private String code;
		private String name;
		
		AllocateOrderSkuOutStatusEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
		
	}
	
	/**
	 * @author admin
	 * 调拨单出库单状态
	 */
	public enum AllocateOutOrderStatusEnum {
		
		WAIT_NOTICE("0","待通知出库"),
		OUT_RECEIVE_SUCC("1","出库仓接收成功"),
		OUT_RECEIVE_FAIL("2","出库仓接收失败"),
		OUT_SUCCESS("3","出库完成"),
		OUT_EXCEPTION("4","出库异常"),
		CANCEL("5","已取消");
		
		private String code;
		private String name;
		
		AllocateOutOrderStatusEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public void setCode(String code) {
			this.code = code;
		}
		
	}
}

