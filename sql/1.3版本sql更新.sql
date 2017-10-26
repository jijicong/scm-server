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




DELETE FROM dict WHERE type_code = 'supplierOrderStatus';
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


INSERT INTO `dict_type` (`id`, `code`, `name`, `description`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (157, 'orderDeliverStatus', '订单发货状态', '订单发货状态', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-16 18:34:22', '2017-10-16 18:34:22');

INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (443, 'orderDeliverStatus', '待发货', '1', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-16 18:34:37', '2017-10-16 18:34:37');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (444, 'orderDeliverStatus', '全部发货', '4', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-16 18:34:46', '2017-10-16 18:34:46');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (445, 'orderDeliverStatus', '部分发货', '6', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-16 18:34:55', '2017-10-16 18:34:55');
INSERT INTO `dict` (`id`, `type_code`, `name`, `value`, `is_valid`, `is_deleted`, `create_operator`, `create_time`, `update_time`) VALUES (446, 'orderDeliverStatus', '已取消', '7', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-10-16 18:35:05', '2017-10-16 18:35:05');



INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (234, 204020105, '供应商订单导出', 'order/exportSupplierOrder', 'GET', 20402, 2, '1', 'admin', '0', '2017-10-12 20:31:48', '2017-10-12 20:31:48');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (235, 205, '财务管理', '1', '1', 2, 2, '0', 'admin', '0', '2017-10-18 10:25:59', '2017-10-18 10:25:59');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (236, 20501, '京东代发财务管理', '1', '1', 205, 2, '0', 'admin', '0', '2017-10-18 10:26:18', '2017-10-18 10:26:18');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (237, 205010101, '账户信息接口', 'bill/balance', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:30:59', '2017-10-18 10:37:58');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (238, 205010102, '订单对比明细分页查询接口', 'bill/orderDetailPage', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:31:37', '2017-10-18 10:38:07');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (239, 205010103, '订单对比明细导出接口', 'bill/exportOrderDetail', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:32:08', '2017-10-18 10:38:17');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (240, 205010104, '余额变动明细分页查询接口', 'bill/balanceDetailPage', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:32:40', '2017-10-18 10:38:28');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (241, 205010105, '余额变动明细导出接口', 'bill/exportBalanceDetail', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:33:15', '2017-10-18 10:38:41');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (242, 205010301, '订单对比明细操作接口', 'bill/operate', 'PUT', 20501, 2, '1', 'admin', '0', '2017-10-18 10:33:59', '2017-10-18 10:38:56');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (243, 205010106, '业务类型读取接口', 'bill/ treadType', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:34:33', '2017-10-18 10:39:06');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (244, 205010107, '订单对比明细操作查询接口', '^bill/getOperate/[1-9]\\d*$', 'GET', 20501, 2, '1', 'admin', '0', '2017-10-18 10:37:25', '2017-10-18 10:37:25');
INSERT  INTO `acl_resource`  (`id`, `code`, `name`, `url`, `method`, `parent_id`, `belong`, `type`, `create_operator`, `is_deleted`, `create_time`, `update_time`) VALUES (245, 204020301, '供应商订单取消', 'order/orderCancel', 'PUT', 20402, 2, '1', 'admin', '0', '2017-10-18 17:23:16', '2017-10-18 17:23:16');
