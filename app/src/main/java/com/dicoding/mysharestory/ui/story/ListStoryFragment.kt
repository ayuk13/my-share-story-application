package com.dicoding.mysharestory.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.dicoding.mysharestory.R
import com.dicoding.mysharestory.adapter.ListStoryAdapter
import com.dicoding.mysharestory.adapter.LoadingStateAdapter
import com.dicoding.mysharestory.databinding.FragmentListStoryBinding
import com.dicoding.mysharestory.model.LoginResult
import com.dicoding.mysharestory.model.Story
import com.dicoding.mysharestory.ui.MainActivity
import com.dicoding.mysharestory.ui.maps.MapsActivity
import com.dicoding.mysharestory.utils.ViewModelFactory

class ListStoryFragment : Fragment() {

    private val viewModel: ListStoryViewModel by activityViewModels(){
        ViewModelFactory(requireContext())
    }
    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListStoryAdapter
    private var userToken: String = ""
    private var userFullName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.getTokenUser()
                getData()
                swipeRefresh.isRefreshing = false
            }

            btnLogout.setOnClickListener {
                viewModel.clearTokenUser()
            }

            btnAdd.setOnClickListener {
                toDetail(loginUser = LoginResult(userFullName, userToken, null))
            }

            btnMaps.setOnClickListener {
                Intent(requireActivity(), MapsActivity::class.java).apply{
                    startActivity(this)
                }
            }
        }

        observeLiveData()
    }

    private fun getData(){
        adapter = ListStoryAdapter(this)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )

        viewModel.getListStory(userToken).observe(viewLifecycleOwner){ story ->
            story?.let{
                adapter.submitData(lifecycle,it)
            }
        }

    }

    private fun observeLiveData() {

        viewModel.loadingStatus.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                with(binding) {
                    progressBar.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.successStatus.observe(viewLifecycleOwner) { isLoading ->
            isLoading.getContentIfNotHandled()?.let {
                if (!it) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.fetching_api_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.onCleared.observe(viewLifecycleOwner) { isCleared ->
            isCleared?.let {
                Intent(requireActivity(), MainActivity::class.java).apply {
                    requireContext().startActivity(this)
                }.also { requireActivity().finish() }
            }
        }

        viewModel.getNameUser().observe(viewLifecycleOwner) { userName ->
            userName?.let {
                userFullName = userName
                binding.tvUserName.text = requireContext().getString(
                    R.string.welcome_user,
                    userName
                )
            }
        }

        viewModel.getTokenUser().observe(viewLifecycleOwner) { token ->
            token?.let {
                userToken = token
                getData()
            }
        }
    }

    fun toDetail(story: Story? = null, loginUser: LoginResult? = null) {
        val action = ListStoryFragmentDirections.navigateToCreateStory()
        action.detailStory = story
        action.loginUser = loginUser
        Navigation.findNavController(binding.root).navigate(action)
    }

}