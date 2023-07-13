package com.udacity.politicalpreparedness.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.ui.MainActivity
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionListAdapter
import com.udacity.politicalpreparedness.ui.election.adapter.ElectionModel

/**
 * @author Komi Donon
 * @since 7/9/2023
 */

@BindingAdapter("listData")
fun electionBindRecyclerView(recyclerView: RecyclerView, data: List<ElectionModel>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}

@BindingAdapter("viewVisibility")
fun setViewVisibility(view: View, count: Int?) {
    if (count != null) {
        view.visibility = if (count > 0) View.VISIBLE else View.GONE
    } else {
        view.visibility = View.GONE
    }
}

fun Fragment.setTitle(title: String) {
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).supportActionBar?.title = title
    }
}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (requireActivity() is MainActivity) {
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(bool)
    }
}

@BindingAdapter("showError")
fun showError(view: View, count: Int?) {
    if (count != null) {
        view.visibility = if (count > 0) {
            view.fadeIn()
            View.GONE
        } else {
            view.fadeOut()
            View.VISIBLE
        }
    } else {
        view.visibility = View.VISIBLE
        view.fadeOut()
    }
}

@BindingAdapter("reloadData")
fun bindRefreshing(refreshView: SwipeRefreshLayout, loading: Boolean?) {
    refreshView.isRefreshing = loading == true
}

@BindingAdapter("favoriteButtonText")
fun bindButtonText(textView: TextView, saved: Boolean?) {
    if (saved != null) {
        val text = textView.context.getString(if (saved) R.string.voter_remove_from_saved else R.string.voter_add_to_saved)
        textView.fadeIn()
        textView.text = text
        textView.contentDescription = text
    } else {
        textView.visibility = View.GONE
    }
}

/**
 * Animate changing the view visibility.
 */
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

/**
 * Animate changing the view visibility.
 */
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}
