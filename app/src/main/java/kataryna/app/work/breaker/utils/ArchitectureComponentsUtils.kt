@file:JvmName("ArchitectureComponentsUtils")
package kataryna.app.work.breaker.utils

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T : Any> Fragment.observeFlow(
    flow: Flow<T>,
    function: (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                function.invoke(it)
            }
        }
    }
}

fun <T : Any, L : LiveData<T>> AppCompatActivity.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}

fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(body))
}

fun <T : Any, L : LiveData<T>> AppCompatActivity.observeNonNull(liveData: L, body: (T) -> Unit) {
    liveData.observe(this, Observer { item ->
        if (item != null) {
            body.invoke(item)
        }
    })
}

fun <T : Any, L : LiveData<T>> Fragment.observeNonNull(liveData: L, body: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer { item ->
        if (item != null) {
            body.invoke(item)
        }
    })
}

fun <T : Any, L : LiveData<T>> Fragment.observeOnceNonNull(liveData: L, action: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            if (t != null) {
                action.invoke(t)
                liveData.removeObserver(this)
            }
        }
    })
}

fun <T : ViewDataBinding> AppCompatActivity.getViewDataBinding(@LayoutRes layoutId: Int): T =
    DataBindingUtil.setContentView(this, layoutId)

fun <T : ViewDataBinding> Fragment.getViewDataBinding(
    container: ViewGroup?,
    @LayoutRes layoutId: Int
): T = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
