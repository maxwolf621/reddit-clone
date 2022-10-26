package com.pttbackend.pttclone.controller;

import com.pttbackend.pttclone.dto.UpdatePasswordDTO;
import com.pttbackend.pttclone.dto.UserDTO;
import com.pttbackend.pttclone.interfaces.StorageService;
import com.pttbackend.pttclone.service.UserService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api/userprofile")
@Slf4j
public class UserController {

    private final StorageService storageService;
    private final UserService userService;

    
    /**
     * UserProfile
     * @return {@code UserDTO}
     */
    @Operation(summary = "GET USER INFORMATION")
    @GetMapping("/account")
    public ResponseEntity<UserDTO> getUserProfile(){
        log.info("Show Up User Profile");
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile());
    }
    
    /**
     * update User's avatar 
     * @param uploadFileData user's Avatar
     * @return {@code ResponseEntity<String>} if Update Avatar Successfully
     * @see <a href="https://www.baeldung.com/spring-data-partial-update"> JPA update </a>
     * @see <a href="https://stackoverflow.com/questions/39741102/how-to-beautifully-update-a-jpa-entity-in-spring-data">
     *      reference of update a user </a>
     * @see <a href="https://newbedev.com/how-do-i-update-an-entity-using-spring-data-jpa">
     *      Update A Entity </a>
     * @see <a href="https://www.baeldung.com/spring-data-partial-update">
     *      DTO Mapping to MODEL </a>
     */
    @Operation(summary = "UPDATE AVATAR version 2")
    @PostMapping(value = "/updateAvatar")
    public ResponseEntity<String> updateAvatar(@RequestPart("file") MultipartFile uploadFileData){
       userService.updateAvatar(uploadFileData);
        return ResponseEntity.status(HttpStatus.OK).body("Update Avatar Successfully");   
    }

    /**
     * update User information
     * @param userDTO {@link UserDTO}
     * @return {@code ResponseEntity<String>} if update Account Successfully
     */
    @Operation(summary = "UPDATE USER'S PROFILE")
    @PostMapping(value="/updateAccount")
    public ResponseEntity<String> updateAccount(@RequestBody UserDTO userDTO){
        userService.updateAccount(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Update Account Successfully");   
    }

    /**
     * upload File
     * @param file uploaded File data
     * @return {@code ResponseEntity<String>}
     */
    @Operation(summary = "UPDATE AVATAR")
    @PostMapping(value ="/updateFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        storageService.store(file);

        return ResponseEntity.status(HttpStatus.OK).body("Upload File Successfully");   
    }
    
    /**
     * Change Password
     * @param updatePasswordDTO {@link UpdatePasswordDTO}
     * @return {@code ResponseEntity<Void>}
     */
    @Operation(summary = "CHANGE USER'S PASSWORD")
    @PostMapping(value = "/changePassword")
    public ResponseEntity<Void> changePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        
        if(userService.changePassword(updatePasswordDTO)){
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    
    }
}
