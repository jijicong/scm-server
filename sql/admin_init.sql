/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : scm_test

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-07-27 18:11:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for acl_resource
-- ----------------------------
DROP TABLE IF EXISTS `acl_resource`;
CREATE TABLE `acl_resource` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` bigint(20) DEFAULT NULL COMMENT '权限编码',
  `name` varchar(32) DEFAULT NULL COMMENT '权限的名称或者权限的类型',
  `url` varchar(128) NOT NULL COMMENT 'url路径',
  `method` varchar(32) NOT NULL COMMENT '请求的方法',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父节点ID',
  `belong` tinyint(4) DEFAULT NULL COMMENT '所属 标记渠道的分支或者是全局的分支',
  `type` varchar(2) DEFAULT NULL COMMENT '标志是虚拟资源，还是真实资源（0.虚拟资源 1.真实资源）',
  `create_operator` varchar(64) NOT NULL COMMENT '创建人',
  `is_deleted` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否删除:0-否,1-是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,格式yyyy-mm-dd hh:mi:ss',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8 COMMENT='资源表';

-- ----------------------------
-- Records of acl_resource
-- ----------------------------
INSERT INTO `acl_resource`  VALUES ('1', '1', '全局角色', '1', '1', '0', '0', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:09:57');
INSERT INTO `acl_resource`  VALUES ('2', '2', '渠道角色', '1', '1', '0', '0', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:09:58');
INSERT INTO `acl_resource`  VALUES ('3', '101', '供应商管理', '1', '1', '1', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:02');
INSERT INTO `acl_resource`  VALUES ('4', '102', '类目管理', '1', '1', '1', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:02');
INSERT INTO `acl_resource`  VALUES ('5', '103', '商品管理', '1', '1', '1', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:03');
INSERT INTO `acl_resource`  VALUES ('6', '104', '系统管理', '1', '1', '1', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:04');
INSERT INTO `acl_resource`  VALUES ('7', '201', '商品管理', '1', '1', '2', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:04');
INSERT INTO `acl_resource`  VALUES ('8', '202', '采购管理', '1', '1', '2', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:05');
INSERT INTO `acl_resource`  VALUES ('9', '203', '订单管理', '1', '1', '2', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:10:06');
INSERT INTO `acl_resource`  VALUES ('10', '10101', '供应商管理', '1', '1', '101', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:56');
INSERT INTO `acl_resource`  VALUES ('11', '10102', '供应商申请审批', '1', '1', '101', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:54');
INSERT INTO `acl_resource`  VALUES ('12', '10201', '品牌管理', '1', '1', '102', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:53');
INSERT INTO `acl_resource`  VALUES ('13', '10202', '属性管理', '1', '1', '102', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:51');
INSERT INTO `acl_resource`  VALUES ('14', '10203', '分类管理管', '1', '1', '102', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:51');
INSERT INTO `acl_resource`  VALUES ('15', '10301', '自采商品管理', '1', '1', '103', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:50');
INSERT INTO `acl_resource`  VALUES ('16', '10401', '仓库管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:49');
INSERT INTO `acl_resource`  VALUES ('17', '10402', '渠道管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:49');
INSERT INTO `acl_resource`  VALUES ('18', '10403', '授权管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:48');
INSERT INTO `acl_resource`  VALUES ('19', '10404', '字典类型管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:47');
INSERT INTO `acl_resource`  VALUES ('20', '10405', '字典管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:46');
INSERT INTO `acl_resource`  VALUES ('21', '20101', '商品查询', '1', '1', '201', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:44');
INSERT INTO `acl_resource`  VALUES ('22', '20201', '供应商管理', '1', '1', '202', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:44');
INSERT INTO `acl_resource`  VALUES ('23', '20202', '采购组管理', '1', '1', '202', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:43');
INSERT INTO `acl_resource`  VALUES ('24', '20203', '采购单管理', '1', '1', '202', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:41');
INSERT INTO `acl_resource`  VALUES ('25', '20204', '采购单审核', '1', '1', '202', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:40');
INSERT INTO `acl_resource`  VALUES ('26', '20205', '入库通知单管理', '1', '1', '202', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:40');
INSERT INTO `acl_resource`  VALUES ('27', '20301', '订单管理', '1', '1', '203', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:39');
INSERT INTO `acl_resource`  VALUES ('28', '20302', '供应商订单', '1', '1', '203', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:39');
INSERT INTO `acl_resource`  VALUES ('29', '20303', '出库通知单管理', '1', '1', '203', '2', '0', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:36');
INSERT INTO `acl_resource`  VALUES ('30', '104040101', '字典类型分页查询', 'config/dictTypePage', 'GET', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:35');
INSERT INTO `acl_resource`  VALUES ('31', '104040102', '字典类型列表查询', 'config/dictTypes', 'GET', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:33');
INSERT INTO `acl_resource`  VALUES ('32', '104040103', '字典类型按主键查询', '^config/dictType/[1-9]\\d*$', 'GET', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:31');
INSERT INTO `acl_resource`  VALUES ('33', '104040104', '字典类型按字典类型编码查询', 'config/dictType', 'GET', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:30');
INSERT INTO `acl_resource`  VALUES ('34', '104040201', '字典类型新增', 'config/dictType', 'POST', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:29');
INSERT INTO `acl_resource`  VALUES ('35', '104040301', '字典类型修改', '^config/dictType/[1-9]\\d*$', 'PUT', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:29');
INSERT INTO `acl_resource`  VALUES ('36', '104040401', '字典类型删除', '^config/dictType/[1-9]\\d*$', 'DELETE', '10404', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:27');
INSERT INTO `acl_resource`  VALUES ('37', '104050101', '字典分页查询', 'config/dictPage', 'GET', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:27');
INSERT INTO `acl_resource`  VALUES ('38', '104050103', '字典按主键查询', '^config/dict/[1-9]\\d*$', 'GET', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:26');
INSERT INTO `acl_resource`  VALUES ('39', '104050104', '字典按字典类型编码查询', 'config/dict', 'GET', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:26');
INSERT INTO `acl_resource`  VALUES ('40', '104050201', '字典新增', 'config/dict', 'POST', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:23');
INSERT INTO `acl_resource`  VALUES ('41', '104050301', '字典修改', '^config/dict/[1-9]\\d*$', 'PUT', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:23');
INSERT INTO `acl_resource`  VALUES ('42', '104050401', '字典删除', '^config/dict/[1-9]\\d*$', 'DELETE', '10405', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:22');
INSERT INTO `acl_resource`  VALUES ('43', '102010101', '品牌管理分页查询', 'category/brandPage', 'GET', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:21');
INSERT INTO `acl_resource`  VALUES ('44', '102010102', '品牌按主键查询', '^category/brand/[1-9]\\d*$', 'GET', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:20');
INSERT INTO `acl_resource`  VALUES ('45', '102010103', '品牌列表查询', 'category/brands', 'GET', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:19');
INSERT INTO `acl_resource`  VALUES ('46', '102010201', '保存品牌', 'category/brand', 'POST', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:19');
INSERT INTO `acl_resource`  VALUES ('47', '102010301', '更新品牌', '^category/brand/[1-9]\\d*$', 'PUT', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:16');
INSERT INTO `acl_resource`  VALUES ('48', '102010302', '更新品牌状态', '^category/brand/state/[1-9]\\d*$', 'PUT', '10201', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:15');
INSERT INTO `acl_resource`  VALUES ('49', '102020101', '属性管理分页查询', 'category/propertyPage', 'GET', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:14');
INSERT INTO `acl_resource`  VALUES ('50', '102020102', '属性值列表查询', 'category/propertyValues/search', 'GET', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:13');
INSERT INTO `acl_resource`  VALUES ('51', '102020103', '属性按主键查询', '^category/property/[1-9]\\d*$', 'GET', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:13');
INSERT INTO `acl_resource`  VALUES ('52', '102020201', '属性新增', 'category/property', 'POST', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:12');
INSERT INTO `acl_resource`  VALUES ('53', '102020301', '属性修改', '^category/property/[1-9]\\d*$', 'PUT', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:11');
INSERT INTO `acl_resource`  VALUES ('54', '102020302', '更新属性状态', '^category/property/state/[1-9]\\d*$', 'PUT', '10202', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:07');
INSERT INTO `acl_resource`  VALUES ('55', '101020101', '供应商申请审批分页', 'supplier/supplierApplyAuditPage', 'GET', '10102', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:07');
INSERT INTO `acl_resource`  VALUES ('56', '101020102', '供应商申请审批按主键查询', '^supplier/supplierApplyAudit/[1-9]\\d*$', 'GET', '10102', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:04');
INSERT INTO `acl_resource`  VALUES ('57', '101020301', '供应商申请审批修改', '^supplier/supplierApplyAudit/[1-9]\\d*$', 'PUT', '10102', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:03');
INSERT INTO `acl_resource`  VALUES ('58', '202010101', '供应商申请分页', 'supplier/supplierApplyPage', 'GET', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:02');
INSERT INTO `acl_resource`  VALUES ('59', '202010102', '供应商申请按主键查询', '^supplier/supplierApply/[1-9]\\d*$', 'GET', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:02');
INSERT INTO `acl_resource`  VALUES ('60', '202010103', '查询可申请的供应商列表', 'supplier/applySupplierPage', 'GET', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:06:01');
INSERT INTO `acl_resource`  VALUES ('61', '202010201', '供应商申请保存', 'supplier/supplierApply', 'POST', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:58');
INSERT INTO `acl_resource`  VALUES ('62', '202010301', '供应商申请修改', '^supplier/supplierApply/[1-9]\\d*$', 'PUT', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:58');
INSERT INTO `acl_resource`  VALUES ('63', '202010401', '供应商申请删除', '^supplier/supplierApply/[1-9]\\d*$', 'POST', '20201', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:57');
INSERT INTO `acl_resource`  VALUES ('64', '104020101', '渠道分页查询', 'system/channelPage', 'GET', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:56');
INSERT INTO `acl_resource`  VALUES ('65', '104020102', '渠道列表查询', 'system/channels', 'GET', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:54');
INSERT INTO `acl_resource`  VALUES ('66', '104020103', '渠道按主键查询', '^system/channel/[1-9]\\d*$', 'GET', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:55');
INSERT INTO `acl_resource`  VALUES ('67', '104020104', '渠道按名称查询', 'system/channel', 'GET', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:51');
INSERT INTO `acl_resource`  VALUES ('68', '104020201', '渠道新增', 'system/channel', 'POST', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:50');
INSERT INTO `acl_resource`  VALUES ('69', '104020301', '渠道修改', '^system/channel/[1-9]\\d*$', 'PUT', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:50');
INSERT INTO `acl_resource`  VALUES ('70', '104020302', '渠道状态修改', '^system/channel/updateState/[1-9]\\d*$', 'PUT', '10402', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:49');
INSERT INTO `acl_resource`  VALUES ('71', '103010101', '商品分页查询', 'goods/goodsPage', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:46');
INSERT INTO `acl_resource`  VALUES ('72', '103010102', '商品列表查询', 'goods/goodsList', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:44');
INSERT INTO `acl_resource`  VALUES ('73', '103010103', '根据SPU编码查询商品', '^goods/goods/spuCode/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:43');
INSERT INTO `acl_resource`  VALUES ('74', '103010104', '分类信息查询', 'category/categorys', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:43');
INSERT INTO `acl_resource`  VALUES ('75', '103010105', '根据品牌ID查询品牌信息', '^category/brand/[1-9]\\d*$', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:42');
INSERT INTO `acl_resource`  VALUES ('76', '103010106', '分类品牌查询', 'category/categoryBrands', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:41');
INSERT INTO `acl_resource`  VALUES ('77', '103010107', '分类属性查询', '^goods/itemsCategoryProperty/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$/[1-9]\\d*$', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:41');
INSERT INTO `acl_resource`  VALUES ('78', '103010201', '商品新增', 'goods/goods', 'POST', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:38');
INSERT INTO `acl_resource`  VALUES ('79', '103010301', '商品修改', '^goods/goods/[1-9]\\d*$', 'PUT', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:37');
INSERT INTO `acl_resource`  VALUES ('80', '103010302', '商品启/停用', '^goods/isValid/[1-9]\\d*$', 'PUT', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:36');
INSERT INTO `acl_resource`  VALUES ('81', '103010303', '商品SKU启/停用', '^goods/skuValid/[1-9]\\d*$', 'PUT', '10301', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:35');
INSERT INTO `acl_resource`  VALUES ('82', '101010101', '供应商分页查询', 'supplier/supplierPage', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:35');
INSERT INTO `acl_resource`  VALUES ('83', '101010102', '供应商列表查询', 'supplier/suppliers', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:34');
INSERT INTO `acl_resource`  VALUES ('84', '101010104', '供应商分类查询', 'supplier/supplierCategorys', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:33');
INSERT INTO `acl_resource`  VALUES ('85', '101010105', '供应商品牌查询', 'supplier/supplierBrands', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:32');
INSERT INTO `acl_resource`  VALUES ('86', '101010106', '供应商渠道关系查询', 'supplier/channels', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:32');
INSERT INTO `acl_resource`  VALUES ('87', '101010107', '根据供应商编码查询供应商', '^supplier/supplier/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:31');
INSERT INTO `acl_resource`  VALUES ('88', '101010108', '供应商性质查询', 'select/supplierNature', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:30');
INSERT INTO `acl_resource`  VALUES ('89', '101010109', '国家查询', 'select/country', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:30');
INSERT INTO `acl_resource`  VALUES ('90', '101010201', '新增供应商', 'supplier/supplier', 'POST', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:27');
INSERT INTO `acl_resource`  VALUES ('91', '101010301', '修改供应商', '^supplier/supplier/[1-9]\\d*$', 'PUT', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:26');
INSERT INTO `acl_resource`  VALUES ('92', '101010302', '供应商启/停用', '^supplier/isValid/[1-9]\\d*$', 'PUT', '10101', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:25');
INSERT INTO `acl_resource`  VALUES ('93', '102030101', '分类页面树结构查询', 'category/tree', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:25');
INSERT INTO `acl_resource`  VALUES ('94', '102030102', '查询分类关联的品牌', 'category/categoryBrands', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:24');
INSERT INTO `acl_resource`  VALUES ('95', '102030103', '校验起停用', '^category/valid/[1-9]\\d*$', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:23');
INSERT INTO `acl_resource`  VALUES ('96', '102030104', '分类列表查询', 'category/categorys', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:19');
INSERT INTO `acl_resource`  VALUES ('97', '102030105', '查询分类关联的属性', 'category/categoryProperty', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:18');
INSERT INTO `acl_resource`  VALUES ('98', '102030106', '查询分类一级二级三级路径', '^category/query/[1-9]\\d*$', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:17');
INSERT INTO `acl_resource`  VALUES ('99', '102030201', '分类新增', 'category/category', 'POST', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:17');
INSERT INTO `acl_resource`  VALUES ('100', '102030202', '保存分类属性关联', '^category/linkProperty/[1-9]\\d*$', 'POST', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:16');
INSERT INTO `acl_resource`  VALUES ('101', '102030203', '保存分类品牌关联', '^category/link/[1-9]\\d*$', 'POST', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:13');
INSERT INTO `acl_resource`  VALUES ('102', '102030301', '分类修改', '^category/category/[1-9]\\d*$', 'PUT', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:12');
INSERT INTO `acl_resource`  VALUES ('103', '102030302', '分类启停状态修改', '^category/category/updateState/[1-9]\\d*$', 'PUT', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:12');
INSERT INTO `acl_resource`  VALUES ('104', '102030303', '更新分类属性关联', '^category/updateProperty/[1-9]\\d*$', 'PUT', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:11');
INSERT INTO `acl_resource`  VALUES ('105', '102030304', '分类页面排序修改', 'category/sort', 'PUT', '10203', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:11');
INSERT INTO `acl_resource`  VALUES ('106', '104030101', '授权信息分页查询', 'accredit/accreditInfoPage', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:08');
INSERT INTO `acl_resource`  VALUES ('107', '104030102', '查询已启用的渠道', 'accredit/select/channel', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:07');
INSERT INTO `acl_resource`  VALUES ('108', '104030103', '查询拥有采购员角色的用户', 'accredit/purchase', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:06');
INSERT INTO `acl_resource`  VALUES ('109', '104030104', '查询选择用户对应角色', 'accredit/rolelist', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:04');
INSERT INTO `acl_resource`  VALUES ('110', '104030105', '查询手机号是否已经注册', 'accredit/checkPhone', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:04');
INSERT INTO `acl_resource`  VALUES ('111', '104030106', '用户采购组状态查询', '^accredit/checkPurchase/[1-9]\\d*$', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:03');
INSERT INTO `acl_resource`  VALUES ('112', '104030107', '编辑用户之前,查询是否有角色被停用', '^accredit/rolevalid/[1-9]\\d*$', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:05:03');
INSERT INTO `acl_resource`  VALUES ('113', '104030108', '角色信息分页查询', 'accredit/rolePage', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:59');
INSERT INTO `acl_resource`  VALUES ('114', '104030109', '根据角色名查询角色', 'accredit/role', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:58');
INSERT INTO `acl_resource`  VALUES ('115', '104030110', '根据Id查询角色', '^accredit/role/[1-9]\\d*$', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:58');
INSERT INTO `acl_resource`  VALUES ('116', '104030111', '根据角色的id 查询使用该角色的用户数量，以及启用状态', 'accredit/roleAccreditInfo', 'GET', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:57');
INSERT INTO `acl_resource`  VALUES ('117', '104030201', '状态的修改', '^accredit/accreditInfo/updateState/[1-9]\\d*$', 'PUT', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:56');
INSERT INTO `acl_resource`  VALUES ('118', '104030202', '新增授权', 'accredit/saveaccredit', 'POST', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:56');
INSERT INTO `acl_resource`  VALUES ('119', '104030203', '保存角色信息以及与之对应的角色权限关联表信息的保存', 'accredit/role', 'POST', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:49');
INSERT INTO `acl_resource`  VALUES ('120', '104030301', '授权用户修改', '^accredit/updateaccredit/[1-9]\\d*$', 'PUT', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:48');
INSERT INTO `acl_resource`  VALUES ('121', '104030302', '修改角色信息以及与之对应的角色权限关联表信息的修改', '^accredit/role/[1-9]\\d*$', 'PUT', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:45');
INSERT INTO `acl_resource`  VALUES ('122', '104030303', '角色状态修改', '^accredit/updateaccredit/[1-9]\\d*$', 'PUT', '10403', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:44');
INSERT INTO `acl_resource`  VALUES ('123', '104010101', '仓库分页查询', 'system/warehousePage', 'GET', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:44');
INSERT INTO `acl_resource`  VALUES ('124', '104010102', '仓库列表查询', 'system/warehouses', 'GET', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:43');
INSERT INTO `acl_resource`  VALUES ('125', '104010103', '仓库按主键查询', '^system/warehouse/[1-9]\\d*$', 'GET', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:43');
INSERT INTO `acl_resource`  VALUES ('126', '104010104', '仓库按名称查询', 'system/warehouse', 'GET', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:42');
INSERT INTO `acl_resource`  VALUES ('127', '104010201', '仓库新增', 'system/warehouse', 'POST', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:42');
INSERT INTO `acl_resource`  VALUES ('128', '104010301', '仓库修改', '^system/warehouse/[1-9]\\d*$', 'PUT', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:41');
INSERT INTO `acl_resource`  VALUES ('129', '104010302', '仓库状态修改', '^system/warehouse/updateState/[1-9]\\d*$', 'PUT', '10401', '1', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:41');
INSERT INTO `acl_resource`  VALUES ('130', '202020101', '采购组分页查询', 'purchase/purchaseGroupPage', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:40');
INSERT INTO `acl_resource`  VALUES ('131', '202020102', '采购组列表查询 ', 'purchase/purchaseGroups', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:40');
INSERT INTO `acl_resource`  VALUES ('132', '202020103', '采购组按主键查询', '^purchase/purchaseGroup/[1-9]\\d*$', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:39');
INSERT INTO `acl_resource`  VALUES ('133', '202020104', '采购组按名称查询', 'purchase/purchaseGroup', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:38');
INSERT INTO `acl_resource`  VALUES ('134', '202020105', '采购组人员按编码查询', 'purchase/purchasePerson', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:35');
INSERT INTO `acl_resource`  VALUES ('135', '202020106', '采购组按编码查询', '^purchase/purchaseGroupCode/[A-Z]\\b{1,}[0-9]\\d*$', 'GET', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:33');
INSERT INTO `acl_resource`  VALUES ('136', '202020201', '采购组新增', 'purchase/purchaseGroup', 'POST', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:32');
INSERT INTO `acl_resource`  VALUES ('137', '202020301', '采购组修改', '^purchase/purchaseGroup/[1-9]\\d*$', 'PUT', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:32');
INSERT INTO `acl_resource`  VALUES ('138', '202020302', '采购组状态修改', '^purchase/purchaseGroup/updateState/[1-9]\\d*$', 'PUT', '20202', '2', '1', 'admin', '0', '2017-06-13 12:19:47', '2017-08-02 15:04:31');
INSERT INTO `acl_resource`  VALUES ('139', '101010110', '分类品牌查询', 'category/categoryBrands', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 16:07:25', '2017-08-02 15:04:31');
INSERT INTO `acl_resource`  VALUES ('140', '101010111', '分类查询', 'category/tree', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-13 16:07:25', '2017-08-02 15:04:30');
INSERT INTO `acl_resource`  VALUES ('141', '103010108', '分类查询', 'category/tree', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-13 16:07:25', '2017-08-02 15:04:29');
INSERT INTO `acl_resource`  VALUES ('142', '102030107', '分类需要的品牌', 'category/brandPageCategory', 'GET', '10203', '1', '1', 'admin', '0', '2017-06-14 11:29:16', '2017-08-02 15:04:29');
INSERT INTO `acl_resource`  VALUES ('143', '101010112', '检查分类品牌启停用状态', '^supplier/checkCategoryBrandValidStatus/[1-9]\\d*$', 'GET', '10101', '1', '1', 'admin', '0', '2017-06-14 11:55:00', '2017-08-02 15:04:28');
INSERT INTO `acl_resource`  VALUES ('144', '103010109', '检查分类品牌启停用状态', '^supplier/checkCategoryBrandValidStatus/[1-9]\\d*$', 'GET', '10301', '1', '1', 'admin', '0', '2017-06-14 11:55:00', '2017-08-02 15:04:28');
INSERT INTO `acl_resource`  VALUES ('145', '104060101', '查询资源加载树', 'accredit/jurisdictionTree', 'GET', '10406', '1', '1', 'admin', '0', '2017-06-14 16:09:30', '2017-08-02 15:04:27');
INSERT INTO `acl_resource`  VALUES ('146', '104060201', '新增资源', 'accredit/jurisdictionSave', 'POST', '10406', '1', '1', 'admin', '0', '2017-06-15 11:27:21', '2017-08-02 15:04:22');
INSERT INTO `acl_resource`  VALUES ('147', '104060301', '编辑资源', '^accredit/jurisdictionEdit/[1-9]\\d*$', 'PUT', '10406', '1', '1', 'admin', '0', '2017-06-15 16:10:38', '2017-08-02 15:04:21');
INSERT INTO `acl_resource`  VALUES ('148', '10406', '资源管理', '1', '1', '104', '1', '0', 'admin', '0', '2017-07-13 20:49:37', '2017-08-02 15:04:20');
INSERT INTO `acl_resource`  VALUES ('149', '202030101', '采购订单分页', 'purchase/purchaseOrderPage', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 14:28:37', '2017-08-02 15:04:19');
INSERT INTO `acl_resource`  VALUES ('150', '202030201', '保存采购单', 'purchase/purchaseOrder', 'POST', '20203', '2', '1', 'admin', '0', '2017-07-21 14:29:29', '2017-08-02 15:04:17');
INSERT INTO `acl_resource`  VALUES ('151', '202030202', '采购单提交审核', 'purchase/purchaseOrderAudit', 'POST', '20203', '2', '1', 'admin', '0', '2017-07-21 14:29:58', '2017-08-02 15:04:15');
INSERT INTO `acl_resource`  VALUES ('152', '202030102', '查询供应商', 'purchase/suppliers', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 14:31:04', '2017-08-02 15:04:14');
INSERT INTO `acl_resource`  VALUES ('153', '202030103', '采购商品分页查询', 'purchase/suppliersItems/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 14:56:46', '2017-08-02 15:04:13');
INSERT INTO `acl_resource`  VALUES ('154', '202030301', '修改采购单', 'purchase/purchaseOrder/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:01:40', '2017-08-02 15:04:13');
INSERT INTO `acl_resource`  VALUES ('155', '202030302', '提交审核', 'purchase/purchaseOrderAudit/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:02:37', '2017-08-02 15:04:12');
INSERT INTO `acl_resource`  VALUES ('156', '202030203', '入库通知状态的作废', 'purchase/warahouseAdvice/cancellation/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:03:51', '2017-08-02 15:04:11');
INSERT INTO `acl_resource`  VALUES ('157', '202030104', '查询采购商品', 'purchase/suppliersAllItems/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 15:07:30', '2017-08-02 15:04:10');
INSERT INTO `acl_resource`  VALUES ('158', '202030105', '查询采购单', 'purchase/purchaseOrder/[1-9]\\d*$', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 15:09:13', '2017-08-02 15:04:07');
INSERT INTO `acl_resource`  VALUES ('159', '202030106', '根据编码查询采购单', 'purchase/purchaseOrderByCode/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '20203', '2', '1', 'admin', '0', '2017-07-21 15:11:10', '2017-08-02 15:04:06');
INSERT INTO `acl_resource`  VALUES ('160', '202030204', '修改采购单的状态', 'purchase/purchaseOrder/updateState/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:12:20', '2017-08-02 15:04:06');
INSERT INTO `acl_resource`  VALUES ('161', '202030205', '冻结采购单', 'purchase/purchaseOrder/freeze/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:13:03', '2017-08-02 15:04:05');
INSERT INTO `acl_resource`  VALUES ('162', '202030206', '采购单入库通知', 'purchase/purchaseOrder/warahouseAdvice/[1-9]\\d*$', 'PUT', '20203', '2', '1', 'admin', '0', '2017-07-21 15:14:15', '2017-08-02 15:04:04');
INSERT INTO `acl_resource`  VALUES ('163', '202040101', '采购订单审核分页', 'purchase/purchaseOrderAuditPage', 'GET', '20204', '2', '1', 'admin', '0', '2017-07-21 15:16:22', '2017-08-02 15:04:04');
INSERT INTO `acl_resource`  VALUES ('164', '202040301', '采购订单审核', 'purchase/purchaseOrderAudit', 'PUT', '20204', '2', '1', 'admin', '0', '2017-07-21 15:16:55', '2017-08-02 15:04:03');
INSERT INTO `acl_resource`  VALUES ('165', '202050101', '入库通知的分页查询', 'warehouseNotice/warehouseNoticePage', 'GET', '20205', '2', '1', 'admin', '0', '2017-07-21 15:17:25', '2017-08-02 15:04:03');
INSERT INTO `acl_resource`  VALUES ('166', '202050301', '通知仓库入库', 'warehouseNotice/receiptAdvice/[1-9]\\d*$', 'PUT', '20205', '2', '1', 'admin', '0', '2017-07-21 15:18:15', '2017-08-02 15:03:59');
INSERT INTO `acl_resource`  VALUES ('167', '202040302', '入库通知和修改货运单号', 'warehouseNotice/receiptAdviceInfo/[1-9]\\d*$', 'PUT', '20205', '2', '1', 'admin', '0', '2017-07-21 15:21:24', '2017-08-02 15:03:58');
INSERT INTO `acl_resource`  VALUES ('168', '202050201', '查询入库通知单信息', 'warehouseNotice/warehouseNoticeInfo/[1-9]\\d*$', 'GET', '20205', '2', '1', 'admin', '0', '2017-07-21 15:23:50', '2017-08-02 15:03:58');
INSERT INTO `acl_resource`  VALUES ('169', '202050302', '入库明细查询', 'warehouseNotice/warehouseNoticeDetail', 'GET', '20205', '2', '1', 'admin', '0', '2017-07-21 15:24:34', '2017-08-02 15:03:57');
INSERT INTO `acl_resource`  VALUES ('170', '10302', '代发商品管理', '1', '1', '103', '1', '0', 'admin', '0', '2017-07-21 18:30:33', '2017-08-02 15:03:56');
INSERT INTO `acl_resource`  VALUES ('171', '103020101', '代发商品分页查询', 'goods/externalGoodsPage', 'GET', '10302', '1', '1', 'admin', '0', '2017-07-21 18:31:36', '2017-08-02 15:03:54');
INSERT INTO `acl_resource`  VALUES ('172', '103020102', '代发商品分页查询(供应商商品)', 'goods/externalGoodsPage2', 'GET', '10302', '1', '1', 'admin', '0', '2017-07-21 18:33:52', '2017-08-02 15:03:53');
INSERT INTO `acl_resource`  VALUES ('173', '103020103', '代发商品列表查询', 'goods/externalItemSkus', 'GET', '10302', '1', '1', 'admin', '0', '2017-07-21 18:34:06', '2017-08-02 15:03:51');
INSERT INTO `acl_resource`  VALUES ('174', '103020201', '代发商品新增', 'goods/externalItemSku', 'POST', '10302', '1', '1', 'admin', '0', '2017-07-21 18:34:28', '2017-08-02 15:03:50');
INSERT INTO `acl_resource`  VALUES ('175', '103020301', '代发商品修改', 'goods/externalItemSku/[1-9]\\d*$', 'PUT', '10302', '1', '1', 'admin', '0', '2017-07-21 18:34:48', '2017-08-02 15:03:48');
INSERT INTO `acl_resource`  VALUES ('176', '103020302', '代发商品启/停用', 'goods/externalItemsValid/[1-9]\\d*$', 'PUT', '10302', '1', '1', 'admin', '0', '2017-07-21 18:35:07', '2017-08-02 15:03:47');
INSERT INTO `acl_resource`  VALUES ('177', '203010101', '订单管理分页查询', 'order/shopOrderPage', 'GET', '20301', '2', '1', 'admin', '0', '2017-07-21 18:35:32', '2017-08-02 15:03:46');
INSERT INTO `acl_resource`  VALUES ('178', '203010102', '店铺订单查询', 'order/shopOrders', 'GET', '20301', '2', '1', 'admin', '0', '2017-07-21 18:36:24', '2017-08-02 15:03:45');
INSERT INTO `acl_resource`  VALUES ('179', '203020101', '供应商订单分页查询', 'order/warehouseOrderPage', 'GET', '20302', '2', '1', 'admin', '0', '2017-07-21 18:36:49', '2017-08-02 15:03:44');
INSERT INTO `acl_resource`  VALUES ('180', '203020102', '仓库订单详情查询', 'order/warehouseOrder/warehouseOrderCode/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '20302', '2', '1', 'admin', '0', '2017-07-21 18:37:19', '2017-08-02 15:03:43');
INSERT INTO `acl_resource`  VALUES ('181', '203020103', '平台订单查询', 'order/platformOrders', 'GET', '20302', '2', '1', 'admin', '0', '2017-07-21 18:37:38', '2017-08-02 15:03:43');
INSERT INTO `acl_resource`  VALUES ('182', '203010103', '平台订单查询', 'order/platformOrders', 'GET', '20301', '2', '1', 'admin', '0', '2017-07-21 18:37:57', '2017-08-02 15:03:42');
INSERT INTO `acl_resource`  VALUES ('183', '203020201', '提交京东订单', 'order/jingDongOrder', 'POST', '20302', '2', '1', 'admin', '0', '2017-07-21 18:38:16', '2017-08-02 15:03:42');
INSERT INTO `acl_resource`  VALUES ('184', '102010104', '品牌联想搜索', 'brands/associationSearch', 'GET', '10201', '1', '1', 'admin', '0', '2017-07-24 11:44:22', '2017-08-02 15:03:41');
INSERT INTO `acl_resource`  VALUES ('185', '201010101', '商品查询', 'goods/goodsSkuPage', 'GET', '20101', '2', '1', 'admin', '0', '2017-07-25 18:33:55', '2017-08-02 15:03:40');
INSERT INTO `acl_resource`  VALUES ('186', '201010102', '查询商品详情', 'goods/goods/spuCode/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$', 'GET', '20101', '2', '1', 'admin', '0', '2017-07-25 18:35:40', '2017-08-02 15:03:38');
INSERT INTO `acl_resource`  VALUES ('187', '104030112', '根据手机号查询用户', 'accredit/getNameByPhone', 'GET', '10403', '1', '1', 'admin', '0', '2017-08-01 11:44:27', '2017-08-02 15:03:37');
INSERT INTO `acl_resource`  VALUES ('188', '201010103', '分类品牌查询', 'category/brands', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-01 15:23:17', '2017-08-02 15:03:37');
INSERT INTO `acl_resource`  VALUES ('189', '105', '账单管理', '1', '1', '1', '1', '0', 'admin', '0', '2017-08-03 13:34:32', '2017-08-03 14:25:07');
INSERT INTO `acl_resource`  VALUES ('190', '10501', '对账管理', '1', '1', '105', '1', '0', 'admin', '0', '2017-08-03 13:45:33', '2017-08-03 14:25:15');
INSERT INTO `acl_resource`  VALUES ('191', '105010101', '对账管理分页查询', '123', 'GET', '10501', '1', '1', 'admin', '0', '2017-08-03 13:45:46', '2017-08-03 14:25:16');
INSERT INTO `acl_resource`  VALUES ('192', '105010102', '查询业务类型', '123', 'GET', '10501', '1', '1', 'admin', '0', '2017-08-03 13:46:51', '2017-08-03 14:25:19');
INSERT INTO `acl_resource`  VALUES ('193', '202010104', '国家查询', 'select/country', 'GET', '20201', '2', '1', 'admin', '0', '2017-08-04 16:54:03', '2017-08-04 16:54:03');
INSERT INTO `acl_resource`  VALUES ('194', '202010105', '供应商渠道关系查询', 'supplier/channels', 'GET', '20201', '2', '1', 'admin', '0', '2017-08-04 16:56:57', '2017-08-04 16:56:57');
INSERT INTO `acl_resource`  VALUES ('195', '201010104', '分类页面树结构查询', 'category/tree', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-04 17:02:44', '2017-08-04 17:02:44');
INSERT INTO `acl_resource`  VALUES ('196', '201010105', '代发商品分页查询', 'goods/externalGoodsPage', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-04 17:06:03', '2017-08-04 17:06:03');
INSERT INTO `acl_resource`  VALUES ('197', '202010106', '供应商分类查询', 'supplier/supplierCategorys', 'GET', '20201', '2', '1', 'admin', '0', '2017-08-04 17:07:51', '2017-08-04 17:07:51');
INSERT INTO `acl_resource`  VALUES ('198', '202010107', '供应商品牌查询', 'supplier/supplierBrands', 'GET', '20201', '2', '1', 'admin', '0', '2017-08-04 17:09:59', '2017-08-04 17:09:59');
INSERT INTO `acl_resource`  VALUES ('199', '202010108', '渠道列表查询', 'system/channels', 'GET', '20201', '2', '1', 'admin', '0', '2017-08-04 17:10:32', '2017-08-04 17:10:32');
INSERT INTO `acl_resource`  VALUES ('200', '203020104', '供应商列表查询', 'supplier/suppliers', 'GET', '20302', '2', '1', 'admin', '0', '2017-08-04 17:12:03', '2017-08-04 17:12:03');
INSERT INTO `acl_resource`  VALUES ('201', '202020107', '采购人员查询', 'accredit/purchase', 'GET', '20202', '2', '1', 'admin', '0', '2017-08-05 15:44:12', '2017-08-05 15:43:34');
INSERT INTO `acl_resource`  VALUES ('202', '201010106', '分类查询', 'category/categorys', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-11 20:35:21', '2017-08-11 20:35:21');
INSERT INTO `acl_resource`  VALUES ('203', '201010107', '查询指定分类品牌', 'category/brand/[1-9]\\d*$', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-11 20:36:54', '2017-08-11 20:37:10');
INSERT INTO `acl_resource`  VALUES ('204', '201010108', '分类品牌查询2', 'category/categoryBrands', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-11 20:43:09', '2017-08-11 20:43:09');
INSERT INTO `acl_resource`  VALUES ('205', '201010109', '代发商品列表查询', 'goods/externalItemSkus', 'GET', '20101', '2', '1', 'admin', '0', '2017-08-12 12:16:46', '2017-08-12 12:16:46');
INSERT INTO `acl_resource`  VALUES ('206', '103010110', '检查属性启停用状态', 'goods/checkPropetyStatus', 'GET', '10301', '1', '1', 'admin', '0', '2017-08-24 16:07:02', '2017-08-24 16:07:02');

-- ----------------------------
-- Table structure for acl_role
-- ----------------------------
DROP TABLE IF EXISTS `acl_role`;
CREATE TABLE `acl_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `role_type` varchar(32) NOT NULL COMMENT '角色类型',
  `is_valid` varchar(2) NOT NULL DEFAULT '1' COMMENT '是否有效:0-无效,1-有效',
  `create_operator` varchar(64) NOT NULL COMMENT '创建人',
  `is_deleted` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否删除:0-否,1-是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,格式yyyy-mm-dd hh:mi:ss',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of acl_role
