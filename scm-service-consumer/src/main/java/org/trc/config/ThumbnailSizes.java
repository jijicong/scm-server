package org.trc.config;

import org.trc.enums.ZeroToNineEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panshuai on 17/7/28.
 */
public class ThumbnailSizes {

    private List<ThumbnailSize> thumbnailSizes;

    private ThumbnailSizes(ThumbnailSizesBuilder builder) {
        this.thumbnailSizes = builder.thumbnailSizes;
    }

    public List<ThumbnailSize> getThumbnailSizes() {
        return thumbnailSizes;
    }

    public static class ThumbnailSizesBuilder {

        private List<ThumbnailSize> thumbnailSizes;

        public ThumbnailSizesBuilder(){
            thumbnailSizes = new ArrayList<>();
            thumbnailSizes.add(new ThumbnailSize(150, 150, ZeroToNineEnum.ONE.getCode()));
        }

        public ThumbnailSizesBuilder size150X100() {
            thumbnailSizes.add(new ThumbnailSize(150, 100, ZeroToNineEnum.ONE.getCode()));
            return this;
        }

        public ThumbnailSizesBuilder size100X100() {
            thumbnailSizes.add(new ThumbnailSize(100, 100, ZeroToNineEnum.ONE.getCode()));
            return this;
        }

        public ThumbnailSizesBuilder size250X200() {
            thumbnailSizes.add(new ThumbnailSize(250, 200, ZeroToNineEnum.ONE.getCode()));
            return this;
        }

        public ThumbnailSizes build() {
            return new ThumbnailSizes(this);
        }

    }

    public static void main(String[] args){
        ThumbnailSizes t = new ThumbnailSizes.ThumbnailSizesBuilder().build();
        ThumbnailSizes t1 = new ThumbnailSizes.ThumbnailSizesBuilder().size100X100().build();


        List<ThumbnailSize> ts = t.getThumbnailSizes();
        for(ThumbnailSize temp : ts) {
            System.out.println(temp.getWidth() + "===" + temp.getHeight());
        }

        System.out.println("#########################");

        ts = t1.getThumbnailSizes();
        for(ThumbnailSize temp : ts) {
            System.out.println(temp.getWidth() + "===" + temp.getHeight());
        }
    }
}
