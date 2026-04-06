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
GO
CREATE INDEX idx_answers_task ON task_answers(task_id);
GO
CREATE INDEX idx_answers_score ON task_answers(score);