package com.dicoding.mysharestory.ui.primary.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.mysharestory.R
import com.dicoding.mysharestory.databinding.FragmentLoginBinding
import com.dicoding.mysharestory.model.LoginRequest
import com.dicoding.mysharestory.ui.primary.PrimaryViewModel
import com.dicoding.mysharestory.ui.story.StoryActivity
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: PrimaryViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnSignIn.setOnClickListener {
                if (etEmail.isNotEmpty) {
                    if (etEmail.isEmailValid) {
                        lifecycleScope.launch{
                            viewModel.loginUser(
                                LoginRequest(
                                    etEmail.text.toString(),
                                    etPassword.text.toString()
                                )
                            )
                        }
                    } else etEmail.invalidEmail()
                } else etEmail.emptyForm()

                if (!etPassword.isPasswordValid) {
                    etPassword.invalidPassword()
                    return@setOnClickListener
                }
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.loadingStatus.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                binding.progressLoading.visibility = if (it) View.VISIBLE else View.GONE
                binding.btnSignIn.visibility = if (it) View.GONE else View.VISIBLE
                binding.tvBack.visibility = if (it) View.GONE else View.VISIBLE
            }
        }

        viewModel.loginUserStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    Intent(requireActivity(), StoryActivity::class.java).apply {
                        requireContext().startActivity(this)
                    }.also { requireActivity().finish() }
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.login_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}