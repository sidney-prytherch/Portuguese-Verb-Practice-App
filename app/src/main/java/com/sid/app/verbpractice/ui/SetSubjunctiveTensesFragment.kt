package com.sid.app.verbpractice.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R

class SetSubjunctiveTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetSubjunctiveSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetSubjunctiveSettings {
        fun onSetSubjunctiveTense(dialog: DialogFragment, result: BooleanArray)
        fun getSubjunctiveValues():BooleanArray
    }

    interface SubjunctivePositiveClickListener {
        fun resetSubjunctiveTextView()
    }
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as SetAndGetSubjunctiveSettings
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement SetAndGetSubjunctiveSettings"))
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_subjunctive, null)

            checkBoxes = arrayOf<CheckBox>(
                view.findViewById(R.id.presSubjCheckbox),
                view.findViewById(R.id.impeSubjCheckbox),
                view.findViewById(R.id.futuSubjCheckbox),
                view.findViewById(R.id.presPerfSubjCheckbox),
                view.findViewById(R.id.plupSubjCheckbox),
                view.findViewById(R.id.futuPerfSubjCheckbox)
            )
            listener.getSubjunctiveValues().forEachIndexed{index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_indicative_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetSubjunctiveTense(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetSubjunctiveTextView()
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