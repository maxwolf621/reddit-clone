package com.pttbackend.pttclone.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

import com.pttbackend.pttclone.dto.LoginRequest;
import com.pttbackend.pttclone.dto.RefreshTokenRequest;
import com.pttbackend.pttclone.dto.RefreshTokenResponse;
import com.pttbackend.pttclone.dto.RegisterRequest;
import com.pttbackend.pttclone.dto.UpdatePasswordDTO;
import com.pttbackend.pttclone.dto.UserDTO;
import com.pttbackend.pttclone.service.AuthenticationService;
import com.pttbackend.pttclone.service.RefreshTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller for Authentication Login, register, Account Activate,refresh the token ,Forget Password
 * @see <a href="https://www.baeldung.com/spring-security-registration-i-forgot-my-password">
 *      reference[reset the password] </a>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authService;
    private final RefreshTokenService refreshTokenService;
    /**
     * sign up
     * @param req : {@link RegisterRequest}
     * @return {@code ResponseEntity<>("Upon Successful Registration",HttpStatus.OK)}
     */
    @ApiOperation(
        value = "REGISTRATION",
        notes = "https://mailtrap.io/signin")
    @ApiResponses(value = 
        {@ApiResponse(code = 500,message = "Duplicate Email")}
    )
    @PostMapping(value="/signup")
    public ResponseEntity<String> signup(@ApiParam(value = "DTO") @RequestBody RegisterRequest req) {
        authService.signup(req);
        return new ResponseEntity<>("Upon Successful Registration",HttpStatus.OK);
    }

    /**
     * Via the valid Token sent by Server's {@code SendMailService} to activate the account 
     * @param token : A token provided by user for backend to verify this token 
     * @return {@code ResponseEntity<>("Token Legitimate",HttpStatus.OK)}
     */
    @ApiOperation(value = "ACTIVATE A NEW USER")
    @ApiResponses(
        {@ApiResponse(code = 500, message = "Illegitimate Token")})
    @GetMapping(value="/accountVerification/")
    public ResponseEntity<String> activeUserAccount(@RequestParam("token") String token){
        
        log.info("--token : " + token);
        authService.verifyToken(token);
        
        return ResponseEntity.status(HttpStatus.OK).body("Token Legitimate");
    }

    /**
     * Send RestPasswordToken to User who FORGETS the password
     * @param user {@link UserDTO}
     * @return {@code ResponseEntity<>("Mail has been sent",HttpStatus.OK)}
     */
    @Operation(summary = "FORGET PASSWORD PROCESS")
    @PostMapping("/forgetPassword")
    public ResponseEntity<String> sendResetPasswordToken(@RequestBody UserDTO user){
        
        authService.sendMailToResetPassword(user.getMail());
        
        return ResponseEntity.status(HttpStatus.OK).body("Mail has been sent");
    }

    /**
     * Reset Password 
     * @param updatePasswordDTO {@link UpdatePasswordDTO}
     * @return {@code ResponseEntity<>("Reset Password Successfully",HttpStatus.OK)}
     */
    @Operation(summary = "RESET USER'S PASSWORD")
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        authService.resetPassword(updatePasswordDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Reset Password Successfully");
    }

    /**
     * Log In
     * @param loginRequest {@link LoginRequest}
     * @return RefreshTokenResponse to client (Saving new legitimate token in https session)
     */
    @Operation(summary = "LOGIN PROCESS")
    @PostMapping("/login")
    public RefreshTokenResponse login(@RequestBody LoginRequest loginRequest) {
        log.info(loginRequest.getUsername() + ": Is Logging In ");
        return authService.login(loginRequest);
    }

    /**
     * Refresh Jwt 
     * @param refreshTokenRequest {@link RefreshTokenRequest}
     * @return RefreshTokenResponse to client (Saving new legitimate token in https session)
     */
    @Operation(summary = "REFRESH TOKEN IF JWT HAS EXPIRED")
    @PostMapping("/refreshToken")
    public RefreshTokenResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Token is Expired");
        log.info("  '--- Start to Process of Refresh Token");
        return authService.refreshToken(refreshTokenRequest);
    }

    @Operation(summary = "LOG OUT")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Log Out Successfully!!");
    }

    @Operation(summary = "Delete User's Account")
    @PostMapping("/delete_Account")
    public ResponseEntity<String> delete(@RequestBody UserDTO userDTO) {
        authService.deleteUser(userDTO.getMail());
        return ResponseEntity.status(HttpStatus.OK).body("Delete The User Successfully!!");
    }
}
    