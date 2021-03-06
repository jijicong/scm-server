/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/8/31 11:22:23                           */
/*==============================================================*/

/*==============================================================*/
/* Table: acl_resource                                          */
/*==============================================================*/
create table acl_resource
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   code                 bigint comment '权限编码',
   name                 varchar(32) comment '权限的名称或者权限的类型',
   url                  varchar(128) not null comment 'url路径',
   method               varchar(32) not null comment '请求的方法',
   parent_id            bigint comment '父节点ID',
   belong               tinyint comment '所属 标记渠道的分支或者是全局的分支',
   type                 varchar(2) comment '0.虚拟资源 1.url操作资源',
   create_operator      varchar(64) not null comment '创建人',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table acl_resource comment '资源表';

/*==============================================================*/
/* Table: acl_role                                              */
/*==============================================================*/
create table acl_role
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   name                 varchar(64) not null comment '角色名称',
   role_type            varchar(32) not null comment '角色类型',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   create_operator      varchar(64) not null comment '创建人',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   remark               varchar(1024) comment '备注信息',
   primary key (id)
);

alter table acl_role comment '角色表';

/*==============================================================*/
/* Table: acl_role_resource_relation                            */
/*==============================================================*/
create table acl_role_resource_relation
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   role_id              bigint not null comment '角色id',
   resource_code        bigint not null comment '权限id',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table acl_role_resource_relation comment '角色权限关系表';

