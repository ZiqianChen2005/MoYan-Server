CREATE TABLE anonymous_mapping (
    mapping_id INT PRIMARY KEY IDENTITY(1,1),
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    anonymous_num INT NOT NULL,
    UNIQUE (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);