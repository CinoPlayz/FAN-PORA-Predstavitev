package com.example.fantesting

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.androidnetworking.widget.ANImageView
import org.json.JSONArray
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textView = findViewById<TextView>(R.id.report)


        AndroidNetworking.initialize(applicationContext);

        /*AndroidNetworking.get("https://echo.hoppscotch.io")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    textView.text = response
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        textView.text = anError.errorCode.toString()
                    }
                }

            })*/



        AndroidNetworking.get("http://localhost:8080/work/{name}")
            .addPathParameter("name", "Nejc")
            .addHeaders("Authorization", "Bearer pwKu08iZNRO2xSyPpRv0fd0m4nGtXRwq0768sLH0nna0Fo7Y")
            .setPriority(Priority.LOW)
            .setExecutor(Executors.newSingleThreadExecutor())
            .setTag("Test")
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    runOnUiThread {
                        textView.text = response.toString()
                    }
                }

                override fun onError(error: ANError) {
                    runOnUiThread {
                        textView.text = error.errorCode.toString()
                    }
                }
            })

        AndroidNetworking.cancel("Test");
        AndroidNetworking.cancelAll();

        AndroidNetworking.get("https://dog.ceo/api/breeds/list/all")
            .setExecutor(Executors.newSingleThreadExecutor())
            .build()
            .prefetch()

        AndroidNetworking.get("https://dog.ceo/api/breeds/list/all")
            .setExecutor(Executors.newSingleThreadExecutor())
            .responseOnlyIfCached
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    runOnUiThread {
                        //textView.text = response
                    }
                }

                override fun onError(anError: ANError?) {
                    if (anError != null) {
                        runOnUiThread {
                            textView.text = anError.errorCode.toString()
                        }
                    }
                }

            })


        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val button = findViewById<Button>(R.id.button)

        val pathDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

        button.setOnClickListener {
            AndroidNetworking.download("https://shorturl.at/epMVt", pathDir, "image.png")
                .setPriority(Priority.MEDIUM)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .setDownloadProgressListener(object: DownloadProgressListener{
                    override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                        println("$bytesDownloaded / $totalBytes")
                        runOnUiThread {
                            progressBar.progress = ((bytesDownloaded * 100 )/ totalBytes).toInt()
                        }
                    }
                })
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        println("Download completed")
                    }

                    override fun onError(error: ANError?) {
                        if (error != null) {
                            println(error.message.toString())
                        }
                    }
                })
        }

        val imageView = findViewById<ANImageView>(R.id.imageView)
        imageView.setDefaultImageResId(R.drawable.default_icon)
        imageView.setErrorImageResId(R.drawable.error)
        imageView.setImageUrl("https://shorturl.at/epMVt")




    }
}