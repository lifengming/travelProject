package com.chinatelecom.service.back.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.chinatelecom.dao.IDeptDAO;
import com.chinatelecom.dao.IEmpDAO;
import com.chinatelecom.service.back.IDeptServiceBack;
import com.chinatelecom.service.back.abs.AbstractService;
import com.chinatelecom.vo.Dept;
import com.chinatelecom.vo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeptServiceBackImpl extends AbstractService
		implements
        IDeptServiceBack {
	@Autowired
	private IDeptDAO deptDAO;
	@Autowired
	private IEmpDAO empDAO;

    @Override
    public boolean editLevel(Long did,String ineid) {
        // 1、先根据部门编号查询出该部门的已有数据信息，这样可以得到该部门的领导编号
        Dept dept = this.deptDAO.findById(did) ;
        Emp emp = new Emp() ;
        emp.setEid(dept.getEid()); 	// 取得原始的部门的领导编号
        if (dept != null) {	// 现在已经查询到了部门的信息，意味着可以得到了部门的领导信息
            // 2、判断当前的处理用户的信息是否为经理
            Emp mgr = this.empDAO.findById(ineid) ;	// 取得修改者的信息
            if ("manager".equals(mgr.getLid())) {	// 当前操作者的身份是经理
                dept.setEid(null); 	// 取消掉已有的部门领导信息
                if (this.deptDAO.doUpdateManager(dept)) {	// 3、更新部门的领导信息
                    // 4、更新雇员的信息，主要是进行级别的变更
                    emp.setLid("staff");	// 领导设置为普通员工
                    emp.setIneid(ineid);	// 当前修改此雇员信息的领导项
                    return this.empDAO.doUpdateLevel(emp) ;
                }
            }
        }
        return false;
    }

    @Override
	public Map<String, Object> list() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allDepts", this.deptDAO.findAll()) ;
		map.put("allEmps", this.empDAO.findAllManager()) ;
		return map;
	}

    @Override
    public boolean edit(Dept vo) {
        return this.deptDAO.doUpdate(vo);
    }

}
