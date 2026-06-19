# 乐观锁实现指南

## 📋 已添加的文件和修改

### 1️⃣ **Student.java** (实体类) - ✅ 已修改
**位置**: `src/main/java/com/luqingyang/boot_demo/dao/Student.java`

**添加内容**（第33-36行）：
```java
@Version
@Column(name = "version")
private Long version;
```

**作用**：
- `@Version` 注解告诉JPA这是乐观锁的版本字段
- JPA会自动在每次更新时检查和递增这个版本号
- 如果版本号不匹配，会抛出 `OptimisticLockingFailureException`

---

### 2️⃣ **Student.java** (Getter/Setter) - ✅ 已添加
**位置**：在 Student.java 底部（第110-117行）

**添加内容**：
```java
public Long getVersion() {
    return version;
}

public void setVersion(Long version) {
    this.version = version;
}
```

---

### 3️⃣ **V1__add_version_to_student.sql** - 📄 新建
**位置**: `db/migration/V1__add_version_to_student.sql`

**作用**：数据库迁移脚本，向 `student` 表添加 `version` 列

**执行步骤**（二选一）：

**方式A：使用Flyway自动执行（推荐）**
1. 在 `pom.xml` 中添加 Flyway 依赖：
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

2. 在 `application.properties` 中配置：
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

3. 启动应用，Flyway 会自动执行

**方式B：手动执行SQL**
```bash
mysql -u root -p your_database < db/migration/V1__add_version_to_student.sql
```

---

### 4️⃣ **Result.java** - 📄 新建
**位置**: `src/main/java/com/luqingyang/boot_demo/common/Result.java`

**作用**：统一API响应格式，包含版本冲突的响应

**特殊方法**：
```java
Result.versionConflict()  // 返回 409 状态码 + 提示信息
```

---

### 5️⃣ **GlobalExceptionHandler.java** - 📄 新建
**位置**: `src/main/java/com/luqingyang/boot_demo/common/GlobalExceptionHandler.java`

**关键方法**：
```java
@ExceptionHandler(OptimisticLockingFailureException.class)
public Result<?> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
    return Result.versionConflict();  // 自动捕获乐观锁异常
}
```

**作用**：
- 自动捕获乐观锁冲突异常
- 返回清晰的409状态码和用户友好的提示信息
- 用户无需手动处理异常

---

### 6️⃣ **StudentServiceImpl.java** - ✅ 已修改
**位置**: `src/main/java/com/luqingyang/boot_demo/service/StudentServiceImpl.java`

**关键修改**（第113-117行）：
```java
@Override
@Transactional  // ← 添加此注解
public StudentsDto updateStudentDto(Integer id, StudentsDto dto) {
    // ... 更新逻辑 ...
    Student updatedStudent = studentRepository.save(existingStudent);  // 自动检查version
}
```

**工作流程**：
1. 查询学生（获取当前version，如v=5）
2. 修改学生信息
3. 调用save()时，JPA自动执行：
   ```sql
   UPDATE student SET ... WHERE student_id = ? AND version = 5;
   ```
4. 如果另一个用户同时修改（version已变为6），更新失败
5. 触发 `OptimisticLockingFailureException`
6. `GlobalExceptionHandler` 捕获并返回409

---

## 🔄 工作流程示意图

```
用户A                          数据库                          用户B
  ↓                             ↓                               ↓
查询学生                    (version=1)
version=1 ←─────────────────────┘
  ↓
显示编辑页面
  ↓
修改信息                                              查询学生
  ↓                                                  (version=1)
  ↓                                               version=1 ←
  ↓                                                  ↓
  ↓                                            修改信息，发送更新
  ↓                                                  ↓
  ↓                                            UPDATE WHERE version=1
  ↓                                                  ↓
  ↓                                            ✅ 更新成功，version→2
发送更新                                            ↓
  ↓
UPDATE WHERE version=1
  ↓
❌ 0行受影响（版本已变为2）
  ↓
OptimisticLockingFailureException
  ↓
返回409：数据已被其他用户修改，请刷新后重试
  ↓
用户A 重新刷新编辑页面获取最新数据
```

---

## 📝 使用示例

### 前端（假设使用Vue/React）：

```javascript
// 获取学生信息
async function fetchStudent(id) {
    const response = await fetch(`/api/student/${id}`);
    return response.json();
}

// 更新学生信息
async function updateStudent(id, studentData) {
    const response = await fetch(`/api/student/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(studentData)
    });
    
    if (response.status === 409) {
        // 版本冲突：提示用户数据已被修改
        alert('数据已被其他用户修改，请刷新后重试');
        location.reload();
    } else if (response.ok) {
        alert('更新成功');
    }
}
```

### 后端API使用：

```bash
# 1. 查询学生
GET http://localhost:8080/api/student/1

# 响应示例：
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "studentId": 1,
        "studentName": "张三",
        "className": "1班",
        "gender": "男",
        "email": "zhangsan@example.com",
        "version": 1
    }
}

# 2. 更新学生（发送时包含version）
PUT http://localhost:8080/api/student/1
{
    "studentId": 1,
    "studentName": "张三（已修改）",
    "className": "1班",
    "gender": "男",
    "email": "zhangsan@example.com",
    "version": 1
}

# 响应成功示例（version自动递增）：
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "studentId": 1,
        "studentName": "张三（已修改）",
        "className": "1班",
        "gender": "男",
        "email": "zhangsan@example.com",
        "version": 2
    }
}

# 响应冲突示例（409）：
{
    "code": 409,
    "message": "数据已被其他用户修改，请刷新后重试",
    "data": null
}
```

---

## ✅ 检查清单

- [x] Student.java 添加 @Version 字段
- [x] Student.java 添加 version Getter/Setter
- [x] 创建 V1__add_version_to_student.sql 迁移脚本
- [x] 创建 Result.java 响应类
- [x] 创建 GlobalExceptionHandler.java 异常处理器
- [x] StudentServiceImpl.java 添加 @Transactional 注解
- [ ] **运行数据库迁移脚本（手动执行）**
- [ ] 测试并验证乐观锁功能

---

## 🧪 测试乐观锁

### 场景：两个用户同时修改同一学生

**步骤**：
1. 打开两个浏览器窗口（模拟两个用户）
2. 两个窗口都查询同一个学生（都获得 version=1）
3. 第一个窗口修改并保存（版本自动变为2）
4. 第二个窗口修改并保存 → **收到409错误**
5. 第二个用户需要刷新重新编辑

---

## 💡 常见问题

**Q: 为什么我的更新总是失败？**
A: 检查是否执行了迁移脚本，确保数据库的 `student` 表有 `version` 列。

**Q: 怎样获取 version 字段返回给前端？**
A: 在 StudentsDto 中添加 version 字段，并在转换时包含它。

**Q: 乐观锁适用于什么场景？**
A: 冲突少的场景（如后台管理系统）。如果冲突频繁，考虑悲观锁或使用Redis分布式锁。

