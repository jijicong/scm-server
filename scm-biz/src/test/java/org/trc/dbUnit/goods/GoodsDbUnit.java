package org.trc.dbUnit.goods;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.service.BaseTest;

import java.util.ArrayList;
import java.util.List;

public class GoodsDbUnit  extends BaseTest {

    @Autowired
    private IGoodsBiz goodsBiz;

    @Test
    public void supplierSkuUpdateNotice_test() throws Exception{
        //删除原数据
        execSql(conn,"delete from external_item_sku");
        execSql(conn,"delete from external_picture");
        //从xml文件读取数据并插入数据库中
        prepareData(conn, "goods/preInsertExternalItemSkuData.xml");
        prepareData(conn, "goods/preInsertExternalPictureData.xml");
        //测试方法
        //goodsBiz.supplierSkuUpdateNotice(getsupplierSkuUpdateNoticeParam());


    }

    private String getsupplierSkuUpdateNoticeParam(){
        return "[\n" +
                "    {\n" +
                "        \"brandName\":\"金龙鱼\",\n" +
                "        \"category\":\"食品饮料;粮油调味;食用油\",\n" +
                "        \"categoryCode\":\"1320;1584;2676\",\n" +
                "        \"createTime\":1505253895000,\n" +
                "        \"detailImagePath\":\"http://img13.360buyimg.com/n12/jfs/t6472/271/1544081908/44068/b20429c2/595352aeNb6fc0833.jpg,http://img13.360buyimg.com/n12/jfs/t6385/176/1544568071/42056/1e08bc25/59535284N6a7df8ae.jpg,http://img13.360buyimg.com/n12/jfs/t6649/236/1509868224/44123/79ea6e07/5953528cN946e37a2.jpg,http://img13.360buyimg.com/n12/jfs/t6061/304/3413530684/53397/321c182e/5953528aNa4800e3a.jpg,\",\n" +
                "        \"id\":539152,\n" +
                "        \"imagePath\":\"http://img13.360buyimg.com/n12/jfs/t6163/119/1559780836/103437/72b424b3/59545829Nd3aa7f0f.jpg\",\n" +
                "        \"isDeleted\":\"0\",\n" +
                "        \"isUsed\":\"1\",\n" +
                "        \"isValid\":\"1\",\n" +
                "        \"name\":\"【京东超市】金龙鱼 食用油 谷维多稻米油5L（产品配方升级，新老包装随机发放）\",\n" +
                "        \"notifyTime\":1510678500353,\n" +
                "        \"pageNum\":\"2676\",\n" +
                "        \"productArea\":\"秦皇岛\",\n" +
                "        \"saleUnit\":\" \",\n" +
                "        \"skuSize\":0,\n" +
                "        \"state\":\"1\",\n" +
                "        \"supplierCode\":\"LY\",\n" +
                "        \"supplierPrice\":9900,\n" +
                "        \"supplyName\":\"粮油\",\n" +
                "        \"supplyPrice\":9800,\n" +
                "        \"supplySku\":\"HN1075570153\",\n" +
                "        \"upc\":\"6948195806929\",\n" +
                "        \"updateFlag\":\"0\",\n" +
                "        \"updateTime\":1509678001000,\n" +
                "        \"weight\":4.72\n" +
                "    },\n" +
                "    {\n" +
                "        \"brandName\":\"鲁花\",\n" +
                "        \"category\":\"食品饮料;粮油调味;食用油\",\n" +
                "        \"categoryCode\":\"1320;1584;2676\",\n" +
                "        \"createTime\":1505253895000,\n" +
                "        \"detailImagePath\":\"jfs/t3463/146/2039304410/206288/9a8259f6/583d370aN20c61b21.jpg,jfs/t4024/359/112546189/204045/1869a5d9/583d370fNadaade8f.jpg,jfs/t6649/236/1509868224/44123/79ea6e07/5953528cN946e37a2.jpg,\",\n" +
                "        \"id\":539158,\n" +
                "        \"imagePath\":\"jfs/t6163/119/1559780836/103437/72b424b3/59545829Nd3aa7f0f.jpg\",\n" +
                "        \"isDeleted\":\"0\",\n" +
                "        \"isUsed\":\"1\",\n" +
                "        \"isValid\":\"1\",\n" +
                "        \"name\":\"【京东超市】鲁花 食用油 非转基因 菜籽油 5L\",\n" +
                "        \"notifyTime\":1510678500353,\n" +
                "        \"pageNum\":\"2676\",\n" +
                "        \"productArea\":\"江苏、湖北等\",\n" +
                "        \"saleUnit\":\" \",\n" +
                "        \"skuSize\":0,\n" +
                "        \"state\":\"1\",\n" +
                "        \"supplierCode\":\"JD\",\n" +
                "        \"supplierPrice\":8500,\n" +
                "        \"supplyName\":\"京东\",\n" +
                "        \"supplyPrice\":12900,\n" +
                "        \"supplySku\":\"5731593\",\n" +
                "        \"upc\":\"6938749816122\",\n" +
                "        \"updateFlag\":\"0\",\n" +
                "        \"updateTime\":1510464825000,\n" +
                "        \"weight\":4.72\n" +
                "    }\n" +
                "]";
    }


}
