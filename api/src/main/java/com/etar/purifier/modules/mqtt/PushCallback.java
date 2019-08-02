package com.etar.purifier.modules.mqtt;


import com.etar.purifier.config.ApplicationContextUtils;
import com.etar.purifier.modules.advStatic.entity.AdvStatic;
import com.etar.purifier.modules.advertising.entity.Advertising;
import com.etar.purifier.modules.advertising.service.AdvertisingService;
import com.etar.purifier.modules.dev.entiy.Device;
import com.etar.purifier.modules.dev.service.DeviceService;
import com.etar.purifier.modules.filterinfo.service.FilterInfoService;
import com.etar.purifier.utils.ConstantUtil;
import com.etar.purifier.utils.MqttUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;


import java.util.Date;

/**
 * mqtt回调函数
 *
 * @author hzh
 * @version 1.0
 * @date 2019/1/17 13:42
 */
@Component
public class PushCallback implements MqttCallback {
    private static Logger log = LoggerFactory.getLogger(PushCallback.class);


    @Override
    public void connectionLost(Throwable cause) {
        try {
            MqttClient client = MqttPushClient.getClient();
            if (!client.isConnected()) {
                client.reconnect();
                System.out.println("client连接断开，重连成功");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            int msgId = token.getMessageId();
            System.out.println("deliveryComplete---------" + msgId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题:" + topic);
        String[] infos = topic.split("/");

        System.out.println("接收消息Qos:" + message.getQos());
        String ret = new String(message.getPayload());
        ret = StringUtils.trim(ret);
        System.out.println("接收消息内容:" + ret);
        if (topic.contains(ConstantUtil.MQTT_RET_TOPIC)) {
            String devCode = infos[1].trim();
            if (ConstantUtil.MQTT_ACTIVE_SUCCESS.equals(ret)) {
                System.out.println("激活:" + ret);
                ApplicationContextUtils.getDeviceService().updateBindStatus(ConstantUtil.ACTIVE, devCode);
                return;
            }
            //解除绑定
            if (ConstantUtil.MQTT_UNBIND_SUCCESS.equals(ret)) {
                System.out.println("解除绑定 : " + ret);
                ApplicationContextUtils.getDeviceService().updateBindStatus(ConstantUtil.UN_ACTIVE, devCode);
                return;
            }
            //上线
            if (ConstantUtil.MQTT_OPEN_SUCCESS.equals(ret)) {
                System.out.println("开机 : " + ret);
                try {
                    ApplicationContextUtils.getDeviceService().updateOnline(ConstantUtil.ONLINE, devCode);
                } catch (MqttException e) {
                    try {
                        MqttPushClient.getClient().reconnect();
                    } catch (MqttException e1) {
                        e1.printStackTrace();
                    }
                }
                return;
            }
            //滤芯寿命
            if (ret.contains(ConstantUtil.MQTT_FILTER_LIFE)) {
                String[] split = ret.split(",");
                System.out.println("滤芯寿命 : " + ret);
                Integer filterLife = Integer.valueOf(split[1].trim());
                ApplicationContextUtils.getDeviceService().updateFilterLife(filterLife, devCode);
                return;
            }
            //硬件恢复出厂设置，发送解绑
            if (ret.contains(ConstantUtil.MQTT_HARDWARE_UNBIND)) {
                System.out.println("硬件手动解绑 : " + ret);
                try {
                    ApplicationContextUtils.getDeviceService().hardwareUnbind(ConstantUtil.UN_ACTIVE, devCode);
                } catch (MqttException e) {
                    try {
                        MqttPushClient.getClient().reconnect();
                    } catch (MqttException e1) {
                        e1.printStackTrace();
                    }
                }
                return;
            }
            //更换滤芯
            if (ret.startsWith(ConstantUtil.MQTT_HARDWARE_CHANGE_FILTER)) {
                String[] split = ret.split(",");
                if (split[1].equals(ConstantUtil.HARD_GET_TIME_SUCCESS)) {
                    System.out.println("更换滤芯 : " + split[1].trim());
                    Device byDevCode = ApplicationContextUtils.getDeviceService().findByDevCode(devCode);
                    if (byDevCode != null) {
                        //滤芯更新为已使用
                        ApplicationContextUtils.getFilterInfoService().updateFilterCode(1, byDevCode.getFilterCode());
                        //绑定滤芯
                        ApplicationContextUtils.getDeviceService().updateFilterLife(ConstantUtil.ACTIVE_FILTER, devCode);
                    }
                } else if (split[1].trim().equals(ConstantUtil.HARD_GET_TIME_FAIL)) {
                    log.info("设备同步时间失败");
                }
            }
        } else if (topic.contains(ConstantUtil.MQTT_HARDWARE_ADVRET)) {
            System.out.println("群发广告应答");
            String advRet = new String(message.getPayload());
            String[] advRets = advRet.split(",");
            if ("1".equals(advRets[0].trim())) {
                int bytesToInt = MqttUtil.bytesToInt(advRets[2].getBytes(), 0);
                Advertising advertising = ApplicationContextUtils.getAdvertisingService().findById(bytesToInt);

                AdvStatic advStatic = new AdvStatic();
                advStatic.setDevId(advRets[1].trim());
                advStatic.setAdvName(advertising.getName());
                advStatic.setAdvId(bytesToInt);
                advStatic.setReportTime(new Date());

                ApplicationContextUtils.getAdvStaticService().save(advStatic);
            }
        }
    }


}

