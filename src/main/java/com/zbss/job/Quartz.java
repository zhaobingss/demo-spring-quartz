package com.zbss.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

/**
 * @Desc
 * @Auther zbss
 * @Date 2017-09-28 13:59
 */
@Component("quartz")
public class Quartz {

	private Scheduler scheduler;	// 任务调度器

	/**
	 * 初始化调度器
	 */
	@PostConstruct
	public void init(){
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			initJob();
			scheduler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化调度任务
	 * @throws Exception
	 */
	private void initJob() throws Exception {
		Properties jobConf = getProperties("job.properties");
		List<String> jobs = distinctJob(jobConf);
		for (String jobKey : jobs){
			schduleJob(jobKey, jobConf);
		}
	}


	/**
	 * 添加调度任务
	 * @param jobKey
	 */
	private void schduleJob(String jobKey, Properties jobConf) throws Exception {
		String className = jobConf.getProperty(jobKey+".class");
		String group = jobConf.getProperty(jobKey+".group");
		String name = jobConf.getProperty(jobKey+".name");
		String cron = jobConf.getProperty(jobKey+".cron");
		String triggerGroup = jobConf.getProperty(jobKey+".trigger.group");
		String triggerName = jobConf.getProperty(jobKey+".trigger.name");
		Class jobClass = Class.forName(className);
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).withSchedule(cronScheduleBuilder).build();

		scheduler.scheduleJob(job, trigger);
	}

	/**
	 * 获取所有的jobkey
	 * @param p
	 * @return
	 */
	private List<String> distinctJob(Properties p){
		List<String> jobs = new ArrayList<>();
		Iterator iter = p.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			String keys[] = key.split("\\.");
			if (keys[0].contains("job")){
				if (!jobs.contains(keys[0])){
					jobs.add(keys[0]);
				}
			}
		}
		return jobs;
	}

	/**
	 * 加载配置文件
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private  Properties getProperties(String filePath) throws Exception{
		Properties p = new Properties();
		p.load(this.getClass().getClassLoader().getResourceAsStream(filePath));
		return p;
	}

	/**
	 * 销毁调度器
	 */
	@PreDestroy
	public void destroy(){
		try {
			scheduler.shutdown(false);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取调度器便于增加或者删除任务
	 * @return
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}
}
