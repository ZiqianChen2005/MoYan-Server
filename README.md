# 陌言服务端项目说明

================================================================================
一、项目运行环境
================================================================================

JDK版本：1.8 或 11
数据库：SQL Server 2019+
开发工具：Eclipse IDE for Enterprise Java
端口：8888（Socket通信端口）

================================================================================
二、项目导入步骤
================================================================================

步骤1：打开Eclipse，选择 File -> Import -> Existing Projects into Workspace

步骤2：选择项目文件夹 MoYanServer，点击 Finish

步骤3：右键项目 -> Build Path -> Configure Build Path -> Libraries
       确认以下JAR已添加：
       - mssql-jdbc-12.4.0.jre8.jar
       - gson-2.10.1.jar

步骤4：修改数据库连接配置
       打开 src/com/moyan/util/DBUtil.java
       修改以下三行：
       - USER = "你的数据库用户名"
       - PASSWORD = "你的数据库密码"
       - 确认databaseName（与数据库名称一致）

步骤5：执行建表SQL
       在SQL Server中执行之前提供的8张表的建表语句

步骤6：启动服务器
       右键项目 -> Run As -> Java Application
       选择主类：com.moyan.server.SocketServer
       点击 OK

步骤7：看到控制台输出以下信息表示启动成功
       ========== 陌言服务端启动 ==========
       监听端口: 8888
       ===================================

================================================================================
三、API接口说明（所有请求通过Socket发送JSON）
================================================================================

【重要说明】
- 所有请求格式：{"action":"操作名称","params":{参数对象}}
- 所有响应格式：{"code":0,"msg":"提示信息","data":数据对象}
- code为0表示成功，1表示失败
- 测试阶段登录验证码固定为：123456


