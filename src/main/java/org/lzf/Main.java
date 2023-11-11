package org.lzf;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ${USER}
 * @version 1.0
 * @description:
 * @date ${DATE} ${TIME}
 */
public class Main {
    static String isCreditCode = "true";
    static String error_CreditCode = "社会信用代码有误";
    static String error_CreditCode_min = "社会信用代码不足18位，请核对后再输！";
    static String error_CreditCode_max = "社会信用代码大于18位，请核对后再输！";
    static String error_CreditCode_empty = "社会信用代码不能为空！";
    private static Map<String, Integer> datas = null;
    static int[] power = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
    // 社会统一信用代码不含（I、O、S、V、Z） 等字母
    static char[] code = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'W', 'X', 'Y'};

    static {
        //初始化放入hashMap
        initDatas(code.length);
    }

    /**
     * 判断是否是一个有效的社会信用代码
     *
     * @param creditCode
     * @return
     */
    static String isCreditCode(String creditCode) {
        if (creditCode != null) {
            creditCode = creditCode.trim();
        } else {
            return error_CreditCode_empty;
        }
        if ("".equals(creditCode) || " ".equals(creditCode)) {
            return error_CreditCode_empty;
        } else if (creditCode.length() < 18) {
            return error_CreditCode_min;
        } else if (creditCode.length() > 18) {
            return error_CreditCode_max;
        } else {
            char[] pre17s = pre17(creditCode);
            int sum = sum(pre17s);
            int temp = sum % 31;
            temp = temp == 0 ? 31 : temp;

            return creditCode.substring(17, 18).equals(code[31 - temp] + "") ? isCreditCode : error_CreditCode;
        }
    }

    /**
     * @param chars
     * @return
     */
    private static int sum(char[] chars) {
        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            int code = datas.get(chars[i] + "");
            sum += power[i] * code;
        }
        return sum;

    }

    /**
     * 获取前17位字符
     *
     * @param creditCode
     */
    static char[] pre17(String creditCode) {
        String pre17 = creditCode.substring(0, 17);
        return pre17.toCharArray();
    }

    /**
     * 初始化数据
     *
     * @param count
     */
    static void initDatas(int count) {
        datas = new HashMap<>();
        for (int i = 0; i < code.length; i++) {
            datas.put(code[i] + "", i);
        }
    }

    /**
     * （调用）
     * 验证信用社会代码
     */
    public static String validateCreditCode(String creditCode) {

        return isCreditCode(creditCode);
    }

    public static void main(String[] args) {
        BufferedWriter writer = null;
        try {
            File directory = new File("C:\\home\\");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            List<String> creditCodeList = Files.lines(Paths.get("C:\\home\\出票人账号（统一社会信用代码）.txt")).collect(Collectors.toList());
            List<String> drawerList = Files.lines(Paths.get("C:\\home\\出票人.txt")).collect(Collectors.toList());
            writer = new BufferedWriter(new FileWriter("C:\\home\\校验结果.txt"));
            for (int i = 0; i < creditCodeList.size(); i++) {
                String drawer = drawerList.get(i);
                String creditCode = creditCodeList.get(i);
                if (!"true".equalsIgnoreCase(validateCreditCode(creditCode).trim())) {
                    while (drawer.length() < 30) {
                        drawer += "\u3000";
                    }
                    while (creditCode.length() < 20) {
                        creditCode += " ";
                    }
                    writer.write(drawer + "-" + creditCode + "：" + validateCreditCode(creditCode) + "\r\n");
                }
            }
        } catch (Exception exception) {
            try {
                writer = new BufferedWriter(new FileWriter("C:\\home\\校验结果.txt"));
                writer.write("在【C】盘的【home】目录下未找到相关的文件【出票人账号（统一社会信用代码）.txt和出票人.txt】");
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
            exception.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