-- ----------------------------
INSERT INTO `acl_role` VALUES ('1', '采购组员', 'channelJurisdiction', '1', 'E2E4BDAD80354EFAB6E70120C271968C', '0', '2017-07-27 18:02:09', '2017-07-27 18:03:23', '');
INSERT INTO `acl_role` VALUES ('2', '全局角色', 'wholeJurisdiction', '1', 'E2E4BDAD80354EFAB6E70120C271968C', '0', '2017-07-27 18:00:27', '2017-07-27 18:00:27', '');
INSERT INTO `acl_role` VALUES ('3', '渠道角色', 'channelJurisdiction', '1', 'E2E4BDAD80354EFAB6E70120C271968C', '0', '2017-07-27 17:59:50', '2017-07-27 18:03:31', '');

-- ----------------------------
-- Table structure for acl_role_resource_relation
-- ----------------------------
DROP TABLE IF EXISTS `acl_role_resource_relation`;
CREATE TABLE `acl_role_resource_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `resource_code` bigint(20) NOT NULL COMMENT '权限id',
  `create_operator` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,格式yyyy-mm-dd hh:mi:ss',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8 COMMENT='角色权限关系表';

-- ----------------------------
-- Records of acl_role_resource_relation
-- ----------------------------
INSERT INTO `acl_role_resource_relation` VALUES ('1', '3', '20101', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('2', '3', '20201', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('3', '3', '20202', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('4', '3', '20203', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('5', '3', '20204', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('6', '3', '20205', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('7', '3', '20301', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('8', '3', '20302', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('9', '3', '20303', null, '2017-07-27 17:59:50', '2017-07-27 18:07:38');
INSERT INTO `acl_role_resource_relation` VALUES ('10', '2', '10101', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('11', '2', '10102', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('12', '2', '10201', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('13', '2', '10202', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('14', '2', '10203', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('15', '2', '10301', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('16', '2', '10302', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('17', '2', '10401', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('18', '2', '10402', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('19', '2', '10403', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('20', '2', '10404', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('21', '2', '10405', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('22', '2', '10406', null, '2017-07-27 18:00:27', '2017-07-27 18:00:27');
INSERT INTO `acl_role_resource_relation` VALUES ('23', '2', '10501', null, '2017-07-27 18:02:09', '2017-07-27 18:06:27');
-- ----------------------------
-- Table structure for acl_user_accredit_info
-- ----------------------------
DROP TABLE IF EXISTS `acl_user_accredit_info`;
CREATE TABLE `acl_user_accredit_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` varchar(64) NOT NULL COMMENT '用户中心的用户id',
  `phone` varchar(16) NOT NULL COMMENT '手机号',
  `name` varchar(32) NOT NULL COMMENT '用户姓名',
  `user_type` varchar(16) NOT NULL COMMENT '用户类型',
  `channel_code` varchar(32) DEFAULT NULL COMMENT '渠道编码',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `is_valid` varchar(2) NOT NULL DEFAULT '1' COMMENT '是否有效:0-无效,1-有效',
  `is_deleted` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否删除:0-否,1-是',
  `create_operator` varchar(64) NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,格式yyyy-mm-dd hh:mi:ss',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间,格式yyyy-mm-dd hh:mi:ss',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_phone` (`phone`),
  UNIQUE KEY `uniq_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户授权信息表';

-- ----------------------------
-- Records of acl_user_accredit_info
-- ----------------------------
INSERT INTO `acl_user_accredit_info` VALUES ('1', 'E2E4BDAD80354EFAB6E70120C271968C', '15757195796', 'admin', 'mixtureUser', 'QD001', 'admin', '1', '0', 'E2E4BDAD80354EFAB6E70120C271968C', '2017-07-27 17:40:18', '2017-07-27 17:40:18');

-- ----------------------------
-- Table structure for acl_user_accredit_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `acl_user_accredit_role_relation`;
CREATE TABLE `acl_user_accredit_role_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_accredit_id` bigint(20) NOT NULL COMMENT '用户授权id',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户中心的用户id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `is_valid` varchar(2) NOT NULL DEFAULT '1' COMMENT '是否有效:0-无效,1-有效',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `create_operator` varchar(64) NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,格式yyyy-mm-dd hh:mi:ss',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间,格式yyyy-mm-dd hh:mi:ss',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='用户角色关系表';

-- ----------------------------
-- Records of acl_user_accredit_role_relation
-- ----------------------------
INSERT INTO `acl_user_accredit_role_relation` VALUES ('1', '1', 'E2E4BDAD80354EFAB6E70120C271968C', '2', '1', null, 'E2E4BDAD80354EFAB6E70120C271968C', '2017-07-27 18:10:34', '2017-07-27 18:11:30');
INSERT INTO `acl_user_accredit_role_relation` VALUES ('2', '1', 'E2E4BDAD80354EFAB6E70120C271968C', '3', '1', null, 'E2E4BDAD80354EFAB6E70120C271968C', '2017-07-27 18:10:44', '2017-07-27 18:11:31');

