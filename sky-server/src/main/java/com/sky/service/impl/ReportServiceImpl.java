package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.LocalDateTime2TurpleDTO;
import com.sky.dto.OrderAmount;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        //改进后的方法,应该将LocalDate转化为LocalDateTime
        List<LocalDateTime2TurpleDTO> localDateTimes = localDate2LocalDateTime(dateList);

        List<OrderAmount> orderAmountList = orderMapper.countSumByDay(localDateTimes);
        List<Double> turnoverList = new ArrayList<>();
        for (OrderAmount orderAmount : orderAmountList) {
            if (orderAmount == null) {
                turnoverList.add(0.0);
            } else {
                turnoverList.add(orderAmount.getSum());
            }
        }

        //此处为视频中的方法，网络io资源消耗大
//        List<Double> turnoverList = new ArrayList<>();
//
//        for (LocalDate localDate:dateList){
//            LocalDateTime min = LocalDateTime.of(localDate, LocalTime.MIN);
//            LocalDateTime max   = LocalDateTime.of(localDate,LocalTime.MAX);
//
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("beginTime",min);
//            map.put("endTime",max);
//            map.put("status",5);
//            Double sum = orderMapper.sumByMap(map);
//            sum = sum == null ? 0.0 : sum;
//            turnoverList.add(sum);
//        }

        return TurnoverReportVO.builder()
            .dateList(StringUtils.join(dateList, ','))
            .turnoverList(StringUtils.join(turnoverList, ','))
            .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        //改进后用union all
        List<LocalDateTime2TurpleDTO> localDateTime2TurpleDTOList = localDate2LocalDateTime(dateList);

        List<Integer> newUserList = userMapper.countByDateList(localDateTime2TurpleDTOList);
        List<Integer> totalUserList = userMapper.sumByDateList(localDateTime2TurpleDTOList);

//          视频中的方法，io资源较大
//        for (LocalDate localDate : dateList) {
//
//            LocalDateTime min = LocalDateTime.of(localDate, LocalTime.MIN);
//            LocalDateTime max = LocalDateTime.of(localDate, LocalTime.MAX);
//
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("endTime", max);
//            totalUserList.add(userMapper.countByMap(map));
//            map.put("beginTime", min);
//            newUserList.add(userMapper.countByMap(map));
//
//        }

        return UserReportVO.builder()
            .dateList(StringUtils.join(dateList, ','))
            .newUserList(StringUtils.join(newUserList, ','))
            .totalUserList(StringUtils.join(totalUserList, ','))
            .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<LocalDateTime2TurpleDTO> localDate2LocalDateTimeList = localDate2LocalDateTime(dateList);

        List<Integer> orderCountList = orderMapper.countOrderNumByDay(localDate2LocalDateTimeList);
        List<Integer> validCountList = orderMapper.sumValidNumByDay(localDate2LocalDateTimeList);
        Integer total = orderCountList.stream().reduce(Integer::sum).get();
        Integer valid = validCountList.stream().reduce(Integer::sum).get();
        Double rate = 0.0;

        if (total != 0) {
            rate = valid.doubleValue() / total;
        }

        return OrderReportVO.builder()
            .dateList(StringUtils.join(dateList, ','))
            .orderCountList(StringUtils.join(orderCountList, ','))
            .validOrderCountList(StringUtils.join(validCountList, ','))
            .totalOrderCount(total)
            .validOrderCount(valid)
            .orderCompletionRate(rate)
            .build();
    }

    @Override
    public SalesTop10ReportVO getTopTenStatistics(LocalDate beginTime, LocalDate endTime) {
        LocalDateTime begin = LocalDateTime.of(beginTime, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endTime, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTO = orderDetailMapper.countTopTenByTimeSpan(begin, end);

        return SalesTop10ReportVO.builder()
            .nameList(StringUtils.join(goodsSalesDTO.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ','))
            .numberList(StringUtils.join(goodsSalesDTO.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ','))
            .build();

    }


    /**
     * 将LocalDate转化为LocalDateTime方便数据库查询
     *
     * @param dateList
     * @return
     */
    public List<LocalDateTime2TurpleDTO> localDate2LocalDateTime(List<LocalDate> dateList) {
        List<LocalDateTime2TurpleDTO> localDateTimes = new ArrayList<>();
        for (LocalDate date : dateList) {
            localDateTimes.add(LocalDateTime2TurpleDTO.builder()
                .begin(LocalDateTime.of(date, LocalTime.MIN))
                .end(LocalDateTime.of(date, LocalTime.MAX))
                .build());
        }

        return localDateTimes;
    }

    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        //生成begin到end之间的日期
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }

    public Integer getOrderCount(LocalDate begin, LocalDate end, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countOrderNumByMap(map);
    }
}
