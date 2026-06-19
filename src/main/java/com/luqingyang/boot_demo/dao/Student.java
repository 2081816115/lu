package com.luqingyang.boot_demo.dao;

import jakarta.persistence.*;

@Entity
@Table(name="student")
public class Student {
    // 对应表中的 student_id（主键，自增）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    // 对应 student_name
    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    // 对应 class_name
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    // 对应 gender（枚举类型）
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // 对应 age
    @Column(name = "age")
    private Integer age;

    // 对应 email
    @Column(name = "email", length = 100)
    private String email;

    // ========== 乐观锁字段 ==========
    // 版本号，用于并发控制
    @Version
    @Column(name = "version")
    private Long version;

    // 枚举类：和表中的枚举值保持一致
    public enum Gender {
        男, 女, 其他
    }

    // 无参构造（JPA 必须要有）
    public Student() {
    }

    // 全参构造（可选，方便创建对象）
    public Student(String studentName, String className, Gender gender, Integer age, String email) {
        this.studentName = studentName;
        this.className = className;
        this.gender = gender;
        this.age = age;
        this.email = email;
    }

    // Getter 和 Setter（JPA 读写数据需要）
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
