# 牛客论坛 - Vue 3 前后端分离版本

## 项目架构

### 前端 (Vue 3 + Element Plus)
```
frontend/                 # 前端项目
├── src/
│   ├── api/             # API接口层
│   ├── views/           # 页面组件
│   ├── router/          # 路由配置
│   └── main.js          # 入口文件
└── package.json
```

### 后端 (Spring Boot)
```
src/
├── main/
│   ├── java/
│   │   └── com/yue/nklt/
│   │       ├── config/       # 配置类（含CORS）
│   │       ├── controller/   # REST API控制器
│   │       ├── service/      # 业务层
│   │       └── mapper/       # 数据层
│   └── resources/
└── pom.xml
```

## 快速开始

### 1. 启动后端

```bash
# 在项目根目录
mvn spring-boot:run
```

后端运行在: http://localhost:8081

### 2. 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端运行在: http://localhost:3000

## 技术栈对比

### 之前（传统SSR）
- **前端**: Thymeleaf模板 + jQuery
- **后端**: Spring Boot + Thymeleaf
- **渲染**: 服务端渲染
- **特点**: 前后端耦合

### 现在（前后端分离）
- **前端**: Vue 3 + Element Plus + Vite
- **后端**: Spring Boot REST API
- **渲染**: 客户端渲染（CSR）
- **特点**: 前后端完全独立

## 主要改动

### 后端改动

1. **新增CORS配置** (`CorsConfig.java`)
   - 允许跨域请求
   - 支持携带凭证

2. **Controller返回JSON**
   - 使用 `@RestController`
   - 返回 `Result` 对象

### 前端特性

1. **Vue 3 Composition API**
   - 使用 `<script setup>` 语法
   - 响应式数据管理

2. **Element Plus UI组件**
   - 美观的UI界面
   - 丰富的组件库

3. **Vue Router路由管理**
   - 单页应用（SPA）
   - 页面无刷新切换

4. **Axios HTTP客户端**
   - 统一的API调用
   - 请求/响应拦截器

## API文档

### 用户相关

- `POST /users/register` - 用户注册
- `POST /users/login` - 用户登录
- `POST /users/logout` - 退出登录
- `GET /users/{id}/profile` - 获取用户信息

### 帖子相关

- `GET /posts` - 获取帖子列表
- `GET /posts/{id}` - 获取帖子详情
- `POST /posts` - 发布帖子
- `POST /posts/{id}/comments` - 评论帖子
- `POST /posts/{id}/like` - 点赞帖子

## 开发指南

### 添加新页面

1. 在 `frontend/src/views/` 创建页面组件
2. 在 `frontend/src/router/index.js` 添加路由
3. 在导航菜单中添加链接

### 添加新API

1. 在 `frontend/src/api/` 中添加API方法
2. 在页面组件中调用API
3. 后端创建对应的Controller方法

### 样式定制

- 使用 Element Plus 主题定制
- 在组件中使用 `<style scoped>`
- 全局样式在 `App.vue` 中定义

## 生产部署

### 前端构建

```bash
cd frontend
npm run build
```

生成 `dist` 目录，可部署到 Nginx

### 后端打包

```bash
mvn clean package
```

生成 `target/nklt-0.0.1-SNAPSHOT.jar`

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # 前端静态文件
    location / {
        root /var/www/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 常见问题

### Q: 前端无法连接后端？
A: 检查后端是否启动，CORS配置是否正确

### Q: 登录后刷新页面丢失状态？
A: Token已存储在localStorage，需在前端添加登录状态恢复逻辑

### Q: Element Plus样式没有生效？
A: 确保在main.js中正确引入了CSS文件

## 后续优化建议

- [ ] 添加Pinia状态管理
- [ ] 实现Token自动刷新
- [ ] 添加路由守卫（登录拦截）
- [ ] 优化首屏加载速度
- [ ] 添加骨架屏
- [ ] 实现图片懒加载
- [ ] 添加单元测试

## 贡献指南

欢迎提交Issue和Pull Request！

## 许可证

MIT License

