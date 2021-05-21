package sk.uniza.fri.sudora.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import sk.uniza.fri.sudora.R
import sk.uniza.fri.sudora.databinding.FragmentSettingsBinding

/**
 * Fragment, v ktorom si pouzivatel moze nastavit svoje preferencie
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //zobrazi action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
        //ziska aktualnu preferenciu od pouzivatela ohladom tmaveho rezimu
        val appSettingsPrefs: SharedPreferences = this.requireContext().getSharedPreferences(getString(R.string.app_settings_prefs), 0)
        val sharedPrefsEdit : SharedPreferences.Editor = appSettingsPrefs.edit()
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean(getString(R.string.dark_modeKey), false)

        val switchDarkMode = binding.darkModeSwitch
        //ak je pouzivatelom z preferencii nastaveny switch ako zapnuty tak ho zapne
        switchDarkMode.isChecked = isNightModeOn
        switchDarkMode.setOnClickListener {
            if (isNightModeOn){
                //ak sa zmenila poloha switchu vypni tmavy rezim
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //zapis pouzivatelovej preferencie
                sharedPrefsEdit.putBoolean(getString(R.string.dark_modeKey), false)
                sharedPrefsEdit.apply()
                Toast.makeText(this.context, getString(R.string.dark_mode_off), Toast.LENGTH_SHORT).show()
            } else {
                //ak sa zmenila poloha switchu zapni tmavy rezim
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //zapis pouzivatelovej preferencie
                sharedPrefsEdit.putBoolean(getString(R.string.dark_modeKey), true)
                sharedPrefsEdit.apply()
                Toast.makeText(this.context, getString(R.string.dark_mode_on), Toast.LENGTH_SHORT).show()
            }
        }

        //switch pre nastavenie preferencie, ci sa ma zobrazovat nova poznamka ako prva alebo ako posledna
        val isNewNoteTopON: Boolean = appSettingsPrefs.getBoolean(getString(R.string.new_note_top), false)
        val newNoteToTop = binding.newNotesAddSwitch
        //ak je pouzivatelom z preferencii nastaveny switch ako zapnuty tak ho zapne
        newNoteToTop.isChecked = isNewNoteTopON
        newNoteToTop.setOnClickListener {
            if (isNewNoteTopON){
                //zapis pouzivatelovej preferencie
                sharedPrefsEdit.putBoolean(getString(R.string.new_note_top), false)
                sharedPrefsEdit.apply()
            } else {
                //zapis pouzivatelovej preferencie
                sharedPrefsEdit.putBoolean(getString(R.string.new_note_top), true)
                sharedPrefsEdit.apply()
            }
        }
        return binding.root
    }
}