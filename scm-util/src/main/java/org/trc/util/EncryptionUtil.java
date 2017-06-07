package org.trc.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by george on 2017/5/23.
 */
public class EncryptionUtil {

    private static Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

    public static final String MD5 = "MD5";

    public static final String SHA1 = "SHA-1";

    public static String encryption(String str, String encryptionMethod, String charset) throws Exception {
        if (StringUtils.isBlank(encryptionMethod)) {
            encryptionMethod = MD5;
        }
        MessageDigest md = MessageDigest.getInstance(encryptionMethod);
        md.update(str.getBytes(charset));
        return new BigInteger(1, md.digest()).toString(16);
    }

    public static void main(String[] args){
        try {
            System.out.println(encryption("78v2qm0ujMLN0an31XeS2017-06-07 10:13:14ZZ9bjjpKbXKjIARvxm6h小泰科技vsp6485ce0e3d9ee07af005da24ca71ccb8access_token78v2qm0ujMLN0an31XeS",MD5,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
