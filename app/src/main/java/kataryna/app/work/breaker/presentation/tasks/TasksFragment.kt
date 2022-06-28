package kataryna.app.work.breaker.presentation.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kataryna.app.work.breaker.R
import kataryna.app.work.breaker.databinding.FragmentTasksBinding
import kataryna.app.work.breaker.presentation.tasks.TasksViewModel.TasksUiState.*
import kataryna.app.work.breaker.utils.getViewDataBinding
import kataryna.app.work.breaker.utils.observe

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding

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
            binding.enableGeoFeatureImage.isEnabled = isChecked
        }
        binding.tasksInputField.doOnTextChanged { text, _, _, _ ->
            viewModel.saveUserInput(text.toString())
        }

        observe(viewModel.state) {
            when (it) {
                Loading -> loadDefaultImage(binding.backgroundImage)
                is UserTasks -> updateUserTasksText(it.userTasks)
                is ScreenBackground -> loadBgImage(binding.backgroundImage, it.bgImageUrl)
                is Error, null -> loadDefaultImage(binding.backgroundImage)
            }
        }

        viewModel.fetchBackgroundPhoto()
        viewModel.fetchUserTasks()
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