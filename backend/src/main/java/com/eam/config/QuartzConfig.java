package com.eam.config;

import com.eam.job.MaintenancePlanJob;
import com.eam.job.StockWarningJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz定时任务配置
 * Task 12.1: 配置MaintenancePlanJob执行时间（每日执行）
 * Task 12.2: 配置StockWarningJob执行时间（每小时检查）
 */
@Configuration
public class QuartzConfig {

    // 定时任务执行时间配置
    @Value("${maintenance.job.hour:02}")
    private int maintenanceJobHour;

    @Value("${maintenance.job.minute:00}")
    private int maintenanceJobMinute;

    @Value("${stock.warning.job.interval:3600}")
    private int stockWarningJobInterval;

    /**
     * 维护计划检查任务
     * 每天凌晨2点执行，根据配置可调整
     */
    @Bean
    public JobDetail maintenancePlanJobDetail() {
        return JobBuilder.newJob(MaintenancePlanJob.class)
                .withIdentity("maintenancePlanJob")
                .storeDurably()
                .build();
    }

    /**
     * 维护计划任务触发器
     * Task 12.1.1: 在QuartzConfig中配置JobDetail
     * Task 12.1.2: 配置每日执行的时间
     * Task 12.1.3: 设置定时任务的参数
     * Task 12.1.4: 配置任务触发器
     */
    @Bean
    public Trigger maintenancePlanJobTrigger() {
        // 构建Cron表达式：秒 分 时 日 月 周 年
        String cronExpression = String.format("0 %d %d * * ?",
                                      maintenanceJobMinute, maintenanceJobHour);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        return TriggerBuilder.newTrigger()
                .forJob(maintenancePlanJobDetail())
                .withIdentity("maintenancePlanTrigger")
                .withDescription("维护计划检查任务-每日执行")
                .withSchedule(scheduleBuilder)
                .withMisfireHandlingInstruction(MisfireHandlingInstruction.FIRE_AND_PROCEED)
                .build();
    }

    /**
     * 库存预警检查任务
     * 每小时检查一次，根据配置可调整间隔
     * Task 12.2: 配置StockWarningJob执行时间（每小时检查）
     */
    @Bean
    public JobDetail stockWarningJobDetail() {
        return JobBuilder.newJob(StockWarningJob.class)
                .withIdentity("stockWarningJob")
                .storeDurably()
                .build();
    }

    /**
     * 库存预警任务触发器
     * Task 12.2.1: 在QuartzConfig中配置JobDetail
     * Task 12.2.2: 配置每小时执行
     * Task 12.2.3: 设置库存预警阈值
     * Task 12.2.4: 配置任务触发器
     */
    @Bean
    public Trigger stockWarningJobTrigger() {
        // 配置每小时的简单Cron表达式
        // "0 0 * * * ?" 表示每小时的0分0秒执行
        String cronExpression = "0 0 * * * ?";

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

        return TriggerBuilder.newTrigger()
                .forJob(stockWarningJobDetail())
                .withIdentity("stockWarningTrigger")
                .withDescription("库存预警检查任务-每小时执行")
                .withSchedule(scheduleBuilder)
                .withMisfireHandlingInstruction(MisfireHandlingInstruction.FIRE_AND_PROCEED)
                .build();
    }

    /**
     * 库存预警任务手动触发接口（用于测试）
     */
    public JobDetail stockWarningJobManualDetail() {
        return JobBuilder.newJob(StockWarningJob.class)
                .withIdentity("stockWarningJobManual")
                .storeDurably(false)
                .build();
    }

    /**
     * 库存预警任务手动触发Trigger（用于测试）
     */
    public Trigger stockWarningJobManualTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(stockWarningJobManualDetail())
                .withIdentity("stockWarningManualTrigger")
                .startNow()
                .build();
    }
}
