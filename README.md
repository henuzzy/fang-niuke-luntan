# 牛客论坛项目

这是一个基于Spring Boot + MyBatis + Thymeleaf的仿牛客论坛项目。

## 项目结构

```
nklt/
├── src/
│   ├── main/
│   │   ├── java/com/yue/nklt/
│   │   │   ├── config/          # 配置文件
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # MyBatis Mapper接口
│   │   │   ├── service/         # 服务层
│   │   │   ├── utils/           # 工具类
│   │   │   └── NkltApplication.java
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML映射文件
│   │       ├── static/          # 静态资源
│   │       │   ├── css/         # 样式文件
│   │       │   ├── js/          # JavaScript文件
│   │       │   └── img/         # 图片资源
│   │       ├── templates/       # Thymeleaf模板
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 技术栈

- **后端框架**: Spring Boot 3.4.10
- **持久层**: MyBatis 3.0.3
- **数据库**: MySQL 8.0
- **模板引擎**: Thymeleaf
- **前端**: HTML5 + CSS3 + JavaScript
- **缓存**: Redis
- **消息队列**: Kafka

## 功能模块

### 已实现功能

1. **用户模块**
   - 用户注册
   - 用户登录（支持"记住我"功能）
   - 退出登录
   - 查看个人主页
   - 修改密码
   - 上传头像

2. **帖子模块**
   - 分页查询帖子列表（支持最新/最热排序）
   - 发布帖子
   - 查询帖子详情
   - 评论帖子
   - 点赞帖子（待完善）

3. **消息模块**
   - 查询私信列表

## 数据库表结构

项目包含以下5个主要数据表：

1. `user` - 用户表
2. `discuss_post` - 帖子表
3. `comment` - 评论表
4. `login_ticket` - 登录凭证表
5. `message` - 私信表

详细的表结构请查看 `src/main/resources/sql/` 目录下的SQL文件。

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+（可选）
- Kafka 2.8+（可选）

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE nowcoder CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本（位于 `src/main/resources/sql/` 目录）：
```sql
source init_schema.sql;
source init_data.sql;
```

3. 修改 `application.properties` 中的数据库配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nowcoder
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者使用IDE直接运行 NkltApplication.java
```

### 4. 访问应用

浏览器访问：http://localhost:8081

## API接口文档

详细的接口文档请查看 `接口信息/仿牛客论坛接口1.md`

## 统一响应格式

所有API接口返回统一的响应格式：

```json
{
    "code": 1,           // 1表示成功，0表示失败
    "msg": "success",    // 提示信息
    "data": {},          // 返回的数据
    "total": 100         // 总数（分页时使用）
}
```

## 项目截图

请查看 `页面原型/` 目录下的设计图。

详细的前端设计说明请查看 `README_FRONTEND.md`。

## 待完善功能

- [ ] 点赞功能（使用Redis实现）
- [ ] 关注功能
- [ ] 站内通知（使用Kafka实现）
- [ ] 帖子搜索（使用Elasticsearch）
- [ ] 敏感词过滤
- [ ] 权限管理（Spring Security）

## 注意事项

1. 项目使用了Lombok，请确保IDE安装了Lombok插件
2. 数据库表的字符集建议使用utf8mb4
3. 密码使用MD5+盐值进行加密存储
4. 登录凭证存储在数据库中，后续可优化为Redis存储

## 开发者

- 开发者：Yue
- 创建时间：2025

## 许可证

本项目仅供学习交流使用。

