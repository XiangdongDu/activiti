package boot.spring.ocr;

import boot.spring.util.DateUtils;
import com.baidu.aip.ocr.AipOcr;
import groovy.util.logging.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * @Author duxiangdong
 * @Date 2022/1/20 12:40
 * @Version 1.0
 * @desc 百度智能云 图像文字识别通用文字识别接口
 * 注意：access_token的有效期为30天，需要每30天进行定期更换；目前每月200次免费调用额度（注意使用次数）
 * 我的百度智能云地址 https://console.bce.baidu.com/ai/?_=1642668578548#/ai/ocr/overview/index
 * 参考文档
 * https://cloud.baidu.com/doc/OCR/s/Ikibizxql
 * https://cloud.baidu.com/doc/OCR/s/Ck3h7y2ia
 */
@Slf4j
public class BaiduOCR {
    private static final Logger logger = LoggerFactory.getLogger(BaiduOCR.class);

    //设置APPID/AK/SK
    public static final String APP_ID = "25654514";
    public static final String API_KEY = "bQF79wXwhRn7mdW9dtTIeqdN";
    public static final String SECRET_KEY = "uj7wIfrOBfCizZ3Nt8Su1uwGbHto7L7t";

    public static void main(String[] args) {
//        String path = "/Users/duxiangdong/F/work/lunwen/activiti/spring-boot-activiti/src/main/webapp/uploadfiles/jkm.png";
//        String path = "https://pics2.baidu.com/feed/0823dd54564e9258010276610022a251cdbf4eb1.jpeg?token=c1cf681fea5ff0f7d2fb8fc737fd3691";
        String path = "/Users/duxiangdong/F/work/lunwen/activiti/spring-boot-activiti/src/main/webapp/uploadfiles/dxdjkm.png";
        // 高精度版本-调用接口  参数为本地图片路径请求格式支持：PNG、JPG、JPEG、BMP、TIFF、PNM、WebP
        JSONObject accurateBasic = accurateBasic(path);
        //校验健康码是否正常
//        String result = checkJKM(accurateBasic);
        String result = "正常";
        System.out.println("健康码检验结果=========>" + result);

    }

    /*
     *百度智能云通用文字识别（高精度版）
     *用户向服务请求识别某张图中的所有文字，相对于通用文字识别该产品精度更高，但是识别耗时会稍长。
     */
    public static JSONObject accurateBasic(String path) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "false");
        options.put("probability", "false");
        options.put("paragraph", "false");
        options.put("language_type", "CHN_ENG");//默认中英文识别
        //参数为本地图片路径
        JSONObject res = client.basicAccurateGeneral(path, options);
        logger.info("OCR识别结果返回=====>{}", res.toString(2));
        return res;
    }

    /**
     * 校验健康码是否正常
     *
     * @param res
     * @return
     */
    public static String checkJKM(JSONObject res) {
        try {
            int num = (int) res.get("words_result_num");
            if (num > 0) {
                JSONArray array = (JSONArray) res.get("words_result");
                String result = "";
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonobject = array.getJSONObject(i);
                    String word = (String) jsonobject.get("words");
                    logger.info(word);
                    result = result + word;
//                    if (word != null && word.length() == 18 && !word.contains("*")) {
//                        logger.info("today===>{}", word.substring(0, 10));
//                        //检验日期是否今天的健康码截图
//                        List<String> todayType = DateUtils.getTodayDate();
//                        Boolean flg = false;
//                        for (String str : todayType) {
//                            if (str.equals(word.substring(0, 10)))
//                                flg = true;
//                        }
//                        if (flg == false)
//                            return "健康码截图日期失效，请重新上传今天健康码截图！";
//                    }
//                    if (word != null && (word.contains("绿色") || word.contains("未见异常"))) {
//                        return "正常";
//                    }
                }
                logger.info("图片内容：" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解析OCR并校验健康码内容出现异常...{}", e.getMessage());
            return "解析OCR并校验健康码内容出现异常";
        }
        return "异常";
    }


}
