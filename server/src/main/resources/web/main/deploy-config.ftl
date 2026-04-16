<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>kkFileView 部署配置文档</title>
    <link rel="icon" href="./favicon.ico" type="image/x-icon">
<#--    <link rel="preconnect" href="https://fonts.googleapis.com">-->
<#--    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>-->
<#--    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@400;600&family=Space+Grotesk:wght@500;700&display=swap" rel="stylesheet">-->
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/theme.css"/>
    <link rel="stylesheet" href="css/main-pages.css"/>
    <script type="text/javascript" src="js/jquery-3.6.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="highlight/highlight.min.js"></script>
    <style>
        .config-section {
            margin-bottom: 3rem;
        }
        .config-section h2 {
            border-bottom: 2px solid #007bff;
            padding-bottom: 0.5rem;
            margin-bottom: 1.5rem;
        }
        .config-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 1.5rem;
        }
        .config-table th,
        .config-table td {
            border: 1px solid #dee2e6;
            padding: 0.75rem;
            vertical-align: top;
        }
        .config-table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        .config-table tr:nth-child(even) {
            background-color: #f8f9fa;
        }
        .env-var {
            font-family: Consolas, Monaco, 'Courier New', monospace;
            background-color: #e9ecef;
            padding: 0.2rem 0.4rem;
            border-radius: 0.2rem;
        }
        .note {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 0.25rem;
            padding: 1rem;
            margin-bottom: 1.5rem;
        }
        .note strong {
            color: #856404;
        }
        .page-toc {
            position: sticky;
            top: 2rem;
            max-height: calc(100vh - 4rem);
            overflow-y: auto;
        }
        .page-toc ul {
            list-style: none;
            padding-left: 0;
        }
        .page-toc li {
            margin-bottom: 0.5rem;
        }
        .page-toc a {
            color: #495057;
            text-decoration: none;
        }
        .page-toc a:hover {
            color: #007bff;
        }
        .docs-layout {
            display: grid;
            grid-template-columns: 1fr 3fr;
            gap: 2rem;
        }
        @media (max-width: 992px) {
            .docs-layout {
                grid-template-columns: 1fr;
            }
            .page-toc {
                position: static;
                max-height: none;
            }
        }
    </style>
</head>

<body class="app-shell">
<nav class="site-nav navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="https://kkview.cn" target="_blank">kkFileView</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a href="./index">首页</a></li>
            <li><a href="./integrated">接入说明</a></li>
            <li><a href="./record">版本发布记录</a></li>
            <li class="active"><a href="./deploy-config">部署配置</a></li>
<#--            <li><a href="./sponsor">赞助开源</a></li>-->
<#--            <li><a href="./contact">技术支持</a></li>-->
        </ul>
    </div>
</nav>

