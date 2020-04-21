package com.chinatelecom.dao;

import java.util.Set;

import com.chinatelecom.util.dao.IBaseDAO;
import com.chinatelecom.vo.Action;

public interface IActionDAO extends IBaseDAO<String, Action> {
	/**
	 * 根据雇员编号查询该雇员对应的所有权限标记（权限id）
	 * @param eid 雇员编号
	 * @return 全部权限标记信息
	 */
	public Set<String> findAllIdByEmp(String eid) ;
}
