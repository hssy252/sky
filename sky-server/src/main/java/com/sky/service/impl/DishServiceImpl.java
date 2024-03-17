package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
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

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表里添加1条数据
        dishMapper.insert(dish);
        //为口味赋值菜品在数据库中的id,利用了主键返回
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        //可能用户并没有提交口味数据
        if (flavors!=null && flavors.size()>0){
            //向口味表里添加多条数据
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors) ;
        }

    }
}
