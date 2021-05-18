package org.iottree.core;

import org.iottree.core.util.SecureUtil;
import org.junit.Test;

import junit.framework.TestCase;

public class SecureTest extends TestCase
{
    public void test1() throws Exception{
 
        String password = "123456!@#$";
 
        String salt = SecureUtil.generateSalt();
        String pbkdf2 = SecureUtil.encryptPsw(password,salt);

        //System.out.println("psw src:"+password);
//        System.out.println("solt:"+salt);
//        System.out.println("enc psw:"+pbkdf2);
        
        boolean res1 = SecureUtil.checkPsw(password, pbkdf2, salt);
        assertTrue(res1);
        res1 = SecureUtil.checkPsw("123457", pbkdf2, salt);
        assertFalse(res1);
        //System.out.println("Test success");
 
    }
}
