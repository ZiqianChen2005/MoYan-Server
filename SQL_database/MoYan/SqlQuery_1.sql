CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    phone VARCHAR(11) NOT NULL UNIQUE,
    nickname NVARCHAR(20) NOT NULL UNIQUE,
    avatar_url NVARCHAR(500),
    password_hash VARCHAR(128),  -- 预留，暂不用
    is_vip BIT DEFAULT 0,
    vip_expire_date DATETIME,
    warning_count INT DEFAULT 0,
    is_banned BIT DEFAULT 0,
    register_time DATETIME DEFAULT GETDATE(),
    last_login_time DATETIME
);


CREATE TABLE admins (
    admin_id INT PRIMARY KEY IDENTITY(1,1),
    phone VARCHAR(11) NOT NULL UNIQUE,
    nickname NVARCHAR(20) NOT NULL UNIQUE,
    avatar_url NVARCHAR(500),
    password_hash VARCHAR(128),  -- 预留，暂不用
    is_vip BIT DEFAULT 0,
    vip_expire_date DATETIME,
    warning_count INT DEFAULT 0,
    is_banned BIT DEFAULT 0,
    register_time DATETIME DEFAULT GETDATE(),
    last_login_time DATETIME
);
-- 索引
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_nickname ON users(nickname);



CREATE TABLE posts (
    post_id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    is_anonymous BIT DEFAULT 0,
    title NVARCHAR(100) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    tags NVARCHAR(200),  -- 用逗号分隔，如 "诗歌,现代诗,爱情"
    post_time DATETIME DEFAULT GETDATE(),
    is_newbie BIT DEFAULT 0,  -- 新人帖标记
    status TINYINT DEFAULT 0,  -- 0待审核 1已通过 2已拒绝 3已举报待处理
    view_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_time ON posts(post_time);
CREATE INDEX idx_posts_user ON posts(user_id);


CREATE TABLE replies (
    reply_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    is_anonymous BIT DEFAULT 0,
    anonymous_num INT,  -- 同一帖子下的匿名编号
    content NVARCHAR(1000) NOT NULL,
    reply_time DATETIME DEFAULT GETDATE(),
    status TINYINT DEFAULT 0,  -- 0待审核 1已通过 2已拒绝
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_replies_post ON replies(post_id);
CREATE INDEX idx_replies_user ON replies(user_id);


CREATE TABLE ratings (
    rating_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    tag_accuracy TINYINT CHECK (tag_accuracy BETWEEN 1 AND 5),
    article_score TINYINT CHECK (article_score BETWEEN 1 AND 5),
    comment NVARCHAR(500),
    rating_time DATETIME DEFAULT GETDATE(),
    UNIQUE (post_id, user_id),  -- 同一用户对同一帖子只能评一次
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
CREATE INDEX idx_ratings_post ON ratings(post_id);


CREATE TABLE tips (
    tip_id INT PRIMARY KEY IDENTITY(1,1),
    from_user_id INT NOT NULL,
    to_user_id INT NOT NULL,
    post_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    platform_fee DECIMAL(10,2) NOT NULL,  -- 平台抽成
    author_income DECIMAL(10,2) NOT NULL,  -- 作者实际收入
    tip_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (from_user_id) REFERENCES users(user_id),
    FOREIGN KEY (to_user_id) REFERENCES users(user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
);

CREATE INDEX idx_tips_to_user ON tips(to_user_id);
CREATE INDEX idx_tips_post ON tips(post_id);


CREATE TABLE reports (
    report_id INT PRIMARY KEY IDENTITY(1,1),
    reporter_id INT NOT NULL,
    target_type TINYINT,  -- 1帖子 2回复
    target_id INT NOT NULL,  -- post_id 或 reply_id
    reason NVARCHAR(200),
    report_time DATETIME DEFAULT GETDATE(),
    status TINYINT DEFAULT 0,  -- 0待处理 1已处理(撤下) 2驳回
    handler_id INT,  -- 处理的管理员ID
    handle_time DATETIME,
    handle_note NVARCHAR(200),
    FOREIGN KEY (reporter_id) REFERENCES users(user_id)
);

CREATE INDEX idx_reports_status ON reports(status);


CREATE TABLE daily_tasks (
    task_id INT PRIMARY KEY IDENTITY(1,1),
    task_type TINYINT,  -- 1续写 2改写 3优化
    title NVARCHAR(100),
    content NVARCHAR(MAX) NOT NULL,
    publish_date DATE NOT NULL,
    is_active BIT DEFAULT 1
);

CREATE INDEX idx_tasks_date ON daily_tasks(publish_date);


CREATE TABLE task_answers (
    answer_id INT PRIMARY KEY IDENTITY(1,1),
    task_id INT NOT NULL,
    user_id INT NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    score INT DEFAULT 0,  -- 用户评分总和
    submit_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (task_id) REFERENCES daily_tasks(task_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_answers_task ON task_answers(task_id);
CREATE INDEX idx_answers_score ON task_answers(score);


CREATE TABLE anonymous_mapping (
    mapping_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    anonymous_num INT NOT NULL,
    UNIQUE (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);