package in.ac.amu.zhcet.controller.department;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.service.core.CourseManagementService;
import in.ac.amu.zhcet.service.core.FacultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
public class CoursesController {

    private final FacultyService facultyService;
    private final CourseManagementService courseManagementService;

    @Autowired
    public CoursesController(FacultyService facultyService, CourseManagementService courseManagementService) {
        this.facultyService = facultyService;
        this.courseManagementService = courseManagementService;
    }

    @GetMapping("/department/courses")
    public String getCourses(Model model, @RequestParam(value = "active", required = false) Boolean active) {
        if (active == null)
            return "redirect:/department/courses?active=true";

        model.addAttribute("page_description", "View and manage courses for the Department");
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        Department department = FacultyService.getDepartment(facultyMember);
        model.addAttribute("department", department);
        model.addAttribute("page_title", "Courses : " + department.getName() + " Department");
        model.addAttribute("page_subtitle", "Course Management");

        List<Course> courses = courseManagementService.getAllActiveCourse(department, active);
        courses.sort(Comparator.comparing(Course::getCode));

        model.addAttribute("courses", courses);

        return "department/courses";
    }

}
