-- =====================================================
-- 青旅综合服务平台 - 核心基础表设计
-- 数据库: youth_hostel
-- 字符集: utf8mb4
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS youth_hostel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE youth_hostel;

-- =====================================================
-- 1. 用户/会员表 (sys_user)
-- =====================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50)     NOT NULL COMMENT '用户名/登录账号',
    password        VARCHAR(100)    NOT NULL COMMENT '密码(加密存储)',
    nickname        VARCHAR(50)     DEFAULT NULL COMMENT '昵称',
    real_name       VARCHAR(50)     DEFAULT NULL COMMENT '真实姓名',
    phone           VARCHAR(20)     DEFAULT NULL COMMENT '手机号码',
    email           VARCHAR(100)    DEFAULT NULL COMMENT '邮箱地址',
    avatar          VARCHAR(255)    DEFAULT NULL COMMENT '头像URL',
    gender          TINYINT         DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    id_card         VARCHAR(18)     DEFAULT NULL COMMENT '身份证号',
    birthday        DATE            DEFAULT NULL COMMENT '出生日期',
    member_level    TINYINT         DEFAULT 0 COMMENT '会员等级: 0-普通 1-银卡 2-金卡 3-白金',
    member_points   INT             DEFAULT 0 COMMENT '会员积分',
    balance         DECIMAL(10,2)   DEFAULT 0.00 COMMENT '账户余额',
    status          TINYINT         DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    last_login_time DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip   VARCHAR(50)     DEFAULT NULL COMMENT '最后登录IP',
    remark          VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT          DEFAULT NULL COMMENT '创建人ID',
    update_by       BIGINT          DEFAULT NULL COMMENT '更新人ID',
    deleted         TINYINT         DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    KEY idx_member_level (member_level),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户/会员表';

