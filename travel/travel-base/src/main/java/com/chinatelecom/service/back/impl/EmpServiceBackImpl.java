package com.chinatelecom.service.back.impl;

import java.util.*;


import com.chinatelecom.dao.*;
import com.chinatelecom.exception.DeptManagerExistException;
import com.chinatelecom.exception.LevelNotEnoughException;
import com.chinatelecom.service.back.IEmpServiceBack;
import com.chinatelecom.service.back.abs.AbstractService;
import com.chinatelecom.vo.Dept;
import com.chinatelecom.vo.Emp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class    EmpServiceBackImpl extends AbstractService implements IEmpServiceBack {
	@Autowired
	private IEmpDAO empDAO;
	@Autowired
    private IDeptDAO deptDAO;
	@Autowired
	private IRoleDAO roleDAO;
	@Autowired
	private IActionDAO actionDAO;
	@Autowired
	private ILevelDAO levelDAO;

    @Override
    public boolean delete(Set<String> eids, String heid) {
        // 取得当前的操作者的雇员完整信息
        Emp humanEmp = this.empDAO.findById(heid);
        // 1、查询出所有要删除的雇员信息
        List<Emp> allEmp = this.empDAO.findAllByIds(eids.toArray());
        // 2、遍历出所有要进行删除的数据信息，检查是否存在有领导信息
        Iterator<Emp> iter = allEmp.iterator();
        while (iter.hasNext()) {
            Emp emp = iter.next();
            emp.setIneid(heid); 	// 设置操作者的eid信息
            if ("manager".equals(emp.getLid())) { // 要删除的对象为领导
                if ("manager".equals(humanEmp.getLid())) {    //当前操作者为领导
                    Dept dept = new Dept();
                    dept.setDid(emp.getDid()); // 当前雇员所在的部门
                    emp.setLid("staff");
                    if (this.empDAO.doUpdateLevel(emp)) { // 先进行领导讲解
                        if (this.deptDAO.doUpdateManager(dept)) { // 更新部门的领导信息
                            emp.setLocked(2); // 逻辑删除位
                            return this.empDAO.doUpdateLocked(emp);
                        }
                    }
                }
            } else if ("chief".equals(emp.getLid())) {
                continue;   //退出此循环
            } else {
                emp.setLocked(2); // 逻辑删除位
                return this.empDAO.doUpdateLocked(emp);
            }
        }
        return false;
    }

    @Override
    public boolean edit(Emp vo) throws DeptManagerExistException,LevelNotEnoughException {
        // 1、取得当前操作者的信息，主要的目的是为了判断它的级别
        Emp humanEmp = this.empDAO.findById(vo.getIneid());
        // 如果现在要修改的雇员信息原本为经理，而且当前的修改者的级别不是经理，那么不能够修改
        Emp oldEmp = this.empDAO.findById(vo.getEid()); // 取得原始数据信息
        if ("manager".equals(oldEmp.getLid())) { // 如果其原本是一个经理
            if (!"manager".equals(humanEmp.getLid())) { // 修改者不是经理
                // 应该抛出一个异常，表示没有足够的级别
                throw new LevelNotEnoughException("您不具备有该级别用户的操作权限！");
            }
            // 如果现在发现部门没有改变，但是现在其领导的级别变为了员工
            if ("staff".equals(vo.getLid())
                    || oldEmp.getDid().equals(vo.getDid())) { // 原始为领导，现在为员工
                Dept newDept = new Dept();
                newDept.setDid(oldEmp.getDid()); // 原始部门编号
                this.deptDAO.doUpdateManager(newDept); // 删除原始的部门的领导的编号
                return this.empDAO.doUpdate(vo); // 直接修改员工信息
            }
            // 现在要更换部门领导，就需要判断要更换目标部门是否存在有领导
            if ("manager".equals(vo.getLid())
                    || (!oldEmp.getDid().equals(vo.getDid()))) {
                Dept dDept = this.deptDAO.findById(vo.getDid());
                if (dDept.getEid() != null) { // 要更换的部门存在有领导
                    // 应该抛出一个异常，表示没有足够的级别
                    throw new LevelNotEnoughException("您不具备有该级别用户的操作权限！");
                } else { // 如果现在该部门没有领导
                    dDept.setEid(vo.getEid()); // 设置为新的部门领导编号
                    if (this.deptDAO.doUpdateManager(dDept)) {
                        return this.empDAO.doUpdate(vo);
                    }
                }
            }
        } else { // 如果现在是之前的用户级别为普通员工
            if ("manager".equals(humanEmp.getLid())) { // 操作者级别为manager
                if ("staff".equals(vo.getLid())) {
                    return this.empDAO.doUpdate(vo);
                } else {
                    Dept dept = this.deptDAO.findById(vo.getDid()); // 判断要处理的部门信息
                    if (dept.getEid() == null) { // 该部门现在没有经理
                        dept.setEid(vo.getEid()); // 新雇员为部门经理
                        if (this.deptDAO.doUpdateManager(dept)) { // 没有更新领导
                            return this.empDAO.doUpdate(vo);
                        }
                    } else {
                        throw new DeptManagerExistException(
                                "该部门已经有经理了，无法进行新任经理的添加！");
                    }
                }
            }  else {
                // 应该抛出一个异常，表示没有足够的级别
                throw new LevelNotEnoughException("您不具备有该级别用户的操作权限！");
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> getEditPre(String eid) {
        Map<String, Object> map = new HashMap<>();
        map.put("allDepts", this.deptDAO.findAll());
        map.put("allLevels", this.levelDAO.findAll());
        map.put("emp", this.empDAO.findById(eid));
        return map;
    }

    @Override
    public Map<String, Object> list(Long currentPage, Integer lineSize, String column, String keyWord) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> param = super.handleParam(currentPage, lineSize, column, keyWord);
        map.put("allLevels", this.levelDAO.findAll());
        map.put("allDepts", this.deptDAO.findAll());
        map.put("allEmps", this.empDAO.findAllSplit(param));
        map.put("allRecorders", this.empDAO.getAllCount(param));
        return map;
    }

    @Override
    public Emp getEid(String eid) {
        return this.empDAO.findById(eid);
    }

    @Override
    public boolean add(Emp vo) throws DeptManagerExistException {
        //1,要判断当前追加的雇员编号信息是否存在，存在则无法添加
        if (this.empDAO.findById(vo.getEid()) == null) {
            vo.setHiredate(new Date());    //雇用日期设置为当前日期
            vo.setLocked(0);    //新增用户默认为活跃状态
            //2判断当前操作者的级别，及所在的部门信息,需要先取得操作者的雇员信息
            Emp humanEmp = this.empDAO.findById(vo.getIneid());
            //3，判断当前操作者的部门是否是人事部门
            if (humanEmp.getDid().equals(2L)) {
                //4判断当前要追加的雇员的级别是否为普通员工
                if ("staff".equals(vo.getLid())) {
                    return this.empDAO.doCreate(vo);
                } else {
                    //5,追加的雇员是经理或总监
                    //6，判断当前操作者是否为manager
                    if ("manager".equals(humanEmp.getLid())) {
                        //7,判断当前要追加的部门是否在已存在经理
                        Dept dept = this.deptDAO.findById(vo.getDid());
                        //8,当前部门不存在经理
                        if (dept.getEid() == null) {
                            if (this.empDAO.doCreate(vo)) {
                                dept.setEid(vo.getEid());    //新雇员保存为部门经理
                                return this.deptDAO.doUpdateManager(dept);
                            }
                            //9.当前部门存在经理
                        } else {
                            throw new DeptManagerExistException("该部门已经有经理了，无法进行新任经理的添加");
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public Map<String, Object> getAddPre() {
        Map<String, Object> map = new HashMap<>();
        map.put("allDepts", this.deptDAO.findAll());
        map.put("allLevels", this.levelDAO.findAll());
        return map;
    }

    @Override
	public Map<String, Object> get(String eid, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		Emp emp = this.empDAO.findById(eid);
		if (emp != null) {
			if (password.equals(emp.getPassword())) {
				map.put("level", this.levelDAO.findById(emp.getLid()));
			}
		}
		map.put("emp", emp);
		return map;
	}

	@Override
	public Map<String, Set<String>> listRoleAndAction(String eid) {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		map.put("allRoles", this.roleDAO.findAllIdByEmp(eid));
		map.put("allActions", this.actionDAO.findAllIdByEmp(eid));
		return map;
	}

    @Override
    public Map<String, Object> getDetails(String eid) {
        Map<String, Object> map = new HashMap<>();
        Emp emp = this.empDAO.findById(eid);
        if (emp != null) {
            map.put("dept", this.deptDAO.findById(emp.getDid()));
            map.put("level", this.levelDAO.findById(emp.getLid()));
        }
        map.put("emp", emp);
        return map;
    }
}
