package com.chinatelecom.action.back;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinatelecom.exception.DeptManagerExistException;
import com.chinatelecom.exception.LevelNotEnoughException;
import com.chinatelecom.service.back.IEmpServiceBack;
import com.chinatelecom.util.action.AbstractAction;
import com.chinatelecom.util.encrypt.PasswordUtil;
import com.chinatelecom.util.split.SplitPageUtil;
import com.chinatelecom.util.upload.FileUtils;
import com.chinatelecom.vo.Dept;
import com.chinatelecom.vo.Emp;
import com.chinatelecom.vo.Level;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/pages/back/admin/emp/*")
public class EmpActionBack extends AbstractAction {
    @Autowired
    private IEmpServiceBack empServiceBack;

	private static final String FLAG = "雇员";


    @RequestMapping("add_check")
    @RequiresUser
    @RequiresRoles("emp")
    @RequiresPermissions("emp:add")
    public ModelAndView check(HttpServletResponse response, String eid) {
        super.print(response, this.empServiceBack.getEid(eid) == null);
        return null;
    }

	@RequestMapping("add_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView addPre() {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.add.page"));
        mav.addAllObjects(this.empServiceBack.getAddPre());
		return mav;
	}

	@RequestMapping("add")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:add")
	public ModelAndView add(Emp vo, MultipartFile pic, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
        vo.setIneid(super.getEid());    //通过session取得当前操作者的雇员编号
        vo.setPassword(PasswordUtil.getPassword(vo.getPassword()));    //密码加密
        if (pic != null) {
            vo.setPhoto(super.upload(pic));
        }
        try {
            if (this.empServiceBack.add(vo)) {
                super.setUrlAndMsg(request, "emp.add.action", "vo.add.success", FLAG);
            }
            else {
                super.setUrlAndMsg(request, "emp.add.action", "vo.add.failure", FLAG);
            }
        } catch (DeptManagerExistException e) {
            super.setUrlAndMsg(request, "emp.add.action", "emp.add.dept.mgr.failure");
        }
        return mav;
	}

	@RequestMapping("edit_pre")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView editPre(String eid) {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.edit.page"));
        mav.addAllObjects(this.empServiceBack.getEditPre(eid));
		return mav;
	}

	@RequestMapping("edit")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:edit")
	public ModelAndView edit(Emp vo, MultipartFile pic, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
        FileUtils fileUtil = null;
        vo.setIneid(super.getEid());    //通过session取得当前操作者的雇员编号
        if (!(vo.getPassword() == null || "".equals(vo.getPassword()))) {    //修改密码
            vo.setPassword(PasswordUtil.getPassword(vo.getPassword()));   //密码加密
        }else {
            vo.setPassword(null); // “”字符串问题
        }
        if (!pic.isEmpty()) { // 如果说现在文件有上传
            fileUtil = new FileUtils(pic);
            // 原始图片名称为nophoto.png,则使用新生成的图片名称，
            // 不使用隐藏域传输过来的"nophoto.png"名称！！！
            if ("nophoto.png".equals(vo.getPhoto())) {
                vo.setPhoto(fileUtil.createFileName()); // 把生成的文件名称保存在VO类之中
            }
        }
        try {
            if (this.empServiceBack.edit(vo)) {
                if (fileUtil != null) { // 准备上传文件
                    //原来若有图片（非"nophoto.png"），则新上传的图片名称不变，仍然使用隐藏域传输过来的旧的图片名称，但图片内容改变
                    fileUtil.saveFile(request, "upload/member/", vo.getPhoto());
                }
                super.setUrlAndMsg(request, "emp.list.action", "vo.edit.success", FLAG);
            } else {
                super.setUrlAndMsg(request, "emp.list.action", "vo.edit.failure", FLAG);
            }
        } catch (DeptManagerExistException e) {
            super.setUrlAndMsg(request, "emp.list.action", "emp.add.dept.mgr.failure");
        } catch (LevelNotEnoughException e) {
            super.setUrlAndMsg(request, "emp.list.action", "level.not.enough.failure");
        }
        return mav;
	}

	@RequestMapping("get")
	@RequiresUser
    @RequiresRoles(value = {"emp", "empshow"}, logical = Logical.OR)
    @RequiresPermissions(value = {"emp:get", "empshow:get"}, logical = Logical.OR)
	public ModelAndView get(String eid, HttpServletResponse response) {
        Map<String, Object> map = this.empServiceBack.getDetails(eid);
        JSONObject obj = new JSONObject();
        obj.put("emp", map.get("emp"));
        obj.put("dept", map.get("dept"));
        obj.put("level", map.get("level"));
        super.print(response, obj);
		return null;
	}

	@RequestMapping("list")
	@RequiresUser
	@RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
	@RequiresPermissions(value = { "emp:list", "empshow:list" }, logical = Logical.OR)
	public ModelAndView list(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView(super.getUrl("emp.list.page"));
        SplitPageUtil spu = new SplitPageUtil(request, "雇员编号:eid|雇员姓名:ename|联系电话:phone", super.getMsg("emp.list.action"));
        Map<String, Object> map = this.empServiceBack.list(spu.getCurrentPage(), spu.getLineSize(), spu.getColumn(), spu.getKeyWord());
        mav.addAllObjects(map);    //把内容交给request属性范围
        List<Dept> allDepts = (List<Dept>) map.get("allDepts");
        List<Level> allLevels = (List<Level>) map.get("allLevels");
        Map<Long, String> deptMap = new HashMap<>();
        Iterator<Dept> iter = allDepts.iterator();
        while (iter.hasNext()) {
            Dept dept = iter.next();
            deptMap.put(dept.getDid(), dept.getDname());
        }
        Map<String, String> levelMap = new HashMap<>();
        Iterator<Level> iter2 = allLevels.iterator();
        while (iter2.hasNext()) {
            Level level = iter2.next();
            levelMap.put(level.getLid(), level.getTitle());
        }
        mav.addObject("allDepts", deptMap);    //属性名称一样会出现覆盖
        mav.addObject("allLevels", levelMap);    //属性名称一样会出现覆盖
		return mav;
	}

	@RequestMapping("delete")
	@RequiresUser
	@RequiresRoles("emp")
	@RequiresPermissions("emp:delete")
	public ModelAndView delete(String ids, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
        if (this.empServiceBack.delete(super.handleStringIds(ids),super.getEid())) {
            super.setUrlAndMsg(request, "emp.list.action", "vo.delete.success",
                    FLAG);
        } else {
            super.setUrlAndMsg(request, "emp.list.action", "vo.delete.failure",
                    FLAG);
        }
        return mav;
	}

    @Override
    public String getUploadDir() {
        return "/WEB-INF/upload/member/";
    }
}
