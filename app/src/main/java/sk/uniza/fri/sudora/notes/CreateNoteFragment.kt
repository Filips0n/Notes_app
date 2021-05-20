package sk.uniza.fri.sudora.notes

import android.app.Activity
import android.content.Context
import android.graphics.Color.*
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import sk.uniza.fri.sudora.R
import sk.uniza.fri.sudora.databinding.FragmentCreateNoteBinding
import java.util.*


/**
 * Frogment pre vytvaranie a upravovanie poznamok
 */
@Suppress("NAME_SHADOWING")
class CreateNoteFragment : Fragment() {
    private lateinit var binding: FragmentCreateNoteBinding
    private val args by navArgs<CreateNoteFragmentArgs>()
    var note = Note(UUID.randomUUID(), "", "", NoteColor.YELLOW)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //skrytie action baru pred vstupom do fragmentu
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)
        try {
            note = args.editNote!!
        } catch (e: NullPointerException) {}
        //napise do editovacich poli titulok a text poznamky
        binding.noteTitleInput.setText(note.noteTitle, TextView.BufferType.EDITABLE)
        binding.noteInput.setText(note.noteText, TextView.BufferType.EDITABLE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ak je stacene tlacidlo spat, chod na main fragment
        binding.backButton.setOnClickListener { view: View ->
            view.findNavController().navigate(CreateNoteFragmentDirections.actionCreateNoteFragmentToMainFragment(note))
        }
        //ak je stlacene tlacitlo ulozenia poznamky, skontroluj ci je v poznamke text, uloz poznamku a chod na main fragment
        binding.doneButton.setOnClickListener {
            if (isText()) {
                saveNote()
                view.findNavController().navigate(CreateNoteFragmentDirections.actionCreateNoteFragmentToMainFragment(note))
            }
        }
    }

    /**
     * Nastavi poznamke nadpis a text z vstupu
     */
    private fun saveNote(){
        val title = binding.noteTitleInput.text.toString()
        val text = binding.noteInput.text.toString()
        note.noteTitle = title
        note.noteText = text
    }

    /**
     * Programovo skryje klavesnicu
     * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically?rq=1
     */
    private fun hideKeyboard(activity: Activity) {
        val view: View = activity.findViewById(android.R.id.content)
        val imm =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Zisti ci sa nachadza vo vstupnych poliach text
     * @return true ak je text v nadpise aj v poznamke, inak false
     */
    private fun isText() : Boolean {
        if (binding.noteTitleInput.text.isNullOrEmpty()){
            val toast = Toast.makeText(context, getString(R.string.note_title_is_required), Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            return false
        } else if (binding.noteInput.text.isNullOrEmpty()){
            val toast = Toast.makeText(context, getString(R.string.note_description_is_required), Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            return false
        }
        return true
    }

    /**
     * Pri odchode z fragmentu skry klavesnicu
     */
    override fun onPause() {
        super.onPause()
        hideKeyboard(this.requireActivity())
    }
}