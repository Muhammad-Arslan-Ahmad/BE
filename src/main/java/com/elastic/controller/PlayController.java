package com.elastic.controller;


import com.elastic.payload.*;
import com.elastic.service.PlayService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/play")
public class PlayController {

    private final PlayService service;

    public PlayController(PlayService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdatePlayResponse createUpdatePlay(@RequestBody CreateUpdatePlayRequest request) {
        return this.service.createUpdatePlay(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetPlayResponse getPlay(@PathVariable("id") String id) {
        return this.service.getPlay(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallPlayResponse getSmallPlay(@RequestBody GetSmallPlayRequest request) {
        return this.service.getSmallPlay(request);
    }

    @PostMapping("/search")
    public @ResponseBody SearchPlayResponse searchPlay(@RequestBody SearchPlayRequest request) {
        return this.service.searchPlay(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody SearchPlayResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
