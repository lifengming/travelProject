package com.chinatelecom.vo;

import java.io.Serializable;

/**
 * Description: mybatis-project
 * Created by leizhaoyuan on 20/2/8 下午3:50
 */
public class News implements Serializable {
    private Long nid ;
    private String title ;
    private String content ;

    public Long getNid() {
        return nid;
    }

    public void setNid(Long nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "News{" +
                "nid=" + nid +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
