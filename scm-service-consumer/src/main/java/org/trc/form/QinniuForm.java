package org.trc.form;

/**
 * Created by hzwdx on 2017/5/3.
 */
public class QinniuForm {

    private String accessKey;

    private String secretKey;

    private String bucket;
    /**
     * 域外(公网)访问bucket
     */
    private String domainOfBucket;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDomainOfBucket() {
        return domainOfBucket;
    }

    public void setDomainOfBucket(String domainOfBucket) {
        this.domainOfBucket = domainOfBucket;
    }
}
