package com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notelist

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.shinjaehun.jetpacknotesmvvmkotlinv3.R
import com.shinjaehun.jetpacknotesmvvmkotlinv3.common.startWithFade
import com.shinjaehun.jetpacknotesmvvmkotlinv3.databinding.FragmentNoteListBinding
import com.shinjaehun.jetpacknotesmvvmkotlinv3.note.notelist.buildlogic.NoteListInjector

private const val TAG = "NoteListView"

class NoteListView : Fragment() {

    private lateinit var binding: FragmentNoteListBinding
    private lateinit var viewModel: NoteListViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recListFragment.adapter = null // 이게 아주 중요하다고 합니다! memory leak!!
    }

    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProvider(
            this,
            NoteListInjector(requireActivity().application).provideNoteListViewModelFactory()
        ).get(NoteListViewModel::class.java)

        (binding.imvSpaceBackground.drawable as AnimationDrawable).startWithFade()

        showLoadingState()
        setUpAdapter()
        observeViewModel()

        binding.fabCreateNewItem.setOnClickListener {
            startNoteDetailWithArgs("")
        }

        binding.imvToolbarAuth.setOnClickListener {
            findNavController().navigate(R.id.loginView)
        }

        viewModel.handleEvent(NoteListEvent.OnStart)
    }

    private fun showLoadingState() =
        (binding.imvSatelliteAnimation.drawable as AnimationDrawable).start()

    private fun setUpAdapter() {
        adapter = NoteListAdapter()
        adapter.event.observe(
            viewLifecycleOwner,
            Observer {
                viewModel.handleEvent(it)
            }
        )
        binding.recListFragment.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.error.observe(
            viewLifecycleOwner,
            Observer { errorMessage ->
                showErrorState(errorMessage)
            }
        )

        viewModel.noteList.observe(
            viewLifecycleOwner,
            Observer { noteList ->
                adapter.submitList(noteList)

                if (noteList.isNotEmpty()) {
                    (binding.imvSatelliteAnimation.drawable as AnimationDrawable).stop()
                    binding.imvSatelliteAnimation.visibility = View.INVISIBLE
                    binding.recListFragment.visibility = View.VISIBLE
                }
            }
        )

        viewModel.editNote.observe(
            viewLifecycleOwner,
            Observer { noteId ->
                startNoteDetailWithArgs(noteId)
            }
        )
    }

    private fun showErrorState(errorMessage: String?) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun startNoteDetailWithArgs(nid: String) {
        val bundle = Bundle()
        bundle.putString("noteId", nid)
        findNavController().navigate(R.id.noteDetailView, bundle)
//        NoteListViewDirections.actionNoteListViewToNoteDetailView().apply {
//            noteId = nid
//        }
        // nav with args가 좆 같이 동작해서 bundle을 이용하기로 함...
        // 씨발 google 새끼들 compose 넘어간다고... 예전 feature는 신경도 안 쓰는구나
    }
}