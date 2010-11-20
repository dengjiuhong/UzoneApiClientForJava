package com.ucweb.uzoneapiclient;

import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple UzoneApiClientTest.
 */
public class UzoneApiClientTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UzoneApiClientTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UzoneApiClientTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testUzoneApiClient()
    {
        // UzoneToken为从乐园进入的时候，乐园传给应用的授权信息
        String UzoneToken      = "XXnxvOlc+vxsqSYHq9PyXxqpdHzFlnvw85yWXaywP6hzoMzespfNYzILLRdw/ZtUtJyvbBOpdNcnd40a+Xv8uw==";
        // 读取配置
        Map<String, String> configs = new HashMap<String, String>();
        configs.put("AppKey", "sanguo");
        configs.put("Secret", "123456");
        configs.put("RestServer", "http://api.u.uc.cn/restserver.php");
        configs.put("SsoServer", "http://u.uc.cn/index.php?r=sso/auth");
        configs.put("PrivateKeyPath", "/tmp/private.key");
        // 实例化一个 ApiClinet 的实例
        UzoneApiClient client = new UzoneApiClient(configs);
        // 设置当前授权标记
        client.setUzoneToken(UzoneToken);
        
        // 如果是非授权的uzone_token
        if (!client.checkIsAuth()){
            // 走单点登录流程, 当前地址
            String BackUrl    = "http://localhost/test.php";
            String RdirectURl = client.getRedirectUrl(BackUrl);
            // 302 跳转上面的url
            
        } else {
            // 手工请求指定的接口
            // 1, 获取用户的基本信息
            Map<String, String>  param = new HashMap<String, String>();
            param.put("uids", "10001,10002");
            client.callMathod(param, "user.getInfo");
            // 2, 获取当前用户的好友列表
            Map<String, String>  friendsGetParam = new HashMap<String, String>();
            client.callMathod(friendsGetParam, "friends.get");
        }
        assertTrue( true );
    }
}
