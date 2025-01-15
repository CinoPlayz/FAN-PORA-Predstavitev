package com.example.farmcontractor

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
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
import com.example.farmcontractor.Structs.PostWorkRequest
import com.example.farmcontractor.Structs.PutWorkRequest
import com.example.farmcontractor.Structs.Work
import com.example.farmcontractor.adapters.WorkAdapter
import com.example.farmcontractor.constants.Constants
import com.example.farmcontractor.databinding.ActivityContractorWorkBinding
import com.example.farmcontractor.databinding.DialogAddWorkBinding
import com.example.farmcontractor.databinding.DialogUpdateWorkBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

class ContractorWorkActivity : AppCompatActivity() {
    lateinit var binding: ActivityContractorWorkBinding
    lateinit var app: FarmApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        app = application as FarmApplication
        binding = ActivityContractorWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listOfWork = mutableListOf<String>()
        AndroidNetworking.get("${Constants.url}/work/${app.getUser().username}")
            .setPriority(Priority.MEDIUM)
            .addHeaders("Authorization", "Bearer ${app.getUser().token}")
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
            .getAsString(object : StringRequestListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(response: String?) {
                    val work = Json.decodeFromString<List<Work>>(response ?: "")
                    for (std in work) {
                        listOfWork.add(std.work)
                    }
                    runOnUiThread {
                        binding.recyclerViewWork.adapter?.notifyDataSetChanged()
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

        val workAdapter = WorkAdapter(listOfWork, { value ->
            val bindingDialogUpdate =
                DialogUpdateWorkBinding.inflate(LayoutInflater.from(this), null, false)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(bindingDialogUpdate.root)
            alertDialog.setTitle(getString(R.string.label_update_work))
            alertDialog.setPositiveButton(getString(R.string.label_update)) { _, _ ->
                val newValue = bindingDialogUpdate.textInputWork.text.toString()
                val json = Json.encodeToString(PutWorkRequest(newValue, value))

                AndroidNetworking.put("${Constants.url}/work")
                    .addStringBody(json)
                    .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                    .setContentType("application/json")
                    .setPriority(Priority.MEDIUM)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .getAsString(object : StringRequestListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(response: String?) {
                            listOfWork.remove(value)
                            listOfWork.add(newValue)

                            runOnUiThread {
                                binding.recyclerViewWork.adapter?.notifyDataSetChanged()
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

            bindingDialogUpdate.textInputWork.text = SpannableStringBuilder(value)

            alertDialog.show()
        }, { value ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle(getString(R.string.label_delet_work))
            alertDialog.setMessage(getString(R.string.label_delete_id, value))
            alertDialog.setPositiveButton(R.string.label_remove) { _, _ ->
                val json = Json.encodeToString(PostWorkRequest(value))

                AndroidNetworking.delete("${Constants.url}/work")
                    .addStringBody(json)
                    .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                    .setContentType("application/json")
                    .setPriority(Priority.MEDIUM)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .getAsString(object : StringRequestListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(response: String?) {
                            listOfWork.remove(value)
                            runOnUiThread {
                                binding.recyclerViewWork.adapter?.notifyDataSetChanged()
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
            return@WorkAdapter true
        })

        binding.recyclerViewWork.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewWork.adapter = workAdapter
        binding.recyclerViewWork.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.buttonAdd.setOnClickListener {
            val bindingDialogAdd =
                DialogAddWorkBinding.inflate(LayoutInflater.from(this), null, false)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(bindingDialogAdd.root)
            alertDialog.setTitle(getString(R.string.label_add_work))
            alertDialog.setPositiveButton(getString(R.string.label_add)) { _, _ ->
                val work = bindingDialogAdd.textInputWork.text.toString()
                val json = Json.encodeToString(PostWorkRequest(work))

                AndroidNetworking.post("${Constants.url}/work")
                    .addStringBody(json)
                    .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                    .setContentType("application/json")
                    .setPriority(Priority.MEDIUM)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .getAsString(object : StringRequestListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(response: String?) {
                            listOfWork.add(work)
                            runOnUiThread {
                                binding.recyclerViewWork.adapter?.notifyDataSetChanged()
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
        }
    }
}