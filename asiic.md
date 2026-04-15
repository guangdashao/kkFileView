
主要修改内容：
1. 添加userinfofitler ，以及水印加上用户
2. filecontroller 和indexController 添加profile，只在dev profile启动。
3. 删除 web/main/下所有的
   <#--    <link rel="preconnect" href="https://fonts.googleapis.com">-->
   <#--    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>-->
   <#--    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600;700&family=JetBrains+Mono:wght@400;600&family=Space+Grotesk:wght@500;700&display=swap" rel="stylesheet">-->
4. 






完成文件预览测试：
1. pdf    修改pdfjs 中index 中使用相对路径添加js
    <script type="text/javascript" src="../../js/jquery-3.6.1.min.js"></script>
    <script type="text/javascript" src="../../js/pdfwatermark.js"></script>
2. jpg
3. heic   修改了heic.ftl 引入heic js ，
   <script src="heic/src/index.js" type="text/javascript"></script>
   以及 heic/src/index.js 和worker.js中引入js
   heic/src/index.js:    const workerUrl = 'heic/src/worker.js';
   heic/src/ worker.js:     importScripts('wasm_heif.js');   删除相对路径。
4. docs
5. excel    删除 打印和 以html打开
   <#--<div id="button-area" style="display: none;">-->
   <#--    <label><button onclick="tiaozhuan()">跳转HTML预览</button></label>-->
   <#--    <button id="confirm-button" onclick="print()">打印</button>-->
   <#--</div>-->
    以及 删除easyexcel中的打印。
6. wps
7. zip/rar/jar 
8. xml 
9. properties
10. json
11. bpmn
12. python , yaml, md
13. 