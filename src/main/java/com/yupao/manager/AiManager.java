package com.yupao.manager;

import com.github.rholder.retry.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.yupao.common.ErrorCode;
import com.yupao.exception.BusinessException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager {

    @Resource
    private WebSocketServer webSocketServer;

    // 设置重试，重试次数2次，重试间隔2s
    private final Retryer<HashMap<String, Object>> retryer = RetryerBuilder.<HashMap<String, Object>>newBuilder()
            .retryIfResult(result -> (!isValidResult(result.get("chatResult").toString())))
            .withStopStrategy(StopStrategies.stopAfterAttempt(2))
            .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
            .build();

    /**
     * 分析结果是否存在错误
     *
     * @param result AI返回结果
     * @return 结果是否异常
     */
    private boolean isValidResult(String result) {
        // 匹配{}内的内容
        Pattern pattern = Pattern.compile("\\{(.*)}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(result);
        String genChart;
        if (matcher.find()) {
            genChart = (matcher.group());
        } else {
            return false;
        }
        try {
            JsonParser.parseString(genChart).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            // Json解析异常，直接返回 false
            return false;
        }
        // 匹配结论后面的内容
        pattern = Pattern.compile("结论：(.*)");
        matcher = pattern.matcher(result);
        return matcher.find();
    }

    /**
     * 执行重试机制
     *
     * @param userInput 用户输入
     * @return 调用AI
     * @throws Exception 重试失败
     */
    public HashMap<String, Object> sendMesToAItRetry(String userInput) throws Exception {
        try {
            Callable<HashMap<String, Object>> callable = () -> {
                // 在这里调用sendMesToAI方法
                return sendMesToAI(userInput);
            };
            return retryer.call(callable);
        } catch (RetryException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e + "，AI生成错误,正在重试");
        }
    }

    SparkClient sparkClient = new SparkClient();

    // 设置认证信息
    {
        sparkClient.appid = "55d12898";
        sparkClient.apiKey = "96c34f68daee3f9483f165ed58feb2ea";
        sparkClient.apiSecret = "YTk0MGE5NGYxNGIwN2U4YTAxNTI5Njk5";
    }


    /**
     * 发送信息
     *
     * @param content 用户输入
     * @return AI响应的信息
     */
    public HashMap<String, Object> sendMesToAI(final String content) {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent("你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "'【【【【【'\n" +
                "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
                "'【【【【【'\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n"
                + "下面是一个具体的例子的模板："
                + "【【【【【\n"
                + "{\"xxx\": }"
                + "【【【【【\n" +
                "结论："));
        messages.add(SparkMessage.userContent(content));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传,取值为[1,4096],默认为2048
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用最新2.0版本
                .apiVersion(SparkApiVersion.V3_5)
                .build();
        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        SparkTextUsage textUsage = chatResponse.getTextUsage();
        Integer totalTokens = textUsage.getTotalTokens();
        String chatResult = chatResponse.getContent();
        HashMap<String, Object> result = new HashMap<>();
        result.put("chatResult", chatResult);
        result.put("totalTokens", totalTokens);
        return result;
    }
}
