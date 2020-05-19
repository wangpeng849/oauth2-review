package com.review.springsecurityremember;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@SpringBootTest
class SpringSecurityRememberApplicationTests {

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        //第一段是用户名，这个无需质疑。
        //第二段看起来是一个时间戳，我们通过在线工具或者 Java 代码解析后发现，这是一个两周后的数据。
        //第三段我就不卖关子了，这是使用 MD5 散列函数算出来的值，他的明文格式是 username + ":" + tokenExpiryTime + ":" + password + ":" + key，最后的 key 是一个散列盐值，可以用来防治令牌被修改。
        String s = new String(Base64.getDecoder().decode("d2FuZ3A6MTU5MDQ3NDI4NTk1NzpjOWU0MzJjMTcwMjJjOWZhNzBiNzJkYzE0ODg0YzE2MA"),"utf-8");
        System.out.println("s："+s);//：wangp:1590474285957:c9e432c17022c9fa70b72dc14884c160
    }

}
