package com.eam.job;

import com.eam.entity.SparePart;
import com.eam.service.ISparePartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存预警定时任务
 * 定期检查备件库存，低于预警值时发送通知
 */
@Component
public class StockWarningJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(StockWarningJob.class);

    @Autowired
    private ISparePartService sparePartService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("开始执行库存预警定时任务...");

        // 获取所有库存预警的备件
        List<SparePart> warningParts = sparePartService.listWarning();

        log.info("找到 {} 个库存预警的备件", warningParts.size());

        for (SparePart part : warningParts) {
            try {
                sendWarningNotification(part);
            } catch (Exception e) {
                log.error("发送备件 {} 库存预警通知失败", part.getId(), e);
            }
        }

        log.info("库存预警定时任务执行完成");
    }

    /**
     * 发送库存预警通知
     * 这里可以实现邮件、短信或系统消息通知
     */
    private void sendWarningNotification(SparePart part) {
        // TODO: 实现具体的通知方式（邮件、短信、系统消息等）
        log.warn("库存预警 - 备件: {}, 当前库存: {}, 最低库存: {}",
                part.getPartName(),
                part.getQuantity(),
                part.getMinQuantity());

        // 示例：可以在这里调用消息服务发送通知
        // messageService.sendStockWarning(part);
    }
}
