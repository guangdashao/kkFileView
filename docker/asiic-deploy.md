# kkFileView Docker 部署文档

## 第一部分：Docker Compose 配置说明

`docker-compose.yaml` 文件定义了一个名为 `platform-kkfileview` 的服务，用于部署 kkFileView 文档预览服务。

### 服务配置概览

- **镜像**：`harborx.ansteel.cn/platform/kkfileview:5.0.0`
- **容器名称**：`platform-kkfileview`
- **重启策略**：`unless-stopped`
- **网络模式**：`host`（使用宿主机网络）
- **初始化**：`init: true`（使用 init 进程处理信号）
- **健康检查**：通过 HTTP 请求 `/preview/actuator/health` 端点验证服务状态
- **资源限制**：通过环境变量 `CPU_LIMITS` 和 `MEMORY_LIMITS` 控制 CPU 和内存限制
- **数据卷**：将宿主机 `./logs` 目录挂载到容器内 `/application/logs`，用于持久化日志

### 环境变量配置

在 `environment` 部分可以设置以下环境变量，这些变量将覆盖 `application.properties` 中的对应配置：

| 环境变量 | 默认值（Compose 中） | 说明                                                                                            | 对应 application.properties 配置项 |
|----------|----------------------|-----------------------------------------------------------------------------------------------|-----------------------------------|
| `KK_SERVER_PROFILE` | `dev` | Spring Boot 激活的配置文件。`default` 为生产模式；`dev` 启用调试界面（支持本地上传）。                                     | `spring.profiles.active` |
| `DEFAULT_FILE_UPLOAD_DISABLE` | `false` | 是否禁用首页文件上传功能。`true` 禁用上传（仅预览）；`false` 允许上传（开发调试用）。                                            | `file.upload.disable` |
| `KK_BASE_URL` | `default` | 提供预览服务的地址。默认从请求 URL 读取；若使用 Nginx 等反向代理，需手动设置。                                                 | `base.url` |
| `KK_TRUST_HOST` | `default` | 信任站点白名单（多个用逗号分隔）。为防止 SSRF 攻击，强烈建议配置。`default` 表示仅允许本机测试；`*` 允许所有域名（不推荐）。 yj3dev*.asiic.cn 可匹配yj3dev.asiic.cn 或 yj3dev-admin.asiic.cn | `trust.host` |
| `KK_OFFICE_PREVIEW_TYPE` | `pdf` | Office 文档（Word、PPT）预览类型。可选值：`pdf`（转换为 PDF）、`image`（转换为图片）。预览页面上可切换。                           | `office.preview.type` |
| `WATERMARK_TXT` | （空） | 水印文本内容。如需取消水印，设置为空。示例：`凯京科技内部文件，严禁外泄`。                                                        | `watermark.txt` |
| `KK_FILE_DIR` | `default` | 预览生成资源的存储路径。默认值为程序根目录下的 `file` 目录。请确保磁盘有足够容量。                                                 | `file.dir` |
| `KK_CACHE_TYPE` | `default` | 缓存实现方式。可选值：`default`（内嵌 RocksDB，使用磁盘存储）、`jdk`（纯内存对象存储）、`redis`（外部 Redis 服务）。                  | `cache.type` |
| `KK_SERVER_PORT` | （注释） | 服务器端口号，默认 8012。在 Compose 中已被注释，可通过环境变量设置。                                                     | `server.port` |
| `KK_CONTEXT_PATH` | （注释） | 应用上下文路径，默认 `/preview`。在 Compose 中已被注释，可通过环境变量设置。                                              | `server.servlet.context-path` |

application.properties 有环境变量的，都可以在docker-compose中配置，如果要修改的变量，没有环境变量。
则需要通过挂载配置文件，将完整的配置文件挂载到容器中。
application.properties:/opt/kkFileView-5.0.0/config/application.properties

其中 /opt/kkFileView-5.0.0/config/application.properties 为容器内部默认配置文件。

### 资源限制变量

