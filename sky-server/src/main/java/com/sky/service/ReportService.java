package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import java.time.LocalDate;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
public interface ReportService {

    /**
     * 根据日期查询营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin,LocalDate end);

    /**
     * 根据日期统计用户总数和新增量
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getTopTenStatistics(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}
