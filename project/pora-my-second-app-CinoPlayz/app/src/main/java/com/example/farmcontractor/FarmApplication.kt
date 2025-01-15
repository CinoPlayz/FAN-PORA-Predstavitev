package com.example.farmcontractor

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.example.farmcontractor.Structs.User


class FarmApplication: Application() {
    private var user: User = User()
    override fun onCreate() {

        AndroidNetworking.initialize(applicationContext)
        super.onCreate()
    }

    fun setUser(user: User){
        this.user = user
    }

    fun getUser(): User{
        return user
    }
}