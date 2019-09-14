package com.example.cleartree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.f3401pal.checkabletreeview.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var treeView: SingleRecyclerViewImpl<Node>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        treeView = findViewById(R.id.treeView)
        var root= TreeNodeFactory.buildTestTree()

        val t=TreeNode(TestNode("test",2),root.children.get(1))
        root.children.get(1).children.add(t)

        treeView.setRoots(mutableListOf(root))


        button.setOnClickListener {
            val t=TreeNode(TestNode("test",2),root.children.get(1))
            root.children.get(1).children.add(t)
            treeView.setRoots(mutableListOf(root))
        }
    }
}
