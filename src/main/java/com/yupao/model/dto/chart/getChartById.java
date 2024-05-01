package com.yupao.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过id查询图表

 */
@Data
public class getChartById implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}