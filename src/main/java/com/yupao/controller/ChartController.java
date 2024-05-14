package com.yupao.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao.common.BaseResponse;
import com.yupao.common.ErrorCode;
import com.yupao.common.ResultUtils;
import com.yupao.constant.CommonConstant;
import com.yupao.manager.AiManager;
import com.yupao.manager.RedisLimiterManager;
import com.yupao.model.dto.chart.*;
import com.yupao.model.vo.BiResponse;
import com.yupao.service.ChartService;
import com.yupao.annotation.AuthCheck;
import com.yupao.common.DeleteRequest;
import com.yupao.constant.UserConstant;
import com.yupao.exception.BusinessException;
import com.yupao.exception.ThrowUtils;
import com.yupao.model.entity.Chart;
import com.yupao.model.entity.User;
import com.yupao.service.UserService;
import com.yupao.utils.ExcelUtils;
import com.yupao.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

//    @Resource
//    private ChartInfoService chartInfoService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor executor;
    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        Long id = chartUpdateRequest.getId();
        Chart chart1 = chartService.getById(id);
        //判断是否存在
        ThrowUtils.throwIf(chart1==null,ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                     HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 同步展示结果
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        // 如果名称不为空，并且名称长度大于100，就抛出异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        /**
         * 校验文件
         */
        long size = multipartFile.getSize();
        //原始文件名
        String originalFilename = multipartFile.getOriginalFilename();
        //文件名后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        //检验文件大小
        final long ONE_MB=1024*1024L;
        // 文件大小超过1MB，就抛出异常，并给出提示
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1MB");
        //校验文件后缀
        final List<String> suffixList = Arrays.asList("xlsx","xsl");
        ThrowUtils.throwIf(!suffixList.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀非法");
        User loginUser = userService.getLoginUser(request);
        long modelId=1786421282817560578L;
        //限流
        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());
        /*
    * 用户的输入(参考)
      分析需求：
      分析网站用户的增长情况
      原始数据：
      日期,用户数
      1号,10
      2号,20
      3号,30
    * */

        //构建输入
        StringBuilder input=new StringBuilder();
        input.append("分析需求：").append("\n");
        String userGoal=goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal+=",请使用"+chartType;
        }
        input.append(userGoal).append("\n");
        input.append("原始数据：").append("\n");
        //将压缩后的数据填入
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        input.append(csvData).append("\n");
        //最后返回结果提交任务
        // todo 建议处理任务队列满了后,抛异常的情况(因为提交任务报错了,前端会返回异常)
        String result = aiManager.doChat(modelId, input.toString());
        String[] splits = result.split("【【【【【");
        if(splits.length<3){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Ai生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        //设置执行状态为等待中
        chart.setStatus("wait");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "保存在总表失败");
//        createTableByID(chart.getId());
//        boolean saveResult2 = chartInfoService.insertChart(chart.getId(),genChart,genResult);
//        ThrowUtils.throwIf(!saveResult2, ErrorCode.SYSTEM_ERROR, "保存分表失败");
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        return ResultUtils.success(biResponse);
    }

    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        // 如果分析目标为空，就抛出请求参数错误异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        // 如果名称不为空，并且名称长度大于100，就抛出异常，并给出提示
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        /**
         * 校验文件
         */
        long size = multipartFile.getSize();
        //原始文件名
        String originalFilename = multipartFile.getOriginalFilename();
        //文件名后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        //检验文件大小
        final long ONE_MB=1024*1024L;
        // 文件大小超过1MB，就抛出异常，并给出提示
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小超过1MB");
        //校验文件后缀
        final List<String> suffixList = Arrays.asList("xlsx","xsl");
        ThrowUtils.throwIf(!suffixList.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀非法");
        User loginUser = userService.getLoginUser(request);
        long modelId=1786421282817560578L;
        //限流
        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());
        /*
    * 用户的输入(参考)
      分析需求：
      分析网站用户的增长情况
      原始数据：
      日期,用户数
      1号,10
      2号,20
      3号,30
    * */

        //构建输入
        StringBuilder input=new StringBuilder();
        input.append("分析需求：").append("\n");
        String userGoal=goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal+=",请使用"+chartType;
        }
        input.append(userGoal).append("\n");
        input.append("原始数据：").append("\n");
        //将压缩后的数据填入
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        input.append(csvData).append("\n");

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        //设置执行状态为等待中
        chart.setStatus("wait");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "保存在总表失败");
//        createTableByID(chart.getId());
//        boolean saveResult2 = chartInfoService.insertChart(chart.getId(),genChart,genResult);
//        ThrowUtils.throwIf(!saveResult2, ErrorCode.SYSTEM_ERROR, "保存分表失败");

        //最后返回结果提交任务
        // todo 建议处理任务队列满了后,抛异常的情况(因为提交任务报错了,前端会返回异常)
        CompletableFuture.runAsync(() -> {
    // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。(为了防止同一个任务被多次执行)
            Chart updateChart=new Chart();
            updateChart.setStatus("running");
            boolean b = chartService.updateById(updateChart);
            if (!b){
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            }

            String result = aiManager.doChat(modelId, input.toString());
            String[] splits = result.split("【【【【【");
            if(splits.length<3){
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();

            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus("succeed");
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        },executor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    // 上面的接口很多用到异常,直接定义一个工具类
    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

    public void createTableByID (Long id){
            String url = "jdbc:mysql://localhost:3306/bi";
            String user = "root";
            String password = "123456";
            String tableName = "chart";

            try {
                // 加载数据库驱动
                Class.forName("com.mysql.cj.jdbc.Driver");

                // 建立数据库连接
                Connection connection = DriverManager.getConnection(url, user, password);

                // 创建一个Statement对象，用于执行SQL语句
                Statement statement = connection.createStatement();

                // 根据chart表的ID构建建表语句
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + "_" + id + " " +
                        "(id INT AUTO_INCREMENT PRIMARY KEY,genChart VARCHAR(2056),genResult VARCHAR(2056))";

                // 执行建表语句
                statement.executeUpdate(createTableSQL);

                // 关闭数据库连接
                statement.close();
                connection.close();

                System.out.println("Table created successfully!");
            } catch (ClassNotFoundException e) {
                System.out.println("Error loading database driver: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error executing SQL statement: " + e.getMessage());
            }
    }
    /**
     * 编辑
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
