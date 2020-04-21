package com.chinatelecom.realm;

import com.chinatelecom.service.IMemberService;
import com.chinatelecom.util.encrypt.MyPasswordEncrypt;
import com.chinatelecom.vo.Member;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Description: shiroProject
 * Created by leizhaoyuan on 20/2/18 上午1:13
 */
//@Component
public class MemberRealm extends AuthorizingRealm {
    @Autowired
    private IMemberService memberService;
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("*********1,用户登录认证处理：doGetAuthenticationInfo()");
        //1，登录认证的方法需要先执行，需要用它来判断登录的用户信息是否合法
        String username =(String) token.getPrincipal();    //取得用户输入的用户名
        //2,需要通过用户名取得用户的完整信息，利用模拟业务层的操作取得
        Member vo = null;
        try {
            vo = this.memberService.get(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vo == null) {
            throw new UnknownAccountException("用户名不存在");
        } else {    //用户名存在,进行输入密码的判断
            String password = MyPasswordEncrypt.encryptPassword(new String((char[]) token.getCredentials()));
            //将数据库中的密码与输入的密码进行比较，这样就可以确定当前用户是否可以正常登陆
            if (vo.getPassword().equals(password)) {    //用户输入密码正确
                AuthenticationInfo auth = new SimpleAuthenticationInfo(username, password, "memRealm");
                return auth;
            } else {
                throw new IncorrectCredentialsException("密码错误");
            }
        }
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("*********2,用户角色与权限授权处理：doGetAuthorizationInfo()");
        String username = (String) principals.getPrimaryPrincipal();    //取得用户登录名
        AuthorizationInfo auth = new SimpleAuthorizationInfo();    //定义授权信息的返回数据
        try {
            Map<String, Object> map = this.memberService.listAuthByMember(username);
            ((SimpleAuthorizationInfo) auth).setRoles((Set<String>) map.get("allRoles"));
            ((SimpleAuthorizationInfo) auth).setStringPermissions((Set<String>) map.get("allActions"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return auth;
    }
}
