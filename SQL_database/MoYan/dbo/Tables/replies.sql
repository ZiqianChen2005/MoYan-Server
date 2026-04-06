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
GO
CREATE INDEX idx_replies_post ON replies(post_id);
GO
CREATE INDEX idx_replies_user ON replies(user_id);