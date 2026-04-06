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
GO
CREATE INDEX idx_tips_to_user ON tips(to_user_id);
GO
CREATE INDEX idx_tips_post ON tips(post_id);