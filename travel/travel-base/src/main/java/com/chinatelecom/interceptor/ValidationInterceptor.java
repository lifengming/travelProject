package com.chinatelecom.interceptor;

import com.chinatelecom.util.bean.BaseUriUtil;
import com.chinatelecom.util.bean.MessageUtil;
import com.chinatelecom.util.validate.ActionValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: spring-project
 * Created by leizhaoyuan on 20/2/3 上午11:25
 */
public  class ValidationInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageSource messageSource;    //直接注入资源信息读取Bean
    //设置一个日志操作对象
    private Logger logger = LoggerFactory.getLogger(ValidationInterceptor.class);

    @Override
    //Action执行之前进行拦截控制，如果要进行数据验证，一定要在此方法内完成
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.err.println(request.getClass());
//        this.logger.info("1,preHandle方法," + handler.getClass());
        if (handler instanceof HandlerMethod) {    //是否为HandlerMethod类实例
            HandlerMethod handlerMethod = (HandlerMethod) handler;    //获取handlerMethod对象实例
//            this.logger.info("【Action对象】" + handlerMethod.getBean());
//            this.logger.info("【Action类型】" + handlerMethod.getBeanType());
//            this.logger.info("【Action方法】" + handlerMethod.getMethod());
            String validationKey = handlerMethod.getBeanType().getName() + "." + handlerMethod.getMethod().getName();
            this.logger.info("[验证规则KEY]" + validationKey);
            String validationRules = MessageUtil.getMessage(validationKey, this.messageSource);
            if (validationRules == null || "".equals(validationRules)) {
                return true;    //不需要进行具体的验证处理
            } else {
                this.logger.info("[验证规则VALUE]" + validationRules);
                //实例化具体验证规则的处理类，实例化该类对象，将自动进行规则的验证处理
                ActionValidationUtil avu = new ActionValidationUtil(request, this.messageSource, validationRules);
                if (avu.getErrors().size() > 0) {  //此时产生了具体错误信息
                    request.setAttribute("errors", avu.getErrors());    //保存错误信息
                    String errorPage = MessageUtil.getMessage(validationKey + ".error.page", this.messageSource);    //保存跳转的错误页面
                    if (errorPage == null || "".equals(errorPage)) {
                        errorPage = MessageUtil.getMessage("error.page", this.messageSource);
                    }
                    if ("POST".equalsIgnoreCase(request.getMethod())) {    //判断请求模式
                        response.sendRedirect(BaseUriUtil.getBasePath() + errorPage);

                    } else {
                        request.getRequestDispatcher(errorPage).forward(request, response);
                    }

                    return false;    //不能继续向后执行，拦截器拦截
                } else {    //当前代码没有任何错误，应该继续向后执行
                    return true;
                }
            }
        }
        return true;    //若此处为false,则不会执行Action的调用操作及后续操作！请注意！
    }

//    @Override
//    //在Action处理完成之后，此时还未通过DispatcherServlet返回调用此操作
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        this.logger.info("2,postHandle方法," + handler.getClass());
//    }
//
//    @Override
//    //全部处理完成，整个的控制层代码全部执行完毕
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        this.logger.info("3,afterCompletion方法," + handler.getClass());
//    }
}
