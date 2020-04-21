package com.chinatelecom.dao;

import java.util.Set;


import com.chinatelecom.util.dao.IBaseDAO;
import com.chinatelecom.vo.Role;

public interface IRoleDAO extends IBaseDAO<String, Role> {
	/**
	 * 根据雇员编号查询所有的角色编号（角色id）
	 * @param eid 雇员id
	 * @return 所有的角色标记信息
	 */
	public Set<String> findAllIdByEmp(String eid) ;
}
