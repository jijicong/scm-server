package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.util.area;
import org.trc.domain.util.TreeNode;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.impl.BaseService;

import java.util.*;
import java.util.List;

/**
 * Created by hzdaa on 2017/5/6.
 */
@Service
public class LocationUtilService extends BaseService<area,Long> implements ILocationUtilService {

    public TreeNode getTreeNodeFromLocation(){
         //1.获得location
        area area =new area();
        area.setId(1L);
        area = super.selectOne(area);
        //2.设置顶级父类的ID和TEXT
        TreeNode node=new TreeNode();
        node.setId(area.getCode());
        if(area.getProvince()!=null){
            node.setText(area.getProvince());
        }
        //3.设置省 (new area(area.getId()))
        List<area> provinceAreaList = super.select(new area(area.getId()));

        List<TreeNode> treeNodeProvinceList=new ArrayList<TreeNode>();//用于存放子集的节点

        Map<String,TreeNode> treeNodeProvincesMap=new HashMap<>();
        /**
         * 1.遍历provinceLocation
         * 2.创建treeNode对象加入到treeNodeProvinces中
         * 3.用map保存省节点--key==id==code(String)  value==TreeNode
         */
        for (area area1 : provinceAreaList) {
            TreeNode treeNode=new TreeNode();
            treeNode.setId(area1.getCode());
            if(area1.getProvince()!=null){
                treeNode.setText(area1.getProvince());
            }
            treeNodeProvinceList.add(treeNode); //加入节点
            treeNodeProvincesMap.put(treeNode.getId(),treeNode);//map储存
        }
        node.setChildren(treeNodeProvinceList);//设置子节点

        List<area> allAreaCityList =new ArrayList<area>();//用于存放城市（all）
        /**
         *为省节点，添加市节点
         */
        for (area area1 : provinceAreaList) {
            List<area> cityAreaList = super.select(new area(area1.getId()));//某省下的所有的城市

            List<TreeNode> treeNodeCityList=new ArrayList<TreeNode>();//用于存放子集的节点
            //省节点
            TreeNode provinceTreeNode = treeNodeProvincesMap.get(area1.getCode());

            provinceTreeNode.setChildren(treeNodeCityList);//设置子节点

            for (area area2 : cityAreaList) {
                allAreaCityList.add(area2);

                TreeNode treeNode=new TreeNode();
                treeNode.setId(area2.getCode());
                if(area.getCity()!=null){
                    treeNode.setText(area2.getCity());
                }
                treeNodeCityList.add(treeNode); //加入节点
                treeNodeProvincesMap.put(treeNode.getId(),treeNode);//map储存
            }
        }
        /**
         * 把所有的地区放到城市中
         */
        for (area area1 : allAreaCityList) {
            //市节点
            TreeNode cityTreeNode = treeNodeProvincesMap.get(area1.getCode());
            /**
             * 直辖市ID
             * 1.北京  2
             * 2.天津  19
             * 3.上海市 857
             */
            Long parentId=area1.getParent();
            if(parentId==2L || parentId==19L || parentId==857L){
                List<TreeNode> singleList=new ArrayList<TreeNode>(9);//用于存放子集的节点
                TreeNode treeNode=new TreeNode();
                treeNode.setId(area1.getCode());
                treeNode.setText(area1.getDistrict());
                singleList.add(treeNode);
                cityTreeNode.setChildren(singleList);
                continue;
            }

            List<area> districtAreaList = super.select(new area(area1.getId()));//某市下的所有的地区

            List<TreeNode> treeNodeDistrictList=new ArrayList<TreeNode>();//用于存放子集的节点
            /**
             *市添加地区节点
             */
            for (area area2 : districtAreaList) {
                TreeNode treeNode=new TreeNode();
                treeNode.setId(area2.getCode());
                if(area2.getDistrict()!=null){
                    treeNode.setText(area2.getDistrict());
                }
                treeNodeDistrictList.add(treeNode); //加入节点
            }
            cityTreeNode.setChildren(treeNodeDistrictList);//设置子节点
        }
        return  node;
    }
}