以下变量用于控制容器资源，需在 `.env` 文件或 Docker Compose 运行环境中定义：

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `CPU_LIMITS` | 容器可使用的 CPU 核数（限制与保留）。 | `0.5` |
| `MEMORY_LIMITS` | 容器可使用的内存大小（限制与保留）。 | `512m` |

### 使用说明

1. 根据需要修改环境变量值（可直接在 `docker-compose.yaml` 中修改，或通过外部 `.env` 文件定义）。
2. 启动服务：`docker-compose up -d`
3. 查看日志：`docker-compose logs -f platform-kkfileview`
4. 服务访问地址：`http://宿主机IP:8012/preview`

---

## 第二部分：application.properties 全量配置项说明

`application.properties` 是 kkFileView 的核心配置文件，支持通过环境变量动态覆盖。以下按配置章节列出所有可配置项及其含义、默认值和对应的环境变量。

### 一、服务器基础配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `server.port` | 服务器端口号 | 8012 | `KK_SERVER_PORT` |
| `server.servlet.context-path` | 应用上下文路径 | `/preview` | `KK_CONTEXT_PATH` |
| `server.servlet.encoding.charset` | 字符编码 | `utf-8` | 无 |
| `server.compression.enabled` | 启用响应压缩 | `true` | 无 |
| `server.compression.min-response-size` | 最小压缩响应大小（字节） | 2048 | 无 |
| `server.compression.mime-types` | 压缩的 MIME 类型列表 | （多种类型） | 无 |
| `spring.servlet.multipart.max-file-size` | 单个文件上传大小限制 | 500MB | `MAX_FILE_SIZE` |
| `spring.servlet.multipart.max-request-size` | 整个请求上传大小限制 | 500MB | `MAX_FILE_SIZE` |
| `spring.profiles.active` | 激活的 Spring 配置文件 | `default` | `KK_SERVER_PROFILE` |
| `spring.freemarker.template-loader-path` | FreeMarker 模板加载路径 | `classpath:/web/` | 无 |
| `spring.freemarker.cache` | 是否缓存模板 | `false` | 无 |
| `spring.freemarker.charset` | 模板字符集 | `UTF-8` | 无 |
| `spring.freemarker.check-template-location` | 检查模板位置 | `true` | 无 |
| `spring.freemarker.content-type` | 响应内容类型 | `text/html` | 无 |
| `spring.freemarker.expose-request-attributes` | 暴露请求属性 | `true` | 无 |
| `spring.freemarker.expose-session-attributes` | 暴露会话属性 | `true` | 无 |
| `spring.freemarker.request-context-attribute` | 请求上下文属性名 | `request` | 无 |
| `spring.freemarker.suffix` | 模板后缀 | `.ftl` | 无 |
| `management.endpoints.web.exposure.include` | Actuator 暴露的端点 | `health` | 无 |
| `management.endpoint.health.show-details` | 健康端点显示详情 | `always` | 无 |
| `management.health.defaults.enabled` | 启用默认健康指示器 | `true` | 无 |

### 二、Office 文档处理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `office.home` | Office 组件安装路径（自动查找） | `default` | `KK_OFFICE_HOME` |
| `office.plugin.server.ports` | Office 组件服务端口（负载均衡） | `2001,2002` | 无 |
| `office.plugin.task.timeout` | Office 组件任务超时时间 | `5m` | 无 |
| `office.plugin.task.maxtasksperprocess` | 每个进程最大任务数 | 200 | 无 |
| `office.plugin.task.taskexecutiontimeout` | 任务执行超时时间 | `5m` | 无 |
| `office.pagerange` | Office 文档分页范围（启用后可指定页面范围） | `false` | `KK_OFFICE_PAGERANGE` |
| `office.watermark` | Office 文档水印功能 | `false` | `KK_OFFICE_WATERMARK` |
| `office.quality` | Office 图片质量（1‑100） | 80 | `KK_OFFICE_QUALITY` |
| `office.maximageresolution` | Office 图片最大分辨率 | 150 | `KK_OFFICE_MAXIMAGERESOLUTION` |
| `office.exportbookmarks` | 导出 Office 书签（转换 PDF 时保留） | `true` | `KK_OFFICE_EXPORTBOOKMARKS` |
| `office.exportnotes` | 将 Office 批注作为 PDF 注释导出 | `true` | `KK_OFFICE_EXPORTNOTES` |
| `office.documentopenpasswords` | 加密文档生成的 PDF 添加密码 | `true` | `KK_OFFICE_DOCUMENTOPENPASSWORD` |
| `office.type.web` | Excel（xlsx）前端解析方式（`web` 或 `image`） | `web` | `KK_OFFICE_TYPE_WEB` |
| `office.preview.type` | Office 文档预览类型（`image` / `pdf`） | `pdf` | `KK_OFFICE_PREVIEW_TYPE` |
| `office.preview.switch.disabled` | 是否关闭 Office 预览模式切换开关 | `true` | `KK_OFFICE_PREVIEW_SWITCH_DISABLED` |

