/**
 * UC乐园Rest接口 Java SDK - 基本工具 - 写log
 *
 * @category   tokenpaser
 * @package    com.ucweb.uzoneapiclient.utils
 * @author     Jiuhong Deng <dengjiuhong@gmail.com>
 * @version    $Id:$
 * @copyright  Jiuhong Deng
 * @link       http://u.uc.cn/
 * @since      File available since Release 1.0.0
 */
package com.ucweb.uzoneapiclient.utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;
public class UzoneTokenParser {
    /**
     * @desc 写log对象
     */
    public static UzoneApiClientLogger Log = new UzoneApiClientLogger("UzoneTokenParser");
    /** 
     * @desc  UzoneToken 解密
     * @param token 
     * @param keyPath 密钥相对地址
     * @return 解密后的uid，如果解密失败，或token不合法，返回空“”
     */
    public static String decodeToken(String UzoneToken, String privateKeyPath) {
        Log.d("decodeToken", "UzoneToken: " + UzoneToken + ", privateKey: " + privateKeyPath);
        String result = "";
        try{
            File file                  = new File(privateKeyPath);     //keyfile key文件的地址
            FileInputStream in         = new FileInputStream(file);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] tmpbuf = new byte[1024];
            int count     = 0;
            while ((count = in.read(tmpbuf)) != -1) {
                bout.write(tmpbuf, 0, count);
                tmpbuf = new byte[1024];
            }
            in.close();
            KeyFactory keyFactory         = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bout.toByteArray());
            RSAPrivateKey privateKey      = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
            // 先base64 decode
            byte[] token = Base64.decode(UzoneToken);
            // 使用RSA解密
            byte[] res   = UzoneRSA.rsaPriDecrypt(privateKey, token, 2);
            result       = new String(res);
            Log.d("decodeToken", "res " + result);
        } catch(Exception e){
            Log.e("decodeToken", e.getMessage().toString());
        }
        return result;
    }
}
