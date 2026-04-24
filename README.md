# Public Repo Config Guide

这个仓库已经把敏感信息替换成了 `xxx`。如果你要在本地或服务器运行项目，请按下面的位置补齐配置。
一个基于springboot的项目，可以调用ai生成问题生成评分，回答问题
<img width="2260" height="2323" alt="image" src="https://github.com/user-attachments/assets/70806a21-cd12-40a4-a84d-ced9fda22637" />
<img width="2432" height="1434" alt="image" src="https://github.com/user-attachments/assets/569bf0f3-5ffb-4aff-b7ab-e81dd5972a57" />


## Backend

### 1. 开发环境

文件：`src/main/resources/application.yml`

需要填写的项：

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${MYSQL_DATABASE:xxx}
    username: xxx
    password: xxx

  # 如需启用 Redis，再取消注释并填写
  redis:
    host: localhost
    port: 6379
    password: xxx

cos:
  client:
    accessKey: xxx
    secretKey: xxx
    region: xxx
    bucket: xxx

ai:
  api-key: xxx
```

说明：
- `spring.datasource`：MySQL 连接信息
- `spring.redis`：Redis 连接信息，默认代码里是注释状态，只有启用 Redis Session 时才需要
- `cos.client`：腾讯云 COS 配置
- `ai.api-key`：AI 接口密钥
- `${MYSQL_DATABASE:xxx}`：表示也可以通过环境变量 `MYSQL_DATABASE` 覆盖数据库名

### 2. 测试环境

文件：`src/main/resources/application-test.yml`

需要填写的项：
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.redis.host`
- `spring.redis.port`
- `spring.redis.password`

### 3. 生产环境

文件：`src/main/resources/application-prod.yml`

需要填写的项：
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.redis.host`
- `spring.redis.port`
- `spring.redis.password`
- `knife4j.basic.username`
- `knife4j.basic.password`

### 4. 文件访问域名

文件：`src/main/java/com/yupi/zhuxiaowen/constant/FileConstant.java`

需要填写的项：

```java
String COS_HOST = "https://xxx";
```

说明：
- 这里通常填写你自己的 COS 访问域名或 CDN 域名

## Frontend

### 1. 前端请求后端地址

文件：`zhuxiaowen-frontend/src/request.ts`

需要根据你的后端地址调整：

```ts
baseURL: "http://localhost:8101"
```

### 2. SSE 接口地址

文件：`zhuxiaowen-frontend/src/views/add/components/AiGenerateQuestionDrawer.vue`

需要根据你的后端地址调整：

```ts
"http://localhost:8101/api/question/ai_generate/sse"
```

### 3. OpenAPI 文档生成地址

文件：`zhuxiaowen-frontend/openapi2ts.config.ts`

需要根据你的后端地址调整：

```ts
schemaPath: "http://127.0.0.1:8101/api/v2/api-docs"
```

## Public Repo 建议

- 不要把真实的数据库密码、Redis 密码、COS 密钥、AI Key 直接提交到仓库
- 本地开发时优先填写到你自己的本地配置文件或环境变量里
- 构建产物、IDE 配置、`node_modules`、`target` 这类目录不要提交
- 如果后续要进一步规范，建议把前端接口地址也改成环境变量
