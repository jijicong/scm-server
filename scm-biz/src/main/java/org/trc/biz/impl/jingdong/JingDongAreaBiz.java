package org.trc.biz.impl.jingdong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.jingdong.IJingDongAreaBiz;
import org.trc.domain.util.AreaTreeNode;
import org.trc.domain.util.JingDongArea;
import org.trc.domain.util.JingDongAreaTreeNode;
import org.trc.service.util.IJingdongAreaService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/19.
 */
@Service("jingDongAreaBiz")
public class JingDongAreaBiz implements IJingDongAreaBiz {

    private Logger log = LoggerFactory.getLogger(JingDongAreaBiz.class);

    @Resource
    private IJingdongAreaService iJingdongAreaService;

    /*
    1.放缓存，
    2.每天定时刷新
     */
    public List<JingDongAreaTreeNode>  getJingDongAreaTree() throws Exception {

        return recursion1();

    }

    private  List<JingDongAreaTreeNode> recursion1 (){
        /*
        1.查询所有的省
        SELECT * from mapping_table GROUP BY province
        2.遍历省，拿到省下面所有的市节点
        SELECT * from mapping_table  WHERE province ='山东' GROUP BY city
        3.遍历市，拿到所有的区节点
        SELECT * from mapping_table  WHERE city ='东营市' GROUP BY district
        4.遍历区，拿到所有的镇节点
        SELECT * from mapping_table  WHERE district ='东营区'

        {[ id: 1,text:'山东',isleaf:false,areaCode:null,jdCode:13,children:[
        [id:2,text:'东营市'，islef:false,areaCode:null,jdCode:1090,children:[***],[***]]
        ]]***}
         */
        List<JingDongAreaTreeNode> jingDongAreaTreeNodes = new ArrayList<>();
        Map<String,Object> mapOut = new HashMap<>();
        mapOut.put("group","province");
        mapOut.put("where",null);
        mapOut.put("name",null);
        List<JingDongArea> provinceList = iJingdongAreaService.selectAreaByName(mapOut); //省
        for (JingDongArea  provinceArea: provinceList){
            JingDongAreaTreeNode  jingDongAreaTreeNode = new JingDongAreaTreeNode();
            jingDongAreaTreeNode.setId(provinceArea.getId());
            jingDongAreaTreeNode.setAreaCode(provinceArea.getAreaCode());
            jingDongAreaTreeNode.setIsleaf(false);
            jingDongAreaTreeNode.setJdCode(provinceArea.getJdCode());
            jingDongAreaTreeNode.setText(provinceArea.getProvince());

            Map<String,Object> map = new HashMap<>();
            map.put("group","city");
            map.put("where","province");
            map.put("name",provinceArea.getProvince());

            //jingDongAreaTreeNode.setChildren(recursion2(map));
            jingDongAreaTreeNodes.add(jingDongAreaTreeNode);
        }
        return jingDongAreaTreeNodes;
    }

    private  List<JingDongAreaTreeNode> recursion2 (Map<String,Object> map){
        List<JingDongAreaTreeNode> jingDongAreaTreeNodes = new ArrayList<>();
        List<JingDongArea> list = iJingdongAreaService.selectAreaByName(map);//省下的市
        for (JingDongArea area : list) {

            JingDongAreaTreeNode  jingDongAreaTreeNode = new JingDongAreaTreeNode();
            Map<String ,Object> innerMap = new HashMap<>();

            if("city".equals(map.get("group"))){
                if(area.getCity()==null || "".equals(area.getCity())){
                    continue;
                }
                jingDongAreaTreeNode.setText(area.getCity());
                innerMap.put("group","district");
                innerMap.put("where","city");
                innerMap.put("name",area.getCity());
            }
            if("district".equals(map.get("group"))){
                if(area.getDistrict()==null || "".equals(area.getDistrict())){
                    continue;
                }
                jingDongAreaTreeNode.setText(area.getDistrict());
                jingDongAreaTreeNode.setText(area.getCity());
                innerMap.put("group","town");
                innerMap.put("where","district");
                innerMap.put("name",area.getDistrict());
            }
            if("town".equals(map.get("group"))){
                if(area.getTown()==null || "".equals(area.getTown())){
                    continue;
                }
                jingDongAreaTreeNode.setText(area.getTown());
                innerMap = null;
            }

            jingDongAreaTreeNode.setId(area.getId());
            jingDongAreaTreeNode.setAreaCode(area.getAreaCode());
            if(area.getTown()==null ||  "".equals(area.getTown())){
                jingDongAreaTreeNode.setIsleaf(false);
            }else {
                jingDongAreaTreeNode.setIsleaf(true);
            }

            jingDongAreaTreeNode.setJdCode(area.getJdCode());

            if(innerMap==null){
                jingDongAreaTreeNode.setChildren(null);
            }else {
                jingDongAreaTreeNode.setChildren(recursion2(innerMap));
            }
            jingDongAreaTreeNodes.add(jingDongAreaTreeNode);
        }
        return jingDongAreaTreeNodes;
    }

    
}
