package com.example.cleartree

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.f3401pal.checkabletreeview.*
import com.google.gson.GsonBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var treeView: SingleRecyclerViewImpl<Node>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        treeView = findViewById(R.id.treeView)


        var root= TreeNodeFactory.buildTestTree()
        val t=TreeNode(TestNode("test",2),root.children.get(1))
        root.children.get(1).children.add(t)
//        val clist=TreeNode(QuickCreateNode())
        val croot=TreeNode(QuickCreateNode(),root)
        root.children.add(croot)
        val cleft=TreeNode(QuickCreateNode(),root.children[0])
        root.children[0].children.add(cleft)
        val cright=TreeNode(QuickCreateNode(),root.children[1])
        root.children[1].children.add(cright)
        val cl3l=TreeNode(QuickCreateNode(),root.children[0].children[0])
        root.children[0].children[0].children.add(cl3l)
        val cl3r=TreeNode(QuickCreateNode(),root.children[0].children[1])
        root.children[0].children[1].children.add(cl3r)
//        val list= mutableListOf(root,clist as TreeNode<Node>)
        val list= mutableListOf(root)
        treeView.setRoots(list)

        treeView.setItemOnClick { treeNode, viewHolder ->
//            Log.d(TAG,"test")
//            when(treeNode.value) {
//                is TestNode -> {
                    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    val intent = Intent(this, TestNodeDetailActivity::class.java).apply {
                        putExtra(VariableNames.NODE.name, gson.toJson(treeNode))
                    }
                    startActivity(intent)
//                }
                //TODO: set your item onClick event
//                else ->null
//            }
        }


    }
}