### 三、CAD 文件处理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `cad.preview.type` | CAD 文件预览类型（`svg` / `pdf`） | `svg` | `KK_CAD_PREVIEW_TYPE` |
| `cad.conversionmodule` | CAD 转换模块（1=aspose-cad，2=cadviewer） | 1 | 无 |
| `cad.cadconverterpath` | CAD 后端转换包路径 | `false` | 无 |
| `cad.thread` | CAD 文件处理线程数 | 5 | `KK_CAD_THREAD` |
| `cad.timeout` | CAD 文件处理超时时间（秒） | 90 | `KK_CAD_TIMEOUT` |

### 四、PDF 文件处理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `pdf.presentationMode.disable` | 是否禁止 PDF 演示模式 | `true` | `KK_PDF_PRESENTATION_MODE_DISABLE` |
| `pdf.openFile.disable` | 是否禁止 PDF 文件菜单中的“打开文件”选项 | `true` | `KK_PDF_OPEN_FILE_DISABLE` |
| `pdf.print.disable` | 是否禁止 PDF 打印功能 | `true` | `KK_PDF_PRINT_DISABLE` |
| `pdf.download.disable` | 是否禁止 PDF 下载功能 | `true` | `KK_PDF_DOWNLOAD_DISABLE` |
| `pdf.bookmark.disable` | 是否禁止 PDF 书签/大纲功能 | `false` | `KK_PDF_BOOKMARK_DISABLE` |
| `pdf.disable.editing` | 是否禁止 PDF 编辑功能（注释、表单等） | `true` | `KK_PDF_DISABLE_EDITING` |
| `pdf.max.threads` | PDF 处理最大线程数 | 10 | 无 |
| `pdf.timeout.small` | 小文件超时（秒） | 90 | 无 |
| `pdf.timeout.medium` | 中等文件超时（秒） | 180 | 无 |
| `pdf.timeout.large` | 大文件超时（秒） | 300 | 无 |
| `pdf.timeout.xlarge` | 超大文件超时（秒） | 600 | 无 |
| `pdf.dpi.enabled` | 启用 PDF DPI 智能调整 | `true` | 无 |
| `pdf2jpg.dpi` | PDF 转图片的基准 DPI（当 DPI 优化禁用时使用） | 144 | `KK_PDF2JPG_DPI` |
| `pdf.dpi.small` | 小文件（0‑50 页）DPI | 150 | 无 |
| `pdf.dpi.medium` | 中等文件（50‑100 页）DPI | 120 | 无 |
| `pdf.dpi.large` | 大文件（100‑200 页）DPI | 96 | 无 |
| `pdf.dpi.xlarge` | 超大文件（200‑500 页）DPI | 72 | 无 |
| `pdf.dpi.xxlarge` | 巨量文件（>500 页）DPI | 72 | 无 |

### 五、TIF 文件处理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `tif.preview.type` | TIF 文件预览类型（`tif` / `jpg` / `pdf`） | `tif` | `KK_TIF_PREVIEW_TYPE` |
| `tif.thread` | TIF 文件处理线程数 | 5 | 无 |
| `tif.timeout` | TIF 文件处理超时时间（秒） | 90 | 无 |

