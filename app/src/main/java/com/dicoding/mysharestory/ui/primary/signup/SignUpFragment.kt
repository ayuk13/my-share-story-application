package com.dicoding.mysharestory.ui.primary.signup

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
import com.dicoding.mysharestory.databinding.FragmentSignUpBinding
import com.dicoding.mysharestory.model.RegisterRequest
import com.dicoding.mysharestory.ui.primary.PrimaryViewModel
import com.dicoding.mysharestory.ui.story.StoryActivity
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private val viewModel: PrimaryViewModel by activityViewModels()
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSignUp.setOnClickListener {
                if (!etNameUser.isNotEmpty) {
                    etNameUser.emptyForm()
                    return@setOnClickListener
                }

                if (!etPassword.isPasswordValid) {
                    etPassword.invalidPassword()
                    return@setOnClickListener
                }

                if (etEmail.isNotEmpty) {
                    if (etEmail.isEmailValid) {
                        lifecycleScope.launch{
                            viewModel.registerUser(
                                RegisterRequest(
                                    etNameUser.text.toString(),
                                    etEmail.text.toString(),
                                    etPassword.text.toString()
                                )
                            )
                        }
                    } else etEmail.invalidEmail()
                } else etEmail.emptyForm()
            }

            tvBack.setOnClickListener { requireActivity().onBackPressed() }
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.loadingStatus.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
                binding.btnSignUp.visibility = if (it) View.GONE else View.VISIBLE
                binding.tvBack.visibility = if (it) View.GONE else View.VISIBLE
            }
        }

        viewModel.registerUserStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.register_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

        viewModel.duplicateEmailStatus.observe(viewLifecycleOwner) { isDuplicate ->
            isDuplicate?.let {
                if (isDuplicate) binding.etEmail.duplicateEmail()
            }
        }
    }
}