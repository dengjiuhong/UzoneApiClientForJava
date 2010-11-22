package com.ucweb.uzoneapiclient;
import com.ucweb.uzoneapiclient.utils.UzoneApiClientLogger;
import com.ucweb.uzoneapiclient.interfaces.UzoneApiClientInterface;

// 引入第三方需要用到的库类
import com.ucweb.uzoneapiclient.utils.UzoneTokenParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
/**
 * UC乐园Rest接口 Java SDK
 *
 * @category   main
 * @package    com.ucweb.uzoneapiclient
 * @author     Jiuhong Deng <dengjiuhong@gmail.com>
 * @version    $Id:$
 * @copyright  Jiuhong Deng
 * @link       http://u.uc.cn/
 * @since      File available since Release 1.0.0
 */
public class UzoneApiClient implements UzoneApiClientInterface {
    /**
     * @desc 记录日志的工具
     * @see UzoneApiClientLogger
     */
    public static UzoneApiClientLogger Log = new UzoneApiClientLogger("UZONE_REST_API");
    /**
     * @desc  初始化
     * @param HashMap<String,String> configs    - 接入乐园的时候，分配的配置
     * @example
     * Map<String, String> configs = new HashMap<String, String>();
     * configs.put("AppKey", "sanguo");
     * configs.put("Secret", "123456"); 
     * configs.put("RestServer", "http://api.u.uc.cn/restserver.php");
     * configs.put("PrivateKeyPath", "/tmp/private.key");
     * 
     * UzoneApiClient client = new UzoneApiClient(configs);
     */
    public UzoneApiClient(Map<String, String> configs)
    {
        // 初始化配置
        AppKey     = configs.get("AppKey");
        Secret     = configs.get("Secret");
        PrivateKey = configs.get("PrivateKeyPath");
        RestServer = configs.get("RestServer");
        SsoServer  = configs.get("SsoServer");
    }
    /**
     * @desc 初始化乐园Rest接口       - 有uzoneToken
     * @param String  appKey       - 接入乐园分配到的appKey
     * @param String  secret       - 接入乐园分配到的密钥
     * @param String  privateKey   - 接入乐园用到的RSA privateKey 路径
     * @param String  uzoneToken   - 获取到的uzoneToken
     */
    public UzoneApiClient(String appKey, String secret, String privateKeyPath, String uzoneToken) {
        AppKey     = appKey;
        Secret     = secret;
        PrivateKey = privateKeyPath;
        UzoneToken = uzoneToken;
        // 接受uzone_token的时候，需要解析
        this.parseUzoneToken();
    }

    /**
     * @desc  初始化乐园接口 - 无uzoneToken
     * 
     * @param String  appKey       - 接入乐园分配到的appKey
     * @param String  secret       - 接入乐园分配到的密钥
     * @param String  privateKey   - 接入乐园用到的RSA privateKey 文件存放的路径
     */
    public UzoneApiClient(String appKey, String secret, String privateKeyPath) {
        AppKey     = appKey;
        Secret     = secret;
        PrivateKey = privateKeyPath;
    }

    /**
     * @desc  调用接口的方法
     * 
     * @param List   param  - 请求的参数
     * @param String method - 请求的接口
     * @return String       - 返回的内容
     */
    public List<String> callMathod(Map<String, String> param, String method) {
        Log.d("callMathod", "start to call method " + method);
        return this.parseResponse(this.httpRequest(param, method));
    }
    /**
     * @desc 获取请求返回的
     */
    public String getResponseData()
    {
         if (ResponseStatus.containsKey("data")) {
             return ResponseStatus.get("data");
         }
         return null;
    }
    /**
     * @desc check callMethod is success!
     * @return boolean
     */
    public boolean checkCallMethodIsSuccess()
    {
        String status = this.getCallMethodStatus("status");
        if (status.equals("ok")){
            return true;
        }
        return false;
    }
    /**
     * @desc 获取失败时候的描述信息
     * @return 
     */
    public String getErrorMessage()
    {
        return this.getCallMethodStatus("msg");
    }
    /**
     * @desc  获取接口请求返回的状态 
     * @return Map<String, String> 
     * @example  status => ok, msg => "
     */
    public String getCallMethodStatus(String key) {
        Log.d("getCallMethodStatus", "start: " + key);
        if (ResponseStatus.containsKey(key)) {
            Log.d("getCallMethodStatus", "Current ResponseStatus is " + ResponseStatus.get(key));
            return ResponseStatus.get(key);
        } else {
            Log.e("getCallMethodStatus", "ResponseStatus not contains key " + key);
        }
        return null;
    }

