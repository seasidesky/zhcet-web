package in.ac.amu.zhcet.controller;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FloatedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FacultyController {
    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;

    @Autowired
    public FacultyController(FacultyService facultyService, FloatedCourseService floatedCourseService) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model){
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<FloatedCourse> floatedCourses = floatedCourseService.getByFaculty(facultyMember);
        model.addAttribute("floatedCourses", floatedCourses);
        return "floatedCourse";
    }
}
