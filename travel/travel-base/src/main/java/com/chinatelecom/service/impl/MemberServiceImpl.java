package com.chinatelecom.service.impl;

import com.chinatelecom.dao.IMemberDAO;
import com.chinatelecom.service.IMemberService;
import com.chinatelecom.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: shiroProject
 * Created by leizhaoyuan on 20/2/19 下午1:09
 */
@Service
public class MemberServiceImpl implements IMemberService {
    @Autowired
    private IMemberDAO memberDAO;
    @Override
    public Member get(String mid) throws Exception {
        return this.memberDAO.findById(mid);
    }

    @Override
    public Map<String, Object> listAuthByMember(String mid) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("allRoles", this.memberDAO.findAllRoleByMember(mid));
        map.put("allActions", this.memberDAO.findAllActionByMember(mid));
        return map;
    }

}
