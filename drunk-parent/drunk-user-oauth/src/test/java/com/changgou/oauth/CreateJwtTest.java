package com.changgou.oauth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class CreateJwtTest {

    /**
     * 使用rsa私钥生成JWT令牌
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location = "drunk.jks";
        //密钥库密码
        String key_password = "drunk";
        //密钥密码
        String keypwd = "drunk";
        //密钥别名
        String alias = "drunk";

        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, key_password.toCharArray());

        //读取密钥对（公钥、私钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id",1);
        tokenMap.put("name","jie");
        tokenMap.put("roles","ROLE_ADMIN");

        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivateKey));

        //取出令牌
        String encode = jwt.getEncoded();
        System.out.println(encode);
    }
   
}
