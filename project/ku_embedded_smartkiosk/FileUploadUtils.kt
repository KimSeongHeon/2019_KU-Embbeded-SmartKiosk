package com.project.ku_embedded_smartkiosk

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException

class FileUploadUtils {
    fun sen2Server(file:File){
        var requestBody:RequestBody = MultipartBody.Builder().
            setType(MultipartBody.FORM).
            addFormDataPart("files",file.name,RequestBody.create(MultipartBody.FORM,file)).build()
        var request = Request.Builder().url("http://172.20.10.4:8000/upload").post(requestBody).build()
        var client = OkHttpClient()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Test",response.body!!.string())
            }

        })

    }
}