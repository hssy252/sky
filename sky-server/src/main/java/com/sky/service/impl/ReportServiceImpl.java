package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.LocalDateTime2TurpleDTO;
import com.sky.dto.OrderAmount;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    @Autowired
    private WorkspaceService workspaceService;

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
     * 导出近三十天运营数据
     *
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1.查询数据库获取概览数据
        LocalDate dateBegin = LocalDate.now().plusDays(-30);
        LocalDate dateEnd = LocalDate.now().plusDays(-1);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN),
            LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2.将概览数据写入内存中的xls
        //通过类路径获取输入流对象
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格文件的sheet 页
            XSSFSheet sheet = excel.getSheet("sheet1");
            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间: " + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());


            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN),
                    LocalDateTime.of(dateEnd, LocalTime.MAX));
                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3.将内存中的xls文件对象写入response流
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            outputStream.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
