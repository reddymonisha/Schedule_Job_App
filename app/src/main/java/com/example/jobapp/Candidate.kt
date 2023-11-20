package com.example.jobapp

class Candidate {
    var name: String? = null
    var email: String? = null
    var gender: String? = null
    var jobtitle: String? = null
    var phone: String? = null
    var source: String? = null
    var status: String? = null
    var cid: String? = null
    var intv_date: String? = null
    var intv_time: String? = null
    var isInterview_status = false

    constructor(
        name: String?,
        email: String?,
        gender: String?,
        jobtitle: String?,
        phone: String?,
        source: String?,
        status: String?,
        cid: String?,
        interview_status: Boolean
    ) {
        this.name = name
        this.email = email
        this.gender = gender
        this.jobtitle = jobtitle
        this.phone = phone
        this.source = source
        this.status = status
        this.cid = cid
        isInterview_status = interview_status
        intv_date = ""
        intv_time = ""
    }

    constructor()
    constructor(name: String?, email: String?, jobtitle: String?, phone: String?, cid: String?) {
        this.name = name
        this.email = email
        this.jobtitle = jobtitle
        this.phone = phone
        this.cid = cid
    }
}