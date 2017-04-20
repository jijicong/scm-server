package org.trc.util;

import org.apache.commons.lang.StringUtils;

public class NumberUtils {

	public Integer parseIneger(String str){
		if(StringUtils.isNotEmpty(str)){
			return Integer.parseInt(str);
		}
		return 0;
	}
	
}
