# 牛客论坛前端项目

基于 Vue 3 + Element Plus 的前后端分离项目

## 技术栈

- **框架**: Vue 3 (Composition API)
- **UI库**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **构建工具**: Vite

## 项目结构

```
frontend/
├── public/              # 静态资源
├── src/
│   ├── api/            # API接口
│   │   ├── request.js  # Axios封装
│   │   ├── user.js     # 用户相关API
│   │   └── post.js     # 帖子相关API
│   ├── router/         # 路由配置
│   ├── views/          # 页面组件
│   │   ├── Layout.vue  # 布局组件
│   │   ├── Home.vue    # 首页
│   │   ├── Login.vue   # 登录页
│   │   └── Register.vue # 注册页
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML模板
├── package.json        # 依赖配置
└── vite.config.js      # Vite配置
```

## 安装依赖

```bash
cd frontend
npm install
```

## 开发运行

```bash
npm run dev
```

访问: http://localhost:3000

## 生产构建

```bash
npm run build
```

构建产物在 `dist` 目录

## 配置说明

### API代理

在 `vite.config.js` 中配置了代理，将 `/api` 开头的请求代理到后端服务器：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

### Element Plus

已全局引入 Element Plus 及其图标库，可直接使用所有组件。

## 主要功能

- ✅ 用户注册
- ✅ 用户登录
- ✅ 首页帖子列表
- ✅ 分页功能
- ✅ 响应式布局
- 🚧 帖子详情（开发中）
- 🚧 发布帖子（开发中）
- 🚧 评论功能（开发中）

## 注意事项

1. 确保后端服务运行在 `http://localhost:8081`
2. 后端需要配置CORS允许跨域请求
3. 登录后token存储在 localStorage

