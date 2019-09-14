package com.example.cleartree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.f3401pal.checkabletreeview.SingleRecyclerViewImpl
import com.f3401pal.checkabletreeview.StringNode
import com.f3401pal.checkabletreeview.TreeNodeFactory

class MainActivity : AppCompatActivity() {
    private lateinit var treeView: SingleRecyclerViewImpl<StringNode>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        treeView = findViewById<SingleRecyclerViewImpl<StringNode>>(R.id.treeView)
        val root= TreeNodeFactory.buildTestTree()
        treeView.setRoots(listOf(root))
    }
}
