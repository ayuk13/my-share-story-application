package com.dicoding.mysharestory.ui.primary.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dicoding.mysharestory.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSignUpRegister.setOnClickListener {
                val action = WelcomeFragmentDirections.navigateToSubmitSignUp()
                Navigation.findNavController(binding.root).navigate(action)
            }

            tvSignIn.setOnClickListener {
                val action = WelcomeFragmentDirections.navigateToLoginFragment()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }
}