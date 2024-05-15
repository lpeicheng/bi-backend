package com.yupao.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MultiConsumer {


    public static void main(String[] argv) throws Exception {
        String TASK_QUEUE_NAME = "multi_queue";
        ConnectionFactory factory = new ConnectionFactory();

        for (int i = 0; i < 2; i++) {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicQos(2);
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                System.out.println(" [x] 第 " + finalI + " Received '" + message + "'");
                try {
                    // 这个 multiType 如果是 true 那么表示这个消息是批量确认消息
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    Thread.sleep(20000);
                } catch (Exception e) {
                    e.printStackTrace();
                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), true);
                } finally {
                    System.out.println(" [x] Done");
                }
            };
            // 这个 autoAck 一般建议默认
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}