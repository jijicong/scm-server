package org.trc.service;

import static org.hamcrest.CoreMatchers.startsWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.trc.constants.SupplyConstants;
import org.trc.service.impl.util.SerialUtilService;
import org.trc.util.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"})
@Rollback(value = true)
@Transactional(transactionManager = "transactionManager")
public class SerialNoTest {
	
	@Autowired
	private SerialUtilService serialUtilService;
	//static int code = 0;
	@Test
	public void testSerialNo () throws IOException {
		int count = 1;
		CountDownLatch latch = new CountDownLatch(count);
		for (int i = 0; i < count; i++) {
			/**
			 * 多线程，不回滚~ 
			 **/
			new Thread(() -> {
				try {
					String code = serialUtilService.generateCode(
							SupplyConstants.Serial.OUTBOUND_ORDER_LENGTH, 
							SupplyConstants.Serial.OUTBOUND_ORDER, 
							DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
					System.out.println(code);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				latch.countDown(); // 执行完毕，计数器减1
			}).start();
		}
		try {
			latch.await(); // 主线程等待
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
//	public static void main(String[] args) throws InterruptedException {
//		for (int i = 0; i < 50; i++) {
//			Thread t = new Thread(() -> {
//				System.out.println(code ++);
//			});
//			t.start();
//			t.join();
//		}
//	}

}
