package com.tp.oathapi.User;

import com.tp.oathapi.link.oath.*;
import com.tp.oathapi.ocra.OcraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
@Tag(name = "User", description = "User OTP API")
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Create user",
            description = "Create a user with email. If email exists, the user will not get added.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }) })
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserRequest user) {

        boolean added = userService.addUser(user);
        if (!added){
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Added user");

    }
    @Operation(
            summary = "Generate OTP",
            description = "Generate a time based OTP for a user email and send it to the email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/otp/generate/{email}")
    public ResponseEntity<String> generateOtp(@PathVariable String email) {
        String otp = userService.generateOtp(email);
        if(otp == null){
            return new ResponseEntity<>("Unauthorized access",HttpStatus.UNAUTHORIZED);
        }
        if(otp.isEmpty()){
            return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("");
    }
    @Operation(
            summary = "Validate OTP",
            description = "Validate a time based OTP for a user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/otp/validate/{email}")
    public ResponseEntity<String> validateOtp(@PathVariable String email, @RequestParam("otp") String otp) {
        if(userService.validateOtp(email,otp) != null) {
            userService.setValidOtp(email,otp);
            return ResponseEntity.ok("Valid OTP");
        }
        return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
    }
    @PostMapping("/ocra/v2/generate")
    public ResponseEntity<String> generateOcraV2(@RequestBody OcraRequest request) {
        try {
            userService.generateOcraV2(request);
            return ResponseEntity.ok("Ocra Sent");
        } catch (InvalidOcraSuiteException | InvalidDataModeException | InvalidHashException | InvalidCryptoFunctionException | InvalidSessionException | InvalidQuestionException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid Request", HttpStatus.UNAUTHORIZED);

        }
    }
    @PostMapping("/ocra/v2/validate")
    public String validateOcraV2(@RequestBody OcraRequest request) {
        try {
            return userService.validateOcraV2(request);
        } catch (InvalidOcraSuiteException | InvalidDataModeException | InvalidHashException | InvalidCryptoFunctionException | InvalidSessionException | InvalidQuestionException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
