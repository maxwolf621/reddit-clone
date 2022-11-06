package com.pttbackend.pttclone.controller;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.service.SubsService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sub")
@AllArgsConstructor
@Slf4j
public class SubsController {
    private final SubsService subsService;
    
    /** 
     * @return {@code List<SubDTO>}
     */
    //@Operation(summary = "GET ALL SUBS")
    @GetMapping
    public ResponseEntity<List<SubDTO>> getAllSubs(){
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getAllSubs());
    }

    /**
     * @param subId {@link Sub}'s Id
     * @return {@code ResponseEntity<SubDTO>}
     */
    //@Operation(summary = "GET SUB VIA ITS ID")
    @GetMapping("Sub_Id/{subId}")
    public ResponseEntity<SubDTO> getSubById(
        @PathVariable Long subId){
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getSubID(subId));
    }
    
    /** 
     * @param subDTO {@link SubDTO}
     * @return {@code ResponseEntity<SubDTO>}
     */
    @PostMapping
    public ResponseEntity<SubDTO> createSub(@RequestBody @Valid SubDTO subDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(subsService.save(subDTO));
    }

    /**
     * @param subname {@link Sub}'s name
     * @return {@code ResponseEntity<SubDTO>}
     */
    @GetMapping("Sub_Name/{subname}")
    public ResponseEntity<SubDTO> getSubByName(
        @PathVariable String subname){
        log.info("'--get subname: "+ subname );
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getSubname(subname));
    }
}
