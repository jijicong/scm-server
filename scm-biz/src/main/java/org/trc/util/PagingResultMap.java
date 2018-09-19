package org.trc.util;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Description〈通用内存分页函数〉
 *
 * @author hzliuwei
 * @create 2018/9/18
*/
public class PagingResultMap {

    public static <T> Map<String, Object> getPagingResultMap(List<T> list, Integer currPageNo, Integer pageSize) {
        Map<String, Object> retMap = new HashMap<>();

        if (list.isEmpty()) {
            retMap.put("result", Collections.emptyList());
            retMap.put("pageNo", 0);
            retMap.put("pageRowNum", 0);
            retMap.put("totalRowNum", 0);
            retMap.put("totalPageNum", 0);

            return retMap;
        }

        int totalRowNum = list.size();
        int totalPageNum = (totalRowNum - 1) / pageSize + 1;

        int realPageNo = currPageNo;
        if (currPageNo > totalPageNum) {
            realPageNo = totalPageNum;
        } else if (currPageNo < 1) {
            realPageNo = 1;
        }

        int fromIdx = (realPageNo - 1) * pageSize;
        int toIdx = realPageNo * pageSize;

        if (realPageNo == totalPageNum && totalPageNum * pageSize > totalRowNum) {
            toIdx = totalRowNum;
        }

        List<T> result = list.subList(fromIdx, toIdx);

        retMap.put("result", result);
        retMap.put("pageNo", realPageNo);
        retMap.put("pageRowNum", result.size());
        retMap.put("totalRowNum", totalRowNum);
        retMap.put("totalPageNum", totalPageNum);

        return retMap;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i< 5; i++){
            list.add(i + "");
        }

        Map<String, Object> pagingResultMap = getPagingResultMap(list, 1, 10);
        System.out.println(JSON.toJSONString(pagingResultMap));
    }
}
