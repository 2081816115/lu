package com.luqingyang.boot_demo.controller;

import com.luqingyang.boot_demo.common.Result;
import com.luqingyang.boot_demo.dto.StudentsDto;
import com.luqingyang.boot_demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 1. 新增 POST /student/add
    @PostMapping("/add")
    public Result<StudentsDto> add(@RequestBody StudentsDto dto) {
        StudentsDto data = studentService.saveStudentDto(dto);
        return Result.success("新增成功", data);
    }

    // 2. 根据ID查单个 GET /student/1
    @GetMapping("/{id}")
    public Result<StudentsDto> getOne(@PathVariable Integer id) {
        StudentsDto dto = studentService.getStudentDtoById(id);
        if(dto == null) return Result.fail("学生不存在");
        return Result.success(dto);
    }

    // 3. 查询全部 GET /student
    @GetMapping
    public Result<List<StudentsDto>> list() {
        List<StudentsDto> list = studentService.listStudentsDto();
        return Result.success(list);
    }

    // 4. 根据邮箱查询 GET /student/email/xxx@qq.com
    @GetMapping("/email/{email}")
    public Result<List<StudentsDto>> listByEmail(@PathVariable String email) {
        List<StudentsDto> list = studentService.listByEmail(email);
        return Result.success(list);
    }

    // 5. 修改 PUT /student/{id}
    @PutMapping("/{id}")
    public Result<StudentsDto> update(@PathVariable Integer id, @RequestBody StudentsDto dto) {
        // 改用DTO版本更新方法 updateStudentDto
        StudentsDto data = studentService.updateStudentDto(id, dto);
        if(data == null) {
            return Result.fail("不存在该学生，修改失败");
        }
        return Result.success("修改成功", data);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        // 只传消息，不传递data，泛型自动匹配Void
        return Result.success("删除成功", null);
    }
}