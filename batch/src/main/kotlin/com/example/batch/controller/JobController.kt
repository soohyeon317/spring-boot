package com.example.batch.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobs")
class JobController(
    private val jobLauncher: JobLauncher,
    private val job: Job
) {

    @PostMapping("/import-customers")
    fun importCsvToDBJob() {
        val jobParameters = JobParametersBuilder()
            .addLong("startAt", System.currentTimeMillis()).toJobParameters()
        try {
            jobLauncher.run(job, jobParameters)
        } catch (e: JobExecutionAlreadyRunningException) {
            e.printStackTrace()
        } catch (e: JobRestartException) {
            e.printStackTrace()
        } catch (e: JobInstanceAlreadyCompleteException) {
            e.printStackTrace()
        } catch (e: JobParametersInvalidException) {
            e.printStackTrace()
        }
    }
}