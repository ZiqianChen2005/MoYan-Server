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
GO
CREATE INDEX idx_ratings_post ON ratings(post_id);