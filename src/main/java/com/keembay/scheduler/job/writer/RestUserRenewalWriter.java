package com.keembay.scheduler.job.writer;

import com.keembay.scheduler.dto.ArchiveNotificationDTO;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;

public class RestUserRenewalWriter implements ItemWriter<ArchiveNotificationDTO> {

    @Autowired
    private DataSource dataSource;

    @Override
    public void write(List<? extends ArchiveNotificationDTO> item) throws Exception {
        JdbcBatchItemWriter jdbcBatchItemWriter = itemWriter();
        jdbcBatchItemWriter.write(item);
    }

    public JdbcBatchItemWriter<ArchiveNotificationDTO> itemWriter() {
        JdbcBatchItemWriter<ArchiveNotificationDTO> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("INSERT INTO ARCHIVE_Notification VALUES (null, :createdOn, :email," +
                " :expiryDate,:messageType, :mobile, :sentMessage, :userName)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

}

