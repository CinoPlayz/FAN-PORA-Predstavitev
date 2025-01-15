package com.example.farmcontractor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.farmcontractor.Structs.PostContractRequest
import com.example.farmcontractor.Structs.Work
import com.example.farmcontractor.adapters.ContractorAdapter
import com.example.farmcontractor.constants.Constants
import com.example.farmcontractor.databinding.ActivityFarmerMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.Executors

class FarmerMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFarmerMainBinding
    lateinit var app: FarmApplication
    lateinit var mMap: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        app = application as FarmApplication
        binding = ActivityFarmerMainBinding.inflate(layoutInflater)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var selectedLocLat = 0.0
        var selectedLocLong = 0.0

        /*var currentLocLat = 0.0
        var currentLocLong = 0.0
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val permLocCour = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val permLocFine = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if(permLocCour == PackageManager.PERMISSION_GRANTED && permLocFine == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, object: LocationListener{
                override fun onLocationChanged(location: Location) {
                    currentLocLat = location.latitude
                    currentLocLong = location.longitude
                }
            })
        }*/



        getInstance().load(this, sharedPref)
        getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME


        mMap = binding.map
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())
        val controller = mMap.controller
        controller.setZoom(6.0)

        val firstMarker = Marker(mMap)
        firstMarker.position = mMap.mapCenter as GeoPoint
        firstMarker.setDefaultIcon()
        mMap.overlays.add(firstMarker)

        val tapOverly = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    selectedLocLat = p.latitude
                    selectedLocLong = p.longitude
                }

                firstMarker.position = p
                mMap.invalidate()
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }

        })
        mMap.overlays.add(tapOverly)

        val listOfContractors = mutableListOf<String>()

        binding.textInputWork.doOnTextChanged { text, start, before, count ->
            AndroidNetworking.cancel("getContractors")
            listOfContractors.clear()

            AndroidNetworking.get("${Constants.url}/work/search/$text")
                .setPriority(Priority.MEDIUM)
                .setTag("getContractors")
                .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsString(object : StringRequestListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: String?) {
                        val contractors = Json.decodeFromString<List<Work>>(response ?: "")
                        for (std in contractors) {
                            listOfContractors.add(std.username)
                        }
                        runOnUiThread {
                            binding.recyclerViewPickContractor.adapter?.notifyDataSetChanged()
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


        var selectedContractor = ""
        binding.recyclerViewPickContractor.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPickContractor.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerViewPickContractor.adapter = ContractorAdapter(listOfContractors, { value ->
            selectedContractor = value
        }, { _ -> return@ContractorAdapter true })


        binding.buttonAddContract.setOnClickListener {
            val textWork = binding.textInputWork.text.toString()
            val json = Json.encodeToString(PostContractRequest(textWork, selectedContractor, selectedLocLat, selectedLocLong))

            AndroidNetworking.post("${Constants.url}/contract")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Bearer ${app.getUser().token}")
                .setContentType("application/json")
                .addStringBody(json)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsString(object : StringRequestListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: String?) {
                        Snackbar.make(
                            binding.main,
                            getString(R.string.success_contract),
                            Snackbar.LENGTH_SHORT
                        ).show()
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

    }
}