    /**
     * @desc  通过校验用户名和密码，返回用户的授权uzone_token
     * 
     * @param String  uid  - 用户的uid
     * @param String  pwd  - 用户的密码
     * @param String  sn   - 手机ucweb的sn
     * @param String  imei - 手机的imei
     * @return String UzoneToken
     */
    public String getAuthToken(String uid, String pwd, String sn, String imei) {
        Log.d("getAuthToken", "start get AuthToken ... ");
        Map<String, String> param = new HashMap<String, String>();
        param.put("uid", uid);
        param.put("password", pwd);
        param.put("sn", sn);
        param.put("imei", imei);
        try {
            List<String> res = this.callMathod(param, "sso.getAuthToken");
            Log.d("getAuthToken", "api res: " + res.toString());
            if (!res.isEmpty()) {
                // 暂时没有解析token的方法
                Uid        = Integer.parseInt(uid);
                UzoneToken = res.get(0);
                Log.d("getAuthToken", "got token " + UzoneToken);
                return UzoneToken;
            }
        } catch (Exception e) {
            Log.e("getAuthToken", "exception: " + e.toString());
        }
        Log.d("getAuthToken", "can not get authtoken");
        return null;
    }
    /**
     * @desc 获取授权的用户的uid
     * 
     * @return int
     */
    public int getAuthUid() {
        Log.d("getAuthUid", "Current Uid is " + Uid);
        return Uid;
    }
    /**
     * @desc 检查是否为授权的用户
     * @return boolean
     */
    public boolean checkIsAuth() {
        Log.d("checkIsAuth", "current Uid is " + Uid);
        boolean isAuth = false;
        if (Uid != 0) {
            isAuth = true;
        }
        Log.d("checkIsAuth", "current auth status is " + isAuth);
        return isAuth;
    }
    /**
     * @获取跳转到单点登录系统需要用到的url
     * 
     * @param String  backUrl  - 走完单点登录流程之后，乐园自动返回的地址
     * @return String          - 需要跳转的地址
     */
    public String getRedirectUrl(String backUrl)
    {
        Log.d("getRedirectUrl", "start to gen Url, backUrl is " + backUrl);
        Map<String, String> param = new HashMap<String, String>();
        param.put("appKey", AppKey);
        param.put("v", Ver);
        param.put("backUrl", backUrl);
        Map<String, String> nameValuePairs = this.buildRequestParam(param);
        String res =  this.buildRequestUri(nameValuePairs, SsoServer);
        Log.d("getRedirectUrl", "gen url: " + res);
        return res;
    }
    /**
     * @desc 设置当前uzone_token
     * 
     * @param String  uzoneToken  - 乐园返回(走单点登录流程获取)的uzone_token
     * @return boolean            - 是否设置成功
     */
    public boolean setUzoneToken(String uzoneToken)
    {
        // 设置当前uzone_token
        UzoneToken = uzoneToken;
        // 解析UzoneToken
        this.parseUzoneToken();
        return true;
    }
    /**
     * @desc  设置指定的请求的RestServer Url (一般不需要修改)
     * 
     * @param String  restServer  - 接口的地址
     * @return boolean
     */
    public boolean setRestServer(String restServer) {
        RestServer = restServer;
        return true;
    }
    /**
     * @desc  设置当前接口的版本 (默认为2.0, 不需要设置)
     * @param String ver
     * @return boolean
     */
    public boolean setApiVersion(String ver) {
        Ver = ver;
        return true;
    }
    /**
     * @desc  发起HTTP请求
     * 
     * @param Map<String, String>  param  - 请求的参数  如{'uids' => '10001,10002'}
     * @param String               method - 请求的方法, 如 user.getInfo
     * @return String   server 返回的内容
     */
    private String httpRequest(Map<String, String> param, String method) {
        Log.d("httpRequest", "start to send http request");
        // 添加其他默认需要的参数
        param.put("method", method);
        if (!param.containsKey("v")){
            param.put("v", Ver);
        }
        if (!param.containsKey("appKey")){
            param.put("appKey", AppKey);
        }
        if (!param.containsKey("uzone_token") && UzoneToken != null){
            param.put("uzone_token", UzoneToken);
        }
        // 生成请求用户的参数
        Map<String, String> nameValuePairs = this.buildRequestParam(param);
        String url            = this.buildRequestUri(nameValuePairs, RestServer);
        String response       = "";
        HttpClient client     = new HttpClient();
        InputStream in        = null;
        BufferedReader reader = null;
        try{
            HttpMethod getMethod = new GetMethod(url);
            client.executeMethod(getMethod);
            in     = getMethod.getResponseBodyAsStream();
            reader           = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String temp      = ""; 
            while((temp = reader.readLine()) != null){
                sb.append(temp);
            }   
            response = sb.toString();
            // 关闭链接
            getMethod.releaseConnection(); 
        }catch (Exception e) {
            // TODO: handle exception
        }finally{
            try{
                if(in != null){
                    in.close();
                }   
                if(reader != null){
                    reader.close();
                }
            }catch (IOException e) {
                // TODO: handle exception
            }
        }
        Log.d("httpRequest", "Server Resonse: " + response);
        return response;
    }
    /**
     * @desc 生成请求的链接
     * @param Map<String, String>  nameValuePairs  - 请求的参数
     * @param String               BaseUrl         - 基础参数
     */
    private String buildRequestUri(Map<String, String> nameValuePairs, String baseURl)
    {
        Log.d("buildRequestUri", "start to build request uri");
        String Url = "";
        try{
            for (Entry<String, String> entry : nameValuePairs.entrySet()) {
                // 去掉空的参数
                if (entry.getValue().equals("")) {
                    continue;
                }
                Url += entry.getKey().toString() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8") + "&";
            }
            // 如果baseUrl里面有带 ? ，链接符改为 &
            String prefix = "?";
            int tmp       = baseURl.indexOf("?");
            if (tmp != -1){
                prefix = "&";
            }
            Url = baseURl + prefix + Url;
        } catch(Exception e){
            Log.e("buildRequestUri", "build Url failt " + e.getMessage().toString());
        }
        Log.d("buildRequestUri", "res: " + Url);
        return Url;
    }
    /**
     * @desc   构建请求的参数
     * 
     * @param  Map<String, String> param  - 请求的参数
     * @return List<NameValuePair>        - 编译好的请求的参数
     */
    private Map<String, String> buildRequestParam(Map<String, String> param) {
        Log.d("buildRequestParam", "start to build request param");
        // 将Map转变为数组
        List<String> params = new ArrayList<String>(param.size());
        for (Entry<String, String> entry : param.entrySet()) {
            // 去掉空的参数
            if (entry.getValue().equals("")) {
                continue;
            }
            params.add(entry.getKey().toString() + "=" + entry.getValue().toString());
        }
        // 生成sig
        String sig = this.signature(params);
        Log.d("buildRequestParam", "gen signature " + sig);
        param.put("sig", sig);
        return param;
    }
    /**
     * @desc  生成证书
     * 
     * @param Map<String, String> param - 请求的参数
     * @return String  sig签名
     */
    private String signature(List<String> params) {
        Log.d("signature", "start to gen signature ...");
        StringBuilder buffer = new StringBuilder();
        // 先按照字母排序
        Collections.sort(params);
        for (String param : params) {
            buffer.append(param);
        }
        buffer.append(Secret);
        Log.d("signature", "gen signature before md5: " + buffer.toString());
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            StringBuilder res = new StringBuilder();
            try {
                for (byte b : md.digest(buffer.toString().getBytes("UTF-8"))) {
                    res.append(Integer.toHexString((b & 0xf0) >>> 4));
                    res.append(Integer.toHexString(b & 0x0f));
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                for (byte b : md.digest(buffer.toString().getBytes())) {
                    res.append(Integer.toHexString((b & 0xf0) >>> 4));
                    res.append(Integer.toHexString(b & 0x0f));
                }
            }
            return res.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            Log.e("signature", "MD5 does not appear to be supported" + e);
        }
        return null;
    }

