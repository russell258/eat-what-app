package com.eatwhat.backend.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.eatwhat.backend.model.User;
import com.eatwhat.backend.repository.UserRepository;

@Configuration
public class UserBatchConfig {
    
    @Autowired
    private UserRepository userRepo;

    @Bean
    public Job loadUsersJob(JobRepository jobRepo, Step loadUserStep) {
        return new JobBuilder("loadUserJob", jobRepo)
            .start(loadUserStep)
            .build();
    }

    @Bean
    public Step loadUserStep(JobRepository jobRepository, PlatformTransactionManager transactionmanager,
                                ItemReader<UserCsvRecord> userItemReader,
                                ItemProcessor<UserCsvRecord, User> userItemProcessor,
                                ItemWriter<User> userItemWriter) {
        return new StepBuilder("loadUserStep", jobRepository)
            .<UserCsvRecord, User>chunk(10, transactionmanager)
            .reader(userItemReader)
            .processor(userItemProcessor)
            .writer(userItemWriter)
            .build();
    }

    @Bean
    public ItemReader<UserCsvRecord> userItemReader() {
        return new FlatFileItemReaderBuilder<UserCsvRecord>()
                .name("userItemReader")
                .resource(new ClassPathResource("users.csv"))
                .linesToSkip(1)
                .delimited()
                .names("username", "email", "role")
                .targetType(UserCsvRecord.class) 
                .build();
    }

    @Bean
    public ItemProcessor<UserCsvRecord, User> userItemProcessor(){
        return new ItemProcessor<UserCsvRecord, User>() {
            @Override
            public User process(UserCsvRecord record) throws Exception {
                //skip if user exists
                if (userRepo.existsByUsername(record.getUsername())){
                    return null;
                }

                User.UserRole role;
                try {
                    role = User.UserRole.valueOf(record.getRole().toUpperCase());
                } catch (IllegalArgumentException e){
                    role = User.UserRole.GUEST; //default role
                }

                return new User(record.getUsername(), record.getEmail(), role);
            }
        };
    }

    @Bean
    public ItemWriter<User> userItemWriter() {
        return chunk -> {
            userRepo.saveAll(chunk.getItems());
        };
    }

}
