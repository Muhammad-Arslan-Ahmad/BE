package com.elastic.controller;


import com.elastic.payload.*;
import com.elastic.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdateUserResponse createUpdateUser(@RequestBody CreateUpdateUserRequest request) {
        return this.service.createUpdateUser(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetUserResponse getUser(@PathVariable("id") String id) {
        return this.service.getUser(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallUserResponse getSmallUser(@RequestBody GetSmallUserRequest request) {
        return this.service.getSmallUser(request);
    }

    @PostMapping("/search")
    public @ResponseBody SearchUserResponse searchUser(@RequestBody SearchUserRequest request) {
        return this.service.searchUser(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody SearchUserResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
}
