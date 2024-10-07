package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notedetail

import android.graphics.drawable.AnimationDrawable
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shinjaehun.jetpacknotesmvvmkotlinv3.R
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.startWithFade
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.toEditable
import com.shinjaehun.jetpacknotesmvvmkotlinv3.databinding.FragmentNoteDetailBinding
import com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notedetail.buildlogic.NoteDetailInjector

private const val TAG = "NoteDetailView"

class NoteDetailView : Fragment() {

    private lateinit var binding: FragmentNoteDetailBinding
    private lateinit var viewModel: NoteDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(
            this,
            NoteDetailInjector(requireActivity().application).provideNoteDetailViewModelFactory()
        ).get(NoteDetailViewModel::class.java)

        showLoadingState()

        binding.imbToolbarDone.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnDoneClick(
                    binding.edtNoteDetailText.text.toString()
                )
            )
        }

        binding.imbToolbarDelete.setOnClickListener {
            viewModel.handleEvent(
                NoteDetailEvent.OnDeleteClick
            )
        }

        observeViewModel()

        (binding.fragNoteDetail.background as AnimationDrawable).startWithFade()

//        Log.i(TAG, "noteId: ${arguments?.getString("noteId").toString()}")
        viewModel.handleEvent(
            NoteDetailEvent.OnStart(
//                NoteDetailViewArgs.fromBundle(arguments!!).noteId
                arguments?.getString("noteId").toString()
                // bundle을 사용해서 noteId를 받아옵시다~~
            )
        )
    }

    private fun showLoadingState() =
        (binding.imvNoteDetailSatellite.drawable as AnimationDrawable).start()

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorMessage(errorMessage)
            }
        )

        viewModel.note.observe(
            viewLifecycleOwner,
            Observer { note ->
                binding.edtNoteDetailText.text = note.contents.toEditable()
            }
        )

        viewModel.updated.observe(
            viewLifecycleOwner,
            Observer {
                findNavController().navigate(R.id.noteListView)
            }
        )

        viewModel.deleted.observe(
            viewLifecycleOwner,
            Observer {
                findNavController().navigate(R.id.noteListView)
            }
        )

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.noteListView)
        }
    }

    private fun showErrorMessage(errorMessage: String?) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.noteListView)
    }

}