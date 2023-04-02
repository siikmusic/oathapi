package com.tp.oathapi.User;
import com.tp.oathapi.RandomString;
import jakarta.persistence.*;




@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private  Long id;
    private  String email;
    private  String pkey;
    private  String lastOtp;

    public User(String email) {
        this.email = email;
        generatePkey();
    }

    public User() {

    }

    public Long getId() {
        return id;
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


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }
    public String generatePkey(){

        RandomString randomString = new RandomString(64);
        String pkey = randomString.nextString();
        this.pkey = pkey;
        return pkey;
    }

    public String getLastOtp() {
        return lastOtp;
    }

    public void setLastOtp(String lastOtp) {
        this.lastOtp = lastOtp;
    }
}
