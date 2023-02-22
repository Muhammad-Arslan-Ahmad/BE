package com.elastic.controller;

import com.elastic.payload.*;
import com.elastic.service.RoleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdateRoleResponse createUpdateRole(@RequestBody CreateUpdateRoleRequest request) {
        return this.service.createUpdateRole(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetRoleResponse getRole(@PathVariable("id") String id) {
        return this.service.getRole(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallRoleResponse getSmallRole(@RequestBody GetSmallRoleRequest request) {
        return this.service.getSmallRole(request);
    }

    @PostMapping("/search")
    public @ResponseBody RoleSearchResponse searchRole(@RequestBody RoleSearchRequest request) {
        return this.service.searchRole(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody RoleSearchResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
