package com.tp.oathapi.User;

import com.tp.oathapi.link.oath.*;
import com.tp.oathapi.ocra.OcraRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("{userId}/key")
    public String generatePKey(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        if(user == null) {return null;}
        return userService.generateKey(user);
    }
    @PostMapping
    public void registerUser(@RequestBody User user) {
        userService.addUser(user);
    }
    @GetMapping("/otp/generate/{email}")
    public ResponseEntity<String> generateOtp(@PathVariable String email) {
        String otp = userService.generateOtp(email);
        if(otp == null){
            return new ResponseEntity<>("Unauthorized access",HttpStatus.UNAUTHORIZED);
        }
        if(otp.isEmpty()){
            return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(otp);
    }
    @GetMapping("/otp/validate/{email}")
    public ResponseEntity<String> validateOtp(@PathVariable String email, @RequestParam("otp") String otp) {
        if(userService.validateOtp(email,otp) != null) {
            userService.setValidOtp(email,otp);
            return ResponseEntity.ok("Valid OTP");
        }
        return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/ocra/generate")
    public ResponseEntity<String> generateOcra(@RequestBody OcraRequest request) {
        try {
            userService.generateOcraV2(request);
            return ResponseEntity.ok("Ocra Sent");
        } catch (InvalidOcraSuiteException | InvalidDataModeException | InvalidHashException | InvalidCryptoFunctionException | InvalidSessionException | InvalidQuestionException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);

        }
    }
    @PostMapping("/ocra/validate")
    public ResponseEntity<String> validateOcra(@RequestBody OcraRequest request) {
        try {
            String response = userService.validateOcraV2(request);
            if(response == null){
                return new ResponseEntity<>("Invalid Ocra",HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(userService.validateOcraV2(request), HttpStatus.OK);
        } catch (InvalidOcraSuiteException | InvalidDataModeException | InvalidHashException | InvalidCryptoFunctionException | InvalidSessionException | InvalidQuestionException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid Ocra",HttpStatus.UNAUTHORIZED);
        }
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
