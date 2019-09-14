package com.f3401pal.checkabletreeview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_checkable_text.view.*

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
        var result= mutableListOf<TreeNode<T>>(roots)
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

    @UiThread
    override fun setRoots(roots: MutableList<TreeNode<T>>) {
        with(adapter) {
            var nodesList=mutableListOf<TreeNode<T>>()
            for(root in roots){
                nodesList.addAll(treeToList(root))
            }
            nodes=nodesList
            notifyDataSetChanged()
        }
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

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return nodes[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_checkable_text, parent, false), indentation)
    }

    override fun getItemCount(): Int {
        return nodes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(nodes[position])
    }

    @UiThread
    private fun expand(position: Int) {
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

    @UiThread
    private fun collapse(position: Int) {
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

        internal fun bind(node: TreeNode<T>) {
            itemView.indentation.minimumWidth = indentation * node.getLevel()

            itemView.checkText.text = node.value.toString()
            itemView.checkText.setOnCheckedChangeListener(null)
            val state = node.getCheckedStatus()
            itemView.checkText.isChecked = state.allChildrenChecked
            itemView.checkText.setIndeterminate(state.hasChildChecked)
            itemView.checkText.setOnCheckedChangeListener { _, isChecked ->
                node.setChecked(isChecked)
                notifyDataSetChanged()
            }

            if(node.isLeaf()) {
                itemView.expandIndicator.visibility = View.GONE
            } else {
                itemView.expandIndicator.visibility = View.VISIBLE
                itemView.expandIndicator.setOnClickListener { expandCollapseToggleHandler(node, this) }
                itemView.expandIndicator.setIcon(node.isExpanded)
            }

            Log.d(TAG, "${node.value}: hasChildChecked=${state.hasChildChecked}, allChildrenChecked=${state.allChildrenChecked}")
        }

    }
}

