ALTER table order_item add supplier_order_status varchar(2);
ALTER table order_item MODIFY supplier_order_status varchar(2) NULL DEFAULT NULL COMMENT '1-待发送供应商,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';
ALTER table order_item add old_supplier_order_status varchar(2) NULL  COMMENT '老供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';


ALTER table shop_order add supplier_order_status varchar(2);
ALTER table shop_order MODIFY supplier_order_status varchar(2) NULL DEFAULT NULL COMMENT '1-待发货,2-部分发货,3-全部发货,4-已取消';

ALTER table warehouse_order add pay_time timestamp;
ALTER table warehouse_order MODIFY pay_time timestamp NULL DEFAULT NULL COMMENT '支付时间,格式yyyy-mm-dd hh:mi:ss';

ALTER table warehouse_order add is_cancel varchar(2) NULL DEFAULT '0' COMMENT '是否取消：0-否,1-是';
ALTER table warehouse_order add old_supplier_order_status varchar(2) NULL  COMMENT '老供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';


ALTER table warehouse_order MODIFY supplier_order_status varchar(2)  NULL DEFAULT NULL COMMENT '供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';

ALTER table supplier_order_info MODIFY supplier_order_status varchar(2)  NULL DEFAULT NULL COMMENT '供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';
ALTER table supplier_order_info add old_supplier_order_status varchar(2) NULL  COMMENT '老供应商订单状态:1-待发送供应商,2-供应商下单异常,3-等待供应商发货,4-全部发货,5-供应商下单失败,6-部分发货,7-已取消';

alter table external_item_sku add `notify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近同步时间';

alter table external_item_sku add `min_buy_count` int(11) DEFAULT NULL COMMENT '最小购买数量';


DELETE FROM dict WHERE type_code = 'supplierOrderStatus'
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (436, 'supplierOrderStatus', '待发送供应商', '1', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:38:19', '2017-10-10 11:38:19');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (437, 'supplierOrderStatus', '供应商下单异常', '2', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:38:31', '2017-10-10 11:39:00');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (438, 'supplierOrderStatus', '等待供应商发货', '3', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:38:49', '2017-10-10 11:38:49');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (439, 'supplierOrderStatus', '全部发货', '4', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:39:13', '2017-10-10 11:39:13');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (440, 'supplierOrderStatus', '供应商下单失败', '5', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:39:25', '2017-10-10 11:39:25');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (441, 'supplierOrderStatus', '部分发货', '6', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:39:38', '2017-10-10 11:39:38');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (442, 'supplierOrderStatus', '已取消', '7', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-10 11:39:50', '2017-10-10 11:39:50');


ALTER  TABLE  `items`
ADD  COLUMN  `main_picture`    text  NULL  AFTER  `remark`;

ALTER  TABLE  `items`
MODIFY  COLUMN  `main_picture`    text  CHARACTER  SET  utf8  COLLATE  utf8_general_ci  NULL  COMMENT  'spu主图路径，多张以逗号分隔'  AFTER  `remark`;

ALTER  TABLE  `skus`
ADD  COLUMN  `sku_name`    varchar(256)  NULL  AFTER  `sku_code`;