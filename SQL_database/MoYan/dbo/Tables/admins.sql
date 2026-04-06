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