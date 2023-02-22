package com.elastic.controller;

import com.elastic.common.util.Utils;
import com.elastic.payload.*;
import com.elastic.service.IndustryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/industry")
public class IndustryController {

    private final IndustryService service;

    public IndustryController(IndustryService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdateIndustryResponse createUpdateIndustry(@RequestBody CreateUpdateIndustryRequest request) {
        return this.service.createUpdateIndustry(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetIndustryResponse getIndustry(@PathVariable(name = "id") String id) {
        return this.service.getIndustry(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallIndustryResponse getSmallIndustry(@RequestBody GetSmallIndustryRequest request) {
        return this.service.getSmallIndustry(request);
    }

    @PostMapping("/search")
    public @ResponseBody SearchIndustryResponse searchIndustry(@RequestBody SearchIndustryRequest request) {
        return this.service.searchIndustry(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody SearchIndustryResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
