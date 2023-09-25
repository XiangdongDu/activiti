package boot.spring.ssh;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.beetl.sql.test.MysqlDBConfig.password;
import static org.beetl.sql.test.MysqlDBConfig.userName;

/**
 * @Author duxiangdong
 * @Date 2023/8/22 11:06
 * @Version 1.0
 */
public class SSHConnect {
    private static final Logger logger = LoggerFactory.getLogger(SSHConnect.class);


    public static void main(String[] args) {

        String hostName = "127.0.0.1";
        int port = 22;

        Connection conn = new Connection(hostName, port);
        try {
            conn.connect();
// 第一种,使用用户名密码验证身份
            conn.authenticateWithPassword(userName, password);
// 第二种,使用用户名密码以及秘钥验证身份
//        conn.authenticateWithPassword(/*用户名*/userName, /*秘钥文件*/keyFile, /*密码*/password);
            // 此时就可以用conn do something啦!
            Session session = null;
            session = conn.openSession();

            String command = "cd /home";
            session.execCommand(/*要执行的命令*/command);

            // 获取输入流用来读取执行结果,自己封装流read即可
            InputStream input = new StreamGobbler(session.getStdout());
            // do read

            StringBuffer buffer = new StringBuffer(); // 保存读取到的内容
            byte data[] = new byte[8]; // 开辟1K的空间进行读取
            int len = 0;// 保存数据读取的个数
            do {
                len = input.read(data); // 读取数据到字节数组并返回读取个数
                if (len != -1) {
                    buffer.append(new String(data, 0, len)); // 每次读取到的内容保存在缓冲流中
                }
            } while (len != -1); // 没有读取到底
            try {
                handException(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("读取到的数据内容【" + buffer + "】");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void handException(Session session) throws Exception {

        int exit_status;
        for (int i = 0; i < 60; i++) {
            if (session.getExitStatus() == null) {
                logger.info("执行服务器命令进行中，延长连接时间{}[秒]", i + 1);
                Thread.sleep(1000);
                if (i == 59) {
                    String error = inputStreamRead(session.getStderr());
                    throw new RuntimeException("连接服务器超时60秒自动断开，请重试！" + error);
                }

            } else {
                exit_status = session.getExitStatus();
                if (exit_status != 0) {
                    String error = inputStreamRead(session.getStderr());
                    throw new RuntimeException("执行命令失败，返回错误信息！" + error);
                }
            }
        }


    }

    private static String inputStreamRead(InputStream in) throws IOException {
        InputStream inputStream = new StreamGobbler(in);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) break;
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
            i++;
            if (i > 1000) {
                stringBuilder.append("shell out over 1000 lines,please use stream exec instead");
                break;
            }
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();

    }

}


