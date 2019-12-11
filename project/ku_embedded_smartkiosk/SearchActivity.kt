package com.project.ku_embedded_smartkiosk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    var result = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        init_listener()
    }
    fun init_listener(){
        send_button.setOnClickListener {
            get_sql()
            Log.d("result",result)

        }

    }
    fun get_sql(){
        var client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
        var body = FormBody.Builder().add("name",name_edit.text.toString()).build()
        var request = Request.Builder().url("http://192.168.184.114:8000/request_sql").post(body).build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                //Log.d("Test",response.body!!.string())
                result = response.body!!.string()
                val arr = result.split('/')
                recent_text.text = arr[0]
                stamp_text.text = arr[1]

            }

        })

    }
}
