/**
 * UC乐园Rest接口 PHP SDK - 基本工具 - 写log
 *
 * @category   logger
 * @package    com.ucweb.uzoneapiclient.utils
 * @author     Jiuhong Deng <dengjiuhong@gmail.com>
 * @version    $Id:$
 * @copyright  Jiuhong Deng
 * @link       http://u.uc.cn/
 * @since      File available since Release 1.0.0
 */
package com.ucweb.uzoneapiclient.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UzoneApiClientLogger {
    /**
     * @desc 写log的工具
     */
    private Logger logger;
    /**
     * @desc  设置当前的logger类型
     * @param String  currentTag  - 当前ogger的类别
     * @return boolean            - 是否设置成功
     */
    public UzoneApiClientLogger(String category) {
        logger   = (Logger) LoggerFactory.getLogger(category);
    }
    /**
     * @desc 记录debug日志
     */
    public void d(String Tag, String msg) {
        if (logger.isDebugEnabled()){
            logger.debug(Tag + "`" + msg);
        }
    }
    /**
     * @desc 记录错误日志
     */
    public void e(String Tag, String msg) {
        if (logger.isErrorEnabled()){
            logger.error(Tag + "`" + msg);
        }
    }
}