<div class="page-shell">
    <div class="container" role="main">
        <section class="hero-section release-hero">
            <div class="hero-copy">
                <span class="eyebrow">Deployment Configuration</span>
                <h1 class="hero-title">kkFileView 部署与配置全指南</h1>
                <p class="hero-subtitle hero-subtitle-inline">
                    本文档详细说明 Docker Compose 部署方式以及 application.properties 中的所有配置项。
                </p>
                <div class="note-row">
                    <span class="tag brand">Docker Compose</span>
                    <span class="tag">环境变量</span>
                    <span class="tag highlight">配置项</span>
                    <span class="tag warn">安全</span>
                </div>
            </div>
        </section>

        <div class="docs-layout">
            <aside class="page-toc">
                <h3>文档目录</h3>
                <ul>
                    <li><a href="#part1">第一部分：Docker Compose 配置</a></li>
                    <li><a href="#docker-overview">服务概览</a></li>
                    <li><a href="#docker-env">环境变量</a></li>
                    <li><a href="#docker-usage">使用说明</a></li>
                    <li><a href="#part2">第二部分：application.properties 配置</a></li>
                    <li><a href="#server-base">服务器基础配置</a></li>
                    <li><a href="#office-config">Office 文档处理</a></li>
                    <li><a href="#cad-config">CAD 文件处理</a></li>
                    <li><a href="#pdf-config">PDF 文件处理</a></li>
                    <li><a href="#tif-config">TIF 文件处理</a></li>
                    <li><a href="#media-config">媒体文件处理</a></li>
                    <li><a href="#cache-config">文件存储与缓存</a></li>
                    <li><a href="#security-config">安全与访问控制</a></li>
                    <li><a href="#watermark-config">水印配置</a></li>
                    <li><a href="#ftp-config">FTP 文件访问</a></li>
                    <li><a href="#home-config">首页与文件管理</a></li>
                    <li><a href="#auth-config">权限与认证</a></li>
                    <li><a href="#advanced-config">高级功能与兼容性</a></li>
                    <li><a href="#filetype-config">文件类型分类</a></li>
                </ul>
            </aside>

            <div class="docs-content">
                <div class="note">
                    <strong>说明：</strong>
                    本文档基于 kkFileView 5.0.0 版本编写。配置项支持通过环境变量动态覆盖，环境变量的格式为 <code>&#36;&#123;环境变量名&#58;默认值&#125;</code>。
                </div>

                <section id="part1" class="config-section">
                    <h2>第一部分：Docker Compose 配置说明</h2>
                    <p><code>docker-compose.yaml</code> 文件定义了一个名为 <code>platform-kkfileview</code> 的服务，用于部署 kkFileView 文档预览服务。</p>
                    
                    <div id="docker-overview">
                        <h3>服务配置概览</h3>
                        <ul>
                            <li><strong>镜像</strong>：<code>harborx.ansteel.cn/platform/kkfileview:5.0.0</code></li>
                            <li><strong>容器名称</strong>：<code>platform-kkfileview</code></li>
                            <li><strong>重启策略</strong>：<code>unless-stopped</code></li>
                            <li><strong>网络模式</strong>：<code>host</code>（使用宿主机网络）</li>
                            <li><strong>初始化</strong>：<code>init: true</code>（使用 init 进程处理信号）</li>
                            <li><strong>健康检查</strong>：通过 HTTP 请求 <code>/preview/actuator/health</code> 端点验证服务状态</li>
                            <li><strong>资源限制</strong>：通过环境变量 <code>CPU_LIMITS</code> 和 <code>MEMORY_LIMITS</code> 控制 CPU 和内存限制</li>
                            <li><strong>数据卷</strong>：将宿主机 <code>./logs</code> 目录挂载到容器内 <code>/application/logs</code>，用于持久化日志</li>
                        </ul>
                    </div>

                    <div id="docker-env">
                        <h3>环境变量配置</h3>
                        <p>在 <code>environment</code> 部分可以设置以下环境变量，这些变量将覆盖 <code>application.properties</code> 中的对应配置：</p>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>环境变量</th>
                                    <th>默认值（Compose 中）</th>
                                    <th>说明</th>
                                    <th>对应 application.properties 配置项</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>KK_SERVER_PROFILE</code></td>
                                    <td><code>dev</code></td>
                                    <td>Spring Boot 激活的配置文件。<code>default</code> 为生产模式；<code>dev</code> 启用调试界面（支持本地上传）。</td>
                                    <td><code>spring.profiles.active</code></td>
                                </tr>
                                <tr>
                                    <td><code>DEFAULT_FILE_UPLOAD_DISABLE</code></td>
                                    <td><code>false</code></td>
                                    <td>是否禁用首页文件上传功能。<code>true</code> 禁用上传（仅预览）；<code>false</code> 允许上传（开发调试用）。</td>
                                    <td><code>file.upload.disable</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_BASE_URL</code></td>
                                    <td><code>default</code></td>
                                    <td>提供预览服务的地址。默认从请求 URL 读取；若使用 Nginx 等反向代理，需手动设置。</td>
                                    <td><code>base.url</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_TRUST_HOST</code></td>
                                    <td><code>default</code></td>
                                    <td>信任站点白名单（多个用逗号分隔）。为防止 SSRF 攻击，强烈建议配置。<code>default</code> 表示仅允许本机测试；<code>*</code> 允许所有域名（不推荐）。</td>
                                    <td><code>trust.host</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_OFFICE_PREVIEW_TYPE</code></td>
                                    <td><code>pdf</code></td>
                                    <td>Office 文档（Word、PPT）预览类型。可选值：<code>pdf</code>（转换为 PDF）、<code>image</code>（转换为图片）。预览页面上可切换。</td>
                                    <td><code>office.preview.type</code></td>
                                </tr>
                                <tr>
                                    <td><code>WATERMARK_TXT</code></td>
                                    <td>（空）</td>
                                    <td>水印文本内容。如需取消水印，设置为空。示例：<code>凯京科技内部文件，严禁外泄</code>。</td>
                                    <td><code>watermark.txt</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_FILE_DIR</code></td>
                                    <td><code>default</code></td>
                                    <td>预览生成资源的存储路径。默认值为程序根目录下的 <code>file</code> 目录。请确保磁盘有足够容量。</td>
                                    <td><code>file.dir</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_CACHE_TYPE</code></td>
                                    <td><code>default</code></td>
                                    <td>缓存实现方式。可选值：<code>default</code>（内嵌 RocksDB，使用磁盘存储）、<code>jdk</code>（纯内存对象存储）、<code>redis</code>（外部 Redis 服务）。</td>
                                    <td><code>cache.type</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_SERVER_PORT</code></td>
                                    <td>（注释）</td>
                                    <td>服务器端口号，默认 8012。在 Compose 中已被注释，可通过环境变量设置。</td>
                                    <td><code>server.port</code></td>
                                </tr>
                                <tr>
                                    <td><code>KK_CONTEXT_PATH</code></td>
                                    <td>（注释）</td>
                                    <td>应用上下文路径，默认 <code>/preview</code>。在 Compose 中已被注释，可通过环境变量设置。</td>
                                    <td><code>server.servlet.context-path</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="docker-usage">
                        <h3>使用说明</h3>
                        <ol>
                            <li>根据需要修改环境变量值（可直接在 <code>docker-compose.yaml</code> 中修改，或通过外部 <code>.env</code> 文件定义）。</li>
                            <li>启动服务：<code>docker-compose up -d</code></li>
                            <li>查看日志：<code>docker-compose logs -f platform-kkfileview</code></li>
                            <li>服务访问地址：<code>http://宿主机IP:8012/preview</code></li>
                        </ol>
                        <div class="note">
                            <strong>注意：</strong>
                            本文档中列出的配置项，如果支持环境变量覆盖，都可以在 Docker Compose 的 <code>environment</code> 部分进行配置。
                            如果要修改的配置项没有对应的环境变量，则需要通过挂载完整的配置文件到容器中来覆盖默认配置：
                            <pre><code>volumes:
  - ./application.properties:/opt/kkFileView-5.0.0/config/application.properties</code></pre>
                            其中 <code>/opt/kkFileView-5.0.0/config/application.properties</code> 为容器内部的默认配置文件路径。
                        </div>
                        
                        <div class="note">
                            <strong>配置文件下载：</strong>
                            <p>您可以通过以下方式获取 <code>application.properties</code> 配置文件：</p>
                            <ol>
                                <li><strong>当前版本配置：</strong>：直接从项目源代码获取
                                    <a href="config/download" download="application.properties">application.properties</a>
                                </li>

                                <li><strong>从运行中的容器复制</strong>：
                                    <pre><code># 复制容器内的配置文件到宿主机
