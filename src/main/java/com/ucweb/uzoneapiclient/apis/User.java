/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ucweb.uzoneapiclient.apis;
import java.util.List;
import com.ucweb.uzoneapiclient.UzoneApiClient;
import com.ucweb.uzoneapiclient.schema.UserObject;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author denny
 */
public class User {
    private static UzoneApiClient Client;
    /**
     * @desc  初始化
     * @param UzoneApiClient client   - 乐园接口基础操作对象
     */
    public User(UzoneApiClient client)
    {
        Client = client; 
    }
    /**
     * @desc   获取指定用户的基本信息
     * @param  String[]  uids  - 用户uid的集合
     * @return List<UserObject>
     */
    public List<UserObject> getInfo(List<String> uids)
    {
        // 手工请求指定的接口
        Map<String, String>  param = new HashMap<String, String>();
        String str = "";
        for(String uid: uids){
            str += uid + ",";
        }
        param.put("uids", str);
        Client.callMathod(param, "user.getInfo");
        // 将返回的结果转成UserObject对象
        return null;
    }
}
