package com.fitcrew.trainingservice.util;

import com.fitcrew.FitCrewAppConstant.message.type.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String USER_ID = "userId";
    private static final String BEARER = "Bearer ";
    private static final String ROLES_KEY = "roles";
    private static final String EMAIL = "email";

    public static String createToken(String secret,
                                     RoleType roleType,
                                     String email) {

        long now = (new Date()).getTime();
        Date validity = new Date(now + 43200 * 1000);

        return BEARER.concat(getJwtToken(secret, validity, roleType, email));
    }

    private static String getJwtToken(String secret,
                                      Date validity,
                                      RoleType roleType,
                                      String email) {
        return Jwts.builder()
                .setSubject("test")
                .claim(USER_ID, 121)
                .claim(AUTHORITIES_KEY, "system")
                .claim(ROLES_KEY, roleType)
                .claim(EMAIL, email)
                .signWith(SignatureAlgorithm.HS256, secret)
                .setExpiration(validity)
                .compact();
    }
}
