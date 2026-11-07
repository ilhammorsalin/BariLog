package com.barilog.controller;

import com.barilog.Resident;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    @GetMapping
    public String getResidents() {
        return "Return list of residents";
    }

    @PostMapping
    public String addResident(@RequestBody Resident newResident) {
        return "Recieved add request";
    }

    @GetMapping("/{id}")
    public String getResidentId(@PathVariable Long id) {
        return "Fetching resident id:" + id;
    }
}
