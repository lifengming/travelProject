package com.chinatelecom.util.validate;

import com.chinatelecom.util.bean.MessageUtil;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
/**
 * 实现Action的数据验证处理，该验证处理应该基于指定验证规则完成验证操作
 * 所有的验证失败的信息项都通过Map集合进行存储（key为参数名称、value为错误信息）
 */
public class ActionValidationUtil {
    private HttpServletRequest request ;    // 根据验证规则获取参数内容
    private String rule ; // 获取具体的验证规则信息
    private MessageSource messageSource ; // 获取具体的错误提示信息
    // 保存具体的错误信息，其中key为参数的名称，value为message资源中的属性定义
    private Map<String,String> errors = new HashMap<>() ;
    /**
     * 实例化一个具体验证规则实现类
     * @param request 接收request用于参数接收
     * @param messageSource 进行错误信息的获取
     * @param rule 具体的验证规则（不为空）
     */
    public ActionValidationUtil(HttpServletRequest request,MessageSource messageSource , String rule) {
        this.request = request ;
        this.messageSource = messageSource ;
        this.rule = rule ;
        this.handleValidation(); // 构造完成后直接进行验证处理
    }
    private void handleValidation() {   // 进行具体的验证规则的操作
        String ruleResults [] = this.rule.split("\\|") ; // 拆分验证规则
        for (int x = 0 ; x < ruleResults.length ; x ++) {   // 循环获取每一组验证规则
            try {
                // 第一个索引的内容为参数名称，第二个索引的内容为验证规则
                String rules [] = ruleResults[x].split(":") ;
                switch (rules[1]) {
                    case "string": {
                        if (!this.isString(rules[0])) { // 验证失败
                            this.errors.put(rules[0], MessageUtil.getMessage("validation.error.string",this.messageSource)) ;
                        }
                        break;
                    }
                    case "int": {
                        if (!this.isIntAndLong(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.int",this.messageSource)) ;
                        }
                        break;
                    }
                    case "long": {
                        if (!this.isIntAndLong(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.long",this.messageSource)) ;
                        }
                        break;
                    }
                    case "double": {
                        if (!this.isDouble(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.double",this.messageSource)) ;
                        }
                        break;
                    }
                    case "boolean": {
                        if (!this.isBoolean(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.boolean",this.messageSource)) ;
                        }
                        break;
                    }
                    case "date": {
                        if (!this.isDate(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.date",this.messageSource)) ;
                        }
                        break;
                    }
                    case "datetime": {
                        if (!this.isDatetime(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.datetime",this.messageSource)) ;
                        }
                        break;
                    }
                    case "rand": {
                        if (!this.isRand(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.rand",this.messageSource)) ;
                        }
                        break;
                    }
                    case "int[]": {
                        if (!this.isArrayIntAndLong(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.int[]",this.messageSource)) ;
                        }
                        break;
                    }
                    case "long[]": {
                        if (!this.isArrayIntAndLong(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.long[]",this.messageSource)) ;
                        }
                        break;
                    }
                    case "string[]": {
                        if (!this.isArrayString(rules[0])) { // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.string[]",this.messageSource)) ;
                        }
                        break;
                    }
                    case "image" : {    // 应该调用相应的验证规则
                        if(!this.isImage(rules[0])) {   // 验证失败
                            this.errors.put(rules[0],MessageUtil.getMessage("validation.error.image",this.messageSource)) ;
                        }
                        break ;
                    }
                }
            } catch (Exception e) {}
        }
    }
    /**
     * 判断给定的参数内容是否满足于特定的上传文件的MIME信息定义
     * @param paramName 参数名称
     * @return 如果在指定的MIME范围内则返回true，否则返回false
     */
    private boolean isImage(String paramName) {
        String rule = MessageUtil.getMessage("validation.mime.image",this.messageSource) ;
        if (rule == null) { // 不需要进行验证，那么直接返回true
            return true ;
        }
        if (!(this.request instanceof DefaultMultipartHttpServletRequest)) {    // 当前表单未封装
            return true ; // 不需要进行验证
        } else {    // 现在表单已经被封装了
            // 必须进行HttpServletRequest接口的强制转型，只有通过子类才可以获取到所有的上传文件的信息
            DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) this.request ;
            // 获取全部的上传二进制文件对象，key = 表单参数、value = 上传包装文件“MultipartFile”
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();// 获取全部的上传文件
            MultipartFile file = fileMap.get(paramName) ;   // 获取指定参数名称对应的上传文件
            if (file != null && file.getSize() > 0) { // 现在有上传文件
                return rule.contains(file.getContentType()) ;
            }
        }
        return true ;
    }
    /**
     * 判断给定的字符串内容是否为空
     * @param paramName 要验证的参数名称
     * @return 如果为空则表示错误返回false，如果不为空返回true
     */
    private boolean isString(String paramName) { // 进行字符串数据验证
        String paramValue = this.request.getParameter(paramName) ;
        if (paramValue == null || "".equals(paramValue)) {
            return false ;
        }
        return true ;
    }
    /**
     * 验证给定的参数内容是否为合格的字符串数组操作
     * @param paramName 参数名称
     * @return 如果给定的参数不为空，则直接返回true
     */
    public boolean isArrayString(String paramName) {
        String paramValues [] = this.request.getParameterValues(paramName) ;
        return paramValues != null ;
    }
    /**
     * 验证具体参数的内容是否为整型或者是长整型
     * @param paramName 参数名称
     * @return 如果是数字，那么就可以直接返回true，否则返回false
     */
    public boolean isIntAndLong(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName);
            return paramValue.matches("\\d+");
        }
        return false ;
    }
    /**
     * 判断给定的参数是否为一个合格的整型数组
     * @param paramName 参数名称
     * @return 如果数组有数字所组成，那么就返回true
     */
    public boolean isArrayIntAndLong(String paramName) {
        String paramValues [] = this.request.getParameterValues(paramName) ;
        if (paramValues == null) {
            return false ;
        } else {
            for (int x = 0 ; x < paramValues.length ; x ++) {
                if (!paramValues[x].matches("\\d+")) {
                    return false ;
                }
            }
            return true ;
        }
    }
    /**
     * 验证操作是否为小数
     * @param paramName 参数名称
     * @return 如果是小数则直接返回true，否则返回false
     */
    public boolean isDouble(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName);
            return paramValue.matches("\\d+(\\.\\d+)?");
        }
        return false ;
    }
    /**
     * 验证参数类型是否为布尔型（on、1、yes、up、true都为布尔真值）
     * @param paramName 参数名称
     * @return 如果是指定范围的布尔，那么就直接返回true，否则返回就是false
     */
    public boolean isBoolean(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName);
            if ("on".equalsIgnoreCase(paramValue) ||
                    "1".equalsIgnoreCase(paramValue) ||
                    "up".equalsIgnoreCase(paramValue) ||
                    "yes".equalsIgnoreCase(paramValue) ||
                    "true".equalsIgnoreCase(paramValue)) {
                return true;
            }
        }
        return false ;
    }
    /**
     * 判断给定的参数的内容是否为日期类型（yyyy-MM-dd）
     * @param paramName 参数名称
     * @return 如果为日期类型返回true，否则返回false
     */
    public boolean isDate(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName);
            return paramValue.matches("\\d{4}-\\d{2}-\\d{2}") ;
        }
        return false ;
    }
    /**
     * 判断给定的参数的内容是否为日期时间类型（yyyy-MM-dd HH:mm:ss）
     * @param paramName 参数名称
     * @return 如果为日期时间类型返回true，否则返回false
     */
    public boolean isDatetime(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName);
            return paramValue.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ;
        }
        return false ;
    }
    /**
     * 进行验证码的处理判断
     * @param paramName 参数名称
     * @return 如果输入的验证码与生成的相同则直接返回true，否则返回false
     */
    public boolean isRand(String paramName) {
        if (this.isString(paramName)) {
            String paramValue = this.request.getParameter(paramName) ;
            String rand = (String) this.request.getSession().getAttribute("rand") ;
            return paramValue.equalsIgnoreCase(rand) ;
        }
        return false ;
    }
    /**
     * 返回包含有全部错误信息的Map集合
     * @return 如果没有任何的错误出现，则Map的长度为0，不为null
     */
    public Map<String, String> getErrors()  {
        return errors;
    }
}
