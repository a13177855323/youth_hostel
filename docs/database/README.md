# 青旅综合服务平台 - 数据库设计文档

## 概述

本文档描述了青旅综合服务平台的核心数据库表结构设计，包含4张基础表：用户/会员表、青旅房源/房型表、预定订单表、基础字典表。

---

## 1. 用户/会员表 (t_user)

### 核心作用
存储平台注册用户和会员信息，支持普通用户和会员两种角色，包含完整的用户画像和会员体系。

### 字段设计说明

| 字段名 | 类型 | 说明 | 设计思路 |
|-------|------|------|---------|
| id | BIGINT UNSIGNED | 主键ID | 自增主键，无符号确保正数范围更大 |
| username | VARCHAR(50) | 用户名 | 登录账号，唯一索引确保不重复 |
| password | VARCHAR(100) | 密码 | 加密存储(BCrypt等)，长度预留加密后空间 |
| nickname | VARCHAR(50) | 昵称 | 用户展示名称，可为空 |
| real_name | VARCHAR(50) | 真实姓名 | 实名认证和入住登记用 |
| email | VARCHAR(100) | 邮箱 | 唯一索引，用于登录和通知 |
| phone | VARCHAR(20) | 手机号 | 唯一索引，主要联系方式 |
| avatar | VARCHAR(255) | 头像URL | 存储图片地址 |
| gender | TINYINT | 性别 | 0-未知 1-男 2-女，默认未知保护隐私 |
| birthday | DATE | 生日 | 会员权益计算(如生日优惠) |
| id_card | VARCHAR(18) | 身份证号 | 实名认证和公安系统对接 |
| user_type | TINYINT | 用户类型 | 1-普通用户 2-会员，索引区分用户群体 |
| member_level | TINYINT | 会员等级 | 0-非会员 1-普通 2-银卡 3-金卡 4-钻石，支持会员权益体系 |
| member_points | INT UNSIGNED | 会员积分 | 无符号确保正值，用于积分兑换 |
| status | TINYINT | 账号状态 | 0-禁用 1-启用 2-锁定，索引用于状态筛选 |
| last_login_time | DATETIME | 最后登录时间 | 用户活跃度分析 |
| last_login_ip | VARCHAR(50) | 最后登录IP | 安全审计和异地登录提醒 |

### 索引设计

```sql
-- 唯一索引：确保用户唯一性
UNIQUE KEY uk_username (username)
UNIQUE KEY uk_email (email)
UNIQUE KEY uk_phone (phone)

-- 普通索引：业务查询优化
KEY idx_user_type (user_type)      -- 区分普通用户和会员
KEY idx_member_level (member_level) -- 会员等级筛选和权益计算
KEY idx_status (status)             -- 账号状态筛选
KEY idx_create_time (create_time)   -- 按注册时间排序
```

### 设计亮点
1. **会员体系完整**：支持5级会员体系，积分系统
2. **实名认证支持**：预留身份证号字段
3. **安全审计**：记录登录IP和时间
4. **软删除**：deleted字段实现逻辑删除

---

## 2. 青旅房源/房型表 (t_hostel_room)

### 核心作用
存储青旅的房源信息，包括房型分类、床位配置、价格策略、设施信息等。

### 字段设计说明

| 字段名 | 类型 | 说明 | 设计思路 |
|-------|------|------|---------|
| id | BIGINT UNSIGNED | 主键ID | 自增主键 |
| room_no | VARCHAR(20) | 房间编号 | 如A101、B202，唯一索引便于管理 |
| room_name | VARCHAR(100) | 房间名称 | 展示名称，如"男生四人间" |
| room_type | TINYINT | 房型 | 1-男生间 2-女生间 3-混住间 4-单人间 5-双人间，索引筛选 |
| bed_count | TINYINT | 床位数量 | 青旅核心属性，决定容纳人数 |
| bed_type | VARCHAR(20) | 床型 | 上下铺/平铺/榻榻米，描述床位类型 |
| area | DECIMAL(6,2) | 面积 | 平方米，两位小数精度 |
| floor | TINYINT | 楼层 | 便于导航和分配 |
| price | DECIMAL(10,2) | 单价 | 元/床位/晚，核心定价字段，索引支持价格范围查询 |
| weekend_price | DECIMAL(10,2) | 周末价 | 差异化定价策略 |
| holiday_price | DECIMAL(10,2) | 节假日价 | 高峰期定价 |
| deposit | DECIMAL(10,2) | 押金 | 防止物品损坏 |
| facilities | VARCHAR(500) | 设施 | JSON格式存储数组，灵活可扩展 |
| description | TEXT | 描述 | 详细介绍，支持长文本 |
| images | VARCHAR(1000) | 图片 | JSON数组存储多图URL |
| cover_image | VARCHAR(255) | 封面图 | 列表展示用主图 |
| available_beds | TINYINT | 可用床位 | 实时库存，索引查询可预订房间 |
| max_occupancy | TINYINT | 最大入住 | 安全限制人数 |
| check_in_time | TIME | 入住时间 | 默认14:00 |
| check_out_time | TIME | 退房时间 | 默认12:00 |
| status | TINYINT | 状态 | 0-停用 1-可用 2-维修中 3-已满 |
| sort_order | INT | 排序号 | 越小越靠前，索引支持自定义排序 |

