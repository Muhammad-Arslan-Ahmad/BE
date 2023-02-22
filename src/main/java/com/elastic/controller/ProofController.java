package com.elastic.controller;

import com.elastic.model.Proof;
import com.elastic.payload.CommonDeleteRequest;
import com.elastic.payload.CommonDeleteResponse;
import com.elastic.payload.GlobalSearchRequest;
import com.elastic.payload.ProofSearchRequest;
import com.elastic.payload.ProofSearchResponse;
import com.elastic.payload.SearchMessageResponse;
import com.elastic.service.ProofService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proof")
public class ProofController {

    private final ProofService service;

    public ProofController(ProofService service) {
        this.service = service;
    }

    @PostMapping("/create-update")
    public @ResponseBody Proof createUpdateProof(@RequestBody Proof request) {
        return this.service.createUpdateProof(request);
    }

    @GetMapping("/{id}")
    public @ResponseBody Proof getProof(@PathVariable("id") String id) {
        return this.service.getProof(id);
    }

    @PostMapping("/search")
    public @ResponseBody ProofSearchResponse searchProof(@RequestBody ProofSearchRequest request) {
        return this.service.searchProof(request);
    }

    @PostMapping("/global-search")
    public @ResponseBody ProofSearchResponse globalSearch(@RequestBody GlobalSearchRequest request) {
        return this.service.globalSearch(request);
    }
    
    @PostMapping("/delete")
    public @ResponseBody
    CommonDeleteResponse delete(@RequestBody CommonDeleteRequest request) {
        return this.service.delete(request);
    }
}
