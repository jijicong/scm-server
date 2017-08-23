package org.trc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hzwdx on 2017/8/23.
 */
public class SHAEncrypt {

    private static Logger log = LoggerFactory.getLogger(SHAEncrypt.class);

    private static final String SHB_256 = "SHA-256";
    private static final String SHB_512 = "SHA-512";

    /**
     * 传入文本内容，返回 SHA-256 串
     *
     * @param strText
     * @return
     */
    public static String SHA256(final String strText)
    {
        return SHA(strText, SHB_256);
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     *
     * @param strText
     * @return
     */
    public static String SHA512(final String strText)
    {
        return SHA(strText, SHB_512);
    }

    /**
     * 字符串 SHA 加密
     *
     * @param
     * @return
     */
    private static String SHA(final String strText, final String strType)
    {
        // 返回值
        String strResult = null;

        // 是否是有效字符串
        if (strText != null && strText.length() > 0)
        {
            try{
                // SHA 加密开始
                // 创建加密对象 并传入加密类型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 类型结果
                byte byteBuffer[] = messageDigest.digest();
                // 将 byte 转换成 string
                StringBuffer strHexString = new StringBuffer();
                for (int i = 0; i < byteBuffer.length; i++)
                {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1)
                    {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }
        return strResult;
    }

    public static void main(String[] args){
        String str = "ssssssss2225555555555555";
        System.out.println(SHAEncrypt.SHA256(str));
        System.out.println(SHAEncrypt.SHA512(str));
    }

}
