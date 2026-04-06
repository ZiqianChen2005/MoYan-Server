CREATE TABLE daily_tasks (
    task_id INT PRIMARY KEY IDENTITY(1,1),
    task_type TINYINT,  -- 1续写 2改写 3优化
    title NVARCHAR(100),
    content NVARCHAR(MAX) NOT NULL,
    publish_date DATE NOT NULL,
    is_active BIT DEFAULT 1
);
GO
CREATE INDEX idx_tasks_date ON daily_tasks(publish_date);