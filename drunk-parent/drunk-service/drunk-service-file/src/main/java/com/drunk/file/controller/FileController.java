package com.drunk.file.controller;

import com.drunk.entity.Result;
import com.drunk.file.entity.FastDFSFile;
import com.drunk.file.util.FastdfsClient;
import org.csource.common.MyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        //文件名称
        String fileName = file.getOriginalFilename();
        //文件内容
        byte[] content = file.getBytes();
        //文件扩展名
        String extension = StringUtils.getFilenameExtension(fileName);

        FastDFSFile fastDFSFile = new FastDFSFile(fileName,content,extension);

        String[] uploadResults = FastdfsClient.uploadFile(fastDFSFile);

        return FastdfsClient.getTrackerUrl()+"/"+uploadResults[0]+"/"+uploadResults[1];
        //return "http://192.168.120.132:22122/"+uploadResults[0]+"/"+uploadResults[1];
    }
}
