ALTER table order_item add supplier_order_status varchar(2);
ALTER table order_item MODIFY supplier_order_status varchar(2) NULL DEFAULT NULL COMMENT '1-待发送供应商,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';

ALTER table shop_order add supplier_order_status varchar(2);
ALTER table shop_order MODIFY supplier_order_status varchar(2) NULL DEFAULT NULL COMMENT '1-待发货,2-部分发货,3-全部发货,4-已取消';

ALTER table warehouse_order add pay_time timestamp;
ALTER table warehouse_order MODIFY pay_time timestamp NULL DEFAULT NULL COMMENT '支付时间,格式yyyy-mm-dd hh:mi:ss';

ALTER table warehouse_order MODIFY supplier_order_status varchar(2)  NULL DEFAULT NULL COMMENT '供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';

ALTER table supplier_order_info MODIFY supplier_order_status varchar(2)  NULL DEFAULT NULL COMMENT '供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';

alter table external_item_sku add `notify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近同步时间';

alter table external_item_sku add `min_buy_count` int(11) DEFAULT NULL COMMENT '最小购买数量';


