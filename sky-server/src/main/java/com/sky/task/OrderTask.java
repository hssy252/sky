package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 检查过期的未支付订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟
    public void processTimeOutOrders() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (orders != null && orders.size() != 0) {
            List<Long> ids = new ArrayList<>();
            orders.forEach((order) ->
                ids.add(order.getId())
            );
            orderMapper.updateTimeOutByIds(Orders.CANCELLED, "订单超时，自动取消", LocalDateTime.now(), ids);
        }
    }

    /**
     * 检查一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点
    public void processDeliveringOrders() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (orders != null && orders.size() != 0) {
            List<Long> ids = new ArrayList<>();
            orders.forEach((order) ->
                ids.add(order.getId())
            );
            orderMapper.updateDeliveringByIds(Orders.COMPLETED, ids);
        }
    }

}
