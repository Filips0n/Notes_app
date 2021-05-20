package sk.uniza.fri.sudora

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sk.uniza.fri.sudora.databinding.ActivityMainBinding
import sk.uniza.fri.sudora.notes.Note

/**
 * Vstupna trieda do aplikacie
 */
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: NoteListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        viewModel = ViewModelProvider(this).get(NoteListViewModel::class.java)
        //nacita ulozene poznamky do view modelu
        readDataToViewModel()
        //nastavuje navigation drawer a kontroler
        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //nacita ulozenu preferenciu pouzivatela o tmavom rezime
        val appSettingsPrefs: SharedPreferences = getSharedPreferences(getString(R.string.app_settings_prefs), 0)
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean(getString(R.string.dark_modeKey), false)
        //nastavi ci je v aplikacii nastaveny tmavy rezim
        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    /**
     * Pri konci aplikacie uloz view model
     */
    override fun onStop() {
        super.onStop()
        writeFromViewModel()
    }

    /**
     * Zapise zoznamy v JSON tvare pri ukonceni aplikacie, pre zaznamenanie poznamok
     */
    private fun writeFromViewModel(){
        val gson = Gson()

        val noteJSON = gson.toJson(viewModel.noteList.value)
        val archiveJSON = gson.toJson(viewModel.archiveList.value)
        val trashJSON = gson.toJson(viewModel.trashList.value)

        getPreferences(MODE_PRIVATE).edit().apply {
            putString("noteJSON", noteJSON)
            putString("archiveJSON", archiveJSON)
            putString("trashJSON", trashJSON)
        }.apply()
    }

    /**
     * Nacita ulozene zoznamy v JSON tvare pri spusteni aplikacie
     */
    private fun readDataToViewModel(){
        val gson = Gson()
        val sharedPrefs = getPreferences(MODE_PRIVATE)

        val noteJSON = sharedPrefs.getString("noteJSON", "[]")
        val archiveJSON = sharedPrefs.getString("archiveJSON", "[]")
        val trashJSON = sharedPrefs.getString("trashJSON", "[]")

        val type = object: TypeToken<MutableList<Note?>>() {}.type
        viewModel.addToViewModel(gson.fromJson(noteJSON, type), gson.fromJson(archiveJSON, type), gson.fromJson(trashJSON, type))
    }
}