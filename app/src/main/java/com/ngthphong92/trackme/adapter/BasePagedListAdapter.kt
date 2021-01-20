package com.ngthphong92.trackme.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class BasePagedListAdapter<T>(
    private val onCreateViewHolderFunc: (viewGroup: ViewGroup, viewType: Int) -> ViewDataBinding,
    private val bindFunc: ((binding: ViewDataBinding, list: List<T>, item: T?, position: Int) -> Unit)? = null,
    private val getItemViewTypeFunc: ((position: Int, data: List<T>?) -> Int)? = null,
    diffUtilCallback: (() -> BaseDiffUtilCallback<T>)? = null
) : PagingDataAdapter<Any, BasePagedListAdapter<T>.ViewHolder>(diffCallback<T>(diffUtilCallback?.invoke())) {

    override fun getItemViewType(position: Int): Int =
        getItemViewTypeFunc?.invoke(position, getDataList())
            ?: super.getItemViewType(position)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(onCreateViewHolderFunc.invoke(viewGroup, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getDataList()[position], position)
    }

    override fun getItemCount(): Int {
        return if (super.getItemCount() > 0)
            super.getItemCount()
        else getDataList().size
    }

    fun getDataList(): List<T> {
        val snapshot = (snapshot().items as? List<T>)
        return snapshot ?: emptyList()
    }

    inner class ViewHolder(private val mBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: T?, position: Int) {
            val list = getDataList()
            if (list.isNullOrEmpty())
                return
            bindFunc?.invoke(mBinding, list, item, position)
        }
    }
}

private fun <T> diffCallback(baseDiffUtilCallback: BaseDiffUtilCallback<T>?): DiffUtil.ItemCallback<Any> {
    return object : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
            baseDiffUtilCallback?.areItemsTheSameFunc?.invoke(oldItem as? T, newItem as? T)
                    ?: oldItem == newItem

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
            baseDiffUtilCallback?.areContentsTheSameFunc?.invoke(oldItem as? T, newItem as? T)
                ?: false
    }
}

data class BaseDiffUtilCallback<T>(
    var areItemsTheSameFunc: ((T?, T?) -> Boolean?)? = null,
    var areContentsTheSameFunc: ((T?, T?) -> Boolean?)? = null
)