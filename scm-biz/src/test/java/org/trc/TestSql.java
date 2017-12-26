package org.trc;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.trc.domain.order.OutboundDetail;
import org.trc.mapper.goods.ISkuStockMapper;
import org.trc.mapper.outbound.IOutboundDetailMapper;
import org.trc.util.lock.StockLock;

import com.alibaba.fastjson.JSON;

@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
public class TestSql {
	@Autowired
	ISkuStockMapper skuStockMapper;
	@Autowired
	private IOutboundDetailMapper outboundDetailMapper;
	@Autowired
	private StockLock stockLock;
	@Test
	public void test () {
//		SkuStock record = new SkuStock();
//		record.setSkuCode("SP0201707250000034");
//		Example ex = new Example(SkuStock.class);
//		ex.createCriteria().andEqualTo("skuCode", "SP0201707240000002");
//		List<SkuStock> select = skuStockMapper.selectByExample(ex);
//		System.err.println(select.size());
		
		//List<OutboundDetail> detail = outboundDetailMapper.selectByWarehouseOrderCode("003");
		//System.out.println(JSON.toJSONString(detail));
		OutboundDetail record = new OutboundDetail();
		record.setId(24L);
		OutboundDetail selectOne = outboundDetailMapper.selectOne(record);
		System.out.println(JSON.toJSONString(selectOne));
		
	}
	static int count = 0;
	
	@Test
	public void testLock () throws IOException {
		for (int i = 0; i < 100; i++) {
	            new Thread(new Runnable() {
	                @Override
	                public void run() {
	                	String identifier = stockLock.Lock("XXXXX");
	                	if (StringUtils.isNotBlank(identifier)) {
	                		System.out.println(Thread.currentThread().getName() + "------"+count++);
	                		stockLock.releaseLock("XXXXX",identifier);
	                	} else {
	                		
	                	}
	                }
	            }).start();
	        }
		System.in.read();
	}
	
	public static void main(String[] args) {
//		for (int i = 0; i < 20; i++) {
//			new Thread(() -> {
//				System.out.println(count++);
//			}).start();
//			
//		}

	for (int i = 0; i < 100; i++) {
	            new Thread(new Runnable() {
	                @Override
	                public void run() {
	                    System.out.println("------"+count++);
	                }
	            }).start();
	        }
		}
}
