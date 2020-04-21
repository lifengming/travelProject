package com.chinatelecom.dao;

import com.chinatelecom.util.dao.IBaseDAO;
import com.chinatelecom.vo.Dept;

public interface IDeptDAO extends IBaseDAO<Long, Dept> {
    /**
     * 更新一个部门的领导信息
     * @param vo
     * @return 更新成功返回true
     */
    public boolean doUpdateManager(Dept vo);

}
