package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.WebStaff;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerStaff.requestGetStaffList;


@CrossOrigin()
@RestController
public class ControllerStaff {

    @GetMapping(value = "staff/staff_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public static WebStaff[] getStaffList() {
        return requestGetStaffList();
    }

}
