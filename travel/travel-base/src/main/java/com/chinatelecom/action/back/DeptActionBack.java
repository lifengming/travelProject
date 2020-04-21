package com.chinatelecom.action.back;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.chinatelecom.service.back.IDeptServiceBack;
import com.chinatelecom.util.action.AbstractAction;
import com.chinatelecom.vo.Dept;
import com.chinatelecom.vo.Emp;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/pages/back/admin/dept/*")
public class DeptActionBack extends AbstractAction {
	@Autowired
	private IDeptServiceBack deptServiceBack ;

    @RequestMapping("mgr")
    @RequiresRoles(value = {"emp"}, logical = Logical.OR)
    @RequiresPermissions(value = {"dept:edit","emp:edit"}, logical = Logical.AND)
    public ModelAndView editMgr(HttpServletResponse response,Long did) {
        super.print(response,
                this.deptServiceBack.editLevel(did, super.getEid()));
        return null;
    }

	
	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "dept:list", "deptshow:list" }, logical = Logical.OR)
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView(super.getUrl("dept.list.page"));
		Map<String,Object> map = this.deptServiceBack.list() ;
		mav.addObject("allDepts", map.get("allDepts")) ;	// 保存所有的部门信息
		List<Emp> allEmps = (List<Emp>) map.get("allEmps") ; // 取得所有的部门领导信息
		Map<String,String> empMap = new HashMap<String,String>() ;
		Iterator<Emp> iter = allEmps.iterator() ;
		while (iter.hasNext()) {
			Emp emp = iter.next() ;
			empMap.put(emp.getEid(), emp.getEname()) ;
		}
		mav.addObject("empMap", empMap) ;
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("dept:edit")
	public ModelAndView edit(HttpServletResponse response, Dept vo) {
        super.print(response, this.deptServiceBack.edit(vo));
		return null;
	}
}
