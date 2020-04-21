package com.chinatelecom.action.back;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinatelecom.service.back.ITravelServiceBack;
import com.chinatelecom.util.action.AbstractAction;
import com.chinatelecom.util.split.SplitPageUtil;
import com.chinatelecom.vo.*;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import net.sf.json.JSONObject;

@Controller
@RequestMapping("/pages/back/admin/travel/*")
public class TravelActionBack extends AbstractAction {
	private static final String FLAG = "出差申请";
	@Autowired
	private ITravelServiceBack travelServiceBack;
	@RequestMapping("add_pre")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:add"}, logical = Logical.OR)
	public ModelAndView addPre() {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.add.page"));
		mav.addAllObjects(this.travelServiceBack.addPre());
		return mav;
	}

	@RequestMapping("add")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:add"}, logical = Logical.OR)
	public ModelAndView add(HttpServletRequest request, Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid()); // 当前登录的id就是该出差单的提交者
		if (this.travelServiceBack.add(vo)) {
			super.setUrlAndMsg(request, "travel.add.action", "vo.add.success",
					FLAG);
		} else {
			super.setUrlAndMsg(request, "travel.add.action", "vo.add.failure",
					FLAG);
		}
		return mav;
	}
	
	@RequestMapping("show_emp")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:self"}, logical = Logical.OR)
	public ModelAndView showEmp(HttpServletResponse response,long tid) {
		JSONObject obj = new JSONObject() ;
		obj.putAll(this.travelServiceBack.getTravelEmp(tid));
		super.print(response, obj);
		return null ;
	}

	@RequestMapping("list_emp")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:self"}, logical = Logical.OR)
	public ModelAndView listEmp(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.emp.page"));
		SplitPageUtil aspu = new SplitPageUtil(request,
				"申请标题:title", super.getMsg("travel.list.action"));
		mav.addAllObjects(this.travelServiceBack.listByEmp(super.getEid(),
				aspu.getCurrentPage(), aspu.getLineSize(), aspu.getColumn(),
				aspu.getKeyWord()));
		return mav;
	}

	@RequestMapping("list_self")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:self"}, logical = Logical.OR)
	public ModelAndView listSelf(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.self.page"));
        SplitPageUtil aspu = new SplitPageUtil(request,
				"申请标题:title", super.getMsg("travel.self.action"));
		mav.addAllObjects(this.travelServiceBack.listSelf(super.getEid(),
				aspu.getCurrentPage(), aspu.getLineSize(), aspu.getColumn(),
				aspu.getKeyWord()));
		return mav;
	}

	@RequestMapping("delete_emp")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView deleteEmp(HttpServletResponse response, TravelEmp vo) {
		JSONObject obj = new JSONObject();
		obj.put("status", this.travelServiceBack.deleteTravelEmp(vo));
		super.print(response, obj);
		return null;
	}

    /**
     * 将选中的出差人员添加到travel_emp数据表中，模态中显示的选中数据向travel_user_edit.jsp中列表添加，
     * @param response
     * @param vo
     * @return
     */
	@RequestMapping("add_emp")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView addEmp(HttpServletResponse response, TravelEmp vo) {
		Map<String, Object> map = this.travelServiceBack.addTravelEmp(vo);
		JSONObject obj = new JSONObject();
		obj.putAll(map);
		super.print(response, obj);
		return null;
	}
    //模态分页数据显示,modal-body部分
	@RequestMapping("emp_dept")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView listDept(HttpServletRequest request,
			HttpServletResponse response, long did, long tid) {
		JSONObject obj = new JSONObject();
        SplitPageUtil aspu = new SplitPageUtil(request, "", "");
		Map<String, Object> map = this.travelServiceBack.listByDept(tid, did,
				aspu.getCurrentPage(), aspu.getLineSize(), aspu.getColumn(),
				aspu.getKeyWord());
		obj.put("allRecorders", map.get("allRecorders"));
		obj.put("allEmps", map.get("allEmps"));
		super.print(response, obj);
		return null;
	}

	//负责模态modal-header部门信息回填和提交者所在部门的自动选择(selected)
    //进行travel_user_edit.jsp列表信息的回填
	@RequestMapping("user_edit_pre")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView editUser(long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.user.page"));
		Map<String, Object> map = this.travelServiceBack.listEmp(tid);
		mav.addAllObjects(map);
		// 为了方便进行部门信息的显示将部门信息由List集合变为Map集合
		List<Dept> allDepts = (List<Dept>) map.get("allDepts");
		Map<Long, String> deptMap = new HashMap<Long, String>();
		Iterator<Dept> iterDept = allDepts.iterator();
		while (iterDept.hasNext()) {
			Dept dept = iterDept.next();
			deptMap.put(dept.getDid(), dept.getDname());
		}
		List<Level> allLevels = (List<Level>) map.get("allLevels");
		Map<String, String> levelMap = new HashMap<String, String>();
		Iterator<Level> iter2 = allLevels.iterator();
		while (iter2.hasNext()) {
			Level lev = iter2.next();
			levelMap.put(lev.getLid(), lev.getTitle());
		}
		mav.addObject("allLevels", levelMap); // 属性名称一样会出现覆盖
		mav.addObject("allDepts", deptMap);
		return mav;
	}

	@RequestMapping("delete_cost")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView deleteCost(HttpServletResponse response, long tcid) {
		super.print(response, this.travelServiceBack.deleteCost(tcid));
		return null;
	}

	@RequestMapping("add_cost")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView addCost(HttpServletResponse response, TravelCost vo) {
		JSONObject obj = new JSONObject();
		obj.putAll(this.travelServiceBack.addCost(vo));
		super.print(response, obj);
		return null;
	}

	@RequestMapping("cost_edit_pre")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView editCost(long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.cost.page"));
		Map<String, Object> map = this.travelServiceBack.listCost(tid);
		mav.addAllObjects(map);
		List<Type> allTypes = (List<Type>) map.get("allTypes");
		Iterator<Type> iter = allTypes.iterator();
		Map<Long, String> typeMap = new HashMap<Long, String>();
		while (iter.hasNext()) {
			Type type = iter.next();
			typeMap.put(type.getTpid(), type.getTitle());
		}
		mav.addObject("allTypes", typeMap);
		return mav;
	}

	@RequestMapping("edit_pre")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView editPre(Long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("travel.edit.page"));
		mav.addAllObjects(this.travelServiceBack.editPre(tid));
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:edit"}, logical = Logical.OR)
	public ModelAndView edit(HttpServletRequest request, Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid());
		if (this.travelServiceBack.edit(vo)) {
			super.setUrlAndMsg(request, "travel.self.action", "vo.edit.success",
					FLAG);
		} else {
			super.setUrlAndMsg(request, "travel.self.action", "vo.edit.failure",
					FLAG);
		}
		return mav;
	}

	@RequestMapping("delete")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:delete"}, logical = Logical.OR)
	public ModelAndView delete(HttpServletRequest request, Travel vo) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		vo.setSeid(super.getEid());
		if (this.travelServiceBack.delete(vo)) {
			super.setUrlAndMsg(request, "travel.self.action",
					"vo.delete.success", FLAG);
		} else {
			super.setUrlAndMsg(request, "travel.self.action",
					"vo.delete.failure", FLAG);
		}
		return mav;
	}

	@RequestMapping("submit")
	@RequiresUser
	@RequiresRoles(value = {"travel"}, logical = Logical.OR)
	@RequiresPermissions(value = {"travel:submit"}, logical = Logical.OR)
	public ModelAndView submit(HttpServletRequest request, long tid) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		if (this.travelServiceBack.editSubmit(tid)) {
			super.setUrlAndMsg(request, "travel.self.action",
					"travel.submit.success");
		} else {
			super.setUrlAndMsg(request, "travel.self.action",
					"travel.submit.failure");
		}
		return mav;
	}
}
