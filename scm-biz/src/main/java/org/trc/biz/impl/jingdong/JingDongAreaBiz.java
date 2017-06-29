package org.trc.biz.impl.jingdong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.jingdong.IJingDongAreaBiz;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.domain.jingDong.JingDongArea;
import org.trc.domain.jingDong.JingDongAreaTreeNode;
import org.trc.domain.jingDong.JingDongAreaUpdateResult;
import org.trc.domain.jingDong.Result;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.JingDongAreaException;
import org.trc.service.impl.jingdong.IJingdongAreaService;
import org.trc.util.AssertUtil;
import org.trc.util.RedisUtil;
import tk.mybatis.mapper.entity.Example;

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

    private Logger logger = LoggerFactory.getLogger(JingDongAreaBiz.class);

    @Resource
    private IJingdongAreaService iJingdongAreaService;

    @Resource
    private IJingDongBiz iJingDongBiz;

    private static final String TYPE = "50";
    //京东的无地址更新的接口
    private static final String CODE = "0010";
    /*
        1.先获取，更新地址
        2.若地址已更新，拿到更新的地址，保存到数据库，再调京东的删除接口更新接口
            getJingDongAreaTree（）把京东地址放入缓存
        3.若京东地址无更新，则无需做任何操作--或者调用redis，查看缓存中是否存在京东地址
            若缓存存在，则不做任何操作，否则调用getJingDongAreaTree()
         */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateJingDongArea() throws Exception{

        String msg = iJingdongAreaService.addressUpdate();
        JSONObject jsonObject = JSON.parseObject(msg);
        String resultCode = jsonObject.getString("resultCode");
        if(CODE.equals(resultCode)){//说明无更新
            return;
        }
        //说明有更新
        String result = jsonObject.getString("result");
        JSONArray jsonArray = JSON.parseArray(result);
        String[] strIds = new String[jsonArray.size()]; //用户删除变更id，否则以后还会推送该变更
        for (int i = 0 ; i<jsonArray.size() ;i++ ) {

            Object obj = jsonArray.get(i);
            AssertUtil.notNull(obj,"京东推送的地址变更内容为空");
            if(obj instanceof JingDongAreaUpdateResult){

                JingDongAreaUpdateResult jingDongAreaUpdateResult = (JingDongAreaUpdateResult)obj;
                strIds[i] = jingDongAreaUpdateResult.getId();
                Result rs = jingDongAreaUpdateResult.getResult();

                if(ZeroToNineEnum.ONE.getCode().equals(rs.getOperateType())){//1:插入数据
                    JingDongArea area = new JingDongArea();
                    area.setJdCode(rs.getAreaId());
                    String areaLevel = rs.getAreaLevel();//地址等级 :国家(1)、省(2)、市(3)、县(4)、镇(5)
                    if(ZeroToNineEnum.ONE.getCode().equals(areaLevel)){
                        String err = "无法对国家变更进行操作";
                        logger.error(err);
                        continue;
                    }
                    insertJingDongArea(area,rs);
                }
                if(ZeroToNineEnum.TWO.getCode().equals(rs.getOperateType())){//2:更新数据
                    JingDongArea area = new JingDongArea();
                    area.setJdCode(rs.getAreaId());
                    String areaLevel = rs.getAreaLevel();//地址等级 :国家(1)、省(2)、市(3)、县(4)、镇(5)
                    if(ZeroToNineEnum.ONE.getCode().equals(areaLevel)){
                        String err = "无法对国家变更进行操作";
                        logger.error(err);
                        continue;
                    }
                    updateJingDongArea(area,rs);
                }
                if(ZeroToNineEnum.THREE.getCode().equals(rs.getOperateType())){//3:删除数据 :删除所有的name
                    //TODO
                }
                if ( rs.getOperateType() == null || (rs.getOperateType()!=ZeroToNineEnum.ONE.getCode()&&rs.getOperateType()!=ZeroToNineEnum.TWO.getCode()&&rs.getOperateType()!=ZeroToNineEnum.THREE.getCode()) ){
                    String err = "获取京东的地址变更的数据不正确";
                    logger.error(err);
                    throw new JingDongAreaException(ExceptionEnum.JING_DONG_USE_EXCEPTION,err);
                }

                getJingDongAreaTree();
            }else {

                String err = "获取京东的地址变更的格式不正确";
                logger.error(err);
                throw new JingDongAreaException(ExceptionEnum.JING_DONG_USE_EXCEPTION,err);

            }

        }



    }
    private void updateJingDongArea(JingDongArea area,Result rs) throws Exception {

        String areaLevel = rs.getAreaLevel();
        if(ZeroToNineEnum.TWO.getCode().equals(areaLevel)){
            area.setProvince(rs.getAreaName());
        }
        if(ZeroToNineEnum.THREE.getCode().equals(areaLevel)){//市有可能被迁移到其它的省份：所以还要比较接收到的parentId
            area.setCity(rs.getAreaName());
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            if(!parentArea.getJdCode().equals(rs.getParentId())){//不等，则需要更改省份
                area.setProvince(parentArea.getProvince());
            }
        }
        if(ZeroToNineEnum.FOUR.getCode().equals(areaLevel)){//地区有可能被迁移到其它的市：所以还要比较接收到的parentId
            area.setDistrict(rs.getAreaName());
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            if(!parentArea.getJdCode().equals(rs.getParentId())){//不等，则需要更改省份
                area.setCity(parentArea.getCity());
            }
        }
        if(ZeroToNineEnum.FIVE.getCode().equals(areaLevel)){//城镇有可能被迁移到其它的地区：所以还要比较接收到的parentId
            area.setTown(rs.getAreaName());
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            if(!parentArea.getJdCode().equals(rs.getParentId())){//不等，则需要更改省份
                area.setDistrict(parentArea.getDistrict());
            }
        }
        Example example = new Example(JingDongArea.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("jdCode",rs.getAreaId());
        iJingdongAreaService.updateByExample(area,example);
    }


    private void insertJingDongArea(JingDongArea area,Result rs) throws Exception{

        String areaLevel = rs.getAreaLevel();
        if(ZeroToNineEnum.TWO.getCode().equals(areaLevel)){
            area.setProvince(rs.getAreaName());
        }
        if(ZeroToNineEnum.THREE.getCode().equals(areaLevel)){
            area.setCity(rs.getAreaName());
            //若为市,则需要给省赋值
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            area.setProvince(parentArea.getProvince());
        }
        if(ZeroToNineEnum.FOUR.getCode().equals(areaLevel)){

            area.setDistrict(rs.getAreaName());
            //若为地区，则需要对省，市赋值
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            area.setCity(parentArea.getCity());
            JingDongArea jingDongAreas = iJingdongAreaService.selectProvinceByName(parentArea.getProvince());
            area.setProvince(jingDongAreas.getProvince());

        }
        if(ZeroToNineEnum.FIVE.getCode().equals(areaLevel)){

            area.setTown(rs.getAreaName());
            //若为城镇，则需要对省，市赋值
            JingDongArea parentArea = new JingDongArea();
            parentArea.setJdCode(rs.getParentId());
            parentArea = iJingdongAreaService.selectOne(parentArea);
            area.setDistrict(parentArea.getCity());
            JingDongArea jingDongAreas = iJingdongAreaService.selectCityByName(parentArea.getCity());
            area.setCity(jingDongAreas.getCity());
            jingDongAreas = iJingdongAreaService.selectProvinceByName(parentArea.getProvince());
            area.setProvince(jingDongAreas.getProvince());

        }
        iJingdongAreaService.insert(area);

    }

    public List<JingDongAreaTreeNode>  getJingDongAreaTree() throws Exception {

        List<JingDongAreaTreeNode> jingDongAreaTreeNodeList = recursion1();
        return jingDongAreaTreeNodeList;
        /*try {

            String jsonNodes = JSON.toJSONString(jingDongAreaTreeNodeList);
            boolean  boo = RedisUtil.set("jdArea",jsonNodes);
            if(!boo){//放入redis失败

                RedisUtil.set("jdArea",jsonNodes); //再放一起

            }//若还不成功，则走正常的取，京东地址的时候保存到redis

        }catch (JSONException e){
            //如果初始化不成功
            String msg = "京东地址初始化失败";
            logger.error(msg);

        }*/

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
           // jingDongAreaTreeNode.setJdCode(provinceArea.getJdCode());
            jingDongAreaTreeNode.setText(provinceArea.getProvince());

            Map<String,Object> map = new HashMap<>();
            map.put("group","city");
            map.put("where","province");
            map.put("name",provinceArea.getProvince());

            jingDongAreaTreeNode.setChildren(recursion2(map));
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

            //jingDongAreaTreeNode.setJdCode(area.getJdCode());

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
