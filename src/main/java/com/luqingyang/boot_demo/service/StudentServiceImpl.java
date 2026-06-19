package com.luqingyang.boot_demo.service;

import com.luqingyang.boot_demo.common.Result;
import com.luqingyang.boot_demo.dao.Student;
import com.luqingyang.boot_demo.dao.StudentRepository;
import com.luqingyang.boot_demo.dto.StudentsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // 旧方法：保存 Student 实体
    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // 新方法：保存 StudentsDto（Controller 新增用）
    @Override
    public StudentsDto saveStudentDto(StudentsDto dto) {
        // 1. DTO 转 Student 实体
        Student student = new Student();
        student.setStudentName(dto.getStudentName());
        student.setClassName(dto.getClassName());
        // 字符串转Gender枚举
        student.setGender(Student.Gender.valueOf(dto.getGender()));
        student.setEmail(dto.getEmail());

        // 2. 保存到数据库
        Student savedStudent = studentRepository.save(student);

        // 3. 实体转回 DTO 返回（无age构造器）
        return new StudentsDto(
                savedStudent.getStudentId(),
                savedStudent.getStudentName(),
                savedStudent.getClassName(),
                savedStudent.getGender().name(),
                savedStudent.getEmail()
        );
    }

    @Override
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("学生不存在，删除失败");
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Student updateStudent(Integer id, Student studentDetails) {
        Student existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            return null;
        }
        existingStudent.setStudentName(studentDetails.getStudentName());
        existingStudent.setClassName(studentDetails.getClassName());
        existingStudent.setGender(studentDetails.getGender());
        existingStudent.setAge(studentDetails.getAge());
        existingStudent.setEmail(studentDetails.getEmail());
        return studentRepository.save(existingStudent);
    }

    // 查询全部DTO列表
    @Override
    public List<StudentsDto> listStudentsDto() {
        return studentRepository.findAll().stream()
                .map(student -> new StudentsDto(
                        student.getStudentId(),
                        student.getStudentName(),
                        student.getClassName(),
                        student.getGender().name(),
                        student.getEmail()
                ))
                .collect(Collectors.toList());
    }

    // 根据ID查单个DTO
    @Override
    public StudentsDto getStudentDtoById(Integer id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        return new StudentsDto(
                student.getStudentId(),
                student.getStudentName(),
                student.getClassName(),
                student.getGender().name(),
                student.getEmail()
        );
    }

    /**
     * DTO版本更新 - 带乐观锁处理
     * 
     * 工作原理：
     * 1. 从数据库查询现有学生信息（包含version字段）
     * 2. 更新学生信息
     * 3. 保存时，JPA会检查version是否匹配
     * 4. 如果version不匹配（说明数据已被其他用户修改），抛出OptimisticLockingFailureException
     * 5. 异常由GlobalExceptionHandler捕获并返回409状态码
     * 
     * @param id 学生ID
     * @param dto 更新的学生信息
     * @return 更新后的学生信息
     * @throws OptimisticLockingFailureException 当发生版本冲突时
     */
    @Override
    @Transactional  // 确保整个操作在一个事务中进行
    public StudentsDto updateStudentDto(Integer id, StudentsDto dto) {
        Student existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            throw new RuntimeException("学生不存在，更新失败");
        }
        existingStudent.setStudentName(dto.getStudentName());
        existingStudent.setClassName(dto.getClassName());
        existingStudent.setGender(Student.Gender.valueOf(dto.getGender()));
        existingStudent.setEmail(dto.getEmail());

        // ========== 关键：save时会自动触发version检查 ==========
        // 如果version不匹配，会抛出OptimisticLockingFailureException
        Student updatedStudent = studentRepository.save(existingStudent);
        
        return new StudentsDto(
                updatedStudent.getStudentId(),
                updatedStudent.getStudentName(),
                updatedStudent.getClassName(),
                updatedStudent.getGender().name(),
                updatedStudent.getEmail()
        );
    }

    // 新增：根据邮箱查询
    @Override
    public List<StudentsDto> listByEmail(String email) {
        List<Student> studentList = studentRepository.findByEmail(email);
        return studentList.stream()
                .map(s -> new StudentsDto(
                        s.getStudentId(),
                        s.getStudentName(),
                        s.getClassName(),
                        s.getGender().name(),
                        s.getEmail()
                ))
                .collect(Collectors.toList());
    }
}
