package com.udacity.politicalpreparedness.ui.representative.adapter

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.udacity.politicalpreparedness.R
import com.udacity.politicalpreparedness.ui.representative.model.Representative
import com.udacity.politicalpreparedness.utils.fadeIn
import com.udacity.politicalpreparedness.utils.fadeOut

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        // Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
        Glide.with(view.context)
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop(),
            )
            .into(view)
    }
}

@BindingAdapter("stateSpinnerValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("progressBarVisibility")
fun bindProgressBarVisibility(view: ProgressBar, show: LiveData<Boolean?>?) {
    show?.value?.let {
        if (it) {
            view.fadeIn()
        } else {
            view.fadeOut()
        }
    }
}

@BindingAdapter("loadRepresentatives")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Representative>?) {
    val adapter = recyclerView.adapter as? RepresentativeListAdapter
    adapter?.submitList(data)
}
