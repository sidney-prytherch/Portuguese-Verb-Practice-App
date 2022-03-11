package com.sid.app.verbpractice.ui.dictionary

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R


class WordFilterFragment : DialogFragment() {

    private lateinit var listener: SetAndGetFilterSettings

    interface SetAndGetFilterSettings {
        fun onSetFilterSettings(result: BooleanArray)
        fun getFilterSettings(): BooleanArray
        fun onSetCommonVerbValue(result: Int)
        fun getCommonVerbValue(): Int
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetFilterSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetIndicativeSettings"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.filter_words, null)

            val checkBoxes = arrayOf(view.findViewById<CheckBox>(R.id.selected), view.findViewById(R.id.unselected))
            listener.getFilterSettings()
                .forEachIndexed { index, isSet -> checkBoxes[index].isChecked = isSet }
            val commonalitySlider = view.findViewById<AppCompatSeekBar>(R.id.commonalitySlider)
            val commonLabel = view.findViewById<TextView>(R.id.commonLabel)
            commonalitySlider.progress = listener.getCommonVerbValue()
            commonLabel.text = getString(when (commonalitySlider.progress) {
                0 -> R.string.uncommon
                1 -> R.string.somewhat_common
                2 -> R.string.common
                else -> R.string.very_common
            })

            commonalitySlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    commonLabel.text = getString(when (i) {
                        0 -> R.string.uncommon
                        1 -> R.string.somewhat_common
                        2 -> R.string.common
                        else -> R.string.very_common
                    })
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.filter)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    val filterResults = checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    listener.onSetFilterSettings(filterResults)
                    listener.onSetCommonVerbValue(commonalitySlider.progress)
                    val parent = (parentFragmentManager.primaryNavigationFragment!! as DictionaryFragment)
                    parent.resetFilter(filterResults, commonalitySlider.progress)
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}