### 索引设计

```sql
-- 唯一索引
UNIQUE KEY uk_room_no (room_no)     -- 房间编号唯一

-- 普通索引
KEY idx_room_type (room_type)       -- 房型筛选(男生间/女生间等)
KEY idx_status (status)             -- 状态筛选
KEY idx_price (price)               -- 价格范围查询
KEY idx_available_beds (available_beds) -- 查询可预订房间
KEY idx_sort_order (sort_order)     -- 自定义排序
```

### 设计亮点
1. **多价格策略**：支持平日/周末/节假日差异化定价
2. **实时库存**：available_beds字段实时反映可订床位
3. **灵活设施**：JSON存储支持任意设施组合
4. **房型细分**：5种房型满足青旅特色需求

---

## 3. 预定订单表 (t_order)

### 核心作用
存储用户的预定订单信息，关联用户和房源，完整记录订单生命周期。

### 字段设计说明

| 字段名 | 类型 | 说明 | 设计思路 |
|-------|------|------|---------|
| id | BIGINT UNSIGNED | 主键ID | 自增主键 |
| order_no | VARCHAR(32) | 订单编号 | 唯一业务编号(YH+年月日+随机数)，便于查询和对账 |
| user_id | BIGINT UNSIGNED | 用户ID | 关联t_user.id，索引查询用户订单 |
| room_id | BIGINT UNSIGNED | 房间ID | 关联t_hostel_room.id，索引查询房间订单 |
| bed_count | TINYINT | 床位数量 | 青旅特色，可订多个床位 |
| check_in_date | DATE | 入住日期 | 核心业务字段，索引支持日期范围查询 |
| check_out_date | DATE | 退房日期 | 计算入住晚数 |
| nights | TINYINT | 入住晚数 | 冗余存储，避免重复计算 |
| guest_name | VARCHAR(50) | 入住人姓名 | 实际入住人信息(可能非下单人) |
| guest_phone | VARCHAR(20) | 入住人手机 | 联系入住人 |
| guest_id_card | VARCHAR(18) | 入住人身份证 | 公安登记用 |
| guest_count | TINYINT | 入住人数 | 实际入住人数 |
| room_price | DECIMAL(10,2) | 房间单价 | 下单时的价格快照 |
| total_amount | DECIMAL(12,2) | 订单总金额 | 计算后的总价 |
| discount_amount | DECIMAL(10,2) | 优惠金额 | 会员折扣等 |
| pay_amount | DECIMAL(12,2) | 实付金额 | 最终支付金额 |
| deposit_amount | DECIMAL(10,2) | 押金 | 冻结金额 |
| order_status | TINYINT | 订单状态 | 9种状态完整覆盖生命周期，索引筛选 |
| pay_status | TINYINT | 支付状态 | 4种支付状态，索引查询待支付订单 |
| pay_time | DATETIME | 支付时间 | 记录实际支付时间 |
| pay_way | TINYINT | 支付方式 | 1-微信 2-支付宝 3-银行卡 4-余额 |
| pay_trade_no | VARCHAR(64) | 支付流水号 | 第三方平台流水号，对账用 |
| confirm_time | DATETIME | 确认时间 | 商家确认订单时间 |
| check_in_time | DATETIME | 实际入住时间 | 办理入住时间 |
| check_out_time | DATETIME | 实际退房时间 | 办理退房时间 |
| cancel_time | DATETIME | 取消时间 | 记录取消操作时间 |
| cancel_reason | VARCHAR(255) | 取消原因 | 分析退订原因 |
| refund_amount | DECIMAL(10,2) | 退款金额 | 实际退款金额 |
| refund_time | DATETIME | 退款时间 | 退款完成时间 |

### 订单状态流转

```
待支付 → 已支付 → 已确认 → 已入住 → 已退房 → 已完成
   ↓        ↓         ↓
 已取消   退款中    已退款
```

### 索引设计

```sql
-- 唯一索引
UNIQUE KEY uk_order_no (order_no)   -- 订单编号唯一

-- 普通索引
KEY idx_user_id (user_id)           -- 查询用户订单列表
KEY idx_room_id (room_id)           -- 查询房间订单
KEY idx_order_status (order_status) -- 按状态筛选订单
KEY idx_pay_status (pay_status)     -- 查询待支付/已支付订单
KEY idx_check_in_date (check_in_date) -- 日期范围查询(如查询某天入住订单)
KEY idx_create_time (create_time)   -- 按下单时间排序

-- 联合索引
KEY idx_user_status (user_id, order_status) -- 查询用户特定状态订单(如"我的待支付订单")
```

