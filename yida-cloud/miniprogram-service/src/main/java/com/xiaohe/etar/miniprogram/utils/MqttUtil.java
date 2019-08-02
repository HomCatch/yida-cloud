package com.xiaohe.etar.miniprogram.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import entity.adverstising.Advertising;
import entity.dev.DevConnInfo;
import entity.dev.Device;
import utils.ConstantUtil;

import java.util.Date;

/**
 * @author hzh
 * @date 2018/11/28
 */
public class MqttUtil {
    /**
     * 获取字符传长度
     *
     * @param s 要操作的字符串
     */
    private static int getLength(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    /**
     * 数字小于10返回01,02，03，否则返回本身字符串
     *
     * @param s 要操作的字符串
     */
    private static String strLen(String s) {
        int length = getLength(s);
        String change;
        if (length < ConstantUtil.STRING_LENGTH) {
            change = "0" + length;
        } else {
            change = String.valueOf(length);
        }
        return change;
    }

    /**
     * 组合下发广告类容
     *
     * @param ad 广告实体类
     * @return 组合结果
     */
    public static String getAdvMsg(Advertising ad, String prefix) {
        if (ad == null) {
            return null;
        }

        int id = ad.getId();

        String idStr = String.format("%0" + 4 + "d", id);

        byte[] targets = new byte[4];
        targets[3] = (byte) (id);
        targets[2] = (byte) ((id >> 8) & 0xff);
        targets[1] = (byte) ((id >> 16) & 0xff);
        targets[0] = (byte) ((id >> 24) & 0xff);

        String res = new String(targets);

        return prefix + res + ConstantUtil.MQTT_COMMA + MqttUtil.strLen(ad.getTitle()) + ConstantUtil.MQTT_COMMA

                + MqttUtil.strLen(ad.getSolgan()) + ConstantUtil.MQTT_COMMA + ad.getTitle() + ad.getSolgan();
    }


    /**
     * 组合下发时间
     *
     * @return 组合结果
     */
    public static String getTimeMsg() {
        return ConstantUtil.MQTT_TIME_PREFIX + DateUtil.format(new Date());
    }

    /**
     * 组合时间
     *
     * @return 组合结果
     */
    public static String getActiveTime() {
        return ConstantUtil.MQTT_ACTIVE_PREFIX + DateUtil.format(new Date());
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。
     */
    public static int bytesToInt(byte[] src, int offset) {
        byte[] targetByte = new byte[4];
        System.arraycopy(src, 0, targetByte, 0, 4);
        int value;
        value = (((targetByte[offset] & 0xFF) << 24)
                | ((targetByte[offset + 1] & 0xFF) << 16)
                | ((targetByte[offset + 2] & 0xFF) << 8)
                | (targetByte[offset + 3] & 0xFF));
        return value;
    }


    public static void main(String[] args) {
        Advertising ad = new Advertising();
        ad.setTitle("aaa");
        ad.setName("asdad");
        ad.setSolgan("ssdfsf");
        String advMsg = ConstantUtil.MQTT_ADV_PREFIX_BROADCAST + MqttUtil.strLen(ad.getTitle()) + ConstantUtil.MQTT_COMMA + MqttUtil.strLen(ad.getSolgan()) + ConstantUtil.MQTT_COMMA + ad.getName() + ad.getSolgan();

        System.out.println(advMsg);
        System.out.println(getTimeMsg());
        System.out.println(getActiveTime());
    }


}
