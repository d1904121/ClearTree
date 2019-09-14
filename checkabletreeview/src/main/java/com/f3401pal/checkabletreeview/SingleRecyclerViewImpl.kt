package com.f3401pal.checkabletreeview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_checkable_text.view.*
import kotlinx.android.synthetic.main.item_checkable_text.view.expandIndicator
import kotlinx.android.synthetic.main.item_checkable_text.view.indentation
import kotlinx.android.synthetic.main.item_text_only.view.*

private const val TAG = "SingleRecyclerView"

class SingleRecyclerViewImpl<T : Checkable> : RecyclerView, CheckableTreeView<T> {
    private val adapter: TreeAdapter<T> by lazy {
        val indentation = indentation.px
        TreeAdapter<T>(indentation)
    }
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, style: Int) : super(context, attributeSet, style)
    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        setAdapter(adapter)
    }

    fun treeToList(roots: TreeNode<T>):MutableList<TreeNode<T>>{
        val result= mutableListOf<TreeNode<T>>(roots)
        val iterator =result.listIterator()
        for(item in iterator){
            if(item.isExpanded){
                for(child in item.children){
                    treeToList(child).forEach {
                        iterator.add(it)
                    }
                }
            }
        }
        return result
    }
    @UiThread override fun setRoots(roots: MutableList<TreeNode<T>>) {
        with(adapter) {
            val nodesList=mutableListOf<TreeNode<T>>()
            for(root in roots){
                nodesList.addAll(treeToList(root))
            }
            nodes=nodesList
            notifyDataSetChanged()
        }
    }
    fun setItemOnClick(click:(TreeNode<T>,TreeAdapter<T>.ViewHolder)->Unit){
        adapter.itemOnclick=click
    }
}

class TreeAdapter<T : Checkable>(private val indentation: Int) : RecyclerView.Adapter<TreeAdapter<T>.ViewHolder>() {
    internal var nodes: MutableList<TreeNode<T>> = mutableListOf()
    private val expandCollapseToggleHandler: (TreeNode<T>, ViewHolder) -> Unit = { node, viewHolder ->
        if(node.isExpanded) {
            collapse(viewHolder.adapterPosition)
        } else {
            expand(viewHolder.adapterPosition)
        }
        viewHolder.itemView.expandIndicator.startToggleAnimation(node.isExpanded)
    }
    var itemOnclick:(TreeNode<T>, ViewHolder)->Unit={_,_->;}
    set(value) {
        field={treeNode,viewHolder->
            value(treeNode,viewHolder)
            //TODO: add expand or collapse if needed
        }
    }
    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return nodes[position].id
    }
    override fun getItemViewType(position: Int): Int {
        val node=nodes[position]
        return when(node.value){
            is TestNode -> NodeTypes.TEST_NODE.ordinal
            //TODO: add your node type here
            else -> NodeTypes.NODE.ordinal
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout=when(viewType){
            //TODO: add your item layout here
            NodeTypes.TEST_NODE.ordinal -> R.layout.item_text_only
            else -> R.layout.item_checkable_text
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false), indentation)
    }
    override fun getItemCount(): Int {
        return nodes.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(nodes[position])
    }
    @UiThread private fun expand(position: Int) {
        if(position >= 0) {
            // expand
            val node = nodes[position]
            val insertPosition = position + 1
            val insertedSize = node.children.size
            nodes.addAll(insertPosition, node.children)
            node.isExpanded = true
            notifyItemRangeInserted(insertPosition, insertedSize)
        }
    }
    @UiThread private fun collapse(position: Int) {
        // collapse
        if(position >= 0) {
            val node = nodes[position]
            var removeCount = 0
            fun removeChildrenFrom(cur: TreeNode<T>) {
                nodes.remove(cur)
                removeCount++
                if(cur.isExpanded) {
                    cur.children.forEach { removeChildrenFrom(it) }
                    cur.isExpanded = false
                }
            }
            node.children.forEach { removeChildrenFrom(it) }
            node.isExpanded = false
            notifyItemRangeRemoved(position + 1, removeCount)
        }
    }
    inner class ViewHolder(view: View, private val indentation: Int) : RecyclerView.ViewHolder(view) {
        //TODO: create your bind function
        private fun bindIndentation(node: TreeNode<T>){
            itemView.indentation.minimumWidth = indentation * node.getLevel()
        }
        private fun bindExpandIndicator(node: TreeNode<T>){
            if(node.isLeaf()) {
                itemView.expandIndicator.visibility = View.GONE
            } else {
                itemView.expandIndicator.visibility = View.VISIBLE
                itemView.expandIndicator.setOnClickListener { expandCollapseToggleHandler(node, this) }
                itemView.expandIndicator.setIcon(node.isExpanded)
            }
        }
        private fun bindCommon(node: TreeNode<T>){
            bindIndentation(node)
            bindExpandIndicator(node)
        }
        private fun bindCheckableText(node: TreeNode<T>){
            bindCommon(node)
            itemView.checkText.text = node.value.toString()
            itemView.checkText.setOnCheckedChangeListener(null)
            val state = node.getCheckedStatus()
            itemView.checkText.isChecked = state.allChildrenChecked
            itemView.checkText.setIndeterminate(state.hasChildChecked)
            itemView.checkText.setOnCheckedChangeListener { _, isChecked ->
                node.setChecked(isChecked)
                notifyDataSetChanged()
            }

        }
        private fun bindTextOnly(node:TreeNode<T>){
            bindCommon(node)
            itemView.textView.text = node.value.toString()
        }
        internal fun bind(node: TreeNode<T>) {
            when(node.value){
                //TODO: bind your layout here
                is TestNode -> bindTextOnly(node)
                else -> bindCheckableText(node)
            }
        }

    }
}

