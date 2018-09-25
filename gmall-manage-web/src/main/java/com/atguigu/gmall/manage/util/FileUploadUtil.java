package com.atguigu.gmall.manage.util;

import com.atguigu.gmall.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileUploadUtil {

    public static String fileUploadImg(MultipartFile file) {


            String path = FileUploadUtil.class.getClassLoader().getResource("tracker.conf").getPath();

            try {
                ClientGlobal.init(path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyException e) {
                e.printStackTrace();
            }

            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = null;
            try {
                connection = trackerClient.getConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageClient storageClient = new StorageClient(connection, null);

            String[] gifs = new String[0];
            try {

                String Filename = file.getOriginalFilename();

                String extName = StringUtils.substringAfterLast(Filename, ".");
                gifs = storageClient.upload_file(file.getBytes(), extName, null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyException e) {
                e.printStackTrace();
            }
            String imgUrl = Constant.IMG_URL;
            for (String string : gifs) {

                imgUrl = imgUrl + "/" + string;
            }

            return imgUrl;
        }

}
