package com.sid.app.verbpractice.ui.options

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R
import kotlinx.android.synthetic.main.tenses_options_prog_ind.view.*

class SetProgIndTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetProgressiveSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetProgressiveSettings {
        fun onSetProgIndTenses(dialog: DialogFragment, result: BooleanArray)
        fun getProgIndTenses():BooleanArray
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetProgressiveSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetProgressiveSettings"))
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_prog_ind, null)

            checkBoxes = arrayOf(
                view.presProgCheckbox,
                view.pretProgCheckbox,
                view.impeProgCheckbox,
                view.simpPlupProgCheckbox,
                view.futuProgCheckbox,
                view.condProgCheckbox,
                view.presPerfProgCheckbox,
                view.plupProgCheckbox,
                view.futuPerfProgCheckbox,
                view.condPerfProgCheckbox
            )
            listener.getProgIndTenses().forEachIndexed{ index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_prog_ind_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetProgIndTenses(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetProgIndTextView()
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