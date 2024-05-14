package com.yupao.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao.annotation.AuthCheck;
import com.yupao.common.BaseResponse;
import com.yupao.common.DeleteRequest;
import com.yupao.common.ErrorCode;
import com.yupao.common.ResultUtils;
import com.yupao.constant.CommonConstant;
import com.yupao.constant.UserConstant;
import com.yupao.exception.BusinessException;
import com.yupao.exception.ThrowUtils;
import com.yupao.manager.AiManager;
import com.yupao.manager.RedisLimiterManager;
import com.yupao.model.dto.chart.*;
import com.yupao.model.entity.Chart;
import com.yupao.model.entity.User;
import com.yupao.model.vo.BiResponse;
import com.yupao.service.ChartService;
import com.yupao.service.UserService;
import com.yupao.utils.ExcelUtils;
import com.yupao.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","test"}) //指定执行环境
public class QueueController {

    @Resource
    private ThreadPoolExecutor executor;

    @GetMapping("/add")
    //接受参数放入线程池作为任务
    public void add(String name){
        //使用线程池执行异步任务
        CompletableFuture.runAsync(() -> {
            log.info("任务执行中："+name+",执行人："+Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },executor);
    }

    //返回线程状态信息
    @GetMapping("/get")
    public String get(){
        Map<String,Object> map=new HashMap<>();
        //队列长度
        int size = executor.getQueue().size();
        map.put("队列长度",size);
        long taskCount = executor.getTaskCount();
        map.put("任务总数",taskCount);
        long completedTaskCount = executor.getCompletedTaskCount();
        map.put("已完成任务数",completedTaskCount);
        int activeCount = executor.getActiveCount();
        map.put("正在执行的任务数",activeCount);
        return JSONUtil.toJsonStr(map);
    }
}
