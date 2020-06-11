package com.sid.app.verbpractice.ui.options

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R
import kotlinx.android.synthetic.main.tenses_options_simp_ind.view.*

class SetSimpIndTensesFragment : DialogFragment() {

    private lateinit var listener: SetAndGetIndicativeSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetIndicativeSettings {
        fun onSetSimpIndTenses(dialog: DialogFragment, result: BooleanArray)
        fun getSimpIndTenses():BooleanArray
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetIndicativeSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetIndicativeSettings"))
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.tenses_options_simp_ind, null)

            checkBoxes = arrayOf(
                view.presIndCheckbox,
                view.pretIndCheckbox,
                view.impeIndCheckbox,
                view.plupIndCheckbox,
                view.futuSimpIndCheckbox,
                view.condIndCheckbox
            )
            listener.getSimpIndTenses().forEachIndexed{ index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_simp_ind_tenses)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetSimpIndTenses(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetSimpIndTextView()
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