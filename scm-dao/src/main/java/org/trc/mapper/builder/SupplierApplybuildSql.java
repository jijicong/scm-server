package org.trc.mapper.builder;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
public class SupplierApplybuildSql {
        //你要查询主表的名称
        final String fromTable = "apply_for_supplier a";

        //动态拼写你要写的sql语句
        public String querySupplierApplyList(Map<String,Object> map){
            String fileds = "a.id as 'id',a.apply_code as 'applyCode',b.supplier_name as 'supplierName'," +
                    "a.supplier_code as 'supplierCode',b.supplier_kind_code as 'supplierKindCode',a.status as 'status',a.create_time as 'createTime'";
            String leftJoin="supplier b on a.supplier_id=b.id";
            String where="a.status=1";
            String orderBy = "a.create_time desc";
            if(map.get("supplierName")!=null){
                where +="and b.supplier_name Like concat('%',#{supplierName},'%')";
            }
            if(map.get("supplierCode")!=null){
                where +="and a.supplier_code=#{supplierCode}";
            }
            if(map.get("contact")!=null){
                where +="and b.contact=#{contact}";
            }
            if(map.get("status")!=null){
                where +="and a.status=#{status}";
            }
            if(map.get("startTime")!=null){
                where +="and a.create_time>#{startTime}";
            }
            if(map.get("endTime")!=null){
                where +="and b.create_time<#{endTime}";
            }
            SQL sql = new SQL();
            sql.LEFT_OUTER_JOIN(leftJoin);
            sql.WHERE(where);
            sql.FROM(fromTable);
            sql.SELECT(fileds);
            return sql.toString();
        }

        //统计supplierApply总共有多少记录
        public String queryCountSupplierApply(Map<String,Object> map){
            String fileds="count(0)";
            String where="1=1";
            String leftJoin="supplier b on a.supplier_id=b.id";
            if(map.get("supplierName")!=null){
                where +="and b.supplier_name Like concat('%',#{supplierName},'%')";
            }
            if(map.get("supplierCode")!=null){
                where +="and a.supplier_code=#{supplierCode}";
            }
            if(map.get("contact")!=null){
                where +="and b.contact=#{contact}";
            }
            if(map.get("status")!=null){
                where +="and a.status=#{status}";
            }
            if(map.get("startTime")!=null){
                where +="and a.create_time>#{startTime}";
            }
            if(map.get("endTime")!=null){
                where +="and b.create_time<#{endTime}";
            }
            SQL sql = new SQL();
            sql.LEFT_OUTER_JOIN(leftJoin);
            sql.WHERE(where);
            sql.FROM(fromTable);
            sql.SELECT(fileds);
            return sql.toString();
        }
}
