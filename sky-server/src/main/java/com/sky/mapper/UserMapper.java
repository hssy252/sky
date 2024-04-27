package com.sky.mapper;

import com.sky.dto.LocalDateTime2TurpleDTO;
import com.sky.entity.User;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select * from user where id=#{id}")
    User getById(Long userId);

    Integer countByMap(HashMap<String, Object> map);

    /**
     * 统计用户每天的新增量
     * @param dateList
     * @return
     */
    List<Integer> countByDateList(List<LocalDateTime2TurpleDTO> dateList);

    /**
     * 按天统计用户总量
     * @param dateList
     * @return
     */
    List<Integer> sumByDateList(List<LocalDateTime2TurpleDTO> dateList);

}
