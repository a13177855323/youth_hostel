-- ========================================================
-- 青旅综合服务平台 - 数据库初始化脚本
-- 数据库: youth_hostel
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ========================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS youth_hostel
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE youth_hostel;

-- ========================================================
-- 1. 用户/会员表 (t_user)
-- 核心作用: 存储平台注册用户和会员信息，支持普通用户和会员两种角色
-- ========================================================
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名(登录账号)',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    real_name VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    birthday DATE DEFAULT NULL COMMENT '出生日期',
    id_card VARCHAR(18) DEFAULT NULL COMMENT '身份证号(实名认证用)',
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型: 1-普通用户 2-会员',
    member_level TINYINT DEFAULT 0 COMMENT '会员等级: 0-非会员 1-普通会员 2-银卡 3-金卡 4-钻石',
    member_points INT UNSIGNED DEFAULT 0 COMMENT '会员积分',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态: 0-禁用 1-启用 2-锁定',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username) COMMENT '用户名唯一索引',
    UNIQUE KEY uk_email (email) COMMENT '邮箱唯一索引',
    UNIQUE KEY uk_phone (phone) COMMENT '手机号唯一索引',
    KEY idx_user_type (user_type) COMMENT '用户类型索引(用于区分普通用户和会员)',
    KEY idx_member_level (member_level) COMMENT '会员等级索引(用于会员筛选和权益计算)',
    KEY idx_status (status) COMMENT '状态索引(用于账号状态筛选)',
    KEY idx_create_time (create_time) COMMENT '创建时间索引(用于按时间排序和筛选)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户/会员表';

-- ========================================================
-- 2. 青旅房源/房型表 (t_hostel_room)
-- 核心作用: 存储青旅的房源信息，包括房型、床位、价格等
-- ========================================================
DROP TABLE IF EXISTS t_hostel_room;

