package com.yupao.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChartRetry implements Serializable {
    private Long chartId;
    private User loginUser;

    public Long getChartId() {
        return chartId;
    }

    private static final long serialVersionUID = 6481279756440089007L;
}
