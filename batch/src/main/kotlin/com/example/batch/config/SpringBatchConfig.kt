package com.example.batch.config

import com.example.batch.entity.Customer
import com.example.batch.repository.CustomerRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.LineMapper
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor

@Configuration
@EnableBatchProcessing
class SpringBatchConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val customerRepository: CustomerRepository
) {

    @Bean
    fun reader(): FlatFileItemReader<Customer> {
        val itemReader: FlatFileItemReader<Customer> = FlatFileItemReader<Customer>()
        itemReader.setResource(FileSystemResource("batch/src/main/resources/customers.csv"))
        itemReader.setName("csvReader")
        itemReader.setLinesToSkip(1)
        itemReader.setLineMapper(lineMapper())
        return itemReader
    }

    private fun lineMapper(): LineMapper<Customer> {
        val lineMapper: DefaultLineMapper<Customer> = DefaultLineMapper<Customer>()
        val lineTokenizer = DelimitedLineTokenizer()
        lineTokenizer.setDelimiter(",")
        lineTokenizer.setStrict(false)
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob")
        val fieldSetMapper: BeanWrapperFieldSetMapper<Customer> = BeanWrapperFieldSetMapper<Customer>()
        fieldSetMapper.setTargetType(Customer::class.java)
        lineMapper.setLineTokenizer(lineTokenizer)
        lineMapper.setFieldSetMapper(fieldSetMapper)
        return lineMapper
    }

    @Bean
    fun processor(): CustomerProcessor {
        return CustomerProcessor()
    }

    @Bean
    fun writer(): RepositoryItemWriter<Customer> {
        val writer: RepositoryItemWriter<Customer> = RepositoryItemWriter<Customer>()
        writer.setRepository(customerRepository)
        writer.setMethodName("save")
        return writer
    }

    @Bean
    fun step1(): Step {
        return stepBuilderFactory["csv-step"].chunk<Customer, Customer>(10)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .taskExecutor(taskExecutor())
            .build()
    }

    @Bean
    fun runJob(): Job {
        return jobBuilderFactory["importCustomers"]
            .flow(step1()).end().build()
    }

    @Bean
    fun taskExecutor(): TaskExecutor {
        val asyncTaskExecutor = SimpleAsyncTaskExecutor()
        asyncTaskExecutor.concurrencyLimit = 10
        return asyncTaskExecutor
    }
}