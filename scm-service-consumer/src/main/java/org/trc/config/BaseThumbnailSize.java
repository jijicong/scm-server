package org.trc.config;

import org.trc.enums.ZeroToNineEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/4.
 */
public class BaseThumbnailSize {

    private List<ThumbnailSize> thumbnailSizes;

    public BaseThumbnailSize(){
        thumbnailSizes = new ArrayList<ThumbnailSize>();
        //150*150缩略图
        thumbnailSizes.add(new ThumbnailSize(150, 150, ZeroToNineEnum.ONE.getCode()));
        /*//150*100缩略图
        thumbnailSizes.add(new ThumbnailSize(150, 100, ZeroToNineEnum.ONE.getCode()));
        //100*100缩略图
        thumbnailSizes.add(new ThumbnailSize(100, 100, ZeroToNineEnum.ONE.getCode()));*/
    }

    public List<ThumbnailSize> getThumbnailSizes() {
        return thumbnailSizes;
    }

}
