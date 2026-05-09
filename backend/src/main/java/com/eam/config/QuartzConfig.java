package com.eam.config;

import com.eam.job.MaintenancePlanJob;
import com.eam.job.StockWarningJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz定时任务配置
 */
@Configuration
public class QuartzConfig {

    /**
     * 维护计划检查任务
     * 每天凌晨2点执行
     */
    @Bean
    public JobDetail maintenancePlanJobDetail() {
        return JobBuilder.newJob(MaintenancePlanJob.class)
                .withIdentity("maintenancePlanJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger maintenancePlanJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 2 * * ?")
                .withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
                .forJob(maintenancePlanJobDetail())
                .withIdentity("maintenancePlanTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 库存预警检查任务
     * 每天上午9点和下午3点执行
     */
    @Bean
    public JobDetail stockWarningJobDetail() {
        return JobBuilder.newJob(StockWarningJob.class)
                .withIdentity("stockWarningJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger stockWarningJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 9,15 * * ?")
                .withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
                .forJob(stockWarningJobDetail())
                .withIdentity("stockWarningTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
