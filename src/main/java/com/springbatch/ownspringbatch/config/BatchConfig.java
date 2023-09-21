package com.springbatch.ownspringbatch.config;

import com.springbatch.ownspringbatch.dto.Visitor;
import com.springbatch.ownspringbatch.repository.VisitorRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource",transactionManagerRef = "batchTransactionManager")
public class BatchConfig {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private Visitor visitor;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ItemReader<Visitor> visitorItemReader;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("classPath:/MOCK_DATA.csv")//belli değil daha
    private Resource inputData;

    @Bean
    public Visitor visitor(){
        return new Visitor();
    }

    @Bean Job importVisitorFlight(){
        return new JobBuilder("importVisitorFlight",jobRepository)
                .start(importVisitorStep(jobRepository,visitor,transactionManager))
                .build();
    }

    private Step importVisitorStep(JobRepository jobRepository, Visitor visitor, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importVisitorStep",jobRepository)
                .<Visitor,Visitor>chunk(500,transactionManager)
                .reader(visitorItemReader)
                .processor(itemProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemProcessor<Visitor,Visitor> itemProcessor(){
        return new VisitorItemProcessor();
    }

    @Bean
    public ItemWriter<Visitor> writer(){
        return visitorRepository.saveAll();
    }

}
