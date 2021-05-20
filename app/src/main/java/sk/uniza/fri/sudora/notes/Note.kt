package sk.uniza.fri.sudora.notes

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Datova tieda pre ulozenie poznamky
 *
 * @param noteId jedinecne ID poznamky
 * @param noteTitle nadpis poznmaky
 * @param noteText text poznamky
 * @param color farba poznamky
 */
data class Note(val noteId: UUID,
                         var noteTitle: String,
                         var noteText: String,
                         var color: NoteColor,
                         var isPinned : Boolean = false): Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("noteId"),
            parcel.readString()!!,
            parcel.readString()!!,
            TODO("color")) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(noteTitle);
        dest?.writeString(noteText);
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}