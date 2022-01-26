package boot.spring.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Author duxiangdong
 * @Date 2022/1/21 10:39
 * @Version 1.0
 * 实现获取指定文件夹下的指定格式的所有文件
 */
public class fileName {

    public static void main(String[] args) {

        //文件存放目录
        String path = "/Users/duxiangdong/F/work/lunwen/activiti/spring-boot-activiti/src/main/webapp/uploadfiles";
        File file = new File(path);
        File files[] = file.listFiles(new FRFilenameFilter());
        for (File f : files) {
            if (!f.isDirectory())
                System.out.println(f);

        }
    }

    /**
     * 上传文件到指定路径
     * @param mFile 要上传的文件
     * @param path  指定路径
     */
    public static void uploadFile(MultipartFile mFile, String path) {
        try {
            InputStream in = mFile.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            File file = new File(path);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            OutputStream out = new FileOutputStream(path);
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            System.out.println("----------" + path +"文件上传失败————————");
            e.printStackTrace();
        }
    }
}
