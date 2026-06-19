-- 添加版本号列到student表（如果表已存在）
-- 如果是新建表，请确保包含此列
ALTER TABLE student ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;

-- 为version字段创建索引（可选，提高查询效率）
CREATE INDEX idx_student_version ON student(version);
