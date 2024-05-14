package com.yupao.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态id插入图表
 */
@TableName(value ="chart_#{id}")
@Data
public class ChartInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成分析结果
     */
    private String genResult;
}