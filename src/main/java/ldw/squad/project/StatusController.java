package ldw.squad.project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/test")
    public String status() {
        return "FUNFOU!";
    }
}