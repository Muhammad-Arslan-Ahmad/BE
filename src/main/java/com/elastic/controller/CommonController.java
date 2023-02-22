package com.elastic.controller;

import com.elastic.common.util.Utils;
import com.elastic.service.CommonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final CommonService service;

    public CommonController(CommonService service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public @ResponseBody String echo() {
        return Utils.PONG;
    }

    @DeleteMapping("/clear_all_index")
    public @ResponseBody String clearIndex() {
        return service.clearIndex();
    }
}
