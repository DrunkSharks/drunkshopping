package com.drunk.entity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    //有效期为
    public static final Long JWT_TTL = 3600000L;// 60 * 60 *1000  一个小时

    public static final String JWT_KEY = "forever_love";

    public static String createJwt(String id, String subject, Long ttlMillis){

        //当前时间
        long currentTimeMillis = System.currentTimeMillis();
        //签发日期
        Date iatDate = new Date(currentTimeMillis);

        //默认过期时间一个小时
        if(ttlMillis==null){
            ttlMillis = JwtUtils.JWT_TTL;
        }
        //过期时间
        Date expDate = new Date(currentTimeMillis+ttlMillis);

        SecretKey secretKey = getSecretKey();

        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(id)                   //唯一Id
                .setSubject(subject)         //主题
                .setIssuer("admin")          //签发者
                .setIssuedAt(iatDate)        //签发日期
                .setExpiration(expDate)      //过期日期
                .signWith(SignatureAlgorithm.HS256,secretKey);  //签名算法及密钥
        return jwtBuilder.compact();
    }

    /**
     * 生成加密SecretKey
     * @return
     */
    public static SecretKey getSecretKey(){
        byte[] encode_key = Base64.getEncoder().encode(JwtUtils.JWT_KEY.getBytes());
        SecretKey secretKey = new SecretKeySpec(encode_key,0,encode_key.length,"AES");
        return secretKey;
    }

    /**
     * 解析令牌数据
     * @param jwt
     * @return
     */
    public static Claims parseSecretKey(String jwt){
        Claims claims = Jwts.parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }


}
