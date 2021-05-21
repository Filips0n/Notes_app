package sk.uniza.fri.sudora.listDisplay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import sk.uniza.fri.sudora.R
import sk.uniza.fri.sudora.adapter.NoteAdapter
import sk.uniza.fri.sudora.adapter.NoteListener
import sk.uniza.fri.sudora.databinding.FragmentArchiveBinding
import sk.uniza.fri.sudora.notes.list.ListType
import sk.uniza.fri.sudora.notes.list.NoteListViewModel

/**
 * Fragment, ktory zobrazuje poznamky ulozene v archive
 */
class ArchiveFragment : Fragment() {
    private val viewModel: NoteListViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //zobrazi action bar pred vstupom
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        val binding = DataBindingUtil.inflate<FragmentArchiveBinding>(inflater, R.layout.fragment_archive, container, false)
        //adapter pre zvladnutie prace s poznamkami
        val adapter = NoteAdapter(viewModel, ListType.ARCHIVE ,NoteListener {}, this.context)
        binding.archiveList.adapter = adapter
        //pozorovatel zmeny v zozname poznamok
        viewModel.archiveList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
            adapter.notifyDataSetChanged()
            if (it.size > 0) {
                binding.textViewArchive.text = ""
            } else {
                binding.textViewArchive.text = getString(R.string.your_archive_is_empty)
            }
        })
        return binding.root
    }
}