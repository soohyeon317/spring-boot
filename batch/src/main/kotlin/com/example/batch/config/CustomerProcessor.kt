package com.example.batch.config

import com.example.batch.entity.Customer
import org.springframework.batch.item.ItemProcessor

class CustomerProcessor : ItemProcessor<Customer, Customer?> {

    override fun process(customer: Customer): Customer? {
        return if (customer.country.equals("United States")) {
            customer
        } else {
            null
        }
    }
}