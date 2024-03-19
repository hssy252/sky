package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表里添加1条数据
        dishMapper.insert(dish);
        //为口味赋值菜品在数据库中的id,利用了主键返回
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        //可能用户并没有提交口味数据
        if (flavors != null && flavors.size() > 0) {
            //向口味表里添加多条数据
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 实现菜品分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<DishVO> dishVOs = dishMapper.pageQuery(dto);
        return new PageResult(dishVOs.getTotal(), dishVOs.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //删除菜品要进行大量检查
        //菜品是否在售卖？
//        for (Long id : ids) {
//            Dish dish = dishMapper.getById(id);
//            if (dish.getStatus() == StatusConstant.ENABLE){
//                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
//            }
//        }
        //优化
        List<Dish> dishes = dishMapper.queryUnsale(ids);
        if (dishes != null && dishes.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        //菜品是否有套餐关联
        List<Long> dishIds = setMealDishMapper.getSetmealIdsByDishIds(ids);
        if (dishIds != null && dishIds.size() > 0) {
            //有关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品
        dishMapper.deleteBatch(ids);

        //删除菜品后要删除对应口味
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }
}
