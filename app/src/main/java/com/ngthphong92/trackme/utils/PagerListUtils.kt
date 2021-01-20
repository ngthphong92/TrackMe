package com.ngthphong92.trackme.utils

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException

const val MIN_PAGE_SIZE = 1
const val PAGE_SIZE = 10
const val PAGE_FACTOR = 3

inline fun <reified K> Any.toPagerList(
    jobScope: LifecycleCoroutineScope?,
    pageSize: Int = PAGE_SIZE,
    nextKey: K? = null,
    noinline callbackFunc: suspend (PagingData<Any>) -> Unit
) {
    if (this !is ArrayList<*>)
        return

    val finalPageSize = if (this.size < pageSize * PAGE_FACTOR) maxOf(this.size / PAGE_FACTOR, MIN_PAGE_SIZE) else pageSize
    if (finalPageSize * PAGE_FACTOR > this.size && this.size <= 0)
        return
    jobScope?.launch(Dispatchers.Default) lifecycleScope@{
        Pager(
            PagingConfig(
                pageSize = finalPageSize,
                prefetchDistance = if (finalPageSize * PAGE_FACTOR > this@toPagerList.size) 0 else finalPageSize,
                enablePlaceholders = true,
                maxSize = this@toPagerList.size
            )
        ) {
            PageKeyedPagingSource(this@toPagerList, nextKey)
        }.flow.collectLatest {
            withContext(Dispatchers.Default) {
                callbackFunc.invoke(it)
            }
        }
    }
}

class PageKeyedPagingSource(var list: List<Any>, var nextKey: Any?) : PagingSource<Any, Any>() {
    override suspend fun load(params: LoadParams<Any>): LoadResult<Any, Any> {
        return try {
            Page(
                data = list,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }
}