    /**
     * @desc 初始化接口
     */
    private void init() {
        this.parseUzoneToken();
    }
    /**
     * @desc   使用RSA解析uzoneToken
     * @return boolean  是否设置成功
     */
    private boolean parseUzoneToken() {
        Log.d("parseUzoneToken", "try to parse uzone_token " + UzoneToken);
        //如果初始化的时候传入的uzone_token为非空，则解蝎uzone_token
        if (UzoneToken != null || !UzoneToken.isEmpty()){
            // 解蝎rsa格式的uzone_token
            String Token = UzoneTokenParser.decodeToken(UzoneToken, PrivateKey);
            Log.d("parseUzoneToken", "token parse result: " + Token);
            // 如果为非空的结果，说明uzone_token有效, 解析出来的结果为 uid
            if (!Token.isEmpty()){
                Uid = Integer.parseInt(Token);
            }
        }
        return true;
    }

    /**
     * @desc   解析接口返回来的数据
     * 
     * @param  String  Respnse
     * @return List<String>
     */
    private List<String> parseResponse(String Response) {
        // 返回的数据的格式一般为
        // status = '', code => '', 'msg' => , 'data' => ''
        // 如果返回的数据没有包含 status:ok, 则整个数据返回空
        // {"status":"error","code":"200","msg":"invial password","data":[]}
        ResponseStatus = this.parseResponseStatus(Response);
        Log.d("parseResponse", "get response status " + ResponseStatus.get("status"));
        ArrayList<String> result = new ArrayList<String>();
        if (ResponseStatus.containsKey("status")) {
            if (!ResponseStatus.get("status").equals("ok")) {
                Log.e("parseResponse", "parseResponseStatus not ok, " + Response);
                return null;
            }
            try {
                JSONObject jsonObj = new JSONObject(Response);
                if (jsonObj.has("data")) {
                    String data = jsonObj.getString("data");
                    if (data != null) {
                        result.add(data);
                    } else {
                        org.json.JSONArray jArray = jsonObj.getJSONArray("data");
                        if (jArray == null || jArray.length() < 1) {
                            return null;
                        }
                        for (int i = 0; i < jArray.length(); i++) {
                            result.add(jArray.getString(i));
                        }
                    }
                } else {
                    Log.e("parseResponse", "parseResponse do not has data key");
                }
            } catch (Exception e) {
                Log.e("parseResponse", "parseResponse exception " + e.toString());
            }
        }
        return result;
    }

