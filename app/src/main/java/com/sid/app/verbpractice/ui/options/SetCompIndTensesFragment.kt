package com.sid.app.verbpractice.ui.options

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R


class SetCompIndTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetPerfectSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetPerfectSettings {
        fun onSetCompIndTenses(dialog: DialogFragment, result: BooleanArray)
        fun getCompIndTenses():BooleanArray
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetPerfectSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetPerfectSettings"))
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_comp_ind, null)

            checkBoxes = arrayOf(
                view.findViewById(R.id.futCheckbox),
                view.findViewById(R.id.presPerfCheckbox),
                view.findViewById(R.id.plupPerfCheckbox),
                view.findViewById(R.id.futuPerfCheckbox),
                view.findViewById(R.id.condPerfCheckbox),
                view.findViewById(R.id.pastIntentCheckbox)
            )
            listener.getCompIndTenses().forEachIndexed{ index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_comp_ind_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetCompIndTenses(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetCompIndTextView()
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