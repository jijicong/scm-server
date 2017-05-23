package org.trc.util;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成32位以内全局唯一标识符，规则：前缀（最长6位）+16进制IP（8位）+毫秒级时间（long型，最长13位）+自增计数（最长5位）
 * @author darbean
 * @version $Id: GuidUtil.java, v 0.1 Nov 17, 2015 10:43:08 PM Darbean $
 */
public class GuidUtil {

    // 自增计数器，最大五位（99999），理论上在单台机器10w/ms以内的前提下保证全局唯一
    private static AtomicInteger inc = new AtomicInteger(0);

    // 16进制IP，8位
    private static String ipHex;

    static {
        String ip = "";
        try {
            ip = IpUtil.getRealIp();
        } catch (Exception e) {
            ip = String.valueOf(new SecureRandom().nextInt(99999999));
        }
        String[] split = ip.split("\\.");
        ipHex = "";
        for (String string : split) {
            ipHex += Integer.toHexString(Integer.valueOf(string));
        }
    }
    
    public static void main(String[] args){
        System.out.println(getNextUid("bar").length());
        String[] ip = {"100.111.8.7","100.111.8.8","100.111.8.9","100.109.15.5","100.109.15.6","100.109.15.7","100.109.15.8","100.109.15.9","100.109.15.10","100.109.15.11","100.109.15.12","100.109.15.13","100.109.15.14","100.109.15.15","100.109.15.16","100.109.15.17","100.109.15.18","100.109.15.19","100.109.15.20"};
        for(int i = 0 ; i < ip.length ; i++ ){
	        String[] split = ip[i].split("\\.");
	        ipHex = "";
	        for (String string : split) {
	            ipHex += Integer.toHexString(Integer.valueOf(string));
	        }
	        System.out.println(ip[i]+"  "+"646dff".equals(ipHex));
	        System.out.println("OCNCSN646dff14630397345934190".length());

	        //646df141
        }
    }

    /**
     * 总数=seed位数（前缀, 最长6位）+自动生成不重复26位
     * @param seed
     * @return
     */
    public static String getNextUid(String seed) {
        int curr = inc.getAndIncrement();
        if (curr >= 99999) {
            inc.getAndSet(curr % 99999);
        }
        String prefix = (seed == null) ? "" : seed;
        String currentTmls = String.valueOf(System.currentTimeMillis());
        return prefix + ipHex + currentTmls + curr;
    }


}