CREATE TABLE t_hostel_room (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    room_no VARCHAR(20) NOT NULL COMMENT '房间编号(如: A101, B202)',
    room_name VARCHAR(100) NOT NULL COMMENT '房间名称(如: 男生四人间, 女生六人间)',
    room_type TINYINT NOT NULL COMMENT '房型: 1-男生间 2-女生间 3-混住间 4-单人间 5-双人间',
    bed_count TINYINT NOT NULL COMMENT '床位数量',
    bed_type VARCHAR(20) DEFAULT '上下铺' COMMENT '床型: 上下铺/平铺/榻榻米',
    area DECIMAL(6,2) DEFAULT NULL COMMENT '房间面积(平方米)',
    floor TINYINT DEFAULT 1 COMMENT '所在楼层',
    price DECIMAL(10,2) NOT NULL COMMENT '单价(元/床位/晚)',
    weekend_price DECIMAL(10,2) DEFAULT NULL COMMENT '周末价格(元/床位/晚)',
    holiday_price DECIMAL(10,2) DEFAULT NULL COMMENT '节假日价格(元/床位/晚)',
    deposit DECIMAL(10,2) DEFAULT 0.00 COMMENT '押金金额',
    facilities VARCHAR(500) DEFAULT NULL COMMENT '房间设施(JSON格式: ["空调", "独立卫浴", "WiFi"])',
    description TEXT COMMENT '房间描述',
    images VARCHAR(1000) DEFAULT NULL COMMENT '房间图片(JSON数组: ["url1", "url2"])',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    available_beds TINYINT NOT NULL COMMENT '当前可用床位',
    max_occupancy TINYINT NOT NULL COMMENT '最大入住人数',
    check_in_time TIME DEFAULT '14:00:00' COMMENT '入住时间',
    check_out_time TIME DEFAULT '12:00:00' COMMENT '退房时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '房间状态: 0-停用 1-可用 2-维修中 3-已满',
    sort_order INT DEFAULT 0 COMMENT '排序号(越小越靠前)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_no (room_no) COMMENT '房间编号唯一索引',
    KEY idx_room_type (room_type) COMMENT '房型索引(用于房型筛选)',
    KEY idx_status (status) COMMENT '状态索引(用于房间状态筛选)',
    KEY idx_price (price) COMMENT '价格索引(用于价格范围查询)',
    KEY idx_available_beds (available_beds) COMMENT '可用床位索引(用于查询可预订房间)',
    KEY idx_sort_order (sort_order) COMMENT '排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='青旅房源/房型表';

-- ========================================================
-- 3. 预定订单表 (t_order)
-- 核心作用: 存储用户的预定订单信息，关联用户和房源
-- ========================================================
DROP TABLE IF EXISTS t_order;

CREATE TABLE t_order (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号(唯一业务编号: YH2024032012345678)',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID(关联t_user.id)',
    room_id BIGINT UNSIGNED NOT NULL COMMENT '房间ID(关联t_hostel_room.id)',
    bed_count TINYINT NOT NULL DEFAULT 1 COMMENT '预定床位数量',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    check_out_date DATE NOT NULL COMMENT '退房日期',
    nights TINYINT NOT NULL COMMENT '入住晚数',
    guest_name VARCHAR(50) NOT NULL COMMENT '入住人姓名',
    guest_phone VARCHAR(20) NOT NULL COMMENT '入住人手机号',
    guest_id_card VARCHAR(18) DEFAULT NULL COMMENT '入住人身份证号',
    guest_count TINYINT NOT NULL DEFAULT 1 COMMENT '入住人数',
    room_price DECIMAL(10,2) NOT NULL COMMENT '房间单价',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    pay_amount DECIMAL(12,2) NOT NULL COMMENT '实付金额',
    deposit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '押金金额',
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待支付 1-已支付 2-已确认 3-已入住 4-已退房 5-已完成 6-已取消 7-退款中 8-已退款',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态: 0-未支付 1-已支付 2-部分支付 3-已退款',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    pay_way TINYINT DEFAULT NULL COMMENT '支付方式: 1-微信支付 2-支付宝 3-银行卡 4-余额',
    pay_trade_no VARCHAR(64) DEFAULT NULL COMMENT '第三方支付流水号',
    confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
    check_in_time DATETIME DEFAULT NULL COMMENT '实际入住时间',
    check_out_time DATETIME DEFAULT NULL COMMENT '实际退房时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    cancel_reason VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    refund_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
    refund_time DATETIME DEFAULT NULL COMMENT '退款时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    user_remark VARCHAR(500) DEFAULT NULL COMMENT '用户留言',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no) COMMENT '订单编号唯一索引',
    KEY idx_user_id (user_id) COMMENT '用户ID索引(用于查询用户订单列表)',
    KEY idx_room_id (room_id) COMMENT '房间ID索引(用于查询房间订单)',
    KEY idx_order_status (order_status) COMMENT '订单状态索引(用于状态筛选)',
    KEY idx_pay_status (pay_status) COMMENT '支付状态索引',
    KEY idx_check_in_date (check_in_date) COMMENT '入住日期索引(用于日期范围查询)',
    KEY idx_create_time (create_time) COMMENT '创建时间索引(用于按时间排序)',
    KEY idx_user_status (user_id, order_status) COMMENT '用户+状态联合索引(用于查询用户特定状态订单)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预定订单表';

-- ========================================================
-- 4. 基础字典表 (t_dict)
-- 核心作用: 存储系统基础字典数据，如房型、订单状态、支付方式等枚举值
-- ========================================================
DROP TABLE IF EXISTS t_dict;

CREATE TABLE t_dict (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dict_type VARCHAR(50) NOT NULL COMMENT '字典类型(如: room_type, order_status)',
    dict_code VARCHAR(50) NOT NULL COMMENT '字典编码',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称(显示值)',
    dict_value VARCHAR(100) DEFAULT NULL COMMENT '字典值(存储值,可选)',
    sort_order INT DEFAULT 0 COMMENT '排序号(同类型内排序)',
    parent_id BIGINT UNSIGNED DEFAULT 0 COMMENT '父级ID(用于树形字典,0为顶级)',
    level TINYINT DEFAULT 1 COMMENT '层级(用于树形字典)',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注说明',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统内置: 0-否 1-是(系统内置不可删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_code (dict_type, dict_code) COMMENT '类型+编码唯一索引',
    KEY idx_dict_type (dict_type) COMMENT '字典类型索引(用于查询某类字典)',
    KEY idx_parent_id (parent_id) COMMENT '父级ID索引(用于树形查询)',
    KEY idx_status (status) COMMENT '状态索引',
    KEY idx_sort_order (sort_order) COMMENT '排序索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础字典表';

-- ========================================================
-- 初始化字典数据
-- ========================================================

-- 房型字典
INSERT INTO t_dict (dict_type, dict_code, dict_name, dict_value, sort_order, is_system) VALUES
('room_type', '1', '男生间', '男生间', 1, 1),
('room_type', '2', '女生间', '女生间', 2, 1),
('room_type', '3', '混住间', '混住间', 3, 1),
('room_type', '4', '单人间', '单人间', 4, 1),
('room_type', '5', '双人间', '双人间', 5, 1);

-- 订单状态字典
INSERT INTO t_dict (dict_type, dict_code, dict_name, dict_value, sort_order, is_system) VALUES
('order_status', '0', '待支付', '待支付', 1, 1),
('order_status', '1', '已支付', '已支付', 2, 1),
('order_status', '2', '已确认', '已确认', 3, 1),
('order_status', '3', '已入住', '已入住', 4, 1),
('order_status', '4', '已退房', '已退房', 5, 1),
('order_status', '5', '已完成', '已完成', 6, 1),
('order_status', '6', '已取消', '已取消', 7, 1),
('order_status', '7', '退款中', '退款中', 8, 1),
('order_status', '8', '已退款', '已退款', 9, 1);

-- 支付方式字典
INSERT INTO t_dict (dict_type, dict_code, dict_name, dict_value, sort_order, is_system) VALUES
('pay_way', '1', '微信支付', '微信支付', 1, 1),
('pay_way', '2', '支付宝', '支付宝', 2, 1),
('pay_way', '3', '银行卡', '银行卡', 3, 1),
('pay_way', '4', '余额支付', '余额支付', 4, 1);

-- 会员等级字典
INSERT INTO t_dict (dict_type, dict_code, dict_name, dict_value, sort_order, is_system) VALUES
('member_level', '0', '非会员', '非会员', 1, 1),
('member_level', '1', '普通会员', '普通会员', 2, 1),
('member_level', '2', '银卡会员', '银卡会员', 3, 1),
('member_level', '3', '金卡会员', '金卡会员', 4, 1),
('member_level', '4', '钻石会员', '钻石会员', 5, 1);

-- 房间状态字典
INSERT INTO t_dict (dict_type, dict_code, dict_name, dict_value, sort_order, is_system) VALUES
('room_status', '0', '停用', '停用', 1, 1),
('room_status', '1', '可用', '可用', 2, 1),
('room_status', '2', '维修中', '维修中', 3, 1),
('room_status', '3', '已满', '已满', 4, 1);
