package com.chinatelecom.task;
import com.chinatelecom.service.back.ITravelServiceBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class TravelTask {
	@Autowired
	private ITravelServiceBack travelServiceBack ;
	//@Scheduled(cron = "* * * * * ?") // 每秒一触发
	@Scheduled(cron = "0 0 0 * * ?") //每晚0点触发
	public void runJob() { // 自己定义了一个任务调度执行方法
		this.travelServiceBack.editTask() ;
	}
}