/*==============================================================*/
/* Table: acl_user_accredit_info                                */
/*==============================================================*/
create table acl_user_accredit_info
(
   id                   bigint unsigned not null auto_increment comment '主键id',
   user_id              varchar(64) not null comment '用户中心的用户id',
   phone                varchar(16) not null comment '手机号',
   name                 varchar(32) not null comment '用户姓名',
   user_type            varchar(16) not null comment '用户类型',
   channel_code         varchar(32) comment '渠道编码',
   remark               varchar(1024) comment '备注',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(64) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table acl_user_accredit_info comment '用户授权信息表';

/*==============================================================*/
/* Index: uniq_phone                                            */
/*==============================================================*/
create unique index uniq_phone on acl_user_accredit_info
(
   phone
);

/*==============================================================*/
/* Index: uniq_user_id                                          */
/*==============================================================*/
create unique index uniq_user_id on acl_user_accredit_info
(
   user_id
);

/*==============================================================*/
/* Table: acl_user_accredit_role_relation                       */
/*==============================================================*/
create table acl_user_accredit_role_relation
(
   id                   bigint unsigned not null auto_increment comment '主键id',
   user_accredit_id     bigint not null comment '用户授权id',
   user_id              varchar(64) comment '用户中心的用户id',
   role_id              bigint not null comment '角色id',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   remark               varchar(1024) comment '备注',
   create_operator      varchar(64) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table acl_user_accredit_role_relation comment '用户角色关系表';

/*==============================================================*/
/* Table: apply_for_purchase_order                              */
/*==============================================================*/
create table apply_for_purchase_order
(
   id                   bigint unsigned not null auto_increment comment '主键',
   purchase_order_code  varchar(32) not null comment '采购单编号',
   purchase_order_id    bigint not null comment '采购单id',
   description          varchar(3072) comment '申请说明',
   status               varchar(2) comment '状态:0-暂存,1-提交审核,2-审核通过,3-审核驳回',
   is_deleted           varchar(2) default '0' comment '是否删除:0-否,1-是',
   audit_opinion        varchar(3072) comment '审核意见',
   create_operator      varchar(64) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table apply_for_purchase_order comment '采购单申请信息表';

/*==============================================================*/
/* Table: apply_for_supplier                                    */
/*==============================================================*/
create table apply_for_supplier
(
   id                   bigint unsigned not null auto_increment comment '主键',
   apply_code           varchar(32) not null comment '申请编号',
   supplier_id          bigint not null comment '供应商id',
   channel_id           bigint not null comment '渠道id',
   supplier_code        varchar(32) not null comment '供应商编号',
   channel_code         varchar(32) not null comment '渠道编号',
   description          varchar(3072) comment '申请说明',
   status               varchar(2) comment '状态:0-暂存,1-提交审核,2-审核通过,3-审核驳回',
   is_deleted           varchar(2) default '0' comment '是否删除:0-否,1-是',
   audit_opinion        varchar(3072) comment '审核意见',
   create_operator      varchar(64) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table apply_for_supplier comment '供应商申请信息表';

/*==============================================================*/
/* Index: uniq_applyForSupplier_applyCode                       */
/*==============================================================*/
create unique index uniq_applyForSupplier_applyCode on apply_for_supplier
(
   apply_code
);

/*==============================================================*/
/* Table: area                                                  */
/*==============================================================*/
create table area
(
   id                   bigint not null auto_increment,
   code                 varchar(40) not null,
   province             varchar(40) not null,
   city                 varchar(40) not null,
   district             varchar(40) not null,
   parent               bigint not null,
   primary key (id)
);

alter table area comment '省市区表';

/*==============================================================*/
/* Table: audit_log                                             */
/*==============================================================*/
create table audit_log
(
   id                   bigint not null auto_increment comment '主键',
   apply_code           varchar(64) not null comment '申请编号',
   operation            varchar(32) not null comment '操作',
   operator             varchar(32) comment '操作者',
   remark               varchar(3072) comment '备注',
   operate_time         timestamp not null default CURRENT_TIMESTAMP comment '操作时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table audit_log comment '审核日志信息表';

/*==============================================================*/
/* Table: brand                                                 */
/*==============================================================*/
create table brand
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   name                 varchar(256) not null comment '品牌名称',
   brand_code           varchar(32) not null comment '品牌编码',
   source               varchar(32) not null comment '来源:scm-系统自行添加，trc-泰然城导入',
   alise                varchar(128) comment '品牌别名',
   web_url              varchar(256) comment '品牌网址',
   logo                 varchar(256) comment '品牌LOGO的图片路径',
   sort                 int comment '序号',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   create_operator      varchar(32) not null comment '创建人',
   last_edit_operator   varchar(64) not null comment '最新更新人',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table brand comment '品牌';

/*==============================================================*/
/* Index: uniq_brand_brandCode                                  */
/*==============================================================*/
create unique index uniq_brand_brandCode on brand
(
   brand_code
);

/*==============================================================*/
/* Table: category                                              */
/*==============================================================*/
create table category
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   full_path_id         varchar(64) comment '全路径ID,从第一级到当前节点的id字符串拼接路径格式：上一级节点ID|上一级节点ID,一级分类为空',
   category_code        varchar(32) not null comment '分类编码,系统流水生成',
   source               varchar(32) not null comment '来源:scm-系统自行添加，trc-泰然城导入',
   parent_id            bigint comment '父节点ID',
   level                int comment '级数',
   name                 varchar(128) comment '节点内容',
   sort                 int comment '排序',
   classify_describe    varchar(128) comment '分类描述',
   is_leaf              varchar(2) not null comment '是否叶子节点:0-否,1-是',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   create_operator      varchar(32) comment '创建人',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table category comment '分类信息表';

/*==============================================================*/
/* Index: uniq_category_code                                    */
/*==============================================================*/
create unique index uniq_category_code on category
(
   category_code
);

/*==============================================================*/
/* Table: category_brand                                        */
/*==============================================================*/
create table category_brand
(
   id                   bigint unsigned not null auto_increment,
   category_id          bigint not null comment '分类编号',
   category_code        varchar(32) comment '分类编码',
   brand_id             bigint not null comment '品牌编号',
   brand_code           varchar(32) comment '品牌编码',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否启用',
   is_deleted           varchar(2) comment '是否删除:0-否,1-是',
   primary key (id)
);

alter table category_brand comment '分类相关品牌';

/*==============================================================*/
/* Table: category_property                                     */
/*==============================================================*/
create table category_property
(
   id                   bigint not null auto_increment comment '主键',
   category_id          varchar(32) not null comment '分类编号',
   property_id          bigint not null comment '属性ID',
   property_sort        int not null comment '属性序号',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否启用',
   is_deleted           varchar(2) comment '是否删除:0-否,1-是',
   primary key (id)
);

alter table category_property comment '分类相关属性';

/*==============================================================*/
/* Table: certificate                                           */
/*==============================================================*/
create table certificate
(
   id                   bigint unsigned not null auto_increment comment '主键',
   supplier_id          bigint not null comment '供应商id',
   supplier_code        varchar(32) not null comment '供应链编号',
   business_licence     varchar(64) comment '营业执照',
   business_licence_pic varchar(256) comment '营业执照图片',
   organ_registra_code_certificate varchar(64) comment '组织机构代码证',
   organ_registra_code_certificate_pic varchar(256) comment '组织机构代码证图片',
   tax_registration_certificate varchar(64) comment '税务登记证',
   tax_registration_certificate_pic varchar(256) comment '税务登记证图片',
   multi_certificate_combine_no varchar(64) comment '多证合一号,在证件类型字段值为多证合一时不为空',
   multi_certificate_combine_pic varchar(256) comment '多证合一图片,在证件类型字段值为多证合一时不为空',
   legal_person_id_card varchar(32) comment '法人身份证',
   legal_person_id_card_pic1 varchar(256) not null comment '法人身份证正面',
   legal_person_id_card_pic2 varchar(256) not null comment '法人身份证反面',
   business_licence_start_date varchar(20) comment '营业执照有效期开始日期',
   business_licence_end_date varchar(20) comment '营业执照有效期结束日期',
   organ_registra_start_date varchar(20) comment '组织机构代码证有效期开始日期',
   organ_registra_end_date varchar(20) comment '组织机构代码证有效期结束日期',
   tax_registration_start_date varchar(20) comment '税务登记证有效期开始日期',
   tax_registration_end_date varchar(20) comment '税务登记证有效期结束日期',
   multi_certificate_start_date varchar(20) comment '多证合一有效期开始日期',
   multi_certificate_end_date varchar(20) comment '多证合一有效期结束日期',
   id_card_start_date   varchar(20) comment '法人身份证有效期开始日期',
   id_card_end_date     varchar(20) comment '法人身份证有效期结束日期',
   is_deleted           varchar(2) default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table certificate comment '证件信息表';

/*==============================================================*/
/* Table: change_inventory_flow                                 */
/*==============================================================*/
create table change_inventory_flow
(
   id                   bigint unsigned not null auto_increment comment '主键',
   channel_code         varchar(32) not null comment '渠道编号',
   request_code         varchar(32) not null comment '请求编码：每次请求唯一，不允许重复',
   order_type           varchar(32) not null comment '单据类型',
   order_code           varchar(32) not null comment '订单编码',
   sku_stock_id         bigint not null comment 'sku仓库库存id',
   sku_code             varchar(32) not null comment 'sku编码',
   available_inventory_change bigint comment '可用库存',
   frozen_inventory_change bigint comment '冻结库存',
   real_inventory_change bigint comment '真实库存变更数量',
   defective_inventory_change bigint comment '残次品库存',
   original_available_inventory bigint comment '原可用库存',
   original_frozen_inventory bigint comment '原冻结库存',
   original_real_inventory bigint comment '原真实库存',
   original_defective_inventory bigint comment '原残次品库存',
   newest_available_inventory bigint comment '最新可用库存',
   newest_frozen_inventory bigint comment '最新冻结库存',
   newest_real_inventory bigint comment '最新真实库存',
   newest_defective_inventory bigint comment '最新残次品库存',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table change_inventory_flow comment '库存变更流水';

/*==============================================================*/
/* Table: change_inventory_request_flow                         */
/*==============================================================*/
create table change_inventory_request_flow
(
   id                   bigint unsigned not null auto_increment comment '主键',
   channel_code         varchar(32) not null comment '渠道编号',
   request_code         varchar(32) not null comment '请求编码：每次请求唯一，不允许重复',
   order_type           varchar(32) not null comment '单据类型',
   order_code           varchar(32) not null comment '单据编码',
   request_type         varchar(16) not null comment '请求类型:1-下单冻库存；2-冻库存订单支付扣减库存；3-支付扣减库存；4-退货待定',
   status               varchar(16) not null comment '处理结果状态',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id),
   unique key AK_Key_2 (channel_code, request_code)
);

alter table change_inventory_request_flow comment '库存变更请求流水';

/*==============================================================*/
/* Table: channel                                               */
/*==============================================================*/
create table channel
(
   id                   bigint not null auto_increment comment '主键',
   code                 varchar(32) comment '渠道编号',
   name                 varchar(64) comment '渠道名称',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   remark               varchar(1024) comment '备注信息',
   create_operator      varchar(32) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table channel comment '渠道';

/*==============================================================*/
/* Index: uniq_channel_code                                     */
/*==============================================================*/
create unique index uniq_channel_code on channel
(
   code
);

/*==============================================================*/
/* Table: common_config                                         */
/*==============================================================*/
create table common_config
(
   id                   bigint not null comment '主键',
   code                 varchar(32) not null comment '配置字段',
   value                varchar(255) not null comment '值',
   type                 varchar(64) default NULL comment '类型',
   description          varchar(255) not null comment '备注',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   update_time          timestamp not null default CURRENT_TIMESTAMP comment '更新时间',
   dead_time            timestamp null default NULL comment '失效时间',
   primary key (id)
);

alter table common_config comment '配置信息表';

/*==============================================================*/
/* Table: dict                                                  */
/*==============================================================*/
create table dict
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   type_code            varchar(32) not null comment '字典类型编码',
   name                 varchar(64) not null comment '名称',
   value                varchar(64) comment '字典值',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table dict comment '数据字典';

/*==============================================================*/
/* Table: dict_type                                             */
/*==============================================================*/
create table dict_type
(
   id                   bigint unsigned not null auto_increment comment '主键',
   code                 varchar(32) not null comment '主键ID',
   name                 varchar(64) not null comment '类型名称',
   description          varchar(512) comment '说明',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table dict_type comment '数据字典类型';

/*==============================================================*/
/* Table: external_item_sku                                     */
/*==============================================================*/
create table external_item_sku
(
   id                   bigint unsigned not null auto_increment comment '主键',
   supplier_id          bigint comment '供应商id',
   supplier_code        varchar(32) not null comment '供应链编号',
   supplier_name        varchar(64) comment '供应链名称',
   sku_code             varchar(32) not null comment '商品SKU编号',
   supplier_sku_code    varchar(32) not null comment '供应商商品sku编号',
   item_name            varchar(255) comment '商品名称',
   bar_code             varchar(255) comment '条形码',
   supply_price         bigint comment '供货价,单位/分',
   supplier_price       bigint comment '供应商售价,单位/分',
   market_reference_price bigint comment '市场参考价,单位/分',
   warehouse            varchar(32) comment '仓库',
   subtitle             varchar(64) comment '商品副标题',
   brand                varchar(32) comment '品牌',
   category             varchar(512) comment '分类名称,多个用分号;分隔',
   category_code        varchar(512) comment '分类编码,多个用分号;分隔',
   weight               bigint comment '重量,单位/克',
   producing_area       varchar(32) comment '产地',
   place_of_delivery    varchar(64) comment '发货地',
   item_type            varchar(16) comment '商品类型',
   tariff               decimal(4,3) comment '商品关税税率',
   main_pictrue         varchar(1024) comment '商品主图',
   detail_pictrues      longtext comment '详情图',
   detail               varchar(1024) comment '详情',
   properties           varchar(512) comment '属性',
   stock                bigint comment '库存',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   state                varchar(2) comment '上下架状态 ,1上架，0下架',
   primary key (id)
);

alter table external_item_sku comment '一件代发商品';

/*==============================================================*/
/* Index: uniq_externalItemSku_supplierCode_supplierSkuCode     */
/*==============================================================*/
create unique index uniq_externalItemSku_supplierCode_supplierSkuCode on external_item_sku
(
   supplier_code,
   supplier_sku_code
);

/*==============================================================*/
/* Table: idempotent                                            */
/*==============================================================*/
create table idempotent
(
   id                   bigint not null auto_increment,
   serial_no            varchar(128) not null comment '流水号',
   type                 varchar(32) not null comment '流水类型',
   create_time          timestamp comment '创建时间',
   update_time          timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
   primary key (id)
);

alter table idempotent comment '幂等校验表';

/*==============================================================*/
/* Table: item_nature_propery                                   */
/*==============================================================*/
create table item_nature_propery
(
   id                   bigint unsigned not null auto_increment comment '主键',
   item_id              bigint comment '商品ID',
   spu_code             varchar(32) not null comment '商品SPU编号',
   property_id          bigint not null comment '属性量ID',
   property_value_id    bigint not null comment '属性值ID',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table item_nature_propery comment '商品自然属性';

/*==============================================================*/
/* Index: uniq_itemNaturePropery_spuCode                        */
/*==============================================================*/
create unique index uniq_itemNaturePropery_spuCode on item_nature_propery
(
   spu_code,
   property_id,
   property_value_id
);

/*==============================================================*/
/* Table: item_sales_propery                                    */
/*==============================================================*/
create table item_sales_propery
(
   id                   bigint unsigned not null auto_increment comment '主键',
   item_id              bigint comment '商品ID',
   spu_code             varchar(32) not null comment '商品SPU编号',
   sku_code             varchar(32) not null comment '商品SKU编号',
   property_id          bigint not null comment '属性ID',
   property_value_id    bigint not null comment '属性值ID',
   property_actual_value varchar(256) comment '属性实际值',
   picture              varchar(256) comment '图片',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table item_sales_propery comment '商品销售属性';

/*==============================================================*/
/* Index: idx_itemSalesPropery_spuCode                          */
/*==============================================================*/
create index idx_itemSalesPropery_spuCode on item_sales_propery
(
   spu_code
);

/*==============================================================*/
/* Index: idx_itemSalesPropery_skuCode                          */
/*==============================================================*/
create index idx_itemSalesPropery_skuCode on item_sales_propery
(
   sku_code
);

/*========================================================================*/
/* Index: uniq_itemSalePropery_spuCode_skuCode_propertyId_propertyValueId */
/*========================================================================*/
create unique index uniq_itemSalePropery_spuCode_skuCode_propertyId_propertyValueId on item_sales_propery
(
   spu_code,
   sku_code,
   property_id,
   property_value_id
);

/*==============================================================*/
/* Table: items                                                 */
/*==============================================================*/
create table items
(
   id                   bigint unsigned not null auto_increment comment '主键',
   spu_code             varchar(64) not null comment '商品SPU编号',
   name                 varchar(200) not null comment '商品名称',
   category_id          bigint not null comment '所属类目编号',
   brand_id             bigint not null comment '所属品牌编号',
   trade_type           varchar(32) not null comment '贸易类型',
   item_no              varchar(32) not null comment '商品货号',
   weight               bigint comment '商品重量,单位/g',
   producer             varchar(128) comment '生产商',
   market_price         bigint comment '参考市场价',
   pictrue              varchar(256) comment '商品图片路径,多个路径用逗号隔开',
   remark               varchar(512) comment '备注',
   properties           varchar(512) comment '属性量id；格式为123;223;212;',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table items comment '商品';

/*==============================================================*/
/* Index: uniq_items_spuCode                                    */
/*==============================================================*/
create unique index uniq_items_spuCode on items
(
   spu_code
);

/*==============================================================*/
/* Table: jing_dong_area                                        */
/*==============================================================*/
create table jing_dong_area
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   jd_code              varchar(40) not null comment '京东编码',
   area_code            varchar(40) comment '行政编码',
   province             varchar(40) comment '省',
   city                 varchar(40) comment '市',
   district             varchar(40) comment '区县',
   town                 varchar(255) comment '镇',
   parent               bigint comment '父id',
   primary key (id)
);

alter table jing_dong_area comment '京东地址表';

/*==============================================================*/
/* Table: log_information                                       */
/*==============================================================*/
create table log_information
(
   id                   bigint unsigned not null auto_increment comment '主键',
   entity_type          varchar(32) not null comment '实体类型',
   entity_id            bigint not null comment '实体id',
   operation            varchar(32) not null comment '操作',
   operator_userid      varchar(64) not null comment '操作者id',
   operator             varchar(32) comment '操作者',
   params               longtext comment '请求参数:json格式',
   remark               varchar(256) comment '备注',
   operate_type         varchar(2),
   operate_time         timestamp not null comment '操作时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table log_information comment '日志信息表';

/*==============================================================*/
/* Table: logistics_company                                     */
/*==============================================================*/
create table logistics_company
(
   id                   bigint unsigned not null auto_increment comment '主键',
   company_code         varchar(32) not null comment '物流公司编码',
   company_name         varchar(256) not null comment '物流公司名称',
   type                 varchar(10) not null comment '对接的物流平台类型: TRC-泰然城,XT-小泰乐活,100-快递100',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table logistics_company comment '物流公司信息';

/*==============================================================*/
/* Table: mapping_table                                         */
/*==============================================================*/
create table mapping_table
(
   id                   bigint not null comment '主键',
   area_code            varchar(40) default NULL comment '行政编码',
   province             varchar(40) default NULL comment '省名称',
   city                 varchar(40) default NULL comment '市名称',
   district             varchar(40) default NULL comment '区县名称',
   jd_code              varchar(40) default NULL comment '京东编码',
   primary key (id)
);

alter table mapping_table comment '省市区映射表';

/*==============================================================*/
/* Table: order_flow                                            */
/*==============================================================*/
create table order_flow
(
   platform_order_code  varchar(32) not null comment '平台订单编码',
   shop_order_code      varchar(32) not null comment '店铺订单编码',
   type                 varchar(32) not null comment '业务类型',
   primary key (platform_order_code, shop_order_code, type)
);

alter table order_flow comment '订单流水表';

/*==============================================================*/
/* Index: uniq_platformOrderCode_shopOrderCode_type             */
/*==============================================================*/
create index uniq_platformOrderCode_shopOrderCode_type on order_flow
(
   platform_order_code,
   shop_order_code,
   type
);

/*==============================================================*/
/* Table: order_item                                            */
/*==============================================================*/
create table order_item
(
   id                   bigint unsigned not null auto_increment comment '主键',
   warehouse_order_code varchar(32) not null comment '仓库订单编码',
   shop_order_code      varchar(32) not null comment '店铺订单编码',
   platform_order_code  varchar(32) not null comment '平台订单编码',
   channel_code         varchar(32) not null comment '渠道编码',
   platform_code        varchar(32) not null comment '来源平台编码',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_name       varchar(64) comment '所在仓库名称',
   category             bigint not null comment '所属类目编号',
   shop_id              bigint not null comment '订单所属的店铺id',
   shop_name            varchar(255) not null comment '店铺名称',
   user_id              varchar(64) not null comment '会员id',
   spu_code             varchar(64) comment '商品SPU编号',
   sku_code             varchar(32) comment 'sku编码',
   supplier_sku_code    varchar(32) comment '供应商SKU编码',
   sku_stock_id         bigint comment '商品sku库存id',
   item_no              varchar(32) not null comment '商品货号',
   bar_code             varchar(64) comment '条形码',
   item_name            varchar(128) not null comment '商品名称',
   spec_nature_info     longtext comment '商品规格描述',
   price                decimal(20,3) comment '商品价格,单位/分',
   market_price         decimal(20,3) comment '市场价,单位/分',
   promotion_price      decimal(20,3) comment '促销价,单位/分',
   customs_price        decimal(20,3) comment '报关单价,单位/分',
   transaction_price    decimal(20,3) comment '成交单价,单位/分',
   num                  int comment '购买数量',
   send_num             int comment '明细商品发货数量',
   sku_properties_name  varchar(512) comment 'SKU的值',
   refund_id            varchar(32) comment '最近退款ID',
   is_oversold          varchar(20) default '0' comment '是否超卖',
   shipping_type        varchar(32) comment '运送方式',
   bind_oid             varchar(32) comment '捆绑的子订单号',
   logistics_company    varchar(32) comment '子订单发货的快递公司',
   invoice_no           varchar(32) comment '子订单所在包裹的运单号',
   post_discount        decimal(20,3) comment '运费分摊,单位/分',
   discount_promotion   decimal(20,3) comment '促销优惠分摊,单位/分',
   discount_coupon_shop decimal(20,3) comment '店铺优惠卷分摊金额,单位/分',
   discount_coupon_platform decimal(20,3) comment '平台优惠卷优惠分摊,单位/分',
   discount_fee         decimal(20,3) comment '子订单级订单优惠金额,单位/分',
   total_fee            decimal(20,3) comment '应付金额,单位/分',
   payment              decimal(20,3) comment '实付金额,单位/分',
   total_weight         decimal(20,3) comment '商品重量,单位/克',
   adjust_fee           decimal(20,3) comment '手工调整金额,单位/分',
   status               varchar(32) comment '订单状态:1-待出库 2-部分出库 3-全部出库',
   after_sales_status   varchar(32) comment '售后状态',
   complaints_status    varchar(32) comment '订单投诉状态',
   refund_fee           decimal(20,3) comment '退款金额,单位/分',
   cat_service_rate     decimal comment '商家三级类目签约佣金比例',
   pic_path             varchar(255) comment '商品图片绝对路径',
   outer_iid            varchar(64) comment '商家外部编码',
   outer_sku_id         varchar(64) comment '商家外部sku码',
   sub_stock            varchar(20) comment '是否支持下单减库存',
   dlytmpl_id           int(10) comment '配送模板id',
   supplier_name        varchar(80) comment '供应商名称',
   price_tax            decimal(20,3) comment '商品税费,单位/分',
   promotion_tags       varchar(32) comment '订单应用促销标签',
   obj_type             varchar(32) comment '订单商品类型',
   type                 varchar(1) not null comment '订单类型 0-普通 1-零元购 2-分期购 3-团购',
   tax_rate             decimal(20,3) comment '税率',
   params               varchar(255) comment '订单冗余参数',
   create_time          timestamp default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   pay_time             timestamp null default NULL comment '支付时间',
   consign_time         timestamp null default NULL comment '发货时间',
   update_time          timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   timeout_action_time  timestamp null default NULL comment '超时确认时间',
   end_time             timestamp null default NULL comment '关闭时间',
   primary key (id)
);

alter table order_item comment '订单明细信息表';

/*=========================================================================*/
/* Index: uniq_orderItem_channelCode_platformOrderCode_shopOrderCode_wareh */
/*=========================================================================*/
create unique index uniq_orderItem_channelCode_platformOrderCode_shopOrderCode_wareh on order_item
(
   warehouse_order_code,
   shop_order_code,
   platform_order_code,
   channel_code,
   sku_code
);

/*==============================================================*/
/* Index: idx_orderItem_shopOrderCode                           */
/*==============================================================*/
create index idx_orderItem_shopOrderCode on order_item
(
   shop_order_code
);

/*==============================================================*/
/* Index: idx_orderItem_warehouseOrderCode                      */
/*==============================================================*/
create index idx_orderItem_warehouseOrderCode on order_item
(
   warehouse_order_code
);

/*==============================================================*/
/* Table: outbound_order                                        */
/*==============================================================*/
create table outbound_order
(
   id                   bigint not null comment '主键',
   outbound_order_code  varchar(32) not null comment '出库通知单编码',
   warehouse_order_code varchar(32) not null comment '店铺订单编码',
   shop_order_code      varchar(32) not null comment '店铺订单编码',
   supplier_id          bigint comment '供应商id',
   supplier_code        varchar(32) comment '供应商编号',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_code       varchar(32) not null comment '仓库编号',
   item_num             int comment '商品总数',
   receiver_province    varchar(16) comment '收货人所在省',
   receiver_city        varchar(16) comment '收货人所在城市',
   receiver_district    varchar(16) comment '收货人所在地区',
   receiver_address     varchar(256) comment '收货人详细地址',
   receiver_zip         varchar(16) comment '收货人邮编',
   receiver_name        varchar(128) comment '收货人姓名',
   receiver_phone       varchar(16) comment '收货人电话号码',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table outbound_order comment '出库通知单';

/*==============================================================*/
/* Table: platform_order                                        */
/*==============================================================*/
create table platform_order
(
   id                   bigint not null auto_increment comment '主键',
   platform_order_code  varchar(32) not null comment '平台订单编码',
   channel_code         varchar(32) not null comment '渠道编码',
   platform_code        varchar(32) not null comment '来源平台编码',
   user_id              varchar(64) not null comment '用户id',
   user_name            varchar(32) not null comment '会员名称',
   item_num             int not null comment '买家购买的商品总数',
   pay_type             varchar(16) not null comment '支付类型',
   payment              decimal(20,3) not null comment '实付金额,单位/分',
   points_fee           decimal(20,3) default 0 comment '积分抵扣金额,单位/分',
   total_fee            decimal(20,3) not null comment '订单总金额(商品单价*数量),单位/分',
   adjust_fee           decimal(20,3) comment '卖家手工调整金额,子订单调整金额之和,单位/分',
   postage_fee          decimal(20,3) comment '邮费,单位/分',
   total_tax            decimal(20,3) not null comment '总税费,单位/分',
   need_invoice         varchar(10) not null default '0' comment '是否开票 1-是 0-不是',
   invoice_name         varchar(128) comment '发票抬头',
   invoice_type         varchar(32) comment '发票类型',
   invoice_main         varchar(128) comment '发票内容',
   receiver_province    varchar(16) comment '收货人所在省',
   receiver_city        varchar(16) comment '收货人所在城市',
   receiver_district    varchar(16) comment '收货人所在地区',
   receiver_address     varchar(256) comment '收货人详细地址',
   receiver_zip         varchar(16) comment '收货人邮编',
   receiver_name        varchar(128) comment '收货人姓名',
   receiver_id_card     varchar(32) comment '收货人身份证',
   receiver_id_card_front varchar(255) comment '收货人身份证正面',
   receiver_id_card_back varchar(255) comment '收货人身份证背面',
   receiver_phone       varchar(16) comment '收货人电话号码',
   receiver_mobile      varchar(16) comment '收货人手机号码',
   receiver_email       varchar(64) comment '收货人电子邮箱',
   buyer_area           varchar(32) comment '买家下单地区',
   ziti_memo            varchar(255) comment '自提备注',
   ziti_addr            varchar(255) comment '自提地址',
   anony                varchar(4) not null default '0' comment '是否匿名下单 1-匿名 0-实名',
   obtain_point_fee     int comment '买家下单送积分',
   real_point_fee       int comment '买家使用积分',
   step_trade_status    varchar(32) comment '分阶段付款状态',
   step_paid_fee        decimal(20,3) comment '分阶段已付金额,单位/分',
   is_clearing          varchar(4) comment '是否生成结算清单 0-否 1-是',
   cancel_reason        varchar(255) comment '订单取消原因',
   cancel_status        varchar(32) comment '成功：SUCCESS  待退款：WAIT_REFUND  默认：NO_APPLY_CANCEL',
   status               varchar(32) comment '订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)',
   is_virtual           varchar(4) default '0' comment '是否为虚拟订单 0-否 1-是',
   ip                   varchar(32) comment 'ip地址',
   type                 varchar(4) not null default '0' comment '订单类型：0-普通订单 1-零元购 2-分期购 3-拼团',
   discount_promotion   decimal(20,3) comment '促销优惠总金额,单位/分',
   discount_coupon_shop decimal(20,3) comment '店铺优惠卷优惠金额,单位/分',
   discount_coupon_platform decimal(20,3) comment '平台优惠卷优惠金额,单位/分',
   discount_fee         decimal(20,3) comment '订单优惠总金额,单位/分',
   shipping_type        varchar(32) comment '配送类型',
   platform_type        varchar(32) not null default 'pc' comment '订单来源平台 电脑-pc 手机网页-wap 移动端-app',
   rate_status          varchar(4) not null default '0' comment '用户评价状态 0-未评价 1-已评价',
   coupon_code          varchar(32) comment '应用优惠卷码',
   group_buy_status     varchar(32) comment '拼团状态 NO_APPLY 不应用拼团 IN_PROCESS拼团中 SUCCESS 成功 FAILED 失败',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   pay_time             timestamp null default NULL comment '支付时间',
   consign_time         timestamp null default NULL comment '发货时间',
   receive_time         timestamp null default NULL comment '确认收货时间',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   timeout_action_time  timestamp null default NULL comment '订单未支付超时过期时间',
   end_time             timestamp null default NULL comment '订单结束时间',
   pay_bill_id          varchar(32) comment '支付流水号',
   primary key (id)
);

alter table platform_order comment '平台订单';

/*==============================================================*/
/* Index: uniq_platformOrder_channelCode_platformOrderCode      */
/*==============================================================*/
create unique index uniq_platformOrder_channelCode_platformOrderCode on platform_order
(
   platform_order_code,
   channel_code
);

/*==============================================================*/
/* Index: idx_platformOrder_platformOrderCode                   */
/*==============================================================*/
create index idx_platformOrder_platformOrderCode on platform_order
(
   platform_order_code
);

/*==============================================================*/
/* Table: property                                              */
/*==============================================================*/
create table property
(
   id                   bigint not null auto_increment comment '主键ID',
   name                 varchar(64) not null comment '属性名称',
   description          varchar(256) comment '属性描述',
   type_code            varchar(16) not null comment '属性类型编码,自然属性和购买属性',
   value_type           varchar(2) not null comment '属性值类型,0-文字,1-图片',
   sort                 int not null comment '排序',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   last_edit_operator   varchar(64) not null comment '最新更新人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table property comment '属性量信息表';

/*==============================================================*/
/* Table: property_value                                        */
/*==============================================================*/
create table property_value
(
   id                   bigint unsigned not null auto_increment,
   property_id          bigint not null,
   value                varchar(128) comment '属性值',
   picture              varchar(256)  comment '图片路径',
   sort                 int not null comment '序号',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table property_value comment '属性值信息表';

/*==============================================================*/
/* Table: purchase_detail                                       */
/*==============================================================*/
create table purchase_detail
(
   id                   bigint unsigned not null auto_increment comment '主键',
   purchase_id          bigint not null comment '采购单id',
   purchase_order_code  varchar(32) not null comment '采购单编号',
   spu_code             varchar(64) comment '商品SPU编号',
   item_name            varchar(200) not null comment '商品名称',
   sku_code             varchar(32) not null comment '商品sku编码',
   brand_id             varchar(32) comment '品牌id',
   category_id          varchar(32) comment '所属分类id',
   all_category         varchar(64) comment '所有分类id',
   purchase_price       bigint comment '进价,单位/分',
   purchasing_quantity  bigint comment '采购数量',
   total_purchase_amount bigint comment '采购总金额,单位/分',
   is_deleted           varchar(2) comment '是否删除:0-否,1-是',
   create_time          timestamp default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   create_operator      varchar(32),
   is_valid             varchar(2) comment '是否有效',
   primary key (id)
);

alter table purchase_detail comment '采购明细信息表';

/*==============================================================*/
/* Table: purchase_group                                        */
/*==============================================================*/
create table purchase_group
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   code                 varchar(32) comment '采购组编码',
   name                 varchar(64) not null comment '采购组名称',
   leader_user_id       varchar(64) not null comment '组长',
   leader_name          varchar(64) not null comment '组长',
   member_user_id       varchar(1024) not null comment '组员',
   member_name          varchar(128) not null comment '组员名称',
   channel_code         varchar(32) comment '渠道编码',
   is_deleted           varchar(2) not null comment '是否删除:0-否,1-是',
   is_valid             varchar(2) not null comment '是否有效:0-无效,1-有效',
   create_operator      varchar(32) comment '创建人',
   remark               varchar(1024),
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table purchase_group comment '采购组信息表';

/*==============================================================*/
/* Index: uniq_purchaseGroup_code                               */
/*==============================================================*/
create unique index uniq_purchaseGroup_code on purchase_group
(
   code
);

/*==============================================================*/
/* Table: purchase_group_user_relation                          */
/*==============================================================*/
create table purchase_group_user_relation
(
   id                   bigint unsigned not null auto_increment comment '主键id',
   purchase_group_code  varchar(32) not null comment '采购组编码',
   user_id              varchar(64) not null comment '用户中心的用户id',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table purchase_group_user_relation comment '采购组与用户关系映射表';

/*==============================================================*/
/* Table: purchase_order                                        */
/*==============================================================*/
create table purchase_order
(
   id                   bigint unsigned not null auto_increment comment '主键',
   purchase_order_code  varchar(32) not null comment '采购单编号',
   channel_id           bigint comment '采购渠道ID',
   channel_code         varchar(32) comment '采购渠道编号',
   supplier_id          bigint comment '供应商ID',
   supplier_code        varchar(32) comment '供应商编号',
   contract_id          bigint comment '采购合同ID',
   contract_code        varchar(32) comment '采购合同编号',
   purchase_type        varchar(32) comment '采购类型编号',
   pay_type             varchar(32) comment '付款方式编号',
   payment_proportion   decimal(5,4) comment '付款比例',
   purchase_group_code  varchar(32) comment '归属采购组编号',
   warehouse_id         varchar(32) comment '收货仓库ID',
   currency_type        varchar(32) comment '币种编号',
   purchase_person_id   varchar(64) comment '归属采购人编号',
   receive_address      varchar(256) comment '收货地址',
   warehouse_code       varchar(32) comment '收货仓库编号',
   transport_fee_dest_id varchar(32) comment '运输费用承担方编号',
   take_goods_no        varchar(32) comment '提运单号',
   requried_receive_date varchar(32) comment '要求到货日期,格式:yyyy-mm-dd',
   end_receive_date     varchar(32) comment '截止到货日期,格式:yyyy-mm-dd',
   handler_priority     varchar(2) comment '处理优先级',
   status               varchar(32) comment '状态:0-暂存,1-提交审核
            ,2-审核通过,3-审核驳回,4-全部收货,5-收货异常,6-冻结,7-作废',
   enter_warehouse_notice varchar(2) comment '入库通知:0-待通知,1-已通知',
   virtual_enter_warehouse varchar(2) comment '虚拟入库:0-待入库,1-已入库',
   remark               varchar(3072) comment '备注',
   total_fee            bigint comment '采购总金额,单位/分',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   abnormal_remark      varchar(1024) comment '入库异常说明',
   primary key (id)
);

alter table purchase_order comment '采购信息表';

/*==============================================================*/
/* Index: uniq_purchaseOrder_purchaseOrderCode                  */
/*==============================================================*/
create unique index uniq_purchaseOrder_purchaseOrderCode on purchase_order
(
   purchase_order_code
);

/*==============================================================*/
/* Table: purchase_order_audit_log                              */
/*==============================================================*/
create table purchase_order_audit_log
(
   id                   bigint not null comment '主键',
   apply_code           varchar(64) not null comment '申请编号',
   purchase_order_code  varchar(32) not null comment '采购单编号',
   operation            varchar(32) not null comment '操作',
   operator             varchar(32) comment '操作者',
   remark               varchar(3072) comment '备注',
   operate_time         timestamp not null default CURRENT_TIMESTAMP comment '操作时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table purchase_order_audit_log comment '审核日志信息表';

/*==============================================================*/
/* Table: request_flow                                          */
/*==============================================================*/
create table request_flow
(
   id                   bigint not null auto_increment,
   requester            varchar(32) comment '请求方',
   responder            varchar(32) comment '响应方',
   type                 varchar(64) comment '请求类型',
   request_num          varchar(64) comment '请求号',
   status               varchar(32) comment '状态',
   request_param        text comment '请求参数',
   response_param       text comment '响应参数',
   count                bigint(20) default 0 comment '执行次数',
   request_time         timestamp comment '请求时间',
   end_time             timestamp null default NULL comment '下次执行时间',
   remark               text comment '说明',
   primary key (id)
);

alter table request_flow comment '请求流水记录表';

/*==============================================================*/
/* Index: uniq_requestFlow_requestNum                           */
/*==============================================================*/
create unique index uniq_requestFlow_requestNum on request_flow
(
   request_num
);

/*==============================================================*/
/* Table: retry_config                                          */
/*==============================================================*/
create table retry_config
(
   id                   bigint(20) not null auto_increment comment '主键',
   type                 varbinary(64) default NULL comment '类型',
   count                bigint(20) default 0 comment '重试次数',
   period               bigint(20) default 0 comment '重试时间 单位/小时',
   create_time          timestamp default CURRENT_TIMESTAMP comment '创建时间',
   primary key (id)
);

alter table retry_config comment '重试机制配置表';

/*==============================================================*/
/* Index: uniq_retryConfig_type                                 */
/*==============================================================*/
create index uniq_retryConfig_type on retry_config
(
   type
);

/*==============================================================*/
/* Table: serial                                                */
/*==============================================================*/
create table serial
(
   id                   bigint not null auto_increment,
   name                 varchar(32) comment '流水号的前缀',
   number               int comment '流水位置',
   update_time          timestamp comment '更新时间',
   primary key (id)
);

alter table serial comment '序列号信息表';

/*==============================================================*/
/* Table: shop_order                                            */
/*==============================================================*/
create table shop_order
(
   id                   bigint not null auto_increment comment '主键',
   shop_order_code      varchar(32) not null comment '店铺订单编码',
   platform_order_code  varchar(32) not null comment '平台订单编码',
   channel_code         varchar(32) not null comment '渠道编码',
   platform_code        varchar(32) not null comment '来源平台编码',
   platform_type        varchar(32) not null default 'pc' comment '订单来源平台 电脑-pc 手机网页-wap 移动端-app',
   shop_id              bigint not null comment '订单所属的店铺id',
   shop_name            varchar(255) not null comment '店铺名称',
   user_id              varchar(64) not null comment '会员id',
   dlytmpl_ids          varchar(255) comment '配送模板ids(1,2,3)',
   status               varchar(32) not null comment '子订单状态 WAIT_BUYER_PAY 已下单等待付款 WAIT_SELLER_SEND-已付款等待发货 WAIT_BUYER_CONFIRM-已发货等待确认收货 WAIT_BUYER_SIGNED-待评价 TRADE_FINISHED-已完成 TRADE_CLOSED_BY_REFUND-已关闭(退款关闭订单) TRADE_CLOSED_BY_CANCEL-已关闭(取消关闭订单)',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   payment              decimal(20,3) comment '实付金额,订单最终总额,单位/分',
   total_fee            decimal(20,3) comment '各子订单中商品price * num的和，不包括任何优惠信息,单位/分',
   postage_fee          decimal(20,3) comment '邮费分摊,单位/分',
   discount_promotion   decimal(20,3) comment '促销优惠总金额,单位/分',
   discount_coupon_shop decimal(20,3) comment '店铺优惠卷分摊总金额,单位/分',
   discount_coupon_platform decimal(20,3) comment '平台优惠卷分摊总金额,单位/分',
   discount_fee         decimal(20,3) comment '促销优惠金额,单位/分',
   title                varchar(128) comment '交易标题',
   buyer_message        varchar(255) comment '买家留言',
   adjust_fee           decimal(20,3) comment '卖家手工调整金额,子订单调整金额之和,单位/分,单位/分',
   item_num             int comment '子订单商品购买数量总数',
   shop_memo            longtext comment '卖家备注',
   total_weight         decimal(20,3) comment '商品重量,单位/克',
   trade_memo           longtext comment '交易备注',
   rate_status          varchar(4) default '0' comment '评价状态',
   is_part_consign      varchar(4) comment '是否是多次发货的订单',
   group_buy_status     varchar(32) comment '拼团状态',
   total_tax            decimal(20,3) comment '订单总税费,单位/分',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   pay_time             timestamp null default NULL comment '支付时间',
   consign_time         timestamp null default NULL comment '发货时间',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
   primary key (id)
);

alter table shop_order comment '店铺订单信息表';

/*===================================================================*/
/* Index: uniq_shopOrder_channelCode_platformOrderCode_shopOrderCode */
/*===================================================================*/
create unique index uniq_shopOrder_channelCode_platformOrderCode_shopOrderCode on shop_order
(
   shop_order_code,
   platform_order_code,
   channel_code
);

/*==============================================================*/
/* Index: idx_shopOrder_platformOrderCode                       */
/*==============================================================*/
create index idx_shopOrder_platformOrderCode on shop_order
(
   platform_order_code
);

/*==============================================================*/
/* Table: sku_relation                                          */
/*==============================================================*/
create table sku_relation
(
   id                   bigint(20) not null auto_increment,
   spu_code             varchar(32) comment '商品编码',
   sku_code             varchar(32) comment '供应链sku编码',
   supplier_sku_code    varchar(32) comment '供应商skuCode',
   supplier_code        varchar(32) comment '供应商编码',
   channel_sku_code     varchar(32) comment '渠道方skuCode',
   channel_code         varchar(32) comment '渠道方编码',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table sku_relation comment 'sku关联关系表';

/*==============================================================*/
/* Table: sku_stock                                             */
/*==============================================================*/
create table sku_stock
(
   id                   bigint unsigned not null auto_increment comment '主键',
   spu_code             varchar(64) not null comment '商品SPU编号',
   sku_code             varchar(32) not null comment 'sku编码',
   supplier_id          bigint comment '供应商id',
   supplier_code        varchar(32) comment '供应商编号',
   channel_id           bigint comment '渠道ID',
   channel_code         varchar(32) comment '渠道编码',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_code       varchar(32) not null comment '仓库编号',
   warehouse_item_id    varchar(32) not null comment '仓库对应itemId',
   available_inventory  bigint not null comment '可用库存',
   frozen_inventory     bigint not null comment '冻结库存',
   real_inventory       bigint not null comment '真实库存',
   defective_inventory  bigint not null comment '残次品库存',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table sku_stock comment 'sku库存';

/*==============================================================*/
/* Table: skus                                                  */
/*==============================================================*/
create table skus
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   sku_code             varchar(32) not null comment '商品SKU编号',
   item_id              bigint not null comment '商品ID',
   spu_code             varchar(32) not null comment '商品SPU编号',
   property_value_id    varchar(64) comment '属性值id：格式为123;223;212;',
   property_value       varchar(128) comment '属性值：格式为红色；XL；100cm；',
   bar_code             varchar(64) not null comment '条形码',
   weight               bigint comment '重量,单位/g',
   market_price         bigint comment '参考市场价,单位/分',
   predict_channel_price bigint comment '预计平台售价,单位/分',
   picture              varchar(1024) comment '图片,多个图片路径用逗号分隔',
   channel1_pre_sell_prices bigint comment '预计泰然易购渠道售价',
   channel2_pre_sell_prices bigint comment '预计小泰乐活渠道售价',
   channel3_pre_sell_prices bigint,
   channel4_pre_sell_prices bigint,
   channel5_pre_sell_prices bigint,
   channel6_pre_sell_prices bigint,
   channel7_pre_sell_prices bigint,
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) not null comment '是否启用：0-否,1-是',
   primary key (id)
);

alter table skus comment '商品sku信息';

/*==============================================================*/
/* Index: uniq_skus_skuCode                                     */
/*==============================================================*/
create unique index uniq_skus_skuCode on skus
(
   sku_code
);

/*==============================================================*/
/* Table: supplier                                              */
/*==============================================================*/
create table supplier
(
   id                   bigint unsigned not null auto_increment comment '供应商id',
   supplier_code        varchar(32) not null comment '供应链编号',
   supplier_name        varchar(64) not null comment '供应商名称',
   supplier_kind_code   varchar(32) not null comment '供应商性质编号',
   supplier_type_code   varchar(32) not null comment '供应商类型编号',
   supplier_interface_id varchar(32) comment '供应商接口ID',
   contact              varchar(64) not null comment '供应商联系人',
   phone                varchar(16) not null comment '联系人电话',
   mobile               varchar(16) comment '联系人手机',
   weixin               varchar(32) comment '微信',
   qq                   varchar(32) comment 'QQ',
   dingding             varchar(32) comment '钉钉',
   country              varchar(32) comment '所在国家',
   province             varchar(32) comment '所在省',
   city                 varchar(32) comment '所在城市',
   area                 varchar(32) comment '所在区',
   address              varchar(256) not null comment '详细地址',
   certificate_type_id  varchar(32) comment '证件类型编号',
   remark               varchar(1024) comment '备注',
   is_valid             varchar(2) not null default '1' comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_operator      varchar(64) not null comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table supplier comment '供应商信息表';

/*==============================================================*/
/* Index: uniq_supplier_supplierCode                            */
/*==============================================================*/
create unique index uniq_supplier_supplierCode on supplier
(
   supplier_code
);

/*==============================================================*/
/* Table: supplier_after_sale_info                              */
/*==============================================================*/
create table supplier_after_sale_info
(
   id                   bigint not null auto_increment comment '主键ID',
   supplier_id          bigint not null comment '供应商编号',
   supplier_code        varchar(32) not null comment '供应链编号',
   goods_return_address varchar(256) not null comment '退货地址',
   goods_return_contact_person varchar(32) not null comment '退货联系人',
   goods_return_phone   varchar(32) not null comment '退货联系电话',
   goods_return_strategy varchar(3072) not null comment '退货策略',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table supplier_after_sale_info comment '供应商售后信息';

/*==============================================================*/
/* Table: supplier_brand                                        */
/*==============================================================*/
create table supplier_brand
(
   id                   bigint not null auto_increment comment '主键ID',
   supplier_id          bigint not null comment '供应商编号',
   supplier_code        varchar(32) not null comment '供应链编号',
   brand_id             bigint not null comment '品牌编号',
   brand_code           varchar(32),
   category_id          bigint not null comment '所属类目ID',
   category_code        varchar(32) comment '所属类目编号',
   proxy_aptitude_id    varchar(32) not null comment '代理资质编号',
   proxy_aptitude_start_date varchar(20) comment '资质有效期开始日期',
   proxy_aptitude_end_date varchar(20) comment '资质有效期截止日期',
   aptitude_pic         varchar(256) comment '资质证明图片，多个路径用逗号分隔',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table supplier_brand comment '供应商代理品牌';

/*==================================================================*/
/* Index: uniq_supplierCategory_supplierCode_categoryCode_brandCode */
/*==================================================================*/
create unique index uniq_supplierCategory_supplierCode_categoryCode_brandCode on supplier_brand
(
   supplier_code,
   brand_code,
   category_code
);

/*==============================================================*/
/* Table: supplier_category                                     */
/*==============================================================*/
create table supplier_category
(
   id                   bigint unsigned not null auto_increment comment '主键',
   supplier_id          bigint not null,
   supplier_code        varchar(32) not null comment '供应链编号',
   category_id          bigint not null,
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   is_valid             varchar(2) comment '是否有效:0-无效,1-有效',
   primary key (id)
);

alter table supplier_category comment '供应商代理类目';

/*==============================================================*/
/* Index: uniq_supplierCategory_supplierCode_categoryId         */
/*==============================================================*/
create unique index uniq_supplierCategory_supplierCode_categoryId on supplier_category
(
   category_id,
   supplier_code
);

/*==============================================================*/
/* Table: supplier_channel_relation                             */
/*==============================================================*/
create table supplier_channel_relation
(
   id                   bigint unsigned not null auto_increment comment '主键',
   supplier_id          bigint not null comment '供应商id',
   channel_id           bigint not null comment '渠道id',
   supplier_code        varchar(32) not null comment '供应商编号',
   channel_code         varchar(32) not null comment '渠道编号',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table supplier_channel_relation comment '供应商渠道关系表';

/*==============================================================*/
/* Table: supplier_financial_info                               */
/*==============================================================*/
create table supplier_financial_info
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   supplier_id          bigint not null comment '供应商id',
   supplier_code        varchar(32) not null comment '供应商编号',
   deposit_bank         varchar(128) not null comment '开户银行',
   bank_account         varchar(32) not null comment '银行账号',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table supplier_financial_info comment '供应商财务信息';

/*==============================================================*/
/* Table: supplier_order_info                                   */
/*==============================================================*/
create table supplier_order_info
(
   id                   bigint unsigned not null auto_increment comment '主键',
   warehouse_order_code varchar(32) not null comment '店铺订单编码',
   supplier_order_code  varchar(64) comment '供应商订单编码',
   supplier_code        varchar(32) not null comment '供应商编码',
   status               varchar(32) not null comment '供应商订单下单返回状态码,200表示下单成功，其他的表示失败',
   supplier_order_status varchar(2) not null comment '供应商订单状态:1-代发送:2-已发送,3-代发货,4-已发货,5-下单失败',
   skus                 text comment '供应商订单sku信息,JSONArray字符串',
   message              varchar(1024) comment '下单结果信息',
   jd_city_code         varchar(20) comment '京东地址市编码',
   jd_district_code     varchar(20) comment '京东地址区编码',
   jd_province_code     varchar(20) comment '京东地址省编码',
   jd_town_code         varchar(20) comment '京东地址镇编码',
   jd_city              varchar(20) comment '京东地址市',
   jd_district          varchar(20) comment '京东地址区',
   jd_province          varchar(20) comment '京东地址省',
   jd_town              varchar(20) comment '京东地址镇',
   logistics_status     varchar(2) comment '物流状态：0-未完成,1-已完成',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
   primary key (id)
);

alter table supplier_order_info comment '供应商订单信息表';

/*====================================================================*/
/* Index: uniq_supplierOrderInfo_warehouseOrderCode_supplierOrderCode */
/*====================================================================*/
create unique index uniq_supplierOrderInfo_warehouseOrderCode_supplierOrderCode on supplier_order_info
(
   warehouse_order_code,
   supplier_order_code
);

/*==============================================================*/
/* Table: supplier_order_logistics                              */
/*==============================================================*/
create table supplier_order_logistics
(
   id                   bigint unsigned not null auto_increment comment '主键',
   warehouse_order_code varchar(32) not null comment '店铺订单编码',
   supplier_order_code  varchar(64) not null comment '供应商订单编码',
   type                 varchar(2) comment '信息类型:0-物流单号,1-配送信息',
   supplier_parent_order_code varchar(64) comment '供应商父订单编码',
   supplier_code        varchar(32) not null comment '供应商编码',
   logistics_corporation varchar(128) comment '物流公司,当字段type=0-物流信息时不为空',
   waybill_number       varchar(64) comment '运单号,当字段type=0-物流信息时不为空',
   `skus` text COMMENT '供应商订单sku信息,JSONArray字符串',
   logistics_info       text comment '物流信息,当字段type=1-配送信息时不为空',
   logistics_status     varchar(2) comment '物流状态,0-新建,1-妥投,2-拒收,当字段type=1-配送信息时不为空',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
   primary key (id)
);

alter table supplier_order_logistics comment '供应商订单物流信息表';

/*==============================================================*/
/* Table: system_config                                         */
/*==============================================================*/
create table system_config
(
   id                   bigint unsigned not null auto_increment comment '主键ID',
   code                 varchar(32) comment '编码',
   name                 varchar(32) comment '名称',
   type                 varchar(32) comment '类型',
   content              varchar(256) comment '内容',
   description          varchar(256) comment '描述',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table system_config comment '系统配置信息表';

/*==============================================================*/
/* Table: time_record                                           */
/*==============================================================*/
create table time_record
(
   id                   bigint not null auto_increment comment '主键',
   method               varchar(64) default NULL comment '方法名',
   use_time             varchar(32) comment '耗时(ms)',
   start_time           timestamp default CURRENT_TIMESTAMP comment '结束时间,格式yyyy-mm-dd hh:mi:ss',
   end_time             timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '结束时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table time_record comment '记录时间表';

/*==============================================================*/
/* Index: idx_supplyItemsJdComment_supplySku                    */
/*==============================================================*/
create index idx_supplyItemsJdComment_supplySku on time_record
(
   method
);

/*==============================================================*/
/* Table: warehouse                                             */
/*==============================================================*/
create table warehouse
(
   id                   bigint unsigned not null auto_increment comment '仓库编号',
   code                 varchar(32) not null comment '仓库编号',
   name                 varchar(64) not null comment '仓库名称',
   warehouse_type_code  varchar(32) not null comment '仓库类型编码',
   is_customs_clearance tinyint comment '是否支持清关,在选择保税仓时需要选择  0-不支持 1-支持',
   province             varchar(32) comment '所在省',
   city                 varchar(32) comment '所在市',
   area                 varchar(32) comment '所在区、县',
   address              varchar(256) not null comment '详细地址',
   is_valid             varchar(2) not null comment '是否有效:0-无效,1-有效',
   is_deleted           varchar(2) comment '是否删除:0-否,1-是',
   create_operator      varchar(32) comment '创建人',
   remark               varchar(1024) comment '备注',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table warehouse comment '仓库';

/*==============================================================*/
/* Index: uniq_warehouse_code                                   */
/*==============================================================*/
create unique index uniq_warehouse_code on warehouse
(
   code
);

/*==============================================================*/
/* Table: warehouse_item                                        */
/*==============================================================*/
create table warehouse_item
(
   id                   bigint unsigned not null auto_increment comment '主键',
   sku_code             varchar(32) not null comment 'sku编码',
   supplier_id          bigint comment '供应商id',
   supplier_code        varchar(32) comment '供应商编号',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_code       varchar(32) not null comment '仓库编号',
   warehouse_item_id    varchar(32) not null comment '仓库对应itemId',
   quality_inventory    bigint comment '正品库存(ZP)',
   defective_inventory  bigint comment '残次品库存(CC)',
   js_inventory         bigint comment '机损库存(JS)',
   xs_inventory         bigint comment '箱损库存(XS)',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '最后更新时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table warehouse_item comment '仓库商品信息表';

/*==============================================================*/
/* Table: warehouse_notice                                      */
/*==============================================================*/
create table warehouse_notice
(
   id                   bigint not null auto_increment comment '主键',
   warehouse_notice_code varchar(32) not null comment '入库通知单编号',
   purchase_order_code  varchar(32) not null comment '采购单编号',
   contract_code        varchar(32) comment '采购合同编号',
   purchase_group_code  varchar(32) not null comment '归属采购组编号',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_code       varchar(32) not null comment '仓库编号',
   status               tinyint not null comment '状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
   supplier_id          bigint comment '供应商id',
   supplier_code        varchar(32) comment '供应商编号',
   purchase_type        varchar(32) not null comment '采购类型编号',
   purchase_person_id   varchar(32) not null comment '归属采购人编号',
   take_goods_no        varchar(32) comment '提运单号',
   requried_receive_date varchar(32) not null comment '要求到货日期,格式:yyyy-mm-dd',
   end_receive_date     varchar(32) not null comment '截止到货日期,格式:yyyy-mm-dd',
   remark               varchar(1024) comment '备注',
   create_operator      varchar(32) comment '创建人',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
);

alter table warehouse_notice comment '入库通知单信息';

/*==============================================================*/
/* Index: uniq_warehouseNotice_warehouseNoticeCode              */
/*==============================================================*/
create unique index uniq_warehouseNotice_warehouseNoticeCode on warehouse_notice
(
   warehouse_notice_code
);

/*==============================================================*/
/* Table: warehouse_notice_callback                             */
/*==============================================================*/
create table warehouse_notice_callback
(
   id                   bigint unsigned not null auto_increment comment '主键',
   request_code         varchar(32) comment '请求编号，幂等去重',
   warehouse_code       varchar(32) not null comment '仓库编号',
   warehouse_notice_code varchar(32) comment '入库单编号',
   request_params       longtext comment 'json格式',
   state                tinyint not null comment '状态:1-初始状态,2-处理成功,3-处理失败',
   request_time         timestamp comment '请求时间,格式yyyy-mm-dd hh:mi:ss',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table warehouse_notice_callback comment '入库单确认流水';

/*==============================================================*/
/* Table: warehouse_notice_details                              */
/*==============================================================*/
create table warehouse_notice_details
(
   id                   bigint not null auto_increment comment '主键',
   warehouse_notice_code varchar(32) not null comment '入库通知单编号',
   sku_name             varchar(64) comment '商品名称',
   sku_code             varchar(32) comment 'sku编码',
   brand_id             bigint comment '品牌',
   category_id          bigint comment '分类',
   purchase_price       bigint comment '采购价,单位/分',
   purchasing_quantity  bigint comment '采购数量',
   actual_storage_quantity bigint comment '实际入库数量',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   storage_time         timestamp comment '入库时间,格式yyyy-mm-dd hh:mi:ss',
   primary key (id)
);

alter table warehouse_notice_details comment '入库通知单明细信息表';

/*==============================================================*/
/* Table: warehouse_order                                       */
/*==============================================================*/
create table warehouse_order
(
   id                   bigint unsigned not null auto_increment comment '主键',
   warehouse_order_code varchar(32) not null comment '店铺订单编码',
   shop_id              bigint not null comment '订单所属的店铺id',
   shop_name            varchar(255) not null comment '店铺名称',
   supplier_code        varchar(32) not null comment '供应链编号',
   supplier_name        varchar(64) not null comment '供应商名称',
   shop_order_code      varchar(32) not null comment '店铺订单编码',
   platform_order_code  varchar(32) not null comment '平台订单编码',
   channel_code         varchar(32) not null comment '渠道编码',
   platform_code        varchar(32) not null comment '来源平台编码',
   platform_type        varchar(32) not null default 'pc' comment '订单来源平台 电脑-pc 手机网页-wap 移动端-app',
   warehouse_id         bigint comment '所在仓库id',
   warehouse_name       varchar(64) comment '所在仓库名称',
   user_id              varchar(64) not null comment '会员id',
   status               varchar(4) not null comment '订单状态(自采):1-待出库 2-部分出库 3-全部出库
            产品一期原型的状态
            (0.发送中 1.待仓库反馈 2.全部发货 3.部分发货 4. 已取消)',
   supplier_order_status varchar(2) comment '供应商订单状态(代发):1-待发送,2-已发送,3-代发货,4-已发货,5-下单失败',
   order_type           varchar(2) comment '订单类型:0-自采订单,1-一件代发订单',
   items_num            int comment '商品总数量',
   adjust_fee           decimal(20,3) comment '卖家手工调整金额,子订单调整金额之和,单位/分,单位/分',
   postage_fee          decimal(20,3) comment '邮费分摊,单位/分',
   discount_promotion   decimal(20,3) comment '促销优惠总金额,单位/分',
   discount_coupon_shop decimal(20,3) comment '店铺优惠卷分摊总金额,单位/分',
   discount_coupon_platform decimal(20,3) comment '平台优惠卷分摊总金额,单位/分',
   discount_fee         decimal(20,3) comment '促销优惠金额,单位/分',
   total_fee            decimal(20,3) comment '各子订单中商品price * num的和，不包括任何优惠信息,单位/分',
   payment              decimal(20,3) comment '实付金额,订单最终总额,单位/分',
   is_deleted           varchar(2) not null default '0' comment '是否删除:0-否,1-是',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间,格式yyyy-mm-dd hh:mi:ss',
   update_time          timestamp not null default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
   consign_time         timestamp null default NULL comment '发货时间',
   primary key (id)
);

alter table warehouse_order comment '仓库级订单';

/*=========================================================================*/
/* Index: uniq_warehouseOrder_channelCode_platformOrderCode_shopOrderCode_ */
/*=========================================================================*/
create unique index uniq_warehouseOrder_channelCode_platformOrderCode_shopOrderCode_ on warehouse_order
(
   warehouse_order_code,
   shop_order_code,
   platform_order_code,
   channel_code
);

/*==============================================================*/
/* Index: idx_warehouseOrder_platformOrderCode                  */
/*==============================================================*/
create index idx_warehouseOrder_platformOrderCode on warehouse_order
(
   platform_order_code
);

/*==============================================================*/
/* Index: idx_warehouseOrder_shopOrderCode                      */
/*==============================================================*/
create index idx_warehouseOrder_shopOrderCode on warehouse_order
(
   shop_order_code
);

/*==============================================================*/
/* Index: idx_warehouseOrder_warehouseOrderCode                 */
/*==============================================================*/
create index idx_warehouseOrder_warehouseOrderCode on warehouse_order
(
   warehouse_order_code
);

/*==============================================================*/
/* Table: method_info                                           */
/*==============================================================*/
create table method_info
(
   id                   bigint(20) not null auto_increment comment '主键id',
   class_name           varchar(64) not null,
   method_name          varchar(64) not null,
   args                 varchar(512) default NULL comment '调用参数',
   total_time           bigint(20) unsigned default 0,
   use_number           bigint(20) not null,
   frequency            bigint(20) default 0 comment '调用的频率单位(次/天)\r\n计算方法：总的次数/总的天数\r\n公式：use_number/(now_time-create_time)\r\n',
   average_time         bigint(20) default 0 comment '平均时长（毫秒/次）\r\n计算方法: 总的时长/总的次\r\n公式：total_name/use_number',
   create_time          timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
   primary key (id),
   unique key method (class_name, method_name)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='方法的信息以及方法的调用信息统计';

/*==============================================================*/
/* Table: method_long_time                                      */
/*==============================================================*/
create table method_long_time
(
   id                   bigint(20) not null auto_increment,
   method_id            bigint(20) not null,
   duration             bigint(20) not null comment '耗时',
   create_time          timestamp not null default CURRENT_TIMESTAMP,
   primary key (id)
)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='单次调用的时间和时长';
