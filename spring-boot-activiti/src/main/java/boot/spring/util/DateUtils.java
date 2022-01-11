package boot.spring.util;

import boot.spring.po.LeaveApply;
import boot.spring.service.impl.LeaveServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author duxiangdong
 * @Date 2022/1/11 14:44
 * @Version 1.0
 */
public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    /**
     * 获取两个日期之间的天数
     *
     * @param apply
     */
    public static Integer getDays(LeaveApply apply) {
        String start = apply.getStart_time();
        String end = apply.getEnd_time();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date before = dateFormat.parse(start);
            Date after = dateFormat.parse(end);
            long beforeTime = before.getTime();
            long afterTime = after.getTime();
            return Math.toIntExact((afterTime - beforeTime) / (1000 * 60 * 60 * 24) + 1);
        } catch (ParseException e) {
            logger.error("获取两个日期之间的天数异常！");
            e.printStackTrace();
        }
        return 0;
    }
}
