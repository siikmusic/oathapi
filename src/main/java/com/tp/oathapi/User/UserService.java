package com.tp.oathapi.User;

import com.tp.oathapi.Counter.Counter;
import com.tp.oathapi.Counter.CounterRepository;
import com.tp.oathapi.service.MailSenderService;
import com.tp.oathapi.link.oath.*;
import com.tp.oathapi.ocra.OcraRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CounterRepository counterRepository;
    @Autowired
    public UserService(UserRepository userRepository, CounterRepository counterRepository) throws InvalidOcraSuiteException, InvalidDataModeException, InvalidHashException, InvalidCryptoFunctionException {
        this.userRepository = userRepository;
        this.counterRepository = counterRepository;
    }
    private final OCRASuite ocraSuite = new OCRASuite("OCRA-1:HOTP-SHA256-8:QA08");

    public List<User> getUsers() {
            return userRepository.findAll();
    }
    public boolean addUser(UserRequest user) {
        Optional<User> userByEmail =  userRepository.findUserByEmail(user.getEmail());
        User validUser;
        if(userByEmail.isEmpty()) {
            validUser = new User(user.getEmail());
            validUser.generatePkey();
            userRepository.save(validUser);
            return true;
        }
        return false;

    }

    public String generateKey(User user){
        String key = user.generatePkey();
        userRepository.save(user);
        return key;
    }

    public User getUser(Long id){
        if(id == null) return null;
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }
    public User getUser(String email){
        if(email == null) return null;
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userOptional.orElse(null);
    }
    public String generateOtp(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()) {
            System.out.println("User Present");
            User user = userOptional.get();

            try {

                TOTP totp = new TOTP(user.getPkey().getBytes(),6,360,2,0);
                Calendar calendar = Calendar.getInstance();
                MailSenderService emailSenderService = new MailSenderService();
                String otp =totp.generate(calendar.getTimeInMillis());
                emailSenderService.sendEmail(user.getEmail(),"OTP From FEI Banking System: "+otp,otp);
                return otp;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                return "";

            }
        }
        System.out.println("User not Present");

        return "";
    }

    public Integer validateOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(user.getLastOtp() != null) {
                if(user.getLastOtp().equals(otp)){
                    return null;
                }
            }
            String key = user.getPkey();

            try {
                TOTP totp = new TOTP(key.getBytes(),6,360,2,0);
                Calendar calendar = Calendar.getInstance();

                try {
                    return totp.validate(calendar.getTimeInMillis(),otp);
                } catch (InvalidResponseException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                return null;

            }
        }
        return null;
    }

    public String generateOcra(OcraRequest request) throws InvalidOcraSuiteException, InvalidDataModeException, InvalidHashException, InvalidCryptoFunctionException, InvalidSessionException, InvalidQuestionException, NoSuchAlgorithmException {
        Optional<User> userOptional = userRepository.findUserByEmail(request.getEmail());

        if(userOptional.isEmpty()) return "user";

        User user = userOptional.get();

        List<Counter> counterOptional = counterRepository.findAll();

        if (counterOptional.isEmpty()) {
            return null;
        }

        Counter counter = counterOptional.get(0);
        OCRA ocra = new OCRA(ocraSuite,user.getPkey().getBytes(),0,30,0);
        Calendar calendar = Calendar.getInstance();
        String ocraString = ocra.generate(counter.getCounter(),request.getHash(),"","",calendar.getTimeInMillis());
        MailSenderService emailSenderService = new MailSenderService();
        emailSenderService.sendEmail(user.getEmail(),"OCRA",ocraString);
        return ocraString;
    }

    public String generateOcraV2(OcraRequest request) throws InvalidOcraSuiteException, InvalidDataModeException, InvalidHashException, InvalidCryptoFunctionException, InvalidSessionException, InvalidQuestionException, NoSuchAlgorithmException {
        User user = getUser(request.getUserId());
        if(user == null) return null;
        String sha256hex = DigestUtils.sha256Hex(request.getKey());
        if(!user.getPkey().equals(sha256hex)) {
            return "unauthorized";
        }

        OCRA ocra = new OCRA(ocraSuite,request.getKey().getBytes(),0,30,0);
        Calendar calendar = Calendar.getInstance();


        MailSenderService emailSenderService = new MailSenderService();
        String ocraString = ocra.generate(1,request.getHash(),"","",calendar.getTimeInMillis());
        emailSenderService.sendEmail(user.getEmail(),"Ocra",ocraString);

        return ocraString;
    }

    public String validateOcra(OcraRequest request) throws InvalidOcraSuiteException, InvalidDataModeException, InvalidHashException, InvalidCryptoFunctionException, InvalidSessionException, InvalidQuestionException, NoSuchAlgorithmException {
        Optional<User> userOptional = userRepository.findUserByEmail(request.getEmail());
        if(userOptional.isEmpty()) return null;
        User user = userOptional.get();

        Counter counter = counterRepository.findCounterById(1L).get();

        OCRA ocra = new OCRA(ocraSuite,user.getPkey().getBytes(),0,30,0);
        Calendar calendar = Calendar.getInstance();
        try {
            OCRAState state= ocra.validate(counter.getCounter(),request.getHash(),"","",calendar.getTimeInMillis(),request.getQuestion());
            counter.setCounter((int) state.newCounter);
            System.out.println(counter.getCounter());
            counterRepository.save(counter);
            return "valid";
        } catch (InvalidResponseException e) {
            return null;
        }
    }

    public String validateOcraV2(OcraRequest request) throws InvalidOcraSuiteException, InvalidDataModeException, InvalidHashException, InvalidCryptoFunctionException, InvalidSessionException, InvalidQuestionException, NoSuchAlgorithmException {
        User user = getUser(request.getUserId());
        if(user == null) return "invalid user";

        String sha256hex = request.getKey();
        if(!user.getPkey().equals(sha256hex)) {
            return "unauthorized";
        }

        OCRA ocra = new OCRA(ocraSuite,request.getKey().getBytes(),0,30,0);
        Calendar calendar = Calendar.getInstance();
        System.out.println(request.getKey());
        try {
            String ocraString = ocra.generate(1,request.getHash(),"","",calendar.getTimeInMillis());
            System.out.println(ocraString+" "+request.getQuestion());
            ocra.validate(1,request.getHash(),"","",calendar.getTimeInMillis(),request.getQuestion());
            return "valid";
        } catch (InvalidResponseException e) {
            return null;
        }
    }

    public void setValidOtp(String email, String otp) {
        User user = getUser(email);
        if(user != null) {
            user.setLastOtp(otp);
            userRepository.save(user);
        }

    }
}
