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
GO
CREATE INDEX idx_reports_status ON reports(status);