### 六、媒体文件处理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `media` | 媒体文件类型（音频、视频）列表 | `mp3,wav,mp4,flv,mpd,m3u8,ts,mpeg,m4a` | `KK_MEDIA` |
| `convertMedias` | 需要转换的媒体文件类型列表 | `avi,mov,wmv,mkv,3gp,rm,mpeg` | `KK_CONVERTMEDIAS` |
| `media.timeout.enabled` | 启用媒体文件超时控制 | `true` | 无 |
| `media.small.file.timeout` | 小媒体文件超时（秒） | 30 | 无 |
| `media.medium.file.timeout` | 中等媒体文件超时（秒） | 60 | 无 |
| `media.large.file.timeout` | 大媒体文件超时（秒） | 180 | 无 |
| `media.xl.file.timeout` | 超大媒体文件超时（秒） | 300 | 无 |
| `media.xxl.file.timeout` | 巨量媒体文件超时（秒） | 600 | 无 |
| `media.xxxl.file.timeout` | 特大媒体文件超时（秒） | 1200 | 无 |
| `media.convert.max.size` | 媒体文件转换最大大小（MB） | 300 | 无 |
| `media.convert.disable` | 是否禁用视频格式转换功能 | `false` | `KK_MEDIA_CONVERT_DISABLE` |

### 七、文件存储与缓存配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `file.dir` | 预览生成资源的存储路径 | `default`（程序根目录下的 `file` 目录） | `KK_FILE_DIR` |
| `local.preview.dir` | 允许预览的本地文件夹路径（安全警告：谨慎配置） | `default`（禁止所有本地文件预览） | `KK_LOCAL_PREVIEW_DIR` |
| `cache.enabled` | 是否启用缓存 | `true` | `KK_CACHE_ENABLED` |
| `cache.type` | 缓存实现类型（`jdk` / `redis` / `default`） | `default`（内嵌 RocksDB） | `KK_CACHE_TYPE` |
| `rocksdb.wal.ttl.seconds` | WAL 写前日志过期时间（秒） | 86400（24 小时） | `KK_ROCKSDB_WAL_TTL` |
| `rocksdb.wal.size.limit.mb` | WAL 写前日志大小限制（MB） | 100 | `KK_ROCKSDB_WAL_SIZE_LIMIT` |
| `rocksdb.keep.log.file.num` | 保留的旧 LOG 文件数量上限 | 5 | `KK_ROCKSDB_KEEP_LOG_NUM` |
| `spring.redisson.mode` | Redis 部署模式（`single` / `cluster` / `sentinel` / `master-slave`） | `single` | 无 |
| `spring.redisson.address` | Redis 连接地址 | `redis://127.0.0.1:6379` | `KK_SPRING_REDISSON_ADDRESS` |
| `spring.redisson.password` | Redis 连接密码 | （空） | `KK_SPRING_REDISSON_PASSWORD` |
| `spring.redisson.database` | Redis 数据库索引（0‑15） | 0 | `KK_SPRING_REDISSON_DATABASE` |
| `cache.clean.enabled` | 是否启用缓存自动清理 | `true` | `KK_CACHE_CLEAN_ENABLED` |
| `cache.clean.cron` | 缓存自动清理时间（Quartz cron 表达式） | `0 0 3 * * ?`（每天凌晨 3 点） | `KK_CACHE_CLEAN_CRON` |

### 八、安全与访问控制配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `base.url` | 提供预览服务的地址（默认从请求 URL 读取） | `default` | `KK_BASE_URL` |
| `trust.host` | 信任站点白名单（多个用逗号分隔） | `default`（仅本机测试） | `KK_TRUST_HOST` |
| `not.trust.host` | 不信任站点黑名单（多个用逗号分隔） | `default` | `KK_NOT_TRUST_HOST` |
| `prohibit` | 禁止访问的文件类型（格式：`exe,dll,dat`） | `exe,dll,dat` | `KK_PROHIBIT` |
| `kk.ignore.ssl` | 是否忽略 SSL 证书验证（生产环境建议 `false`） | `false` | 无 |
| `kk.enable.redirect` | 是否启用 URL 重定向功能 | `false` | 无 |

