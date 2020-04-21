package com.chinatelecom.service;

import com.chinatelecom.vo.Member;

import java.util.Map;
import java.util.Set;

/**
 * Description: shiroProject
 * Created by leizhaoyuan on 20/2/19 下午12:55
 */
public interface IMemberService {
    /**
     * 此方法留给realm认证使用,目的是根据用户名取得密码数据
     * @param mid
     * @return
     * @throws Exception
     */
    public Member get(String mid) throws Exception;
    /**
     * 此方法是留给Realm实现授权处理的，主要要根据用户ID查询出所有的角色以及所有对应权限
     * @param mid
     * @return 返回的数据包含有两个内容：<br>
     * <li>key = allRoles、value = 所有的用户角色；</li>
     * <li>key = allActions、value = 所有的用户权限。</li>
     * @throws Exception
     */
    public Map<String,Object> listAuthByMember(String mid) throws Exception ;
}
