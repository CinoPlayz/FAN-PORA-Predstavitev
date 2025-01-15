package com.example.farmcontractor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.farmcontractor.Structs.LoginRequest
import com.example.farmcontractor.Structs.User
import com.example.farmcontractor.constants.Constants
import com.example.farmcontractor.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var app: FarmApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        app = application as FarmApplication
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonLogin.setOnClickListener {
            val username = binding.TextInputName.text.toString()
            val password = binding.TextInputPassword.text.toString()
            val json = Json.encodeToString(LoginRequest(username,password))

            AndroidNetworking.post("${Constants.url}/login")
                .addStringBody(json)
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsString(object : StringRequestListener{
                    override fun onResponse(response: String?) {
                        val user = Json.decodeFromString<User>(response ?: "")
                        app.setUser(user)

                        if(user.typeOfUser == "Contractor"){
                            val intent = Intent(this@MainActivity, ContractorMainActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            val intent = Intent(this@MainActivity, FarmerMainActivity::class.java)
                            startActivity(intent)
                        }

                    }

                    override fun onError(anError: ANError?) {
                        if(anError == null){
                            Snackbar.make(
                                binding.main,
                                getString(R.string.error_generic),
                                Snackbar.LENGTH_SHORT
                            ).show()

                        } else {
                            if (anError.errorCode == 401) {
                                Snackbar.make(
                                    binding.main,
                                    "Username/password incorrect",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            else if (anError.errorDetail == "connectionError"){
                                Snackbar.make(
                                    binding.main,
                                    getString(R.string.error_connection, Constants.url),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                println(anError.errorDetail)
                                println(anError.message ?: anError.errorBody)
                            }
                        }


                    }

                })
        }
    }
}