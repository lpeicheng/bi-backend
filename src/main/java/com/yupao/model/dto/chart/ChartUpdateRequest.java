package com.yupao.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 *图表更新请求

 */
@Data
public class ChartUpdateRequest implements Serializable {

    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;


    private static final long serialVersionUID = 1L;
}