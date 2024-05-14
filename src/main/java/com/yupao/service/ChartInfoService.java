package com.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupao.model.entity.Chart;
import com.yupao.model.entity.ChartInfo;

import java.util.List;
import java.util.Map;

/**
* @author kuang
* @description 针对表【chart(图表信息)】的数据库操作Service
* @createDate 2024-05-01 23:07:24
*/
public interface ChartInfoService extends IService<ChartInfo> {
    List<Map<String,Object>> queryChartData(Long id);
    boolean insertChart(Long id,String genChart,String genResult);
}
