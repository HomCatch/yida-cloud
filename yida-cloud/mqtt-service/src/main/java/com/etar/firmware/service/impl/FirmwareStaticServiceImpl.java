package com.etar.firmware.service.impl;

import com.etar.firmware.jpa.FirmwareStaticRepository;
import com.etar.firmware.service.FirmwareStaticService;
import entity.firmwarestatic.FirmwareStatic;
import entity.firmwarestatic.QueryFirmwareStatic;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * FirmwareStatic服务类
 * </p>
 *
 * @author hzh
 * @since 2019-07-17
 */

@Service
public class FirmwareStaticServiceImpl implements FirmwareStaticService {

    @Autowired
    private FirmwareStaticRepository firmwareStaticRepository;

    /**
     * 保存对象
     *
     * @param firmwareStatic 对象
     *                       持久对象，或者对象集合
     */
    @Override
    public FirmwareStatic save(FirmwareStatic firmwareStatic) {
        return firmwareStaticRepository.save(firmwareStatic);
    }

    /**
     * 删除对象
     *
     * @param firmwareStatic 对象
     */
    @Override
    public void delete(FirmwareStatic firmwareStatic) {
        firmwareStaticRepository.delete(firmwareStatic);
    }

    @Override
    public void deleteAll(List<FirmwareStatic> list) {
        firmwareStaticRepository.deleteAll(list);
    }

    @Override
    public void deleteById(Integer id) {
        firmwareStaticRepository.deleteById(id);
    }

    @Override
    public List<FirmwareStatic> findList() {
        return firmwareStaticRepository.findAll();
    }

    /**
     * 通过id判断是否存在
     *
     * @param id
     */
    @Override
    public boolean existsById(Integer id) {
        return firmwareStaticRepository.existsById(id);
    }

    /**
     * 返回可用实体的数量
     */
    @Override
    public long count() {
        return firmwareStaticRepository.count();
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return FirmwareStatic对象
     */
    @Override
    public FirmwareStatic findById(Integer id) {
        Optional<FirmwareStatic> optional = firmwareStaticRepository.findById(id);
        boolean present = optional.isPresent();
        return present ? optional.get() : null;
    }

    /**
     * 分页查询
     * id处字符串为需要排序的字段，可以传多个，比如 "id","createTime",...
     *
     * @param page     页面
     * @param pageSize 页面大小
     * @return Page<FirmwareStatic>对象
     */
    @Override
    public Page<FirmwareStatic> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        return firmwareStaticRepository.findAll(pageable);
    }

    @Override
    public Page<FirmwareStatic> findAll(int page, int pageSize, QueryFirmwareStatic queryFirmwareStatic) {
        //过滤自己的广告
        Pageable pageable = PageRequest.of(page, pageSize, Sort.Direction.DESC, "id");
        //查询条件构造
        Specification<FirmwareStatic> spec = new Specification<FirmwareStatic>() {
            @Override
            public Predicate toPredicate(Root<FirmwareStatic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                if (queryFirmwareStatic.getFmId() != null) {
                    predicate.getExpressions().add(cb.equal(root.get("fmId").as(Integer.class), queryFirmwareStatic.getFmId()));
                }

                if (StringUtils.isNotBlank(queryFirmwareStatic.getFmName())) {
                    predicate.getExpressions().add(cb.like(root.get("fmName").as(String.class), "%" + queryFirmwareStatic.getFmName() + "%"));
                }
                if (StringUtils.isNotBlank(queryFirmwareStatic.getDevCode())) {
                    predicate.getExpressions().add(cb.like(root.get("devCode").as(String.class), "%" + queryFirmwareStatic.getDevCode() + "%"));
                }
                return predicate;
            }

        };
        return firmwareStaticRepository.findAll(spec, pageable);
    }

    /**
     * 根据Id查询list
     *
     * @param ids id集合
     * @return list
     */
    @Override
    public List<FirmwareStatic> findAllById(List<Integer> ids) {
        return firmwareStaticRepository.findAllById(ids);
    }

    @Override
    public void saveOnRespone(FirmwareStatic firmwareStatic) {
        FirmwareStatic fs = firmwareStaticRepository.findByFmIdAndDevCode(firmwareStatic.getFmId(), firmwareStatic.getDevCode());
        //如果固件id和设备号能查到固件响应结果，则进行更新，否则新增
        if (fs != null) {
            firmwareStatic.setId(fs.getId());
        }
        save(firmwareStatic);
    }
}


