package com.yupao.mapper;

import com.yupao.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author paopao
* @description 针对表【chart(图表信息)】的数据库操作Mapper
* @createDate 2024-05-01 23:07:24
* @Entity com.yupi.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {
       List<Map<String,Object>> queryChartData(String querySql);
}