【1】用户登录
请求：{"action":"login","params":{"phone":"13800138000","code":"123456"}}
响应：{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三",...}}


【2】用户注册
请求：{"action":"register","params":{"phone":"13800138000","nickname":"张三"}}
响应：{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三"}}


【3】获取用户信息
请求：{"action":"getUserInfo","params":{"userId":1}}
响应：{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三",...}}


【4】修改昵称
请求：{"action":"updateNickname","params":{"userId":1,"nickname":"新昵称"}}
响应：{"code":0,"msg":"success","data":null}


【5】修改头像
请求：{"action":"updateAvatar","params":{"userId":1,"avatarUrl":"http://图片地址"}}
响应：{"code":0,"msg":"success","data":null}


【6】发布帖子
请求：{"action":"createPost","params":{"userId":1,"isAnonymous":false,"title":"标题","content":"内容","tags":"诗歌,现代诗"}}
响应：{"code":0,"msg":"success","data":123}  （data是帖子ID）


【7】获取帖子列表（首页推荐流）
请求：{"action":"getPostList","params":{"page":1,"size":20}}
响应：{"code":0,"msg":"success","data":[{"postId":1,"title":"标题",...}]}


【8】获取帖子详情
请求：{"action":"getPostDetail","params":{"postId":1,"userId":1}}
响应：{"code":0,"msg":"success","data":{"postId":1,"title":"标题","content":"内容","replies":[...]}}


【9】搜索帖子
请求：{"action":"searchPosts","params":{"keyword":"关键词","tag":"标签","sortBy":"time","page":1}}
响应：{"code":0,"msg":"success","data":[帖子列表]}


【10】发布回复
请求：{"action":"createReply","params":{"postId":1,"userId":1,"isAnonymous":false,"content":"回复内容"}}
响应：{"code":0,"msg":"success","data":456}  （data是回复ID）


【11】获取回复列表
请求：{"action":"getReplies","params":{"postId":1,"page":1}}
响应：{"code":0,"msg":"success","data":[{"replyId":1,"content":"回复内容",...}]}


【12】给帖子评分
请求：{"action":"ratePost","params":{"postId":1,"userId":1,"tagAccuracy":4,"articleScore":5,"comment":"评论"}}
说明：tagAccuracy和articleScore都是1-5分
响应：{"code":0,"msg":"success","data":null}


【13】打赏帖子
请求：{"action":"tipPost","params":{"postId":1,"fromUserId":1,"amount":10}}
说明：金额单位元，平台抽成8%
响应：{"code":0,"msg":"success","data":null}


【14】举报内容
请求：{"action":"report","params":{"reporterId":1,"targetType":1,"targetId":1,"reason":"举报原因"}}
说明：targetType=1表示帖子，2表示回复
响应：{"code":0,"msg":"success","data":null}


【15】获取今日互动任务
请求：{"action":"getTodayTask","params":{}}
响应：{"code":0,"msg":"success","data":{"taskId":1,"taskType":1,"title":"续写任务","content":"原文内容"}}


【16】提交任务回答
请求：{"action":"submitTaskAnswer","params":{"taskId":1,"userId":1,"content":"回答内容"}}
响应：{"code":0,"msg":"success","data":789}  （data是回答ID）


【17】获取任务优质回答
请求：{"action":"getTopAnswers","params":{"taskId":1,"limit":3}}
响应：{"code":0,"msg":"success","data":[{"answerId":1,"content":"回答内容","score":45}]}


【18】检查是否已提交今日任务
请求：{"action":"hasSubmitted","params":{"taskId":1,"userId":1}}
响应：{"code":0,"msg":"success","data":true}  （true已提交/false未提交）


================================================================================
四、管理员后台接口（后台管理页面调用）
================================================================================

【19】通过帖子审核
请求：{"action":"approvePost","params":{"postId":1,"adminId":1}}
响应：{"code":0,"msg":"success","data":null}


【20】拒绝帖子审核
请求：{"action":"rejectPost","params":{"postId":1,"adminId":1,"reason":"拒绝原因"}}
响应：{"code":0,"msg":"success","data":null}


【21】通过回复审核
请求：{"action":"approveReply","params":{"replyId":1,"adminId":1}}
响应：{"code":0,"msg":"success","data":null}


【22】拒绝回复审核
请求：{"action":"rejectReply","params":{"replyId":1,"adminId":1,"reason":"拒绝原因"}}
响应：{"code":0,"msg":"success","data":null}


【23】处理举报
请求：{"action":"handleReport","params":{"reportId":1,"handlerId":1,"action":1,"note":"处理备注"}}
说明：action=1撤下并警告，2仅警告，3驳回举报
响应：{"code":0,"msg":"success","data":null}


【24】警告用户
请求：{"action":"addWarning","params":{"userId":1}}
说明：警告3次自动封禁
响应：{"code":0,"msg":"success","data":null}


【25】封禁用户
请求：{"action":"banUser","params":{"userId":1}}
响应：{"code":0,"msg":"success","data":null}


【26】解封用户
请求：{"action":"unbanUser","params":{"userId":1}}
响应：{"code":0,"msg":"success","data":null}


================================================================================
五、项目文件对应关系（哪个文件负责什么功能）
================================================================================

【入口文件】
SocketServer.java          - 服务器启动类，监听8888端口

【请求处理】
RequestHandler.java        - 接收所有请求，根据action分发到不同Service

【业务逻辑层】（Service层，处理具体业务）
UserServiceImpl.java       - 处理登录、注册、用户信息修改
PostServiceImpl.java       - 处理发帖、帖子列表、帖子详情、搜索
ReplyServiceImpl.java      - 处理回复、回复列表
RatingServiceImpl.java     - 处理评分
TipServiceImpl.java        - 处理打赏
ReportServiceImpl.java     - 处理举报
DailyTaskServiceImpl.java  - 处理每日互动任务

【数据访问层】（DAO层，操作数据库）
UserDaoImpl.java           - 操作用户表
PostDaoImpl.java           - 操作帖子表
ReplyDaoImpl.java          - 操作回复表
RatingDaoImpl.java         - 操作评分表
TipDaoImpl.java            - 操作打赏表
ReportDaoImpl.java         - 操作举报表
DailyTaskDaoImpl.java      - 操作任务表
TaskAnswerDaoImpl.java     - 操作任务回答表
AnonymousMappingDaoImpl.java - 管理匿名编号

【实体类】（对应数据库表）
User.java                  - 用户实体
Post.java                  - 帖子实体
Reply.java                 - 回复实体
Rating.java                - 评分实体
Tip.java                   - 打赏实体
Report.java                - 举报实体
DailyTask.java             - 任务实体
TaskAnswer.java            - 任务回答实体
AnonymousMapping.java      - 匿名编号实体

【工具类】
DBUtil.java                - 数据库连接，改这里修改数据库密码
StringUtil.java            - 字符串工具（判空、HTML转义等）

【前端页面位置】（WebContent/admin/）
pages/dashboard.html       - 仪表盘
pages/posts.html           - 帖子审核页面
pages/replies.html         - 回复审核页面
pages/reports.html         - 举报处理页面
pages/users.html           - 用户管理页面
pages/tasks.html           - 任务管理页面
pages/publishers.html      - 出版社管理页面

================================================================================
六、常见问题解决
================================================================================

问题1：启动时提示 ClassNotFoundException
解决：检查 JAR 包是否放在 WebContent/WEB-INF/lib/ 目录下

问题2：数据库连接失败
解决：
    1. 检查 SQL Server 服务是否启动
    2. 检查 DBUtil.java 中的用户名和密码是否正确
    3. 检查数据库名称是否为 MoYan

问题3：端口被占用
解决：
    1. 修改 SocketServer.java 中的 PORT 常量（默认8888）
    2. 或关闭占用8888端口的其他程序

问题4：中文乱码
解决：所有读写流已设置为 UTF-8，客户端也需要使用 UTF-8 编码

问题5：Android客户端连不上服务端
解决：
    1. 确保手机/模拟器与电脑在同一网络
    2. 服务端IP不要写localhost，写电脑的实际IP地址
    3. 检查防火墙是否允许8888端口

================================================================================
七、团队协作提醒
================================================================================

【前端同学注意】
1. 后端使用Socket通信，不是HTTP，不要用OkHttp/Retrofit那一套
2. 所有请求必须是JSON格式，末尾加两个换行符 \n\n
3. 所有响应的code字段，0代表成功，1代表失败
4. 测试阶段登录验证码固定123456

【数据库同学注意】
1. 建表前先创建数据库：CREATE DATABASE MoYan
2. 建表顺序：先建users，再建其他表（因为有外键关联）
3. 如果报外键错误，检查引用表的字段是否存在

【后台管理同学注意】
1. 所有后台页面放在 WebContent/admin/ 目录下
2. 访问地址：http://localhost:8080/MoYanServer/admin/login.html
3. 默认管理员账号：admin / admin123

================================================================================
八、联系方式
================================================================================

如遇问题，按以下顺序排查：
1. 查看 Eclipse 控制台错误信息
2. 检查数据库连接是否正常
3. 检查请求JSON格式是否正确
4. 查看 SocketServer 的控制台输出

================================================================================
