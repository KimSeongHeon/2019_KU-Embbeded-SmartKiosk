package com.project.ku_embedded_smartkiosk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        init_listener()
    }
    fun init_listener(){
        register_button.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            //finish()
        }
        search_button.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
            //finish()

        }

    }

}
