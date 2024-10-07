package com.shinjaehun.jetpacknotesmvvmkotlinv3.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv3.R
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.startWithFade
import com.shinjaehun.jetpacknotesmvvmkotlinv3.databinding.FragmentLoginBinding
import com.shinjaehun.jetpacknotesmvvmkotlinv3.login.buildlogic.LoginInjector
import com.shinjaehun.jetpacknotesmvvmkotlinv3.note.NoteActivity

class LoginView: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            LoginInjector(requireActivity().application).provideLoginViewModelFactory()
        ).get(LoginViewModel::class.java)

        (binding.rootFragmentLogin.background as AnimationDrawable).startWithFade()

        setUpClickListeners()
        observeViewModel()

        viewModel.handleEvent(LoginEvent.OnStart)
    }

    private fun setUpClickListeners() {
        binding.btnAuthAttempt.setOnClickListener {
            viewModel.handleEvent(LoginEvent.OnAuthButtonClick(requireContext()))
        }

        binding.imbToolbarBack.setOnClickListener { startListActivity() }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            startListActivity()
        }
    }

    private fun startListActivity() = requireActivity().startActivity(
        Intent(activity, NoteActivity::class.java)
    )

    private fun observeViewModel() {
        viewModel.signInStatusText.observe(
            viewLifecycleOwner,
            Observer { text ->
                binding.lblLoginStatusDisplay.text = text
            }
        )

        viewModel.authButtonText.observe(
            viewLifecycleOwner,
            Observer { text ->
                binding.btnAuthAttempt.text = text
            }
        )

        viewModel.satelliteDrawable.observe(
            viewLifecycleOwner,
            Observer { drawable ->
                binding.imvAntennaAnimation.setImageResource(
                    resources.getIdentifier(drawable, "drawable", activity?.packageName)
                )
            }
        )

        viewModel.startAnimation.observe(
            viewLifecycleOwner,
            Observer {
                binding.imvAntennaAnimation.setImageResource(
                    R.drawable.antenna_loop
                )
                (binding.imvAntennaAnimation.drawable as AnimationDrawable).start()
            }
        )
    }


}