### 设计亮点
1. **订单状态完整**：9种状态覆盖完整生命周期
2. **双状态设计**：order_status + pay_status 分离业务和支付状态
3. **时间轴完整**：记录支付、确认、入住、退房、取消等各节点时间
4. **入住人分离**：支持代订，入住人与下单人不同
5. **价格快照**：记录下单时价格，避免后续调价影响历史订单

---

## 4. 基础字典表 (t_dict)

### 核心作用
存储系统基础字典数据，统一管理枚举值，避免硬编码，支持动态配置。

### 字段设计说明

| 字段名 | 类型 | 说明 | 设计思路 |
|-------|------|------|---------|
| id | BIGINT UNSIGNED | 主键ID | 自增主键 |
| dict_type | VARCHAR(50) | 字典类型 | 如room_type、order_status，索引分组查询 |
| dict_code | VARCHAR(50) | 字典编码 | 存储值(如1、2、3)，联合唯一索引 |
| dict_name | VARCHAR(100) | 字典名称 | 显示值(如"男生间"、"女生间") |
| dict_value | VARCHAR(100) | 字典值 | 扩展值，可选 |
| sort_order | INT | 排序号 | 同类型内排序，索引支持自定义顺序 |
| parent_id | BIGINT UNSIGNED | 父级ID | 支持树形字典，0为顶级，索引树形查询 |
| level | TINYINT | 层级 | 树形字典层级 |
| remark | VARCHAR(255) | 备注 | 说明用途 |
| status | TINYINT | 状态 | 0-禁用 1-启用，索引筛选启用的字典 |
| is_system | TINYINT | 系统内置 | 0-否 1-是，系统内置不可删除 |

### 索引设计

```sql
-- 联合唯一索引
UNIQUE KEY uk_type_code (dict_type, dict_code)  -- 同类型下编码唯一

-- 普通索引
KEY idx_dict_type (dict_type)       -- 查询某类字典(如所有房型)
KEY idx_parent_id (parent_id)       -- 树形查询子节点
KEY idx_status (status)             -- 筛选启用的字典
KEY idx_sort_order (sort_order)     -- 按排序号排序
```

### 初始化数据

```sql
-- 房型字典
1-男生间、2-女生间、3-混住间、4-单人间、5-双人间

-- 订单状态字典
0-待支付、1-已支付、2-已确认、3-已入住、4-已退房、5-已完成、6-已取消、7-退款中、8-已退款

-- 支付方式字典
1-微信支付、2-支付宝、3-银行卡、4-余额支付

-- 会员等级字典
0-非会员、1-普通会员、2-银卡会员、3-金卡会员、4-钻石会员

-- 房间状态字典
0-停用、1-可用、2-维修中、3-已满
```

### 设计亮点
1. **统一管理**：所有枚举值集中管理，避免散落各处
2. **动态配置**：后台可动态修改，无需重启服务
3. **树形支持**：parent_id支持多级字典(如省市区)
4. **系统保护**：is_system标记系统内置，防止误删
5. **扩展性强**：预留dict_value字段支持复杂场景

---

## 表关系图

```
┌─────────────┐         ┌─────────────────┐         ┌─────────────┐
│   t_user    │         │     t_order     │         │ t_hostel_room│
├─────────────┤         ├─────────────────┤         ├─────────────┤
│     id      │◄────────┤    user_id      │         │             │
│  username   │         ├─────────────────┤         │             │
│    phone    │         │    room_id      ├────────►│     id      │
│ member_level│         ├─────────────────┤         │   room_no   │
└─────────────┘         │   order_no      │         │   price     │
                        │  order_status   │         │available_beds│
                        │  total_amount   │         └─────────────┘
                        └─────────────────┘
                                   │
                                   │ 引用字典
                                   ▼
                          ┌─────────────────┐
                          │     t_dict      │
                          ├─────────────────┤
                          │   dict_type     │
                          │   dict_code     │
                          │   dict_name     │
                          └─────────────────┘
```

## 使用说明

### 1. 创建数据库

```bash
mysql -u root -p < docs/database/init.sql
```

### 2. 单独执行某张表

```sql
-- 先选择数据库
USE youth_hostel;

-- 然后执行对应建表语句
```

### 3. 查询示例

```sql
-- 查询可预订的男生间(可用床位>0)
SELECT * FROM t_hostel_room 
WHERE room_type = 1 
  AND available_beds > 0 
  AND status = 1;

-- 查询用户的待支付订单
SELECT o.*, r.room_name 
FROM t_order o
JOIN t_hostel_room r ON o.room_id = r.id
WHERE o.user_id = 1 
  AND o.order_status = 0;

-- 查询某天入住的订单
SELECT * FROM t_order 
WHERE check_in_date = '2024-03-20';
```

---

## 后续扩展建议

1. **订单拆分表**：大订单拆分子订单，支持多房间预订
2. **价格日历表**：记录每天每个房间的价格，支持动态定价
3. **评价表**：用户对房间和服务的评价
4. **优惠券表**：营销活动的优惠券管理
5. **消息通知表**：系统消息和短信通知记录
