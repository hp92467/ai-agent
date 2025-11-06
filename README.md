# 旋转机械故障诊断AI 智能体

## 项目概述

这是一个基于Spring AI和Vue.js构建的智能旋转机械故障诊断系统，专门用于风机、泵、轴承、齿轮箱、电机等旋转设备的故障诊断和预测性维护。系统集成了先进的AI技术、RAG（检索增强生成）知识库、向量数据库和多种诊断工具，为工业设备维护提供智能化的解决方案。


## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.4.9 + Java 21
- **AI框架**: Spring AI + 阿里云DashScope AI
- **数据库**: PostgreSQL + PgVector (向量数据库)
- **文档处理**: iText PDF + Markdown解析
- **工具库**: Hutool AI + JSoup网页抓取
- **API文档**: Knife4j (Swagger)

### 前端技术栈
- **框架**: Vue 3 + Vite
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **实时通信**: Server-Sent Events (SSE)

### 核心模块
- **MCP搜索器**: 独立的MCP (Model Context Protocol) 搜索服务
- **RAG系统**: 基于向量数据库的知识检索增强
- **工具集**: 多种AI工具集成

## 🎯 核心功能

### 1. AI故障诊断大师
**路径**: `/app`

专业的旋转机械故障诊断专家，具备以下能力：

#### 诊断场景覆盖
- **正常运行状态**: 建立振动基线、温度监控、噪声分析、电流监测
- **早期故障状态**: 轴承点蚀、轻微不对中、齿轮磨损、电机断条检测
- **严重故障状态**: 轴承损坏、主轴裂纹、齿轮断齿、紧急停机判断

#### 专业诊断能力
- 振动频谱分析（FFT、包络谱分析）
- 温度异常监测
- 噪声特征识别
- 电流频谱分析
- 油液分析建议

#### 交互式诊断流程
1. **状态评估**: 根据用户描述判断设备当前状态
2. **信息收集**: 引导用户提供关键参数（振动、温度、频率等）
3. **专业分析**: 基于专业知识库进行故障分析
4. **处理建议**: 提供针对性的维修和预防措施

### 2. AI超级智能体 (YuManus)
**路径**: `/manus`

通用AI助手，集成多种工具，能够：
- 文件操作（读取、写入、搜索）
- 网络搜索和信息抓取
- PDF报告生成
- 终端命令执行
- 资源下载和管理

## 📚 知识库系统

### 专业文档库
系统内置了完整的旋转机械故障诊断知识库：

1. **paper1.md**: 正常运行状态诊断
   - 振动基线建立方法
   - 风机正常频谱特征
   - 轴承温度监控标准
   - 齿轮箱声音判断
   - 电机电流分析

2. **paper2.md**: 早期故障诊断
   - 轴承早期点蚀检测
   - 轻微不对中识别
   - 齿轮早期磨损判断
   - 电机断条检测

3. **paper3.md**: 严重故障诊断
   - 轴承严重损坏紧急信号
   - 主轴裂纹检测方法
   - 齿轮断齿故障特征

### RAG增强检索
- **向量存储**: 使用PgVector存储文档向量
- **智能检索**: 基于语义相似度的文档检索
- **上下文增强**: 结合检索内容进行更准确的回答

## 🛠️ 工具集成

### 核心工具集
- **WebSearchTool**: 网络搜索和信息获取
- **FileOperationTool**: 文件系统操作
- **PDFGenerationTool**: 诊断报告生成
- **WebScrapingTool**: 网页内容抓取
- **TerminalOperationTool**: 系统命令执行
- **ResourceDownloadTool**: 资源下载管理

### MCP工具集成
- 支持MCP协议的工具调用
- 可扩展的工具生态系统
- 标准化的工具接口

## 🚀 快速开始

### 环境要求
- Java 21+
- Node.js 16+
- PostgreSQL 12+
- Maven 3.6+

### 后端启动
```bash
# 1. 配置数据库
# 修改 src/main/resources/application.yml 中的数据库连接信息

# 2. 配置AI API密钥
# 设置环境变量 api-key 为您的DashScope API密钥

# 3. 启动应用
mvn spring-boot:run
```

### 前端启动
```bash
cd ai-agent-frontend
npm install
npm run dev
```

### MCP搜索器启动
```bash
cd mcp-searcher
mvn spring-boot:run
```

## 📡 API接口

### 主要端点
- `GET /ai/app/chat/sync` - 同步诊断对话
- `GET /ai/app/chat/sse` - 流式诊断对话
- `GET /ai/manus/chat` - 超级智能体对话
- `GET /health` - 健康检查

### API文档
访问 `http://localhost:8123/api/swagger-ui.html` 查看完整API文档

## 🔧 配置说明

### 应用配置
```yaml
spring:
  application:
    name: ai-agent
  datasource:
    url: jdbc:postgresql://localhost:5432/ai_agent
  ai:
    dashscope:
      api-key: ${api-key}
    chat:
      options:
        model: qwq-plus

server:
  port: 8123
  servlet:
    context-path: /api
```

### 向量数据库配置
系统支持PgVector向量数据库，用于RAG功能：
- 索引类型: HNSW
- 距离度量: COSINE_DISTANCE
- 维度: 1536 (默认)

## 💡 使用场景

### 工业维护
- **预防性维护**: 基于设备状态预测维护需求
- **故障诊断**: 快速定位设备故障原因
- **知识管理**: 积累和传承诊断经验
- **培训支持**: 为维护人员提供学习平台

### 典型工作流程
1. **设备状态描述**: 用户描述设备当前状态和异常现象
2. **参数收集**: AI引导收集关键诊断参数
3. **智能分析**: 基于知识库进行专业分析
4. **诊断报告**: 生成详细的诊断报告和处理建议
5. **跟踪记录**: 保存诊断历史，便于后续参考

## 🔍 项目结构

```
ai-agent/
├── src/main/java/com/hp/aiagent/
│   ├── agent/              # AI代理实现
│   │   ├── BaseAgent.java  # 基础代理类
│   │   ├── YuManus.java    # 超级智能体
│   │   └── ToolCallAgent.java # 工具调用代理
│   ├── app/                # 应用核心
│   │   └── MyApp.java      # 故障诊断应用
│   ├── controller/         # REST控制器
│   ├── tools/              # 工具集成
│   ├── rag/                # RAG系统
│   ├── chatmemory/         # 对话记忆
│   └── config/             # 配置类
├── ai-agent-frontend/      # Vue前端
├── mcp-searcher/           # MCP搜索服务
└── src/main/resources/
    └── document/           # 知识库文档
```




## 📞 联系方式

- 项目维护者: HP
- 邮箱: hp92467@163.com
- 项目链接: (https://github.com/hp92467/ai-agent)

---
