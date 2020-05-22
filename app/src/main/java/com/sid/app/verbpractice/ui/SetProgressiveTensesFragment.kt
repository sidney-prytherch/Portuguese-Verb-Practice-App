package com.sid.app.verbpractice.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import com.sid.app.verbpractice.R

class SetProgressiveTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetProgressiveSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetProgressiveSettings {
        fun onSetProgressiveTense(dialog: DialogFragment, result: BooleanArray)
        fun getProgressiveValues():BooleanArray
    }

    interface ProgressivePositiveClickListener {
        fun resetProgressiveTextView()
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as SetAndGetProgressiveSettings
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement ChangeProgressiveTensesDialogListener"))
        }

    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_progressive, null)

            checkBoxes = arrayOf<CheckBox>(
                view.findViewById(R.id.presProgCheckbox),
                view.findViewById(R.id.impeProgCheckbox),
                view.findViewById(R.id.pretProgCheckbox),
                view.findViewById(R.id.simpPlupProgCheckbox),
                view.findViewById(R.id.futuProgCheckbox),
                view.findViewById(R.id.condProgCheckbox),
                view.findViewById(R.id.presPerfProgCheckbox),
                view.findViewById(R.id.plupProgCheckbox),
                view.findViewById(R.id.futuPerfProgCheckbox),
                view.findViewById(R.id.condPerfProgCheckbox)
            )
            listener.getProgressiveValues().forEachIndexed{index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_indicative_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetProgressiveTense(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetProgressiveTextView()
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