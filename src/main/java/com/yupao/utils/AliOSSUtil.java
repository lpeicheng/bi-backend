package com.yupao.utils;
 
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import java.io.*;

public class AliOSSUtil {
    // Endpoint外网访问域名，以上海为例。
   private static String endpoint = "oss-cn-guangzhou.aliyuncs.com";
    // accessKeyId 和 accessKeySecret 是先前创建用户生成的
    private static  String accessKeyId = "LTAI5t8nARZCGuxz5zo9xoLu";
    private static String accessKeySecret = "UreDDjhJO8sQeKxfyXNl6serynqJsL";
    private static String bucketName="bi-backend";
    public static void uploadFile(String fileName,String saveFileName) throws FileNotFoundException {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流。
        InputStream inputStream = new FileInputStream(fileName);
        ossClient.putObject(bucketName, saveFileName, inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
    }
    public static void main(String[] args) throws FileNotFoundException {
        uploadFile("C:\\Users\\kuang\\Pictures\\preview.jpg","1.jpg");
    }

}