### 九、水印配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `watermark.txt` | 水印文本内容 | （空） | `WATERMARK_TXT` |
| `watermark.x.space` | 水印 X 轴间距 | 10 | `WATERMARK_X_SPACE` |
| `watermark.y.space` | 水印 Y 轴间距 | 10 | `WATERMARK_Y_SPACE` |
| `watermark.font` | 水印字体 | `微软雅黑` | `WATERMARK_FONT` |
| `watermark.fontsize` | 水印字体大小 | `18px` | `WATERMARK_FONTSIZE` |
| `watermark.color` | 水印颜色 | `black` | `WATERMARK_COLOR` |
| `watermark.alpha` | 水印透明度（0.0‑1.0） | 0.2 | `WATERMARK_ALPHA` |
| `watermark.width` | 水印宽度 | 180 | `WATERMARK_WIDTH` |
| `watermark.height` | 水印高度 | 80 | `WATERMARK_HEIGHT` |
| `watermark.angle` | 水印旋转角度 | 10 | `WATERMARK_ANGLE` |

### 十、FTP 文件访问配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `ftp.username` | FTP 连接信息（格式：`地址:端口:用户名:密码:编码`，多个客户端用逗号分隔） | `false` | 无 |

### 十一、首页与文件管理配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `file.upload.disable` | 是否禁用首页文件上传功能 | `true` | `DEFAULT_FILE_UPLOAD_DISABLE` |
| `beian` | 网站备案信息（显示在首页底部） | `default`（空） | `KK_BEIAN` |
| `home.pagenumber` | 首页初始化加载的页码 | 1 | `DEFAULT_HOME_PAGENUMBER` |
| `home.pagesize` | 首页每页显示的文件数量 | 20 | `DEFAULT_HOME_PAGSIZE` |
| `delete.captcha` | 是否启用验证码验证删除文件 | `false` | `KK_DELETE_CAPTCHA` |
| `delete.password` | 删除文件密码 | `123456` | `KK_DELETE_PASSWORD` |
| `delete.source.file` | 是否删除转换后的源文件 | `true` | `KK_DELETE_SOURCE_FILE` |

### 十二、权限与认证配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `kk.Picturespreview` | 是否启用图片预览权限 | `true` | 无 |
| `kk.Getcorsfile` | 是否启用跨域文件获取权限 | `true` | 无 |
| `kk.addTask` | 是否启用添加异步任务权限 | `true` | 无 |
| `kk.key` | API 密钥功能（启用后需提供密钥调用 API） | `false` | 无 |
| `aes.key` | AES 加密密钥（16 位字符） | `1234567890123456` | 无 |
| `basic.name` | Basic 认证配置（格式：`域名:用户名:密码`，多个逗号分隔） | `false` | 无 |
| `useragent` | User‑Agent 验证字符串 | `false` | 无 |

### 十三、高级功能与兼容性配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `kk.refreshschedule` | 异步配置刷新定时时间（秒） | 2 | 无 |
| `kk.isshowaeskey` | 首页是否显示 AES 密钥 | `false` | 无 |
| `kk.xlsxallowedit` | 是否允许 XLSX 编辑 | `false` | 无 |
| `kk.xlsxshowtoolbar` | 是否显示 XLSX 工具栏 | `false` | 无 |
| `kk.isshowkey` | 首页是否显示 key 密钥 | `false` | 无 |
| `kk.scriptjs` | 预览 HTML 文件时是否启用 JavaScript | `true` | 无 |

### 十四、文件类型分类配置

| 配置项 | 描述 | 默认值 | 环境变量 |
|--------|------|--------|----------|
| `simText` | 纯文本文件类型（直接显示）列表 | `txt,html,htm,asp,jsp,xml,json,properties,md,gitignore,log,java,py,c,cpp,sql,sh,bat,m,bas,prg,cmd` | `KK_SIMTEXT` |

---

**说明**：
- 表格中“默认值”一栏为 `application.properties` 文件中定义的默认值。
- 若配置项支持环境变量覆盖，则会在“环境变量”列给出对应的环境变量名；环境变量的格式为 `${环境变量名:默认值}`。
- 通过 Docker Compose 部署时，可在 `environment` 部分设置上述环境变量，以动态调整配置。