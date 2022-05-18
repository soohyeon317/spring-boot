package com.example.batch.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "customers_info")
data class Customer(

    @Id
    @Column(name = "customer_id")
    var id: Int = 0,

    @Column(name = "first_name")
    var firstName: String?,

    @Column(name = "last_name")
    var lastName: String?,

    @Column(name = "email")
    var email: String?,

    @Column(name = "gender")
    var gender: String?,

    @Column(name = "contact")
    var contactNo: String?,

    @Column(name = "country")
    var country: String?,

    @Column(name = "dob")
    var dob: String?
)