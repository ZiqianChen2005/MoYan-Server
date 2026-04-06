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
GO
CREATE INDEX idx_posts_status ON posts(status);
GO
CREATE INDEX idx_posts_time ON posts(post_time);
GO
CREATE INDEX idx_posts_user ON posts(user_id);