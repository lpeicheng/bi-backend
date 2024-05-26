package com.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao.mapper.ChartMapper;
import com.yupao.service.ChartService;
import com.yupao.model.entity.Chart;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author paopao
* @description 针对表【chart(图表信息)】的数据库操作Service实现
* @createDate 2024-05-01 23:07:24
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

}