    /**
     * @desc  解析接口返回的状态
     * @param String  Response - 接口返回的原始json字符串
     * @return
     */
    private Map<String, String> parseResponseStatus(String Response) {
        Log.d("parseResponseStatus", "start to parse response status ... ");
        Map<String, String> status = new HashMap<String, String>();
        try {
            JSONObject jsonObj = new JSONObject(Response);
            Log.d("parseResponseStatus", "parse json str " + jsonObj.toString());
            if (jsonObj.has("status")) {
                Log.d("parseResponseStatus", "response has key status: " + jsonObj.getString("status"));
                status.put("status", jsonObj.getString("status"));
                status.put("code", jsonObj.getString("code"));
                status.put("msg", jsonObj.getString("msg"));
                status.put("data", jsonObj.getString("data"));
            }
        } catch (Exception e) {
            Log.e("parseResponseStatus", "parseResponseStatus error : " + e);
        }
        Log.d("parseResponseStatus", "set private ResponseStatus as " + status.toString());
        return status;
    }
    /**
     * @desc 乐园接口服务的地址
     */
    private static String RestServer  = "";
    /**
     * @desc 接入乐园的时候分配到AppKey
     */
    private static String AppKey      = "";
    /**
     * @desc 接入乐园的时候分配到的密钥
     */
    private static String Secret      = "";
    /**
     * @desc 乐园的单点登录系统的入口地址
     */
    private static String SsoServer   = "";
    /**
     * @desc 接入乐园的时候，分配到的RSA解密密钥的卤鸡
     */
    private static String PrivateKey;
    /**
     * @desc 当前接口的版本号
     */
    private String Ver                = "2.0";
    /**
     * @desc 从乐园进入应用的时候带过来的uzone_token
     * 用户身边信息校验串
     */
    private String UzoneToken;
    private Map<String, String> ResponseStatus;
    /**
     * @desc 当前用户的uid
     */
    private int Uid;
}
