package com.zbss.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Desc
 * @Auther zbss
 * @Date 2017-09-28 14:06
 */
public class Job01 implements Job {
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("this is job01");
	}
}
