package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO turnoverReport(LocalDate begin, LocalDate end) {
        //得到每天的日期格式
        List<LocalDate> localDateList = new ArrayList<LocalDate>();
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            localDateList.add(begin);
        }
        String join = StringUtils.join(localDateList, ",");

        //得到每天营业额，前提是获得每天营业时间最大最小值,然后装入集合并转为字符串
        List<Double> moneyList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end", endTime);
            Double money = orderMapper.sumByMap(map);
            money = money == null ? 0.0 : money;
            moneyList.add(money);
        }
        String join1 = StringUtils.join(moneyList, ",");

        //装入返回值
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(join);
        turnoverReportVO.setTurnoverList(join1);

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //得到每天的日期格式
        List<LocalDate> localDateList = new ArrayList<LocalDate>();
        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            localDateList.add(begin);
        }
        String join = StringUtils.join(localDateList, ",");

        //得到每天新增的用户
        List<Integer> listNew = new ArrayList<>();
        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end", endTime);
            Integer newUser = userMapper.getUser(map);
            listNew.add(newUser);
        }

        //得到全部用户
        List<Integer> listAll = new ArrayList<>();
        for (LocalDate date : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer newUser = userMapper.getUser(map);
            listNew.add(newUser);
        }

        String join1 = StringUtils.join(listNew, ",");
        String join2 = StringUtils.join(listAll, ",");

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(join);
        userReportVO.setNewUserList(join1);
        userReportVO.setTotalUserList(join2);
        return userReportVO;
    }

    @Override
    public OrderReportVO orderReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String join = StringUtils.join(dateList, ",");

        //每天订单总数集合
        List<Integer> allList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer everyDayOrders = getOrderWithStatusAndTime(beginTime, endTime, null);
            allList.add(everyDayOrders);
        }
        String join1 = StringUtils.join(allList, ",");

        //每天有效订单数集合
        List<Integer> effectiveList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer everyDayEffectiveOrders = getOrderWithStatusAndTime(beginTime, endTime, Orders.COMPLETED);
            effectiveList.add(everyDayEffectiveOrders);
        }
        String join2 = StringUtils.join(effectiveList, ",");

        //订单总数
        Integer allOrders = 0;
        for (Integer everydayOrders : allList) {
            allOrders = allOrders + everydayOrders;
        }


        //总有效订单数
        Integer allEffectiveOrders = 0;
        for (Integer everydayEffectiveOrders : effectiveList) {
            allEffectiveOrders = allEffectiveOrders + everydayEffectiveOrders;
        }

        //订单完成率
        Double orderCompletionRate = 0.0;
        if(allOrders != 0){
            orderCompletionRate = allEffectiveOrders.doubleValue() / allOrders;
        }

        //封装返回值
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(join);
        orderReportVO.setOrderCountList(join1);
        orderReportVO.setValidOrderCountList(join2);
        orderReportVO.setTotalOrderCount(allOrders);
        orderReportVO.setValidOrderCount(allEffectiveOrders);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    private Integer getOrderWithStatusAndTime(LocalDateTime beginTime, LocalDateTime endTime, Integer status){
       Map map = new HashMap();
       map.put("status", status);
       map.put("begin",beginTime);
       map.put("end", endTime);
       return orderMapper.getNumberByTimeAndStatus(map);
    }
}
