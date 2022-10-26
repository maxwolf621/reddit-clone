package com.pttbackend.pttclone.controller;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.service.SubsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

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

/**
 * Controller For Subs to 
 * display all, Specific Subs and
 * create Sub by Authenticated User 
 */
@RestController
@RequestMapping("/api/sub")
@AllArgsConstructor
@Slf4j
public class SubsController {
    private final SubsService subsService;
    
    /** 
     * <p> Get all the sub </p>
     * @return {@code List<SubDTO>}
     */
    @Operation(summary = "GET ALL SUBS")
    @GetMapping
    public ResponseEntity<List<SubDTO>> getAllSubs(){
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getAllSubs());
    }

    /**
     * <p> Get specific sub </p>
     * @param subId {@link Sub}'s Id
     * @return {@code ResponseEntity<SubDTO>}
     */
    @Operation(summary = "GET SUB VIA ITS ID")
    @GetMapping("Sub_Id/{subId}")
    public ResponseEntity<SubDTO> getSub(
        @Parameter(description = "Sub's sub id")
        @PathVariable Long subId){
        log.info("'--get sub: "+subId );
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getSubid(subId));
    }
    
    /** 
     * <p> Create a sub </p>
     * @param subdto {@link SubDTO}
     * @return {@code ResponseEntity<SubDTO>}
     */
    @Operation(summary = "CREATE NEW SUB")
    @PostMapping
    public ResponseEntity<SubDTO> createSub(@RequestBody @Valid SubDTO subdto){
        log.info("Create A sub");
        return ResponseEntity.status(HttpStatus.CREATED).body(subsService.save(subdto));
    }

    /**
     * get certain sub by subname 
     * @param subname {@link Sub}'s name
     * @return {@code ResponseEntity<SubDTO>}
     */
    @Operation(summary = "GET SUB VIA ITS NAME")
    @GetMapping("Sub_Name/{subname}")
    public ResponseEntity<SubDTO> getSub(
        @Parameter(description = "name of Sub")
        @PathVariable String subname){
        log.info("'--get subname: "+ subname );
        return ResponseEntity.status(HttpStatus.OK).body(subsService.getSubname(subname));
    }
}
