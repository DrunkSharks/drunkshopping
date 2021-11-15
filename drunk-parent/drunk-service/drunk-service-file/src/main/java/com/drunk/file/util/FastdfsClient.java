package com.drunk.file.util;

import com.drunk.file.entity.FastDFSFile;
import org.assertj.core.internal.Bytes;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class FastdfsClient {
    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageClient storageClient;

    static{
        try{
            String path = new ClassPathResource("fdfs_client.conf").getPath();
            //加载全局配置文件
            ClientGlobal.init(path);
            //创建客户端对象
            trackerClient = new TrackerClient();
            //获取trackerServer信息
            trackerServer = trackerClient.getConnection();
            //获取StorageClient对象
            storageClient = new StorageClient(trackerServer, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static String[] uploadFile(FastDFSFile file) throws Exception{

        NameValuePair[] nameValuePairs = new NameValuePair[1];
        nameValuePairs[0] = new NameValuePair(file.getAuthor());

        /*
         *参数
         * nameValuePairs 额外信息
         * 文件上传后的返回值
         * uploadResults[0]:文件上传所存储的组名，例如:group1
         * uploadResults[1]:文件存储路径,例如：M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg
         */
        return storageClient.upload_file(file.getContent(), file.getExt(), nameValuePairs);
    }

    /**
     * 文件下载
     * @param group
     * @param remote_filename
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static InputStream downloadFile(String group,String remote_filename) throws IOException, MyException {
        //文件下载
        byte[] bytes = storageClient.download_file(group, remote_filename);
        //创建字节数组流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return byteArrayInputStream;
    }

    /**
     * 文件删除
     * @param group
     * @param remote_filename
     * @throws IOException
     * @throws MyException
     */
    public static void deleteFile(String group, String remote_filename) throws IOException, MyException {
        storageClient.delete_file(group, remote_filename);
    }

    /**
     * 获取Tracker服务地址
     * @return
     */
    public static String getTrackerUrl() throws IOException {
        return "http://"+trackerServer.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
    }

    /**
     * 获取文件信息
     * @param group 组名
     * @param remote_filename 文件存储完整名
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static FileInfo getFileInfo(String group,String remote_filename) throws IOException, MyException {
        return storageClient.get_file_info(group, remote_filename);
    }

    /**
     * 根据组名获取Storage组信息
     * @param group
     * @return
     * @throws IOException
     */
    public static StorageServer getStorages(String group) throws IOException {
        //通过Tracker客户端获取Storage组信息
        return trackerClient.getStoreStorage(trackerServer,group);
    }

    /**
     * 获取Storage服务的IP和端口信息
     * @param group
     * @param remote_filename
     * @return
     * @throws IOException
     */
    public static ServerInfo[] getsServerInfo(String group,String remote_filename) throws IOException {
        return trackerClient.getFetchStorages(trackerServer, group, remote_filename);
    }


    public static void main(String[] args) throws IOException, MyException {
        //System.out.println(getFileInfo("group1","M00/00/00/wKh4hGEFVmKAIYoUAAClKhF__y4662.jpg"));

        //deleteFile("group1","M00/00/00/wKh4hGEFVmKAIYoUAAClKhF__y4662.jpg");

        //System.out.println(getStorages("group1").getStorePathIndex());

        ServerInfo[] serverInfos = getsServerInfo("group1", "M00/00/00/wKh4hGEFbamAP_1jAAAtBTlDw_E931.jpg");
        for (ServerInfo serverInfo : serverInfos) {
            System.out.print(serverInfo.getIpAddr());
            System.out.println("    "+serverInfo.getPort());
        }
    }
}
