# 数据库初始化
# @author paopao


-- 创建库
create database if not exists bi;

-- 切换库
use bi;

-- 用户表
create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    userAccount   varchar(256)                           not null comment '账号',
    userPassword  varchar(512)                           not null comment '密码',
    userName      varchar(256)                           null comment '用户昵称',
    userAvatar    varchar(1024)                          null comment '用户头像',
    userRole      varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    checkPassword varchar(512)                           not null comment '校验码',
    userEmail varchar(128)                            null comment '邮箱'

)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_userAccount
    on user (userAccount);

-- 图表信息表
create table if not exists chart
(
    id         bigint auto_increment comment '用户id' primary key,
    `name` varchar(128) null comment '图表名称',
    goal        text     null comment '分析目标',
    chartData text      null comment '图表数据',
    chartType varchar(128)      null comment '图表类型',
    genChart		 text null comment '生成的图表数据',
    genResult  text      null comment '生成分析结果',
    -- 任务状态字段(排队中wait、执行中running、已完成succeed、失败failed)
    status       varchar(128) not null default 'wait' comment 'wait,running,succeed,failed',
    -- 任务执行信息字段
    execMessage  text   null comment '执行信息',
    userId      bigint          null comment '创建用户id',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除'
) comment '图表信息' collate = utf8mb4_unicode_ci;


create table chart_1659210482555121666
(
    日期  int null,
    用户数 int null
);
