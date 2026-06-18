package com.luqingyang.boot_demo.service;

import com.luqingyang.boot_demo.dao.Student;
import com.luqingyang.boot_demo.dto.StudentsDto;

import java.util.List;

public interface StudentService {

    // 旧接口（实体版，保留）
    Student saveStudent(Student student);
    List<Student> listStudents();
    Student getStudentById(Integer id);
    void deleteStudent(Integer id);
    Student updateStudent(Integer id, Student studentDetails);

    // 新接口（DTO版，给 Controller 用）
    StudentsDto saveStudentDto(StudentsDto dto);
    List<StudentsDto> listStudentsDto();
    StudentsDto getStudentDtoById(Integer id);
    // 可选：更新也加一个 DTO 版
    StudentsDto updateStudentDto(Integer id, StudentsDto dto);

    // 新增：根据邮箱查询学生（解决Controller报红核心）
    List<StudentsDto> listByEmail(String email);
}