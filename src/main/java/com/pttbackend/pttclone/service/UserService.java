package com.pttbackend.pttclone.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.pttbackend.pttclone.dto.UpdatePasswordDTO;
import com.pttbackend.pttclone.dto.UserDTO;
import com.pttbackend.pttclone.exceptions.StorageException;
import com.pttbackend.pttclone.mapper.UserMapper;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.UserRepository;
import com.pttbackend.pttclone.utility.AvatarUploadUtils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepo;
    private final AuthenticationService  authService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    
    /**
     * Display All User's information
     * @return {@link UserDTO}
     */
    public UserDTO getUserProfile(){
        return userMapper.mapToUserDTO(authService.getCurrentUser());
    }

    /**
     * Check if Old Password is Valid 
     * @param oldPasswordEncoded a valid password that authenticated user gives
     * @return {@code Optional<User>} or {@code null} 
     */
    private Optional<User> checkIfValidOldPassword(String oldPasswordEncoded){        
        User user = authService.getCurrentUser();
        return passwordEncoder.matches(oldPasswordEncoded,user.getPassword()) ? Optional.of(user) : Optional.empty();
    }

    /**
     * If {@code #checkIfValidOldPassword(String)} returned value is not empty then
     * we save the update our password
     * @param user {@link User}
     * @param newPassword {@link UpdatePasswordDTO}'s Password
     * @return boolean
     */
    private boolean saveNewPassword(final User user, String newPassword){
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return true;
    }

    /**
     * 
     * Change the password to new one 
     * for an Authenticated User
     * @param updatePasswordDTO DTO contains old password and newPassword 
     * @return boolean
     */
    public boolean changePassword(final UpdatePasswordDTO updatePasswordDTO){
        Optional<User> user = this.checkIfValidOldPassword(updatePasswordDTO.getOldPassword());
        
        if(user.isEmpty()){
            return false;
        }
        return this.saveNewPassword(user.get(),updatePasswordDTO.getNewPassword());   
    }

    /**
     * 
     * Update Avatar of The User
     * @param uploadImageData A uploaded File of {@code MultipartFile} data type
     * @see org.springframework.web.multipart.MultipartFile
     */
    public void updateAvatar(MultipartFile uploadImageData ){
        User user = authService.getCurrentUser();

        if(uploadImageData.isEmpty()){
            throw new StorageException("Cannot Read the Uploaded Image Data");
        } 

        // set the root location 
        // saving the avatar in frontend (angular) `assets`
        String rootLocation = "../SpringBootFrontend/src/assets/" + user.getUsername();

        Path rootLocationPath = Paths.get(rootLocation);

        if(!user.getAvatar().isEmpty() && user.getAvatar() != null){
            log.info(" Delete The Avatar if there is another one existing in the storage");
            AvatarUploadUtils.deleteAll(rootLocationPath);
        }

        // get clean filename 
        String uploadImageName = StringUtils.cleanPath(uploadImageData.getOriginalFilename());
        
        user.setAvatar(uploadImageName);
        
        log.info("Saving uploadedFile in Server Device");
        AvatarUploadUtils.saveFile(rootLocation, uploadImageName, uploadImageData);
        userRepo.save(user);
    }

    /**
     * update User's information
     * @param userDTO {@link UserDTO}
     */
    public void updateAccount(UserDTO userDTO){
        User user = authService.getCurrentUser();
        userMapper.updateUserFromDto(userDTO, user);
        userRepo.save(user);
    }
}