docker cp platform-kkfileview:/opt/kkFileView-5.0.0/config/application.properties ./application.properties

# 查看容器内配置文件内容
docker exec platform-kkfileview cat /opt/kkFileView-5.0.0/config/application.properties</code></pre>
                                </li>
                            </ol>
                        </div>
                    </div>
                </section>

                <section id="part2" class="config-section">
                    <h2>第二部分：application.properties 全量配置项说明</h2>
                    <p><code>application.properties</code> 是 kkFileView 的核心配置文件，支持通过环境变量动态覆盖。以下按配置章节列出所有可配置项及其含义、默认值和对应的环境变量。</p>
                    
                    <div id="server-base">
                        <h3>一、服务器基础配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>server.port</code></td>
                                    <td>服务器端口号</td>
                                    <td>8012</td>
                                    <td><code>KK_SERVER_PORT</code></td>
                                </tr>
                                <tr>
                                    <td><code>server.servlet.context-path</code></td>
                                    <td>应用上下文路径</td>
                                    <td><code>/preview</code></td>
                                    <td><code>KK_CONTEXT_PATH</code></td>
                                </tr>
                                <tr>
                                    <td><code>server.servlet.encoding.charset</code></td>
                                    <td>字符编码</td>
                                    <td><code>utf-8</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>server.compression.enabled</code></td>
                                    <td>启用响应压缩</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>server.compression.min-response-size</code></td>
                                    <td>最小压缩响应大小（字节）</td>
                                    <td>2048</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>server.compression.mime-types</code></td>
                                    <td>压缩的 MIME 类型列表</td>
                                    <td>（多种类型）</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.servlet.multipart.max-file-size</code></td>
                                    <td>单个文件上传大小限制</td>
                                    <td>500MB</td>
                                    <td><code>MAX_FILE_SIZE</code></td>
                                </tr>
                                <tr>
                                    <td><code>spring.servlet.multipart.max-request-size</code></td>
                                    <td>整个请求上传大小限制</td>
                                    <td>500MB</td>
                                    <td><code>MAX_FILE_SIZE</code></td>
                                </tr>
                                <tr>
                                    <td><code>spring.profiles.active</code></td>
                                    <td>激活的 Spring 配置文件</td>
                                    <td><code>default</code></td>
                                    <td><code>KK_SERVER_PROFILE</code></td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.template-loader-path</code></td>
                                    <td>FreeMarker 模板加载路径</td>
                                    <td><code>classpath:/web/</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.cache</code></td>
                                    <td>是否缓存模板</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.charset</code></td>
                                    <td>模板字符集</td>
                                    <td><code>UTF-8</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.check-template-location</code></td>
                                    <td>检查模板位置</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.content-type</code></td>
                                    <td>响应内容类型</td>
                                    <td><code>text/html</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.expose-request-attributes</code></td>
                                    <td>暴露请求属性</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.expose-session-attributes</code></td>
                                    <td>暴露会话属性</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.request-context-attribute</code></td>
                                    <td>请求上下文属性名</td>
                                    <td><code>request</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>spring.freemarker.suffix</code></td>
                                    <td>模板后缀</td>
                                    <td><code>.ftl</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>management.endpoints.web.exposure.include</code></td>
                                    <td>Actuator 暴露的端点</td>
                                    <td><code>health</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>management.endpoint.health.show-details</code></td>
                                    <td>健康端点显示详情</td>
                                    <td><code>always</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>management.health.defaults.enabled</code></td>
                                    <td>启用默认健康指示器</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="office-config">
                        <h3>二、Office 文档处理配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>office.home</code></td>
                                    <td>Office 组件安装路径（自动查找）</td>
                                    <td><code>default</code></td>
                                    <td><code>KK_OFFICE_HOME</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.plugin.server.ports</code></td>
                                    <td>Office 组件服务端口（负载均衡）</td>
                                    <td><code>2001,2002</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>office.plugin.task.timeout</code></td>
                                    <td>Office 组件任务超时时间</td>
                                    <td><code>5m</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>office.plugin.task.maxtasksperprocess</code></td>
                                    <td>每个进程最大任务数</td>
                                    <td>200</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>office.plugin.task.taskexecutiontimeout</code></td>
                                    <td>任务执行超时时间</td>
                                    <td><code>5m</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>office.pagerange</code></td>
                                    <td>Office 文档分页范围（启用后可指定页面范围）</td>
                                    <td><code>false</code></td>
                                    <td><code>KK_OFFICE_PAGERANGE</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.watermark</code></td>
                                    <td>Office 文档水印功能</td>
                                    <td><code>false</code></td>
                                    <td><code>KK_OFFICE_WATERMARK</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.quality</code></td>
                                    <td>Office 图片质量（1‑100）</td>
                                    <td>80</td>
                                    <td><code>KK_OFFICE_QUALITY</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.maximageresolution</code></td>
                                    <td>Office 图片最大分辨率</td>
                                    <td>150</td>
                                    <td><code>KK_OFFICE_MAXIMAGERESOLUTION</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.exportbookmarks</code></td>
                                    <td>导出 Office 书签（转换 PDF 时保留）</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_OFFICE_EXPORTBOOKMARKS</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.exportnotes</code></td>
                                    <td>将 Office 批注作为 PDF 注释导出</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_OFFICE_EXPORTNOTES</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.documentopenpasswords</code></td>
                                    <td>加密文档生成的 PDF 添加密码</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_OFFICE_DOCUMENTOPENPASSWORD</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.type.web</code></td>
                                    <td>Excel（xlsx）前端解析方式（<code>web</code> 或 <code>image</code>）</td>
                                    <td><code>web</code></td>
                                    <td><code>KK_OFFICE_TYPE_WEB</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.preview.type</code></td>
                                    <td>Office 文档预览类型（<code>image</code> / <code>pdf</code>）</td>
                                    <td><code>pdf</code></td>
                                    <td><code>KK_OFFICE_PREVIEW_TYPE</code></td>
                                </tr>
                                <tr>
                                    <td><code>office.preview.switch.disabled</code></td>
                                    <td>是否关闭 Office 预览模式切换开关</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_OFFICE_PREVIEW_SWITCH_DISABLED</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="cad-config">
                        <h3>三、CAD 文件处理配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>cad.preview.type</code></td>
                                    <td>CAD 文件预览类型（<code>svg</code> / <code>pdf</code>）</td>
                                    <td><code>svg</code></td>
                                    <td><code>KK_CAD_PREVIEW_TYPE</code></td>
                                </tr>
                                <tr>
                                    <td><code>cad.conversionmodule</code></td>
                                    <td>CAD 转换模块（1=aspose-cad，2=cadviewer）</td>
                                    <td>1</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>cad.cadconverterpath</code></td>
                                    <td>CAD 后端转换包路径</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>cad.thread</code></td>
                                    <td>CAD 文件处理线程数</td>
                                    <td>5</td>
                                    <td><code>KK_CAD_THREAD</code></td>
                                </tr>
                                <tr>
                                    <td><code>cad.timeout</code></td>
                                    <td>CAD 文件处理超时时间（秒）</td>
                                    <td>90</td>
                                    <td><code>KK_CAD_TIMEOUT</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="pdf-config">
                        <h3>四、PDF 文件处理配置</h3>
                        <p>由于 PDF 配置项较多，此处仅列出关键配置。完整列表请参考原始文档。</p>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>pdf.presentationMode.disable</code></td>
                                    <td>是否禁止 PDF 演示模式</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_PDF_PRESENTATION_MODE_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.openFile.disable</code></td>
                                    <td>是否禁止 PDF 文件菜单中的“打开文件”选项</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_PDF_OPEN_FILE_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.print.disable</code></td>
                                    <td>是否禁止 PDF 打印功能</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_PDF_PRINT_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.download.disable</code></td>
                                    <td>是否禁止 PDF 下载功能</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_PDF_DOWNLOAD_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.bookmark.disable</code></td>
                                    <td>是否禁止 PDF 书签/大纲功能</td>
                                    <td><code>false</code></td>
                                    <td><code>KK_PDF_BOOKMARK_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.disable.editing</code></td>
                                    <td>是否禁止 PDF 编辑功能（注释、表单等）</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_PDF_DISABLE_EDITING</code></td>
                                </tr>
                                <tr>
                                    <td><code>pdf.max.threads</code></td>
                                    <td>PDF 处理最大线程数</td>
                                    <td>10</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>pdf.dpi.enabled</code></td>
                                    <td>启用 PDF DPI 智能调整</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>pdf2jpg.dpi</code></td>
                                    <td>PDF 转图片的基准 DPI（当 DPI 优化禁用时使用）</td>
                                    <td>144</td>
                                    <td><code>KK_PDF2JPG_DPI</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="tif-config">
                        <h3>五、TIF 文件处理配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>tif.preview.type</code></td>
                                    <td>TIF 文件预览类型（<code>tif</code> / <code>jpg</code> / <code>pdf</code>）</td>
                                    <td><code>tif</code></td>
                                    <td><code>KK_TIF_PREVIEW_TYPE</code></td>
                                </tr>
                                <tr>
                                    <td><code>tif.thread</code></td>
                                    <td>TIF 文件处理线程数</td>
                                    <td>5</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>tif.timeout</code></td>
                                    <td>TIF 文件处理超时时间（秒）</td>
                                    <td>90</td>
                                    <td>无</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="media-config">
                        <h3>六、媒体文件处理配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>media</code></td>
                                    <td>媒体文件类型（音频、视频）列表</td>
                                    <td><code>mp3,wav,mp4,flv,mpd,m3u8,ts,mpeg,m4a</code></td>
                                    <td><code>KK_MEDIA</code></td>
                                </tr>
                                <tr>
                                    <td><code>convertMedias</code></td>
                                    <td>需要转换的媒体文件类型列表</td>
                                    <td><code>avi,mov,wmv,mkv,3gp,rm,mpeg</code></td>
                                    <td><code>KK_CONVERTMEDIAS</code></td>
                                </tr>
                                <tr>
                                    <td><code>media.timeout.enabled</code></td>
                                    <td>启用媒体文件超时控制</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>media.convert.disable</code></td>
                                    <td>是否禁用视频格式转换功能</td>
                                    <td><code>false</code></td>
                                    <td><code>KK_MEDIA_CONVERT_DISABLE</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="cache-config">
                        <h3>七、文件存储与缓存配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>file.dir</code></td>
                                    <td>预览生成资源的存储路径</td>
                                    <td><code>default</code>（程序根目录下的 <code>file</code> 目录）</td>
                                    <td><code>KK_FILE_DIR</code></td>
                                </tr>
                                <tr>
                                    <td><code>cache.enabled</code></td>
                                    <td>是否启用缓存</td>
                                    <td><code>true</code></td>
                                    <td><code>KK_CACHE_ENABLED</code></td>
                                </tr>
                                <tr>
                                    <td><code>cache.type</code></td>
                                    <td>缓存实现类型（<code>jdk</code> / <code>redis</code> / <code>default</code>）</td>
                                    <td><code>default</code>（内嵌 RocksDB）</td>
                                    <td><code>KK_CACHE_TYPE</code></td>
                                </tr>
                                <tr>
                                    <td><code>spring.redisson.address</code></td>
                                    <td>Redis 连接地址</td>
                                    <td><code>redis://127.0.0.1:6379</code></td>
                                    <td><code>KK_SPRING_REDISSON_ADDRESS</code></td>
                                </tr>
                                <tr>
                                    <td><code>cache.clean.cron</code></td>
                                    <td>缓存自动清理时间（Quartz cron 表达式）</td>
                                    <td><code>0 0 3 * * ?</code>（每天凌晨 3 点）</td>
                                    <td><code>KK_CACHE_CLEAN_CRON</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="security-config">
                        <h3>八、安全与访问控制配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>base.url</code></td>
                                    <td>提供预览服务的地址（默认从请求 URL 读取）</td>
                                    <td><code>default</code></td>
                                    <td><code>KK_BASE_URL</code></td>
                                </tr>
                                <tr>
                                    <td><code>trust.host</code></td>
                                    <td>信任站点白名单（多个用逗号分隔）</td>
                                    <td><code>default</code>（仅本机测试）</td>
                                    <td><code>KK_TRUST_HOST</code></td>
                                </tr>
                                <tr>
                                    <td><code>not.trust.host</code></td>
                                    <td>不信任站点黑名单（多个用逗号分隔）</td>
                                    <td><code>default</code></td>
                                    <td><code>KK_NOT_TRUST_HOST</code></td>
                                </tr>
                                <tr>
                                    <td><code>prohibit</code></td>
                                    <td>禁止访问的文件类型（格式：<code>exe,dll,dat</code>）</td>
                                    <td><code>exe,dll,dat</code></td>
                                    <td><code>KK_PROHIBIT</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="watermark-config">
                        <h3>九、水印配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>watermark.txt</code></td>
                                    <td>水印文本内容</td>
                                    <td>（空）</td>
                                    <td><code>WATERMARK_TXT</code></td>
                                </tr>
                                <tr>
                                    <td><code>watermark.x.space</code></td>
                                    <td>水印 X 轴间距</td>
                                    <td>10</td>
                                    <td><code>WATERMARK_X_SPACE</code></td>
                                </tr>
                                <tr>
                                    <td><code>watermark.y.space</code></td>
                                    <td>水印 Y 轴间距</td>
                                    <td>10</td>
                                    <td><code>WATERMARK_Y_SPACE</code></td>
                                </tr>
                                <tr>
                                    <td><code>watermark.font</code></td>
                                    <td>水印字体</td>
                                    <td><code>微软雅黑</code></td>
                                    <td><code>WATERMARK_FONT</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="ftp-config">
                        <h3>十、FTP 文件访问配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>ftp.username</code></td>
                                    <td>FTP 连接信息（格式：<code>地址:端口:用户名:密码:编码</code>，多个客户端用逗号分隔）</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="home-config">
                        <h3>十一、首页与文件管理配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>file.upload.disable</code></td>
                                    <td>是否禁用首页文件上传功能</td>
                                    <td><code>true</code></td>
                                    <td><code>DEFAULT_FILE_UPLOAD_DISABLE</code></td>
                                </tr>
                                <tr>
                                    <td><code>delete.captcha</code></td>
                                    <td>是否启用验证码验证删除文件</td>
                                    <td><code>false</code></td>
                                    <td><code>KK_DELETE_CAPTCHA</code></td>
                                </tr>
                                <tr>
                                    <td><code>delete.password</code></td>
                                    <td>删除文件密码</td>
                                    <td><code>123456</code></td>
                                    <td><code>KK_DELETE_PASSWORD</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="auth-config">
                        <h3>十二、权限与认证配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>kk.Picturespreview</code></td>
                                    <td>是否启用图片预览权限</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>kk.Getcorsfile</code></td>
                                    <td>是否启用跨域文件获取权限</td>
                                    <td><code>true</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>kk.key</code></td>
                                    <td>API 密钥功能（启用后需提供密钥调用 API）</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="advanced-config">
                        <h3>十三、高级功能与兼容性配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>kk.refreshschedule</code></td>
                                    <td>异步配置刷新定时时间（秒）</td>
                                    <td>2</td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>kk.isshowaeskey</code></td>
                                    <td>首页是否显示 AES 密钥</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>kk.xlsxallowedit</code></td>
                                    <td>是否允许 XLSX 编辑</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                                <tr>
                                    <td><code>kk.xlsxshowtoolbar</code></td>
                                    <td>是否显示 XLSX 工具栏</td>
                                    <td><code>false</code></td>
                                    <td>无</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div id="filetype-config">
                        <h3>十四、文件类型分类配置</h3>
                        <table class="config-table">
                            <thead>
                                <tr>
                                    <th>配置项</th>
                                    <th>描述</th>
                                    <th>默认值</th>
                                    <th>环境变量</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><code>simText</code></td>
                                    <td>纯文本文件类型（直接显示）列表</td>
                                    <td><code>txt,html,htm,asp,jsp,xml,json,properties,md,gitignore,log,java,py,c,cpp,sql,sh,bat,m,bas,prg,cmd</code></td>
                                    <td><code>KK_SIMTEXT</code></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class="note">
                        <strong>注意：</strong>
                        本文档基于 kkFileView 4.4.0 版本编写。配置项支持通过环境变量动态覆盖，环境变量的格式为 <code>&#36;&#123;环境变量名&#58;默认值&#125;</code>。通过 Docker Compose 部署时，可在 <code>environment</code> 部分设置上述环境变量，以动态调整配置。
                    </div>
                </section>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // 高亮代码块
        hljs.highlightAll();
        
        // 平滑滚动到锚点
        $('a[href^="#"]').on('click', function(e) {
            e.preventDefault();
            var target = $(this.getAttribute('href'));
            if (target.length) {
                $('html, body').stop().animate({
                    scrollTop: target.offset().top - 80
                }, 500);
            }
        });
    });
</script>
</body>
</html>