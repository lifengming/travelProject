package com.chinatelecom.service.back;

import com.chinatelecom.exception.DeptManagerExistException;
import com.chinatelecom.exception.LevelNotEnoughException;
import com.chinatelecom.vo.Emp;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

import java.util.Map;
import java.util.Set;

public interface IEmpServiceBack {
    /**
     * 进行雇员信息的删除处理，该删除操作要执行如下步骤：
     * 1、首先要求依次判断所有要删除的员工数据是否具备有领导级别的信息，如果存在有领导级别的信息则要求员工先进行降级，而后再更新部门领导；
     * 2、每一次的更新只需要调用doUpdateLocked()方法就可以进行标志位的操作处理
     * @param eids 要更新的全部雇员编号
     * @param heid 要操作此功能的当前雇员编号
     * @return 删除成功返回true
     */
    @RequiresRoles("emp")
    @RequiresPermissions("emp:delete")
    public boolean delete(Set<String> eids,String heid) ;
    /**
     * 实现雇员信息的追加，该方法需要执行如下的操作<br>
     * 1,根据增加的雇员级别情况，判断能否增加该雇员，若该部门已有经理，又新增经理，则该经理无法添加、
     * 应该抛出异常<br>
     * 2,判断当前操作者的级别是否为经理，若是经理，才可以进行经理的相关处理<br>
     * 3,进行雇员信息的保存<br>
     * @param vo 包含有要修改的雇员的信息
     * @return 增加成功返回true, 重名或者失败返回false
     * @throws DeptManagerExistException 若该部门已有经理，则无法新增经理，抛出此异常
     * @throws LevelNotEnoughException 级别不够，无法处理该操作
     */
    @RequiresRoles("emp")
    @RequiresPermissions("emp:edit")
    public boolean edit(Emp vo) throws DeptManagerExistException, LevelNotEnoughException;


    /**
     * 进行雇员追加前的信息查询处理,该方法执行如下操作<br>
     *     1,利用IDpetDAO.findAll(),查询全部的部门信息<br>
     *         2,1,利用ILevelDAO.findAll(),查询全部的级别信息<br>
     *             3,利用IEmpDAO.findById(),取得要修改的雇员信息<br>
     * @param eid 要修改的雇员编号
     * @return 返回如下的数据内容<br>
     *     1,key=allDepts,value=全部部门信息<br>
     *         2,key=allLevels,value=全部级别信息<br>
     *             3,key=emp,value=原始雇员信息<br>
     */
    @RequiresRoles("emp")
    @RequiresPermissions("emp:edit")
    public Map<String, Object> getEditPre(String eid);

    /**
     * 进行全部雇员信息的数据列表显示处理， 该处理要执行如下操作：<br>
     * 1、调用ILevelDAO.findAll()方法取得全部的级别信息；<br>
     * 2、调用IDeptDAO.findAll()方法取得全部部门信息；<br>
     * 3、调用IEmpDAO.findAllSplit()方法进行全部雇员数据的分页查询；<br>
     * 4、调用IEmpDAO.getAllCount()方法进行全部雇员数量的查询。<br>
     * @param currentPage 当前页
     * @param lineSize 每页显示行
     * @param column 模糊查询列
     * @param keyWord 关键字
     * @return 包含有如下的数据组成：<br>
     * 1、key = allDepts、value = 全部部门信息；<br>
     * 2、key = allLevels、value = 全部的级别信息；<br>
     * 3、key = allEmps、value = 全部雇员信息；<br>
     * 4、key = allRecorders、value = 雇员总量。
     */
    @RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
    @RequiresPermissions(value = { "emp:list", "empshow:list" }, logical = Logical.OR)
    public Map<String, Object> list(Long currentPage, Integer lineSize, String column, String keyWord);
    /**
     * 调用EmpDAO.findById()根据eid查询雇员信息
     * @param eid 雇员编号
     * @return 如果有雇员返回对象，没有返回空
     */
    public Emp getEid(String eid);

    /**
     * 实现雇员信息的追加，该方法需要执行如下的操作<br>
     *     1,要判断当前追加的雇员编号信息是否存在，存在则无法添加<br>
     *         2,根据增加的雇员级别情况，判断能否增加该雇员，若该部门已有经理，又新增经理，则该经理无法添加、
     *         应该抛出异常<br>
     *             3,判断当前操作者的级别是否为经理，若是经理，才可以进行经理的相关处理<br>
     *                 4,需要将雇员的雇用日期设置为今天的日期<br>
     *                     5,进行雇员信息的保存<br>
     * @param vo 包含有新雇员的信息
     * @return 增加成功返回true,重名或者失败返回false
     * @throws DeptManagerExistException 若该部门已有经理，则无法新增经理，抛出此异常
     */
    @RequiresRoles("emp")
    @RequiresPermissions("emp:add")
    public boolean add(Emp vo) throws DeptManagerExistException;

    /**
     * 进行雇员追加前的信息查询处理,该方法执行如下操作<br>
     *     1,利用IDpetDAO.findAll(),查询全部的部门信息<br>
     *         2,1,利用ILevelDAO.findAll(),查询全部的级别信息<br>
     * @return 返回如下的数据内容<br>
     *     1,key=allDepts,value=全部部门信息<br>
     *         2,key=allLevels,value=全部级别信息<br>
     */
    @RequiresRoles("emp")
    @RequiresPermissions("emp:add")
    public Map<String, Object> getAddPre();


    /**
     * 查看一个雇员的完整信息，包括雇员的基本信息，以及所在部门信息
     * @param eid 雇员编号
     * @return 返回信息包含如下内容：<br>
     *     1,key=emp,value=雇员的详细信息<br>
     *         2,key=dept,value=部门的详细信息<br>
     *             3,key=level,value=雇员级别信息<br>
     */
    @RequiresRoles(value = { "emp", "empshow" }, logical = Logical.OR)
    @RequiresPermissions(value = { "emp:edit", "empshow:get" }, logical = Logical.OR)
    public Map<String,Object> getDetails(String eid);

	/**
	 * 根据雇员id获得雇员完整信息
	 * @param eid 雇员编号
	 * @return 包括如下内容：<br>
	 * 1、key = emp、value = 雇员对象，如果该雇员不存在返回null<br>
	 * 2、如果雇员信息可以查询成功，则查询对应级别信息。key = level
	 */
	public Map<String,Object> get(String eid, String password) ;
	/**
	 * 根据雇员编号取得雇员对应的角色与权限数据信息，该操作主要执行如下功能：<br>
	 * 1、调用IRoleDAO.findAllIdByEmp()查询出所有的角色标记信息；<br>
	 * 2、调用IActionDAO.findAllIdByEmp()查询出所有的权限标记信息；<br>
	 * @param eid 要查询角色和权限的雇员编号
	 * @return 返回Map集合数据包含有如下内容：<br>
	 * 1、key = allRoles、value = 所有的角色标记信息；<br>
	 * 2、key = allActions、value = 所有的权限标记信息；<br>
	 */
	public Map<String,Set<String>> listRoleAndAction(String eid) ;
}
