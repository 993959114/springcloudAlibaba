package com.msunsoft.spt.business.service.common.impl;

import com.msunsoft.spt.business.dto.AbstractBaseDomain;
import com.msunsoft.spt.business.service.common.BaseCrudService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.MyMapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.ParameterizedType;
import java.util.Date;

public class BaseCrudServiceImpl<T extends AbstractBaseDomain, M extends MyMapper<T>> implements BaseCrudService<T> {

    @Autowired
    public M mapper;

    private Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Override
    public boolean unique(String property, String value) {
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo(property, value);
        int result = mapper.selectCountByExample(example);
        if (result > 0) {
            return false;
        }
        return true;
    }

    @Override
    public T save(T domain) {
        int result = 0;
        Date currentDate = new Date();
        domain.setUpdated(currentDate);

        // 创建
        if (domain.getId() == null) {
            domain.setCreated(currentDate);

            /**
             * 用于自动回显 ID，领域模型中需要 @ID 注解的支持
             * {@link AbstractBaseDomain}
             */
            result = mapper.insertUseGeneratedKeys(domain);
        }

        // 更新
        else {
            result = mapper.updateByPrimaryKey(domain);
        }

        // 保存数据成功
        if (result > 0) {
            return domain;
        }

        // 保存数据失败
        return null;
    }

    @Override
    public PageInfo<T> page(T domain, int pageNum, int pageSize) {
        Example example = new Example(entityClass);
        example.createCriteria().andEqualTo(domain);

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<T> pageInfo = new PageInfo<>(mapper.selectByExample(example));
        return pageInfo;
    }

    @Override
    public int updateSelective(T domain) {
        int i = 0;
        i=mapper.updateByPrimaryKeySelective(domain);
        return i;
    }

    @Override
    public int deleteById(Object id) {
        int i = 0;
         i = mapper.deleteByPrimaryKey(id);
        return i;
    }
}
