package com.drunk.jwt;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtDemo {

    @Test
    public void createJwt(){
        //eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiJKd3TmtYvor5UiLCJpYXQiOjE2Mjk2NDE5MjF9.UUxFIFwhX92xFek0MuFfzaCJT9L6QlJGKecv4L8nkdc
        JwtBuilder builder = Jwts.builder()
                .setId("888")                  //设置Id
                .setSubject("Jwt测试")         //主题
                .setIssuedAt(new Date())       //签发日期
                //.setExpiration(new Date())     //过期时间
                .signWith(SignatureAlgorithm.HS256,"liuzhijie");   //设置签名，使用HS256算法，并设置SecretKey

        //自定义claim
        Map<String,Object> claim = new HashMap<>();
        claim.put("name","刘志杰");
        claim.put("age",22);
        claim.put("sex","男");
        builder.addClaims(claim);
        System.out.println(builder.compact());
    }

    @Test
    public void parseJwt(){
        String compact = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiJKd3TmtYvor5UiLCJpYXQiOjE2Mjk2NDI4MzYsInNleCI6IueUtyIsIm5hbWUiOiLliJjlv5fmnbAiLCJhZ2UiOjIyfQ.Z92g7ZEAVBAJgzSL34wWfCXBuWUcYPEerTl-yJkTWYU";
        Claims claims = Jwts.parser()
                .setSigningKey("liuzhijie")
                .parseClaimsJws(compact)
                .getBody();
        System.out.println(claims);

    }

    /**
     * Base64解密
     */
    @Test
    public void decode(){
        byte[] decode = Base64.getDecoder().decode("$2a$10$Yvkp3xzDcri6MAsPIqnzzeGBHez1QZR3A079XDdmNU4R725KrkXi2");
        System.out.println(new String(decode));
    }

    @Test
    public void bcrptyEncode(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("changgou"));
    }
}
