package com.etar.purifier.modules.dev.service;

import com.etar.purifier.base.IBaseService;
import entity.common.entity.PageBean;
import entity.dev.DevVo;
import entity.common.entity.Result;
import entity.dev.Device;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Device接口
 * </p>
 *
 * @author hzh
 * @since 2018-10-08
 */

public interface DeviceService extends IBaseService<Device, Integer> {
    /**
     * 通过devCode查询设备是否在线
     *
     * @param devCode 设备编码
     * @return 存在结果
     */
    boolean existsByDevCode(String devCode);

    /**
     * 绑定设备
     *
     * @param devCode 设备编码
     * @param userId  用户id
     */
    @Transactional(rollbackFor = Exception.class)
    void bindDev(String devCode, Integer userId);

    /**
     * 解除某个用户下所有设备的绑定
     *
     * @param devId 设备id
     */
    @Transactional(rollbackFor = Exception.class)
    void unbindDevByDevId(Integer devId);

    /**
     * 批量解除绑定
     *
     * @param devIds devID集合
     */
    @Transactional(rollbackFor = Exception.class)
    void unbindDevBatch(List<Integer> devIds);

    /**
     * 通过devCode查询对象
     *
     * @param devCode 设备编码
     * @return 对象
     */
    Device findByDevCode(String devCode);

    /**
     * 通过devCode 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页个数
     * @param devCode  设备编号
     * @param online   在线状态
     * @param userId   用户id
     * @return page对象
     */
    Page<Device> findPage(int page, int pageSize, String devCode, Integer online, Integer userId, String bindAccount);

    /**
     * 查询用户下所有设备
     *
     * @param userId 用户id
     * @return 集合
     */
    List<Device> findByUserId(Integer userId);

    /**
     * 更新设备
     *
     * @param device 要跟新的设备
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    int updatedDevice(Device device);

    /**
     * 获取设备绑定的账号
     *
     * @param deviceList 要获取绑定账号的列表
     */
    void getDevBindAccount(List<Device> deviceList);


    /**
     * 批量删除
     *
     * @param devIds devI
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteBatch(List<Integer> devIds);

    /**
     * 更新设备绑定状态
     *
     * @param status  设备状态
     * @param devCode 设备码
     * @return 绑定的结果
     */
    @Transactional(rollbackFor = Exception.class)
    int updateBindStatus(Integer status, String devCode);

    /**
     * 硬件手动解除绑定
     *
     * @param status  设备状态
     * @param devCode 设备码
     * @return 解除结果
     * @throws MqttException MqttException
     */
    @Transactional(rollbackFor = Exception.class)
    int hardwareUnbind(Integer status, String devCode) throws MqttException;

    /**
     * 硬件解绑 滤芯归零 激活状态改为未激活
     *
     * @param devCode    设备号
     * @param filterLife 滤芯寿命
     * @param active     激活状态
     */
    @Transactional(rollbackFor = Exception.class)
    void updateFilterLifeAndActive(String devCode, int filterLife, int active);

    /**
     * 开机修改上线状态
     *
     * @param online  上线状态
     * @param devCode 设备码
     * @return int int
     * @throws MqttException MqttException
     */
    @Transactional(rollbackFor = Exception.class)
    int updateOnline(Integer online, String devCode) throws MqttException;

    /**
     * 开机修改上线状态
     *
     * @param online  上线状态
     * @param ip      ip
     * @param devCode 设备码
     * @return int int
     * @throws MqttException MqttException
     */
    @Transactional(rollbackFor = Exception.class)
    int updateOnlineAndIpAndTime(Integer online, String ip, String devCode) throws MqttException;

    /**
     * 更新滤芯寿命
     *
     * @param filterLife 滤芯寿命
     * @param devCode    设备码
     */
    void updateFilterLife(Integer filterLife, String devCode);

    /**
     * 通过用户id的到绑定设备个数
     *
     * @param userId 用户id
     * @return 绑定设备数
     */
    Integer countByUserId(Integer userId);

    /**
     * 获取用户绑定设备列表（小程序）
     *
     * @param status 绑定状态
     * @param userId 用户id
     * @return 查询结果
     */
    List<Device> findUserBindDevList(Integer status, Integer userId);

    /**
     * 更换滤芯
     *
     * @param status  滤芯状态
     * @param devCode 设备编码
     */
    void changeFilter(Integer status, String devCode);

    /**
     * 下载模板
     */
    void downloadTemplate();

    /**
     * 批量导入
     *
     * @param file excel
     * @return 0成功 1失败
     */
    Result batchImport(MultipartFile file) throws Exception;

    /**
     * 批量导出
     *
     * @param ids 滤芯ids
     * @throws Exception Exception
     */
    void batchExport(List<Long> ids) throws Exception;

    /**
     * 统计今天在线设备数
     *
     * @return 设备数
     */
    long countTodayOnline();

    /**
     * 统计今天绑定设备数
     *
     * @return 设备数
     */
    long countTodayBind(Date startTime, Date endTime);

    /**
     * 统计今天激活设备数
     *
     * @return 设备数
     */
    long countTodayActive(Date startTime, Date endTime);

    /**
     * 统计今天入库设备数
     *
     * @return 设备数
     */
    long countInventoryc(Date startTime, Date endTime);

    /**
     * 通过手机号关联查询设备号、手机号、设备在线状态
     *
     * @param phone 手机号
     * @return 查询结果
     */
    PageBean<DevVo> findDevMsgByPhone(String phone, Integer page, Integer pageSize);

    /**
     * 通过设备号查询设备号、手机号、设备在线状态
     *
     * @param devCode 设备号
     * @return 查询结果
     */
    PageBean<DevVo> findDevMsgByDevCode(String devCode);

    /**
     * 通过手机号关联查询设备号的总数
     *
     * @param phone 手机号
     * @return 总数
     */
    Integer countDevMsgByPhone(String phone);

    /**
     * 通过版本号统计的设备数
     *
     * @param verNum 设备版本号
     * @return 设备数
     */
    Integer countByVersionNum(int verNum);
}


