package com.sky.mapper;

import com.sky.entity.DishFlavor;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量增加数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    void deleteBatchByDishIds(List<Long> ids);
}
