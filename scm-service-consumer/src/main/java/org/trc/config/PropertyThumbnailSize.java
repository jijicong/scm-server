package org.trc.config;

import org.trc.enums.ZeroToNineEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 属性相关图片缩略图尺寸
 * Created by hzwdx on 2017/5/4.
 */
public class PropertyThumbnailSize extends BaseThumbnailSize{

    private List<ThumbnailSize> propertyThumbnailSizes;

    public PropertyThumbnailSize(){
        super();
        propertyThumbnailSizes = new ArrayList<ThumbnailSize>();
        propertyThumbnailSizes.addAll(super.getThumbnailSizes());
        propertyThumbnailSizes.add(new ThumbnailSize(250, 200, ZeroToNineEnum.ONE.getCode()));
    }

    public List<ThumbnailSize> getThumbnailSizes() {
        return propertyThumbnailSizes;
    }

    public static void main(String[] args){
        BaseThumbnailSize baseThumbnailSize = new BaseThumbnailSize();
        System.out.println(baseThumbnailSize.getThumbnailSizes().size());
        PropertyThumbnailSize propertyThumbnailSize = new PropertyThumbnailSize();
        BaseThumbnailSize baseThumbnailSize2 = propertyThumbnailSize;
        System.out.println(baseThumbnailSize2.getThumbnailSizes().size());
    }

}
