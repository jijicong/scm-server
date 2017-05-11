package org.trc.form;

/**
 * Created by hzwdx on 2017/5/8.
 */
public class UploadResponse {

    /**
     * 上传文件原始文件名称
     */
    private String fileName;
    /**
     * 文件上传后的存储路径
     */
    private String key;
    /**
     * 文件访问url地址
     */
    private String url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
