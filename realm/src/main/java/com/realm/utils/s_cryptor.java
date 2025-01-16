package com.realm.utils;

import android.util.Base64;

import java.util.ArrayList;

/**
 * Created by Thompson on 4/10/2017.
 */

public class s_cryptor {
    int index;
    String value;
    static char[] alpha = new char[95];
    static char[] omega = new char[95];

    ArrayList<normums> envs=new ArrayList<>();
    ArrayList<normums> denvs=new ArrayList<>();

    public s_cryptor(int index, String value)
    {
        this.index=index;
        this.value=value;
    }
   public static String encrypt(String string)
    {
        String result="";
        for (char c:string.toCharArray())
        {
            int middet=0;
            for(int i = alpha.length-1; i >= 0; i--){
              //  Log.e("CHARACTER => ",i+" "+alpha[i]);
                if(c==alpha[i])
                {
                    middet=1;
                    result+=omega[i];
                }
            }
            if(middet==0)
            {
                result+=c;
            }


        }



        return result;


    }
    public static String s_encrypt(String string)
    {
        String result=encrypt(string);
        String ss= Base64.encodeToString(result.getBytes(),0);
        return encrypt(Base64.encodeToString(result.getBytes(),0));
    }
    public static String s_decrypt(String string)
    {
        String result=deecrypt(string);
        String ss= Base64.encodeToString(result.getBytes(),0);
       // return encrypt(Base64.encodeToString(result.getBytes(),0));
        String res1=new String(Base64.decode(result,0));
        return  deecrypt(res1);
    }
    public static String deecrypt(String string)
        {
            String result="";
        for (char c:string.toCharArray())
        {
            int middet=0;
            for(int i = omega.length; i > 0; i--){
                if(c==omega[i-1])
                {
                    middet=1;
                    result=result+alpha[i-1];
                }
            }
            if(middet==0)
            {
                result=result+c;
            }


        }
       // Log.e("DECRYPTED => ",""+result);

        return result;
    }
    static {


        for(int i = 0; i < 95; i++){
            alpha[i] = (char)(33 + i);
            }
        for(int i = 0; i < 95; i++){
            omega[i] = (char)((33 +i)*(i*i));


        }

    }
}
