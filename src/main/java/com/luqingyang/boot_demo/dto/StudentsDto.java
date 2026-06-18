package com.luqingyang.boot_demo.dto;

// 只保留前端需要的字段，不包含 age
public class StudentsDto {
    private Integer studentId;
    private String studentName;
    private String className;
    private String gender;
    private String email;

    // 无参构造
    public StudentsDto() {
    }

    // 全参构造（方便从 Student 实体转换）
    public StudentsDto(Integer studentId, String studentName, String className, String gender, String email) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.gender = gender;
        this.email = email;
    }

    // Getter 和 Setter
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}