package com.fenchtose.movieratings.base.router

import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.fenchtose.movieratings.base.BaseFragment
import com.fenchtose.movieratings.base.RouterPath
import com.fenchtose.movieratings.R
import java.util.Stack

class Router(activity: AppCompatActivity) {

    val history: Stack<RouterPath<out BaseFragment>> = Stack()
    val manager = activity.supportFragmentManager
    val titlebar: ActionBar? = activity.supportActionBar
    var callback: RouterCallback? = null

    val TAG = "Router"

    fun go(path: RouterPath<out BaseFragment>) {
        if (history.size >= 1) {
            val top = history.peek()
            if (top.javaClass == path.javaClass) {
                return
            }
        }

        _move(path)
        history.push(path)
    }

    fun onBackRequested(): Boolean {
        if (history.empty()) {
            Log.e(TAG, "history is empty. We can't go back")
            return true
        }

        val canTopGoBack = _canTopGoBack()
        if (canTopGoBack) {
            if (history.size == 1) {
                return  true
            }

            goBack()
        }

        return false
    }

    fun goBack(): Boolean {

        if (history.size > 1) {
            _moveBack()
            return true
        }

        return false
    }

    private fun _canTopGoBack(): Boolean {
        val fragment: BaseFragment? = _getTopView()

        fragment?.let {
            return fragment.canGoBack()
        }

        return true

    }

    private fun _getTopView() : BaseFragment? {
        return history.peek().fragment
    }

    private fun _move(path: RouterPath<out BaseFragment>) {
        val fragment = path.createOrGetFragment()
        manager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        titlebar?.setTitle(fragment.getScreenTitle())
        callback?.movedTo(path)
    }

    private fun _moveBack() {
        val path = history.pop()
        callback?.removed(path)
        if (!history.empty()) {
            val top = history.peek()
            top?.let {
                _move(top)
            }
        }
    }

    interface RouterCallback {
        fun movedTo(path: RouterPath<out BaseFragment>)
        fun removed(path: RouterPath<out BaseFragment>)
    }
}