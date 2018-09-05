package org.trc.enums;

import java.util.Objects;

public class AfterSaleOrderEnum {

	//售后单状态
	public enum AfterSaleOrderStatusEnum {
		
		STATUS_0(0,"待客户发货"),
		STATUS_1(1,"客户已经发货"),
		STATUS_3(2,"已经完成"),
		STATUS_4(3,"已经取消");

		private Integer code;
		private String name;

		AfterSaleOrderStatusEnum(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public static AfterSaleOrderStatusEnum getAfterSaleOrderStatusEnumByCode(Integer code) {
			for (AfterSaleOrderStatusEnum validEnum : AfterSaleOrderStatusEnum.values()) {
				if (Objects.equals(validEnum.getCode(),code)) {
					return validEnum;
				}
			}
			return null;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
		
	}
	
	//售后单详情
	public enum AfterSaleOrderDetailTypeEnum {
		
		STATUS_0(0,"自采商品"),
		STATUS_1(1,"代发商品");

		private int code;
		private String name;

		AfterSaleOrderDetailTypeEnum(int code, String name) {
			this.code = code;
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
		
	}
	
	//退货入库单状态
		public enum AfterSaleWarehouseNoticeStatusEnum {
			
			STATUS_0(0,"未到货"),
			STATUS_1(1,"已到货待理货 "),
			STATUS_2(2,"入库完成"),
			STATUS_3(3,"已取消");

			private int code;
			private String name;  

			AfterSaleWarehouseNoticeStatusEnum(int code, String name) {
				this.code = code;
				this.name = name;
			}
			
			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public int getCode() {
				return code;
			}

			public void setCode(int code) {
				this.code = code;
			}
			
		}
		
		//售后类型(0取消发货,1退货,手动新建只对应一种，即“退货)
				public enum AfterSaleTypeEnum {
					
					STATUS_0(0,"取消发货"),
					STATUS_1(1,"退货 ")
					;

					private int code;
					private String name;  

					AfterSaleTypeEnum(int code, String name) {
						this.code = code;
						this.name = name;
					}
					
					public String getName() {
						return name;
					}

					public void setName(String name) {
						this.name = name;
					}

					public int getCode() {
						return code;
					}

					public void setCode(int code) {
						this.code = code;
					}
					
				}
				
				//发起类型(0系统发起,1手动新建)
				public enum launchTypeEnum {
					
					STATUS_0(0,"系统发起"),
					STATUS_1(1,"手动新建");

					private int code;
					private String name;  

					launchTypeEnum(int code, String name) {
						this.code = code;
						this.name = name;
					}
					
					public String getName() {
						return name;
					}

					public void setName(String name) {
						this.name = name;
					}

					public int getCode() {
						return code;
					}

					public void setCode(int code) {
						this.code = code;
					}
					
				}
}
