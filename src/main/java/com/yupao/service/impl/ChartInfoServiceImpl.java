package com.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yupao.model.entity.Chart;
import com.yupao.model.entity.ChartInfo;
import com.yupao.service.ChartInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
* @author kuang
* @description 针对表【chart(图表信息)】的数据库操作Service实现
* @createDate 2024-05-01 23:07:24
*/

//@Service
//public class ChartInfoServiceImpl extends ServiceImpl<ChartInfoMapper, ChartInfo> implements ChartInfoService {
//    @Resource
//    private ChartInfoMapper chartMapper;
//
//    public boolean insertChart(Long id,String genChart,String genResult) {
//        boolean b = chartMapper.insertChart(id, genChart, genResult);
//        return b;
//    }
//
//    @Override
//    public List<Map<String,Object>> queryChartData(Long id) {
//        return chartMapper.queryChartData(id);
//    }
//}




