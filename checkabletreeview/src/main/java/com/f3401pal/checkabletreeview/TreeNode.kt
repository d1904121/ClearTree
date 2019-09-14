package com.f3401pal.checkabletreeview


object TreeNodeFactory {

    fun buildTestTree(): TreeNode<Node> {
        val root = TreeNode(Node("root"))
        val left = TreeNode(Node("left"), root).apply {
            children=mutableListOf(
                TreeNode(Node("level3left"), this),
                TreeNode(Node("level3right"), this))
        }
        val right = TreeNode(Node("right"), root)

        root.children = mutableListOf(left, right)
        return root
    }
}

open class Node(val str: String) : Checkable(false) {
    override fun toString(): String {
        return str
    }
}

//TODO: add your node class here
class TestNode(str: String,var progress:Int):Node(str){
    override fun toString(): String {
        return "[$progress%]$str"
    }
}

class QuickCreateNode():Node("")

class TreeNode<T : Checkable>(
        val value: T,
        val parent: TreeNode<T>?,
        var children: MutableList<TreeNode<T>>,
        override var isExpanded: Boolean =false
) : HasId, Expandable {
    override val id: Long by lazy {
        IdGenerator.generate()
    }
    // constructor for root node
    constructor(value: T) : this(value, null,  mutableListOf<TreeNode<T>>())
    // constructor for leaf node
    constructor(value: T, parent: TreeNode<T>) : this(value, parent,  mutableListOf<TreeNode<T>>())
    // constructor for parent node
    constructor(value: T, children: MutableList<TreeNode<T>>) : this(value, null, children)

    fun isTop(): Boolean {
        return parent == null
    }
    fun isLeaf(): Boolean {
        return children.isEmpty()
    }
    fun getLevel(): Int {
        fun stepUp (node: TreeNode<T>): Int {
            return node.parent?.let { 1 + stepUp(it) } ?: 0
        }
        return stepUp(this)
    }
    fun setChecked(isChecked: Boolean) {
        value.checked = isChecked
        // cascade the action to children
        children.forEach {
            it.setChecked(isChecked)
        }
    }
    fun getCheckedStatus(): NodeCheckedStatus {
        if (isLeaf()) return NodeCheckedStatus(value.checked, value.checked)
        var hasChildChecked = false
        var allChildrenChecked = true
        children.forEach {
            val checkedStatus = it.getCheckedStatus()
            hasChildChecked = hasChildChecked || checkedStatus.hasChildChecked
            allChildrenChecked = allChildrenChecked && checkedStatus.allChildrenChecked
        }
        return NodeCheckedStatus(hasChildChecked, allChildrenChecked)
    }
    fun getAggregatedValues(): List<T> {
        return if (isLeaf()) {
            if (value.checked) listOf(value) else emptyList()
        } else {
            if (getCheckedStatus().allChildrenChecked) {
                listOf(value)
            } else {
                val result = mutableListOf<T>()
                children.forEach {
                    result.addAll(it.getAggregatedValues())
                }
                result
            }
        }
    }
    fun getRoot():TreeNode<T>{
        var result=this
        while(result.parent!=null)result= result.parent!!
        return result
    }
}