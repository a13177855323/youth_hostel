-- =====================================================
-- 青旅综合服务平台 - 核心业务表初始化SQL
-- 数据库: youth_hostel
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `youth_hostel` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `youth_hostel`;

-- =====================================================
-- 表1: sys_dict - 基础字典表
-- 核心作用: 存储系统全局的字典数据（如房型类型、订单状态、支付方式、会员等级等）
-- 设计思路: 采用分类-键值对结构，便于扩展和维护，避免硬编码
-- =====================================================
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_code` VARCHAR(100) NOT NULL COMMENT '字典分类编码（如：order_status, room_type）',
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典分类名称（如：订单状态, 房型）',
    `item_key` VARCHAR(50) NOT NULL COMMENT '字典项键值（如：1, 2, 3）',
    `item_value` VARCHAR(200) NOT NULL COMMENT '字典项显示值（如：待支付, 已确认, 已取消）',
    `item_desc` VARCHAR(500) DEFAULT NULL COMMENT '字典项描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用, 1:启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code_item_key` (`dict_code`, `item_key`, `is_deleted`),
    KEY `idx_dict_code` (`dict_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础字典表';

-- =====================================================
-- 表2: sys_user - 用户/会员表
-- 核心作用: 存储青旅会员和管理员用户的核心信息，支持登录、个人中心、订单关联
-- 设计思路: 区分会员和管理员，支持手机号/邮箱/用户名多方式登录
-- =====================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名（可用于登录）',
    `password` VARCHAR(200) NOT NULL COMMENT '登录密码（加密存储）',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号码（唯一，用于登录和接收通知）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称/显示名',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别（0:未知, 1:男, 2:女）',
    `birthday` DATE DEFAULT NULL COMMENT '出生日期',
    `user_type` TINYINT DEFAULT 1 COMMENT '用户类型（1:会员用户, 2:管理员, 3:超级管理员）',
    `member_level` TINYINT DEFAULT 1 COMMENT '会员等级（1:普通会员, 2:银卡, 3:金卡, 4:钻石）',
    `points` INT DEFAULT 0 COMMENT '会员积分',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(30) DEFAULT NULL COMMENT '身份证号',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0:禁用, 1:正常, 2:锁定）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`, `is_deleted`),
    UNIQUE KEY `uk_username` (`username`, `is_deleted`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_member_level` (`member_level`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户/会员表';

-- =====================================================
-- 表3: hostel_room - 青旅房源/房型表
-- 核心作用: 存储青旅的房间信息，包括房型、价格、库存、设施等
-- 设计思路: 区分房型和具体房间，支持多床位房（如6人间、8人间）和独立房间
-- =====================================================
DROP TABLE IF EXISTS `hostel_room`;
CREATE TABLE `hostel_room` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `room_name` VARCHAR(100) NOT NULL COMMENT '房间名称（如：男生6人间, 豪华单人间）',
    `room_no` VARCHAR(50) DEFAULT NULL COMMENT '房间号（如：301, 502-A）',
    `room_type` TINYINT NOT NULL COMMENT '房型（1:多人间床位, 2:单人间, 3:双人间, 4:家庭房）',
    `bed_count` INT NOT NULL DEFAULT 1 COMMENT '床位数/可住人数',
    `available_beds` INT NOT NULL DEFAULT 1 COMMENT '剩余可用床位数',
    `price_per_bed` DECIMAL(10,2) NOT NULL COMMENT '单价（每床/每间）',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `floor` INT DEFAULT NULL COMMENT '所在楼层',
    `room_area` DECIMAL(5,2) DEFAULT NULL COMMENT '房间面积（㎡）',
    `room_image` VARCHAR(1000) DEFAULT NULL COMMENT '房间图片URL，多个用逗号分隔',
    `facilities` VARCHAR(500) DEFAULT NULL COMMENT '房间设施（如：空调, 独立卫浴, 储物柜）',
    `room_desc` TEXT DEFAULT NULL COMMENT '房间详细描述',
    `is_shared` TINYINT DEFAULT 1 COMMENT '是否合住（0:独立房间, 1:合住床位）',
    `gender_restriction` TINYINT DEFAULT 0 COMMENT '性别限制（0:无限制, 1:仅限男性, 2:仅限女性）',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0:维修中, 1:可预订, 2:已满, 3:清洁中）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_room_type` (`room_type`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price_per_bed`),
    KEY `idx_is_shared` (`is_shared`),
    KEY `idx_floor` (`floor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='青旅房源/房型表';

-- =====================================================
-- 表4: hostel_order - 预定订单表
-- 核心作用: 存储用户的预订订单信息，是青旅业务的核心交易表
-- 设计思路: 支持按床位预订和按房间预订，关联用户和房源，包含完整的订单生命周期
-- =====================================================
DROP TABLE IF EXISTS `hostel_order`;
CREATE TABLE `hostel_order` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_sn` VARCHAR(64) NOT NULL COMMENT '订单号（唯一标识）',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID（关联sys_user.id）',
    `room_id` BIGINT UNSIGNED NOT NULL COMMENT '房间ID（关联hostel_room.id）',
    `checkin_date` DATE NOT NULL COMMENT '入住日期',
    `checkout_date` DATE NOT NULL COMMENT '退房日期',
    `nights` INT NOT NULL COMMENT '入住天数',
    `bed_count` INT NOT NULL DEFAULT 1 COMMENT '预订床位数/房间数',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价（每床/每间）',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    `pay_status` TINYINT DEFAULT 0 COMMENT '支付状态（0:未支付, 1:已支付, 2:已退款, 3:部分退款）',
    `pay_method` TINYINT DEFAULT 0 COMMENT '支付方式（1:微信, 2:支付宝, 3:余额, 4:现金）',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `order_status` TINYINT DEFAULT 1 COMMENT '订单状态（1:待支付, 2:已确认, 3:已入住, 4:已完成, 5:已取消, 6:已退款）',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系人电话',
    `guest_count` INT DEFAULT 1 COMMENT '入住人数',
    `guest_info` TEXT DEFAULT NULL COMMENT '入住人信息（JSON格式存储多人）',
    `special_request` VARCHAR(500) DEFAULT NULL COMMENT '特殊要求',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注（内部使用）',
    `checkin_time` DATETIME DEFAULT NULL COMMENT '实际入住时间',
    `checkout_time` DATETIME DEFAULT NULL COMMENT '实际退房时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_sn` (`order_sn`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_checkin_date` (`checkin_date`),
    KEY `idx_checkout_date` (`checkout_date`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_pay_status` (`pay_status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_user_status` (`user_id`, `order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预定订单表';

-- =====================================================
-- 初始化基础字典数据
-- =====================================================
INSERT INTO `sys_dict` (`dict_code`, `dict_name`, `item_key`, `item_value`, `sort_order`) VALUES
-- 用户类型
('user_type', '用户类型', '1', '会员用户', 1),
('user_type', '用户类型', '2', '管理员', 2),
('user_type', '用户类型', '3', '超级管理员', 3),
-- 会员等级
('member_level', '会员等级', '1', '普通会员', 1),
('member_level', '会员等级', '2', '银卡会员', 2),
('member_level', '会员等级', '3', '金卡会员', 3),
('member_level', '会员等级', '4', '钻石会员', 4),
-- 房型
('room_type', '房型', '1', '多人间床位', 1),
('room_type', '房型', '2', '单人间', 2),
('room_type', '房型', '3', '双人间', 3),
('room_type', '房型', '4', '家庭房', 4),
-- 订单状态
('order_status', '订单状态', '1', '待支付', 1),
('order_status', '订单状态', '2', '已确认', 2),
('order_status', '订单状态', '3', '已入住', 3),
('order_status', '订单状态', '4', '已完成', 4),
('order_status', '订单状态', '5', '已取消', 5),
('order_status', '订单状态', '6', '已退款', 6),
-- 支付状态
('pay_status', '支付状态', '0', '未支付', 1),
('pay_status', '支付状态', '1', '已支付', 2),
('pay_status', '支付状态', '2', '已退款', 3),
('pay_status', '支付状态', '3', '部分退款', 4),
-- 支付方式
('pay_method', '支付方式', '1', '微信支付', 1),
('pay_method', '支付方式', '2', '支付宝', 2),
('pay_method', '支付方式', '3', '余额支付', 3),
('pay_method', '支付方式', '4', '现金支付', 4),
-- 性别
('gender', '性别', '0', '未知', 1),
('gender', '性别', '1', '男', 2),
('gender', '性别', '2', '女', 3),
-- 房间状态
('room_status', '房间状态', '0', '维修中', 1),
('room_status', '房间状态', '1', '可预订', 2),
('room_status', '房间状态', '2', '已满', 3),
('room_status', '房间状态', '3', '清洁中', 4);
