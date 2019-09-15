package com.example.cleartree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_test_node_detail.*

class TestNodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_node_detail)

        val json=intent.getStringExtra(VariableNames.NODE.name)
        val node= Gson().fromJson(json,MutableMap::class.java)
        val obj=JsonParser().parse(json) as JsonObject
        val title=(obj.get("value") as JsonObject).get("str").toString()
        val children=Gson().toJson(obj.get("children") as JsonArray)
        val isExpanded=obj.get("isExpanded")

        titleTextView.setText(title)
        detailTextView.setText("str:$title\n\n" +
                "children:$children\n\n" +
                "expanded:$isExpanded")
    }
}
