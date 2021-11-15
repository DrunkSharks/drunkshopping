package com.changgou.oauth;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJwtTest {

    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfQURNSU4iLCJuYW1lIjoiamllIiwiaWQiOjF9.pROpjULCxaU-V3PY3cY_rlvOs8Q1qW1NTpmJOkciPf_tfx3R0v7-IB7GRJTjiAJg3QUi4SG8CpN1f61tH3jdFAZCjheiDnj6RJMMfgy8Ib6bFmq8qa3LBVZWCYUSFMMw6wQQoFTFFDZ2mvG_r-_HgsUGVvPXcOyphPsszVxn9lZYTemrSGC0Hfsl-3mZgPKjP2YM053hq_7BTYknS8ln8g49aXTCHv9NjirahfmtTvRNpPLEuGw67oV4feT0IVsyZTXIqgo0lAgWskXidm0IoZOqMit6T2eopikVVMQuX0LHyKXMmDE3PZoQEIMsO8m73Y6oatZAk5LKKSjJEIDZpQ";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encode = jwt.getEncoded();
        System.out.println(encode);
    }
}
