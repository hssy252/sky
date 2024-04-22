package com.sky.mapper;

import com.sky.entity.OrderDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
@Mapper
public interface OrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetailList);
}
