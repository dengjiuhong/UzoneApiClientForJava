/**
 * UC乐园Rest接口 Java SDK - 数据对象
 *
 * @category   schema
 * @package    com.ucweb.uzoneapiclient
 * @author     Jiuhong Deng <dengjiuhong@gmail.com>
 * @version    $Id:$
 * @copyright  Jiuhong Deng
 * @link       http://u.uc.cn/
 * @since      File available since Release 1.0.0
 */
package com.ucweb.uzoneapiclient.schema;
public class UserObject {
    /**
     * @desc 用户名字
     */
    public String RealName;
    /**
     * @desc 用户头像地址
     */
    public String Avatar;
    /**
     * @desc 用户状态
     */
    public String Mood;
    /**
     * @desc 用户uid
     */
    public String Uid;
    /**
     * @desc 用户性别
     */
    public int sex;
}
