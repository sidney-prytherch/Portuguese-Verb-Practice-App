package com.sid.app.verbpractice.ui.options

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R
import com.sid.app.verbpractice.databinding.TensesOptionsSubjBinding


class SetSubjTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetSubjunctiveSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetSubjunctiveSettings {
        fun onSetSubjTenses(dialog: DialogFragment, result: BooleanArray)
        fun getSubjTenses():BooleanArray
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetSubjunctiveSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetSubjunctiveSettings"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_subj, null)

            checkBoxes = arrayOf(
                view.findViewById(R.id.presSubjCheckbox),
                view.findViewById(R.id.impeSubjCheckbox),
                view.findViewById(R.id.futuSubjCheckbox),
                view.findViewById(R.id.presPerfSubjCheckbox),
                view.findViewById(R.id.plupSubjCheckbox),
                view.findViewById(R.id.futuPerfSubjCheckbox)
            )
            listener.getSubjTenses().forEachIndexed{ index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_subj_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetSubjTenses(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetSubjTextView()
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