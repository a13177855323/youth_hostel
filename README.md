# 青旅综合服务平台

基于 Java 17 + SpringBoot 3.x + MyBatis-Plus + MySQL 8.x + Redis 的企业级多模块项目。

## 项目结构

```
youth-hostel-platform/
├── pom.xml                          # 父 POM
├── youth-hostel-common/             # 公共模块
├── youth-hostel-entity/             # 实体模块
├── youth-hostel-mapper/             # 数据访问模块
├── youth-hostel-service/            # 业务模块
└── youth-hostel-web/                # 接口模块
```

## 技术栈

- Java 17
- SpringBoot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Redis
- Druid 连接池
- Lombok

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE youth_hostel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 创建用户表

```sql
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3. 修改数据库配置

编辑 `youth-hostel-web/src/main/resources/application-dev.yml`，修改数据库连接信息。

### 4. 编译运行

```bash
# 编译
mvn clean install

# 运行
cd youth-hostel-web
mvn spring-boot:run
```

### 5. 访问接口

启动成功后，访问：
- API 地址: http://localhost:8080
- 用户列表: GET http://localhost:8080/api/user/list

## 模块依赖关系

```
web → service → mapper → entity → common
```

## 开发规范

- 实体类继承 `BaseEntity`
- Service 继承 `IService`，Impl 继承 `ServiceImpl`
- Mapper 继承 `BaseMapper`
- 统一返回 `Result<T>` 包装结果
- 业务异常使用 `BusinessException`

## 后续规划

- [ ] 集成 Spring Security + JWT 认证
- [ ] 添加 Swagger API 文档
- [ ] 接入 Docker 容器化
- [ ] 微服务拆分
- [ ] Kubernetes 部署
