package amu.zhcet.data.course;

import amu.zhcet.data.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {

    Optional<Course> findByCode(String code);

    List<Course> findByDepartment(Department department);

    List<Course> findByDepartmentAndActive(Department department, Boolean active);

}
