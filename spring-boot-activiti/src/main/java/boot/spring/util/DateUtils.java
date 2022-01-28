package boot.spring.util;

import boot.spring.po.LeaveApply;
import boot.spring.service.impl.LeaveServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author duxiangdong
 * @Date 2022/1/11 14:44
 * @Version 1.0
 */
public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    /**
     * 获取两个日期之间的工作日天数（排除节假日以及周六周日）
     *
     * @param strStartDate
     * @param strEndDate
     * @return
     */
    public static int getDutyDays(String strStartDate, String strEndDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = df.parse(strStartDate);
            endDate = df.parse(strEndDate);
        } catch (ParseException e) {
            System.out.println("非法的日期格式,无法进行转换");
            e.printStackTrace();
        }
        int result = 0;
        while (startDate.compareTo(endDate) <= 0) {
            if (startDate.getDay() != 6 && startDate.getDay() != 0)
                result++;
            startDate.setDate(startDate.getDate() + 1);
        }

        return result;
    }


    /**
     * 获取两个日期之间的天数
     */
    public static Integer getDays(String start, String end) {
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

    /**
     * 获取今天各种类型的日期格式
     *
     * @return
     */
    public static List<String> getTodayDate() {
        List<String> today = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today1 = sdf.format(new Date());
        today.add(today1);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
        String today2 = sdf2.format(new Date());
        today.add(today2);
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd");
        String today3 = sdf3.format(new Date());
        today.add(today3);
        return today;
    }

}
