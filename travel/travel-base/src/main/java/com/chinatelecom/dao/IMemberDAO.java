package com.chinatelecom.dao;

import com.chinatelecom.vo.Member;

import java.util.Set;

/**
 * Description: shiroProject
 * Created by leizhaoyuan on 20/2/19 下午12:33
 */
public interface IMemberDAO {
        public Member findById(String mid) ;
        public Set<String> findAllRoleByMember(String mid) ;
        public Set<String> findAllActionByMember(String mid) ;

}