-- =====================================================
-- 2. 青旅房源/房型表 (hostel_room)
-- =====================================================
DROP TABLE IF EXISTS hostel_room;
CREATE TABLE hostel_room (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '房源ID',
    room_no         VARCHAR(50)     NOT NULL COMMENT '房间编号',
    room_name       VARCHAR(100)    NOT NULL COMMENT '房间名称',
    room_type       VARCHAR(50)     NOT NULL COMMENT '房型: SINGLE-单人间 DOUBLE-双人间 DORM-多人间 FAMILY-家庭房',
    bed_type        VARCHAR(50)     DEFAULT NULL COMMENT '床型: SINGLE_BED-单人床 DOUBLE_BED-双人床 BUNK_BED-上下铺',
    bed_count       INT             DEFAULT 1 COMMENT '床位数',
    floor           INT             DEFAULT 1 COMMENT '所在楼层',
    area            DECIMAL(6,2)    DEFAULT NULL COMMENT '房间面积(平方米)',
    price           DECIMAL(10,2)   NOT NULL COMMENT '房价(元/晚)',
    deposit         DECIMAL(10,2)   DEFAULT 0.00 COMMENT '押金(元)',
    facilities      VARCHAR(500)    DEFAULT NULL COMMENT '设施配置(JSON格式): WiFi、空调、独立卫浴等',
    images          VARCHAR(1000)   DEFAULT NULL COMMENT '房间图片(JSON数组)',
    description     TEXT            DEFAULT NULL COMMENT '房间描述',
    max_occupancy   INT             DEFAULT 1 COMMENT '最大入住人数',
    has_window      TINYINT         DEFAULT 1 COMMENT '是否有窗: 0-无 1-有',
    has_bathroom    TINYINT         DEFAULT 1 COMMENT '是否独立卫浴: 0-公共 1-独立',
    has_aircon      TINYINT         DEFAULT 1 COMMENT '是否有空调: 0-无 1-有',
    status          TINYINT         DEFAULT 1 COMMENT '状态: 0-维护中 1-可预订 2-已入住',
    sort_order      INT             DEFAULT 0 COMMENT '排序号',
    remark          VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT          DEFAULT NULL COMMENT '创建人ID',
    update_by       BIGINT          DEFAULT NULL COMMENT '更新人ID',
    deleted         TINYINT         DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_no (room_no),
    KEY idx_room_type (room_type),
    KEY idx_status (status),
    KEY idx_price (price),
    KEY idx_floor (floor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='青旅房源/房型表';

-- =====================================================
-- 3. 预定订单表 (booking_order)
-- =====================================================
DROP TABLE IF EXISTS booking_order;
CREATE TABLE booking_order (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no            VARCHAR(32)     NOT NULL COMMENT '订单编号',
    user_id             BIGINT          NOT NULL COMMENT '用户ID',
    room_id             BIGINT          NOT NULL COMMENT '房源ID',
    room_no             VARCHAR(50)     NOT NULL COMMENT '房间编号(冗余)',
    room_name           VARCHAR(100)    DEFAULT NULL COMMENT '房间名称(冗余)',
    room_type           VARCHAR(50)     DEFAULT NULL COMMENT '房型(冗余)',
    check_in_date       DATE            NOT NULL COMMENT '入住日期',
    check_out_date      DATE            NOT NULL COMMENT '离店日期',
    stay_days           INT             NOT NULL DEFAULT 1 COMMENT '入住天数',
    guest_name          VARCHAR(50)     NOT NULL COMMENT '入住人姓名',
    guest_phone         VARCHAR(20)     NOT NULL COMMENT '入住人电话',
    guest_id_card       VARCHAR(18)     DEFAULT NULL COMMENT '入住人身份证',
    guest_count         INT             DEFAULT 1 COMMENT '入住人数',
    room_price          DECIMAL(10,2)   NOT NULL COMMENT '房间单价(元/晚)',
    total_amount        DECIMAL(10,2)   NOT NULL COMMENT '订单总金额',
    deposit             DECIMAL(10,2)   DEFAULT 0.00 COMMENT '押金',
    discount_amount     DECIMAL(10,2)   DEFAULT 0.00 COMMENT '优惠金额',
    pay_amount          DECIMAL(10,2)   NOT NULL COMMENT '实付金额',
    pay_type            TINYINT         DEFAULT NULL COMMENT '支付方式: 1-微信 2-支付宝 3-余额 4-到店付',
    pay_time            DATETIME        DEFAULT NULL COMMENT '支付时间',
    pay_trade_no        VARCHAR(64)     DEFAULT NULL COMMENT '第三方支付流水号',
    order_status        TINYINT         NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待支付 1-已支付 2-已入住 3-已完成 4-已取消 5-已退款',
    check_in_time       DATETIME        DEFAULT NULL COMMENT '实际入住时间',
    check_out_time      DATETIME        DEFAULT NULL COMMENT '实际离店时间',
    cancel_reason       VARCHAR(200)    DEFAULT NULL COMMENT '取消原因',
    cancel_time         DATETIME        DEFAULT NULL COMMENT '取消时间',
    refund_amount       DECIMAL(10,2)   DEFAULT NULL COMMENT '退款金额',
    remark              VARCHAR(500)    DEFAULT NULL COMMENT '订单备注',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted             TINYINT         DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_room_id (room_id),
    KEY idx_order_status (order_status),
    KEY idx_check_in_date (check_in_date),
    KEY idx_check_out_date (check_out_date),
    KEY idx_create_time (create_time),
    KEY idx_pay_trade_no (pay_trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预定订单表';

-- =====================================================
-- 4. 房间库存表 (sys_room) - 用于库存管理和乐观锁
-- =====================================================
DROP TABLE IF EXISTS sys_room;
CREATE TABLE sys_room (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    room_number     VARCHAR(50)     NOT NULL COMMENT '房间编号',
    room_type       VARCHAR(50)     NOT NULL COMMENT '房型',
    capacity        INT             DEFAULT 1 COMMENT '容量',
    price           DECIMAL(10,2)   NOT NULL COMMENT '价格',
    stock           INT             NOT NULL DEFAULT 0 COMMENT '库存数量',
    description     VARCHAR(500)    DEFAULT NULL COMMENT '描述',
    status          TINYINT         DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    version         INT             DEFAULT 0 COMMENT '乐观锁版本号',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_room_number (room_number),
    KEY idx_room_type (room_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间库存表';

-- =====================================================
-- 5. 基础字典表 (sys_dict)
-- =====================================================
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    dict_type       VARCHAR(50)     NOT NULL COMMENT '字典类型编码',
    dict_label      VARCHAR(100)    NOT NULL COMMENT '字典标签(显示值)',
    dict_value      VARCHAR(100)    NOT NULL COMMENT '字典值(存储值)',
    dict_sort       INT             DEFAULT 0 COMMENT '排序号',
    css_class       VARCHAR(100)    DEFAULT NULL COMMENT 'CSS样式',
    list_class      VARCHAR(100)    DEFAULT NULL COMMENT '表格回显样式',
    is_default      TINYINT         DEFAULT 0 COMMENT '是否默认: 0-否 1-是',
    status          TINYINT         DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    remark          VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT          DEFAULT NULL COMMENT '创建人ID',
    update_by       BIGINT          DEFAULT NULL COMMENT '更新人ID',
    deleted         TINYINT         DEFAULT 0 COMMENT '删除标志: 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_dict_type (dict_type),
    KEY idx_dict_value (dict_value),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础字典表';

-- =====================================================
-- 初始化字典数据
-- =====================================================

-- 房型字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('room_type', '单人间', 'SINGLE', 1, 0, '房型-单人间'),
('room_type', '双人间', 'DOUBLE', 2, 0, '房型-双人间'),
('room_type', '多人间', 'DORM', 3, 0, '房型-多人间'),
('room_type', '家庭房', 'FAMILY', 4, 0, '房型-家庭房');

-- 床型字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('bed_type', '单人床', 'SINGLE_BED', 1, 0, '床型-单人床'),
('bed_type', '双人床', 'DOUBLE_BED', 2, 0, '床型-双人床'),
('bed_type', '上下铺', 'BUNK_BED', 3, 0, '床型-上下铺');

-- 订单状态字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('order_status', '待支付', '0', 1, 1, '订单状态-待支付'),
('order_status', '已支付', '1', 2, 0, '订单状态-已支付'),
('order_status', '已入住', '2', 3, 0, '订单状态-已入住'),
('order_status', '已完成', '3', 4, 0, '订单状态-已完成'),
('order_status', '已取消', '4', 5, 0, '订单状态-已取消'),
('order_status', '已退款', '5', 6, 0, '订单状态-已退款');

-- 支付方式字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('pay_type', '微信支付', '1', 1, 1, '支付方式-微信'),
('pay_type', '支付宝', '2', 2, 0, '支付方式-支付宝'),
('pay_type', '余额支付', '3', 3, 0, '支付方式-余额'),
('pay_type', '到店支付', '4', 4, 0, '支付方式-到店付');

-- 会员等级字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('member_level', '普通会员', '0', 1, 1, '会员等级-普通'),
('member_level', '银卡会员', '1', 2, 0, '会员等级-银卡'),
('member_level', '金卡会员', '2', 3, 0, '会员等级-金卡'),
('member_level', '白金会员', '3', 4, 0, '会员等级-白金');

-- 房源状态字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('room_status', '维护中', '0', 1, 0, '房源状态-维护中'),
('room_status', '可预订', '1', 2, 1, '房源状态-可预订'),
('room_status', '已入住', '2', 3, 0, '房源状态-已入住');

-- 性别字典
INSERT INTO sys_dict (dict_type, dict_label, dict_value, dict_sort, is_default, remark) VALUES
('gender', '未知', '0', 1, 1, '性别-未知'),
('gender', '男', '1', 2, 0, '性别-男'),
('gender', '女', '2', 3, 0, '性别-女');
