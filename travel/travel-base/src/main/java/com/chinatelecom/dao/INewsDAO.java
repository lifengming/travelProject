package com.chinatelecom.dao;

import com.chinatelecom.vo.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;
import java.util.Map;

/**
 * Description: mybatis-project
 * Created by leizhaoyuan on 20/2/12 上午11:54
 */
public interface INewsDAO {
//    使用注解这种方式开发，此时需要取消掉IewsDAO 与 News.xml文件的关联,
//    即News.xml中的命名空间名称不能与INewsDAO的路径相同！
//    @Insert("INSERT INTO news (title,content) VALUES (#{title},#{content})")
//    @SelectKey(before = false, keyProperty = "nid", keyColumn = "nid", resultType = java.lang.Long.class, statement = "SELECT LAST_INSERT_ID()")

    public boolean doCreate(News vo);

    public boolean doEdit(News vo);

    public boolean doRemoveById(Long id);

    public News findByTitle(String title);


//    @Select("SELECT nid,title,content FROM news WHERE nid=#{nid}")

    public News findById(Long id);

    public News findByIdAndTitle(Map<String, Object> params);

    /**
     * 进行数据分页显示的处理程序
     * @param params 可以传递的参数内容如下:<br>
     *               1,key=column,value=要进行数据查询的列<br>
     *               2,key=keyWord,value=模糊查询的关键字<br>
     *               3,key=start,value=当前所在的行<br>
     *               4,key=lineSize,value=每页显示行数<br>
     *               如果此时没有传递column,keyWord，则表示对全部数据进行分页<br>
     * @return 数据集合
     */
//    @Select("SELECT nid,title,content FROM news WHERE ${column} LIKE #{keyWord} LIMIT #{start},#{lineSize}")

    public List<News> findSplit(Map<String, Object> params);

    /**
     * 数据个数统计处理
     * @param params 可以传递的参数内容如下:<br>
     *               1,key=column,value=要进行数据查询的列<br>
     *               2,key=keyWord,value=模糊查询的关键字<br>
     *               如果此时没有传递column,keyWord，则表示统计全部数据个数<br>
     * @return 返回数据行的个数
     */
//    @Select("SELECT COUNT(*) FROM news WHERE ${column} LIKE #{keyWord}")

    public Long getAllCount(Map<String, Object> params);

}
