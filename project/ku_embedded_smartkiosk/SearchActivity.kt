package com.project.ku_embedded_smartkiosk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }
    fun get_sql(){
        var sb = StringBuffer();
        sb.append("select recent from user where name = "+name_edit.text)

    }
}
