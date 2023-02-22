package com.elastic.security.payload;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse extends ServiceResponse implements Serializable {

    private String userName;
    private String token;
    private String expiredIn;

    public LoginResponse(String message) {
        super(false, message);
    }

    public LoginResponse(String userName,String token, String expiredIn) {
        super();
        this.userName = userName;
        this.token = token;
        this.expiredIn = expiredIn;
    }
}
