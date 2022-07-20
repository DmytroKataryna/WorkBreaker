package kataryna.app.work.breaker.presentation.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kataryna.app.work.breaker.R
import kataryna.app.work.breaker.databinding.FragmentTasksBinding
import kataryna.app.work.breaker.extensions.getViewDataBinding
import kataryna.app.work.breaker.extensions.observeFlow

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeFlow(viewModel.state) {
            it.exception?.let {
                loadDefaultImage(binding.backgroundImage)
            }
            it.bgImageUrl?.let { bgImageUrl ->
                loadBgImage(binding.backgroundImage, bgImageUrl)
            }
            if (it.userTasks.isNotEmpty()) {
                updateUserTasksText(it.userTasks)
            }
            updateGeoFeatureSwitch(it.geofencingStatus)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = getViewDataBinding<FragmentTasksBinding>(container, R.layout.fragment_tasks).apply {
        binding = this@apply
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enableGeoFeatureImage.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment_to_mapFragment)
        }
        binding.enableGeoFeatureSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.enableGeoFeatureImage.isActivated = isChecked
        }
        binding.tasksInputField.doAfterTextChanged {
            it?.let { text -> viewModel.saveUserInput(text.toString()) }
        }

        viewModel.checkGeofencingStatus()
        viewModel.fetchBackgroundPhoto()
        viewModel.fetchUserTasks()
    }

    private fun updateGeoFeatureSwitch(isActive: Boolean) {
        binding.enableGeoFeatureSwitch.isChecked = isActive
    }

    private fun updateUserTasksText(userTasks: String) {
        binding.tasksInputField.setText(userTasks)
    }

    private fun loadDefaultImage(imageView: ImageView) {
        imageView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun loadBgImage(imageView: ImageView, bgImageUrl: String) {
        Glide.with(this)
            .load(bgImageUrl)
            .centerCrop()
            .into(imageView)
    }
}