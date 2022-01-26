package boot.spring.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @Author duxiangdong
 * @Date 2022/1/24 13:32
 * @Version 1.0
 */
public class FRFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        // 创建返回值
        boolean flag = false;
        // 定义筛选条件
        if (name.toLowerCase().endsWith(".jpg")
                || name.toLowerCase().endsWith(".png")
                || name.toLowerCase().endsWith(".jpeg")) {
            flag = true;
        }
        return flag;
    }
}
