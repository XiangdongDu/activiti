package boot.spring.ocr;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * 本类暂不使用仅供参考
 * 图像文字识别
 */
public class CheckOCR {

    //百度通用文字识别接口
    private static final String POST_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/webimage?access_token=" + AuthService.getAuth();

    /**
     * 识别本地图片的文字
     *
     * @param path 本地图片地址
     * @return 识别结果，为json格式
     * @throws URISyntaxException URI打开异常
     * @throws IOException        io流异常
     */
    public static String checkFile(String path) throws URISyntaxException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("图片不存在");
        }
        return checkJKMByPost();
    }


    /**
     * 扫描健康码截图信息是否异常
     * 注意：access_token的有效期为30天，需要每30天进行定期更换；
     *
     * @param
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private static String checkJKMByPost() throws URISyntaxException, IOException {
        PostMethod postMethod = new PostMethod(POST_URL);
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        //参数设置，需要注意的就是里边不能传NULL，要传空字符串
        NameValuePair[] data = {
                new NameValuePair("url","https://pics2.baidu.com/feed/0823dd54564e9258010276610022a251cdbf4eb1.jpeg?token=c1cf681fea5ff0f7d2fb8fc737fd3691"),
//                new NameValuePair("url", "https://jkm-1309339742.cos.ap-shanghai.myqcloud.com/dxdjkm.png"),
//                new NameValuePair("url", "/Users/duxiangdong/F/work/lunwen/activiti/spring-boot-activiti/1.jpg"),
                new NameValuePair("detect_direction", "false"),
                new NameValuePair("paragraph", "false"),
                new NameValuePair("probability", "false")
        };
        postMethod.setRequestBody(data);

        HttpClient httpClient = new HttpClient();
        int statusCode = httpClient.executeMethod(postMethod); // 执行POST方法
        String response = postMethod.getResponseBodyAsString();
        System.out.println("===response==============>>" + response);
        return null;

    }

    public static void main(String[] args) {
//        String path = "E:\\find.png";
//        String path = "https://jkm-1309339742.cos.ap-shanghai.myqcloud.com/jkm.png";
        try {
            long now = System.currentTimeMillis();
//            checkFile(path);
//            post(null);
            checkJKMByPost();
//            System.out.println("耗时：" + (System.currentTimeMillis() - now) / 1000 + "s");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}