package com.drunk.file.test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastdfsClientTest {

    public void upload()throws Exception{
        //加载全局配置文件
        ClientGlobal.init("classpath:fdfs_client.conf");
        //创建客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //获取trackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取StorageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, null);
    }
}
