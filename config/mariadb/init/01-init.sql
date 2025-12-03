-- 初始化数据库脚本
CREATE DATABASE IF NOT EXISTS test;
USE test;

-- 创建用户（如果不存在）
CREATE USER IF NOT EXISTS 'testuser'@'%' IDENTIFIED BY 'testpass';
GRANT ALL PRIVILEGES ON test.* TO 'testuser'@'%';
FLUSH PRIVILEGES;

-- 创建示例表（如果需要）
CREATE TABLE IF NOT EXISTS example_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入示例数据
INSERT INTO example_table (name) VALUES ('Example Data 1'), ('Example Data 2');