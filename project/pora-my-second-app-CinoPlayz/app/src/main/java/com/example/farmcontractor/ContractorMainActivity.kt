package com.example.farmcontractor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.farmcontractor.Structs.Contract
import com.example.farmcontractor.adapters.ContractsAdapter
import com.example.farmcontractor.constants.Constants
import com.example.farmcontractor.databinding.ActivityContractorMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

class ContractorMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContractorMainBinding
    lateinit var app: FarmApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        app = application as FarmApplication
        binding = ActivityContractorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonMyWork.setOnClickListener {
            val intent = Intent(this, ContractorWorkActivity::class.java)
            startActivity(intent)
        }

        //Get all contracts
        val listOfContracts = mutableListOf<Contract>()

        AndroidNetworking.get("${Constants.url}/contract/search/contractor/${app.getUser().username}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Bearer ${app.getUser().token}")
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
            .getAsString(object : StringRequestListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(response: String?) {
                    val contracts = Json.decodeFromString<List<Contract>>(response ?: "")
                    for (contract in contracts) {
                        listOfContracts.add(contract)
                    }

                    runOnUiThread {
                        binding.recyclerViewContracts.adapter?.notifyDataSetChanged()
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError == null) {
                        Snackbar.make(
                            binding.main,
                            getString(R.string.error_generic),
                            Snackbar.LENGTH_SHORT
                        ).show()

                    } else {
                        if (anError.errorCode == 401) {
                            Snackbar.make(
                                binding.main,
                                getString(R.string.error_token),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (anError.errorDetail == "connectionError") {
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

        val contractsAdapter = ContractsAdapter(listOfContracts, { value ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Update Status?")
            alertDialog.setPositiveButton("Update") { _, _ ->
                AndroidNetworking.put("${Constants.url}/contract/active/$value")
                    .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                    .setPriority(Priority.MEDIUM)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .getAsString(object : StringRequestListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(response: String?) {
                            val index = listOfContracts.indexOfFirst { contract -> contract.id.oid == value }
                            listOfContracts[index].active = false
                            runOnUiThread {
                                binding.recyclerViewContracts.adapter?.notifyDataSetChanged()
                            }
                        }

                        override fun onError(anError: ANError?) {
                            if (anError == null) {
                                Snackbar.make(
                                    binding.main,
                                    getString(R.string.error_generic),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            } else {
                                if (anError.errorCode == 401) {
                                    Snackbar.make(
                                        binding.main,
                                        getString(R.string.error_token),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                } else if (anError.errorDetail == "connectionError") {
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
            alertDialog.setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
                dialog.dismiss()
            }

            alertDialog.show()
        }, { value ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Delete Contract")
            alertDialog.setMessage("Do you want to delete contract $value")
            alertDialog.setPositiveButton(getString(R.string.label_remove)) { _, _ ->

                AndroidNetworking.delete("${Constants.url}/contract/$value")
                    .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                    .setPriority(Priority.MEDIUM)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .getAsString(object : StringRequestListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(response: String?) {
                            listOfContracts.removeAt(listOfContracts.indexOfFirst { contract -> contract.id.oid == value })
                            runOnUiThread {
                                binding.recyclerViewContracts.adapter?.notifyDataSetChanged()
                            }
                        }

                        override fun onError(anError: ANError?) {
                            if (anError == null) {
                                Snackbar.make(
                                    binding.main,
                                    getString(R.string.error_generic),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            } else {
                                if (anError.errorCode == 401) {
                                    Snackbar.make(
                                        binding.main,
                                        getString(R.string.error_token),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                } else if (anError.errorDetail == "connectionError") {
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
            alertDialog.setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.show()
            return@ContractsAdapter true
        })

        binding.recyclerViewContracts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewContracts.adapter = contractsAdapter
        binding.recyclerViewContracts.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

    }
}