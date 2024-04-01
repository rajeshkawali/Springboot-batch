package com.rajeshkawali.config;

import com.rajeshkawali.entity.Customer;
import com.rajeshkawali.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Rajesh_Kawali
 *
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final CustomerRepository customerRepository;

	@Bean
	public FlatFileItemReader<Customer> customerReader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "surname", "smoothiePreference", "mobileNumber");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	public CustomerProcessor customerProcessor() {
		return new CustomerProcessor();
	}

	@Bean
	public RepositoryItemWriter<Customer> customerWriter() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step step1() {
		return new StepBuilder("csvImport", jobRepository)
				.<Customer, Customer>chunk(1000, platformTransactionManager)
				.reader(customerReader())
				.processor(customerProcessor())
				.writer(customerWriter())
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public Job job(JobRepository jobRepository) {
		return new JobBuilder("customers-import", jobRepository)
				.start(step1())
				//.next(step2()) // If you have more step's then configure in next method.
				//.next(step3()) // If you have more step's then configure in next method.
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}
	
	/*
	In the Conditional Flow example :
	The step1 is successful, and the next step2 should be executed.
	The step1 failed, and, the next step3 should be executed.

	@Bean
	public Job job() {
	return new JobBuilder("csvImport", jobRepository)
				.start(step1())
				.on("*").to(step2())
				.from(step1()).on("FAILED").to(step3())
				.end()
				.build();
	}
	// following values: COMPLETED, STARTING, STARTED, STOPPING, STOPPED, FAILED, ABANDONED, or UNKNOWN.
	// "*" : matches zero or more characters
	*/
}