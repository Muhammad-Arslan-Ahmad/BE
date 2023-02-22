package com.elastic.controller;


import com.elastic.payload.*;
import com.elastic.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody CreateUpdateMessageResponse createUpdateMessage(@RequestBody CreateUpdateMessageRequest request) {
        return this.service.createUpdateMessage(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody GetMessageResponse getMessage(@PathVariable("id") String id) {
        return this.service.getMessage(id);
    }

    @PostMapping("/small")
    public @ResponseBody GetSmallMessageResponse getSmallMessage(@RequestBody GetSmallMessageRequest request) {
        return this.service.getSmallMessage(request);
    }

    @PostMapping("/search")
    public @ResponseBody SearchMessageResponse searchMessage(@RequestBody SearchMessageRequest request) {
        return this.service.searchMessage(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody SearchMessageResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
