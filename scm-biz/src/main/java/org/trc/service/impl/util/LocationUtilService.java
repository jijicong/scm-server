package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.util.AreaTreeNode;
import org.trc.domain.util.area;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.impl.BaseService;

import java.util.*;
import java.util.List;

/**
 * Created by sone on 2017/5/6.
 */
@Service
public class LocationUtilService extends BaseService<area,Long> implements ILocationUtilService {

    public List<AreaTreeNode> getTreeNodeFromLocation(){
         //1.获得location
        area area =new area();
        area.setId(1L);
        area = super.selectOne(area);
        //2.设置顶级父类的ID和TEXT
        AreaTreeNode node=new AreaTreeNode();
        node.setId(area.getCode());
        if(area.getProvince()!=null){
            node.setText(area.getProvince());
        }
        //3.设置省 (new area(area.getId()))
        List<area> provinceAreaList = super.select(new area(area.getId()));

        List<AreaTreeNode> areaTreeNodeProvinceList =new ArrayList<AreaTreeNode>();//用于存放子集的节点

        Map<String,AreaTreeNode> treeNodeProvincesMap=new HashMap<>();
        /**
         * 1.遍历provinceLocation
         * 2.创建treeNode对象加入到treeNodeProvinces中
         * 3.用map保存省节点--key==id==code(String)  value==AreaTreeNode
         */
        for (area area1 : provinceAreaList) {
            AreaTreeNode areaTreeNode =new AreaTreeNode();
            areaTreeNode.setId(area1.getCode());
            if(area1.getProvince()!=null){
                areaTreeNode.setText(area1.getProvince());
            }
            areaTreeNodeProvinceList.add(areaTreeNode); //加入节点
            treeNodeProvincesMap.put(areaTreeNode.getId(), areaTreeNode);//map储存
        }
        node.setChildren(areaTreeNodeProvinceList);//设置子节点

        List<area> allAreaCityList =new ArrayList<area>();//用于存放城市（all）
        /**
         *为省节点，添加市节点
         */
        for (area area1 : provinceAreaList) {
            List<area> cityAreaList = super.select(new area(area1.getId()));//某省下的所有的城市

            List<AreaTreeNode> areaTreeNodeCityList =new ArrayList<AreaTreeNode>();//用于存放子集的节点
            //省节点
            AreaTreeNode provinceAreaTreeNode = treeNodeProvincesMap.get(area1.getCode());

            provinceAreaTreeNode.setChildren(areaTreeNodeCityList);//设置子节点

            for (area area2 : cityAreaList) {
                allAreaCityList.add(area2);

                AreaTreeNode areaTreeNode =new AreaTreeNode();
                areaTreeNode.setId(area2.getCode());
                if(area.getCity()!=null){
                    areaTreeNode.setText(area2.getCity());
                }
                areaTreeNodeCityList.add(areaTreeNode); //加入节点
                treeNodeProvincesMap.put(areaTreeNode.getId(), areaTreeNode);//map储存
            }
        }
        /**
         * 把所有的地区放到城市中
         */
        for (area area1 : allAreaCityList) {
            //市节点
            AreaTreeNode cityAreaTreeNode = treeNodeProvincesMap.get(area1.getCode());
            /**
             * 直辖市ID
             * 1.北京  2
             * 2.天津  19
             * 3.上海市 857
             */
            Long parentId=area1.getParent();
            if(parentId==2L || parentId==19L || parentId==857L){
                List<AreaTreeNode> singleList=new ArrayList<AreaTreeNode>(9);//用于存放子集的节点
                AreaTreeNode areaTreeNode =new AreaTreeNode();
                areaTreeNode.setId(area1.getCode());
                areaTreeNode.setText(area1.getDistrict());
                areaTreeNode.setIsleaf(true);
                singleList.add(areaTreeNode);
                cityAreaTreeNode.setChildren(singleList);
                continue;
            }

            List<area> districtAreaList = super.select(new area(area1.getId()));//某市下的所有的地区

            List<AreaTreeNode> areaTreeNodeDistrictList =new ArrayList<AreaTreeNode>();//用于存放子集的节点
            /**
             *市添加地区节点
             */
            for (area area2 : districtAreaList) {
                AreaTreeNode areaTreeNode =new AreaTreeNode();
                areaTreeNode.setId(area2.getCode());
                areaTreeNode.setIsleaf(true);
                if(area2.getDistrict()!=null){
                    areaTreeNode.setText(area2.getDistrict());
                }
                areaTreeNodeDistrictList.add(areaTreeNode); //加入节点
            }
            cityAreaTreeNode.setChildren(areaTreeNodeDistrictList);//设置子节点
        }
        List<AreaTreeNode> nodeList = node.getChildren();
        return  nodeList;
    }
}
