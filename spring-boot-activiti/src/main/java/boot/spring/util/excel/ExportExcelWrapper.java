package boot.spring.util.excel;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 19:52
 * @Version 1.0
 * excel导出
 */
public class ExportExcelWrapper<T> extends ExportExcelUtil<T> {

    public ByteArrayOutputStream exportExcel(List list) {
        ByteArrayOutputStream bos = null;
        try {
            bos = exportExcel2007(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos;
    }
}
