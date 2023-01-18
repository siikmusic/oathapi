package com.tp.oathapi.User;
import com.tp.oathapi.RandomString;
import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Random;



@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private  Long id;
    private  String username;
    private  String email;
    private  String pkey;
    private  String lastOtp;

    public User(String username, String email) {
        this.username = username;
        this.email = email;


    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPkey() {
        return pkey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }
    public String generatePkey(){
      /*  int leftLimit = 33;
        int rightLimit = 122;
        int targetStringLength = 64;
        Random random = new SecureRandom();

        String pkey = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

       */
        RandomString randomString = new RandomString(64);
        String pkey = randomString.nextString();
        this.pkey = DigestUtils.sha256Hex(pkey);
        return pkey;
    }

    public String getLastOtp() {
        return lastOtp;
    }

    public void setLastOtp(String lastOtp) {
        this.lastOtp = lastOtp;
    }
}
