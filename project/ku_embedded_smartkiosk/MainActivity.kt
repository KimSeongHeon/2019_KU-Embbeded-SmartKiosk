package com.project.ku_embedded_smartkiosk

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.buffer
import okio.source
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var fileuploadutils = FileUploadUtils()
    val WRITE_REQUEST = 1234
    lateinit var datauri:Uri
    lateinit var fd:AssetFileDescriptor
    lateinit var temp_file:File
    var name = "";var phone = "";var like = "";var hate="";var blind = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init_listener()
        init_Permission()
    }
    fun init_Permission(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))){
            Toast.makeText(this,"권한이 승인됨",Toast.LENGTH_SHORT).show()
        }
        else{
            askPermission(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),WRITE_REQUEST)
        }
    }
    fun checkAppPermission(requestPermission: Array<String>): Boolean
    {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult. indices ) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                this,
                requestPermission[i] )== PackageManager. PERMISSION_GRANTED
            if (!requestResult[i]) {
                return false
            }
        }
        return true

    }
    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int)
    {
        ActivityCompat.requestPermissions( this, requestPermission, REQ_PERMISSION ) //requerst Permmision이라는 함수...
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) { //위의 정보에 따라 이게 호출
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            WRITE_REQUEST->{
                if(checkAppPermission(permissions)){
                    Toast.makeText(applicationContext,"권한이 승인됨",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"권한이 승인 안됨",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
    fun send2server_data(){
        var client = OkHttpClient.Builder()
            .connectTimeout(10,TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build()
        var body = FormBody.Builder().add("name",name)
            .add("phone",phone).add("like",like).add("hate",hate)
            .add("blind",blind).build()
        var request = Request.Builder().url("http://192.168.0.3:8000/upload_data").post(body).build()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Test",response.body!!.string())
                fd.close()

            }

        })
    }


    fun init_listener(){
        send_button.setOnClickListener {
            //fileuploadutils.sen2Server(temp_file)
            name = name_edit_text.text.toString()
            phone = phone_edit_text.text.toString()
            var vidieoFile = object: RequestBody() {
                override fun contentLength(): Long {
                    return fd.declaredLength
                }
                override fun contentType(): MediaType? {
                    return contentResolver.getType(datauri).toMediaTypeOrNull()
                }

                override fun writeTo(sink: BufferedSink)  {
                    var input:InputStream = fd.createInputStream()
                    sink.writeAll(input.source().buffer())
                }

            } // 보낼 VideoFile Request로 보내기위하여 세팅

            var date = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Date()) // 비디오의 이름 설정
            var client = OkHttpClient.Builder()
                .connectTimeout(10,TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build() //타임아웃 설정
            var requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("files",date+".mp4",vidieoFile).build() //확장자와 file 설정
            var request = Request.Builder().url("http://192.168.0.3:8000/upload"). // 서버에 Post를 보냄
                post(requestBody).build()
            client.newCall(request).enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) { //만약 동영상 데이터가 보내져서 리스폰스가 왔으면
                    send2server_data() // 사용자 데이터를 보냄냄                    Log.d("Test",response.body!!.string())
                    fd.close()

                }

            })
            finish()

        }
        btnImageSelection.setOnClickListener {
            var intent = Intent()
            intent.setType("video/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,1)
        }
        cancel_button.setOnClickListener {
            finish()
        }
        like_spinner.onItemSelectedListener = (object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                like = parent!!.getItemAtPosition(position).toString()
            }

        })
        hate_spinner.onItemSelectedListener = (object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                hate = parent!!.getItemAtPosition(position).toString()
            }

        })
        yes_spinner.onItemSelectedListener = (object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                blind = parent!!.getItemAtPosition(position).toString()
            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if(requestCode != 1 || resultCode != Activity.RESULT_OK){
            return;
        }
        datauri = data!!.data
        fd = contentResolver.openAssetFileDescriptor(datauri,"r")
        if(fd == null){
            throw  FileNotFoundException("could not open file descriptor")
        }
        video_info_text.setText("동영상 선택이 완료 되었습니다.")
        btnImageSelection.visibility = View.INVISIBLE
        /*try{
            //ImageView에 이미지 출력
            var inputstream:InputStream = contentResolver.openInputStream(datauri)
            var image = BitmapFactory.decodeStream(inputstream)
            imgVwSelected.setImageBitmap(image)
            inputstream.close()
            //선택한 이미지 임시 저장
            var date = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Date())
            temp_file = File(Environment.getExternalStorageDirectory().absolutePath+File.separator+ "temp_" + date + ".jpeg")
            temp_file.createNewFile()
            var out = FileOutputStream(temp_file)
            image.compress(Bitmap.CompressFormat.JPEG,100,out)
            out.close()

        }catch (ex:IOException){
            ex.printStackTrace()
        }*/
    }
}
