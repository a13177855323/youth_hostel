# 青旅综合服务平台 - 数据库设计文档

## 一、数据库设计规范

| 项 | 规范 |
|-----|-----|
| 数据库名 | `youth_hostel` |
| 字符集 | `utf8mb4` |
| 排序规则 | `utf8mb4_unicode_ci` |
| 存储引擎 | `InnoDB` |
| 主键命名 | `id`（BIGINT UNSIGNED，自增） |
| 时间字段 | `create_time`, `update_time`（自动维护） |
| 逻辑删除 | `is_deleted`（0:未删除, 1:已删除） |

---

## 二、核心表设计说明

### 1. 基础字典表 `sys_dict`

**核心作用**：存储系统全局的枚举值和配置数据，避免代码硬编码。

**字段设计思路**：

| 字段 | 类型 | 说明 | 设计原因 |
|------|------|------|---------|
| `dict_code` | VARCHAR(100) | 字典分类编码 | 用于分组，如：`order_status`、`room_type` |
| `dict_name` | VARCHAR(100) | 字典分类名称 | 显示用，如："订单状态"、"房型" |
| `item_key` | VARCHAR(50) | 字典项键值 | 代码中使用的标识（如：1, 2, 3） |
| `item_value` | VARCHAR(200) | 字典项显示值 | 页面显示的文字（如："待支付"） |
| `sort_order` | INT | 排序号 | 控制页面显示顺序 |

**索引设计**：
- `uk_dict_code_item_key`：联合唯一索引，确保同一分类下键值唯一
- `idx_dict_code`：按分类查询的普通索引

---

### 2. 用户/会员表 `sys_user`

**核心作用**：存储青旅会员、管理员的核心信息，是用户中心的基础表。

**字段设计思路**：

| 字段 | 类型 | 说明 | 设计原因 |
|------|------|------|---------|
| `phone` | VARCHAR(20) | 手机号码 | **唯一登录凭证**，用于接收通知 |
| `username` | VARCHAR(50) | 用户名 | 可选登录方式，支持自定义 |
| `password` | VARCHAR(200) | 密码 | 加密存储（BCrypt） |
| `user_type` | TINYINT | 用户类型 | 区分会员/管理员/超级管理员 |
| `member_level` | TINYINT | 会员等级 | 1-4级，用于权益计算 |
| `points` | INT | 会员积分 | 可用于抵扣、兑换 |
| `balance` | DECIMAL(10,2) | 账户余额 | 支持储值消费 |
| `real_name` / `id_card` | VARCHAR | 实名信息 | 入住登记需要 |

**索引设计**：
- `uk_phone` / `uk_username`：唯一索引，防止重复注册
- `idx_user_type` / `idx_member_level`：用于统计和筛选
- `idx_create_time`：按注册时间排序

---

### 3. 青旅房源/房型表 `hostel_room`

**核心作用**：存储青旅的房间和床位信息，是预订业务的核心资源表。

**字段设计思路**：

| 字段 | 类型 | 说明 | 设计原因 |
|------|------|------|---------|
| `room_name` | VARCHAR(100) | 房间名称 | 如："男生6人间"、"豪华单人间" |
| `room_type` | TINYINT | 房型 | 1:多人间, 2:单人间, 3:双人间, 4:家庭房 |
| `bed_count` | INT | 总床位数 | 房间可容纳人数 |
| `available_beds` | INT | 可用床位数 | **实时库存**，预订时扣减 |
| `price_per_bed` | DECIMAL(10,2) | 单价 | 支持按床位定价（青旅特色） |
| `is_shared` | TINYINT | 是否合住 | 区分床位房和独立房间 |
| `gender_restriction` | TINYINT | 性别限制 | 青旅多人间常需性别分区 |
| `facilities` | VARCHAR(500) | 房间设施 | 空调/卫浴/储物柜等（冗余设计，便于展示） |

**索引设计**：
- `idx_room_type` / `idx_is_shared`：房型筛选
- `idx_price`：价格区间筛选
- `idx_status` / `idx_floor`：状态和楼层筛选

---

### 4. 预定订单表 `hostel_order`

**核心作用**：存储用户的预订信息，是交易系统的核心表，关联用户和房源。

**字段设计思路**：

| 字段 | 类型 | 说明 | 设计原因 |
|------|------|------|---------|
| `order_sn` | VARCHAR(64) | 订单号 | **业务唯一标识**（如：YH202403010001） |
| `user_id` | BIGINT | 下单用户ID | 关联 `sys_user.id` |
| `room_id` | BIGINT | 房间ID | 关联 `hostel_room.id` |
| `checkin_date` / `checkout_date` | DATE | 入住/退房日期 | 计算入住天数 |
| `bed_count` | INT | 预订床位数 | 支持预订多个床位 |
| `total_amount` / `pay_amount` | DECIMAL | 金额 | 区分原价和实付，支持优惠 |
| `order_status` | TINYINT | 订单状态 | 1:待支付→2:已确认→3:已入住→4:已完成 |
| `pay_status` | TINYINT | 支付状态 | 与订单状态解耦，支持部分退款 |
| `guest_info` | TEXT | 入住人信息 | JSON格式存储多人入住信息 |
| `contact_name` / `contact_phone` | VARCHAR | 联系人 | 可能与下单人不同 |

**索引设计**：
- `uk_order_sn`：订单号唯一索引
- `idx_user_id` / `idx_room_id`：关联查询
- `idx_checkin_date` / `idx_checkout_date`：日期范围查询（房态、统计）
- `idx_order_status` / `idx_pay_status`：状态筛选
- `idx_user_status`：**联合索引**，优化"我的订单"分页查询

---

## 三、表关系图

```
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   sys_user  │      │ hostel_order │      │ hostel_room  │
│   (用户)    │      │   (订单)     │      │   (房间)     │
├─────────────┤      ├──────────────┤      ├──────────────┤
│ id          │←──┐  │ id           │   ┌─→│ id           │
│ phone       │   └──┤ user_id      │   │  │ room_name    │
│ username    │      │ room_id      │───┘  │ room_type    │
│ member_level│      │ order_sn     │      │ price_per_bed│
│ ...         │      │ checkin_date │      │ ...          │
└─────────────┘      │ checkout_date│      └──────────────┘
                     │ total_amount │
                     │ order_status │
                     │ ...          │
                     └──────┬───────┘
                            │
                            ▼
┌─────────────┐      ┌──────────────┐
│   sys_dict  │      │  订单状态枚举 │
│  (字典表)   │      │  支付方式枚举 │
├─────────────┤      │  房型枚举     │
│ dict_code   │      │  ...          │
│ item_key    │      └──────────────┘
│ item_value  │
│ ...         │
└─────────────┘
```

---

## 四、执行方式

```bash
# 方式1：MySQL命令行
mysql -u root -p < youth-hostel-parent/docs/sql/01_init_tables.sql

# 方式2：Navicat / DBeaver 等GUI工具
# 直接复制SQL内容执行
```

---

## 五、后续扩展建议

1. **新增表**：`hostel_room_inventory`（房态日历表，优化日期范围查询）
2. **新增表**：`sys_payment_record`（支付记录表，与订单表解耦）
3. **新增表**：`hostel_review`（评价表，完善评价体系）
4. **分库分表**：订单表数据量大会按 `user_id` 或 `create_time` 分片
