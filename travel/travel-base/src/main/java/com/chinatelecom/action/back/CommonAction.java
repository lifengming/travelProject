package com.chinatelecom.action.back;

import com.chinatelecom.util.action.AbstractAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: travelProject
 * Created by leizhaoyuan on 20/2/29 上午2:40
 */
@Controller
public class CommonAction extends AbstractAction {
    @RequestMapping("/errors")
    public String errors() {
        return "errors";
    }
}
