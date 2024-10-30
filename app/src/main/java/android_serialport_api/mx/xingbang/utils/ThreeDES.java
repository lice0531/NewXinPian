package android_serialport_api.mx.xingbang.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings({ "restriction" })
public class ThreeDES {
    private static final String IV = "";
    public static final String KEY = "uatspdbcccgame2014061800";

    /**
     * DESCBC加密
     *
     * @param src
     *            数据源
     * @param key
     *            密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static String encryptDESCBC(final String src, final String key) throws Exception {

        // --生成key,同时制定是des还是DESede,两者的key长度要求不同
        final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        final SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // --加密向量
        final IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));

        // --通过Chipher执行加密得到的是一个byte的数组,Cipher.getInstance("DES")就是采用ECB模式,cipher.init(Cipher.ENCRYPT_MODE,
        // secretKey)就可以了.
        final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        final byte[] b = cipher.doFinal(src.getBytes("UTF-8"));

        // --通过base64,将加密数组转换成字符串
        final BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b);
    }

    /**
     * DESCBC解密
     *
     * @param src
     *            数据源
     * @param key
     *            密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static String decryptDESCBC(final String src, final String key) throws Exception {
        // --通过base64,将字符串转成byte数组
        final BASE64Decoder decoder = new BASE64Decoder();
        final byte[] bytesrc = decoder.decodeBuffer(src);

        // --解密的key
        final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        final SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // --向量
        final IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));

        // --Chipher对象解密Cipher.getInstance("DES")就是采用ECB模式,cipher.init(Cipher.DECRYPT_MODE,
        // secretKey)就可以了.
        final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        final byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);

    }

    // 3DESECB加密,key必须是长度大于等于 3*8 = 24 位哈
    public static String encryptThreeDESECB(final String src, final String key) throws Exception {
        
    	final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
    	//final DESedeKeySpec dks = new DESedeKeySpec(hex(key));
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        final SecretKey securekey = keyFactory.generateSecret(dks);

        final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        final byte[] b = cipher.doFinal(src.getBytes("utf-8"));

        final BASE64Encoder encoder = new BASE64Encoder();
       // System.out.println(encoder.encode(b));
        return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");

    }

    // 3DESECB解密,key必须是长度大于等于 3*8 = 24 位哈
    public static String decryptThreeDESECB(final String src, final String key) throws Exception {
        // --通过base64,将字符串转成byte数组
        final BASE64Decoder decoder = new BASE64Decoder();
        final byte[] bytesrc = decoder.decodeBuffer(src);
        // --解密的key
        final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        final SecretKey securekey = keyFactory.generateSecret(dks);

        // --Chipher对象解密
        final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        final byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);
    }
    public static void main(String[] args) throws Exception {
        final String key = "jadl12345678912345678912";
        // 加密流程
        String telePhone = "1234abcdefgsabaa,,,}";
       // telePhone =URLEncoder.encode(telePhone,"utf-8");
       // ThreeDES threeDES = new ThreeDES();
        String telePhone_encrypt = "";
        //telePhone_encrypt = threeDES.encryptThreeDESECB(URLEncoder.encode(telePhone, "UTF-8"), key);
        telePhone_encrypt = ThreeDES.encryptThreeDESECB(telePhone, key);
        
        System.out.println(telePhone_encrypt);// nWRVeJuoCrs8a+Ajn/3S8g==

        telePhone_encrypt="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";
        // 解密流程
        String tele_decrypt = ThreeDES.decryptThreeDESECB(telePhone_encrypt, key);
        
        System.out.println("模拟代码解密:" + tele_decrypt);
        
 
        tele_decrypt="{\"uid\":\"5980503106B44,598050311A46B,59805030B63AD,59805030BBB2F,59805030BCCF8,59805030B6AE0,59805030BE740,5980503120DD0,598050311D89B,5980503107244,59805030FCED6,5980503107B78,59805030FD050,59805031196B0,598050311C27F,598050310776E,59805030BC74E,59805030BC7E9,59805030BEAFA,59805030B6095,59805031220E2,59805030B6D8C,5980503127A1D,5980503118E36,5980503127CDF,598050311CB5D,598050311190C,5980503115F8D,59805031062B5,5980503101E6D,598050311912B,59805030FDF53,5980503102F07,5980503125A16,5980503105862,598050311E4E6,5980503122F4C,5980503126082,5980503107FF2,59805031229CD,598050312159B,59805031025DC,5980503109218,5980503108BCB,5980503119C67,598050311DAF3,5980503122C2C,59805030FA033,598050311BD0E,5980503103B5E,598050310C3EB,5980503127A7C,598050311C69E,5980503118497,5980503106BAA,598050310732E,5980503106229,598050311DCC8,598050310CEEB,5980503110065,5980503115AFA,5980503119119,5980503106908,5980503121241,598050317BDAC,5980503120607,598050310BC05,5980503118821,59805031100E9,5980503127D3F,5980503115FAB,598050310C30B,5980503108D95,5980503100513,5980503124F8E,59805030FAE96,5980503104287,5980503115D98,59805031086E3,5980503119457,598050311C9BE,5980503115F41,5980503127B31,59805031107BE,59805031214EE,5980503119234,59805031116BC,5980503127241,59805030FD872,598050311E092,59805031101B6,5980503127D67,5980503118CC5,5980503127F5C,5980503103AC2,5980503103844,5980503120268,5980503101638,59805031206DE,598050312282C\",\"sbbh\":\"F6400000029\",\"jd\":\"115.487789\",\"bpsj\":\"2018-06-01 17:47:04\",\"xmbh\":\"522400X18050010\",\"wd\":\"37.764935\",\"bprysfz\":\"522121196410065631\",\"htid\":\"\"}";
        String en=ThreeDES.encryptThreeDESECB(tele_decrypt, key);
       
        System.out.println("模拟代码加密:" + en);
    }
}