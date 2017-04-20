drop table if exists group_resource_relationship;

drop table if exists resource;

drop table if exists resource_column;

drop table if exists resource_group;

/*==============================================================*/
/* Table: group_resource_relationship                           */
/*==============================================================*/
create table group_resource_relationship
(
   id                   bigint not null comment '主键',
   resource_action      varchar(16) not null comment '资源编码',
   source_sys           varchar(16) not null comment '系统编码',
   group_id             bigint not null comment '资源组id',
   created_by           varchar(64) not null comment '创建者',
   create_time          timestamp not null comment '创建时间',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源与资源组关系表';

/*==============================================================*/
/* Table: resource                                              */
/*==============================================================*/
create table resource
(
   id                   bigint not null auto_increment comment '主键',
   resource_column_id   bigint not null comment '资源栏目id',
   resource_code        varchar(16) not null comment '资源编码',
   resource_name        varchar(64) not null comment '资源名称',
   resource_url         varchar(128) not null comment '资源路径',
   access_method        varchar(12) not null comment '访问方法(GET,POST,PUT,DELETE)',
   source_sys           varchar(16) not null comment '系统编码',
   view_mode            varchar(16) not null comment '是否可见',
   created_by           varchar(64) not null comment '创建者',
   create_time          timestamp not null comment '创建时间',
   primary key (id),
   UNIQUE key AK_method_url_key (resource_url, access_method)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源表';

/*==============================================================*/
/* Table: resource_column                                       */
/*==============================================================*/
create table resource_column
(
   id                   bigint not null comment '主键',
   column_name          varchar(64) not null comment '栏目名称',
   created_by           varchar(64) not null comment '创建者',
   create_time          timestamp not null comment '创建时间',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源栏';

/*==============================================================*/
/* Table: resource_group                                        */
/*==============================================================*/
create table resource_group
(
   id                   bigint not null comment '主键',
   group_name           varchar(32) not null comment '组名',
   group_info           varchar(128) comment '组说明',
   created_by           varchar(64) not null comment '创建者',
   create_time          timestamp not null comment '创建时间',
   primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='资源组';
