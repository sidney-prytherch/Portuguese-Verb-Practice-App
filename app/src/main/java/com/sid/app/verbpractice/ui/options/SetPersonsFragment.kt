package com.sid.app.verbpractice.ui.options

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sid.app.verbpractice.R


class SetPersonsFragment : DialogFragment() {

    private lateinit var listener: SetAndGetPersonsSettings
    private var checkBoxes = arrayOf<CheckBox>()

    interface SetAndGetPersonsSettings {
        fun onSetPersons(dialog: DialogFragment, result: BooleanArray)
        fun getPersons():BooleanArray
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetAndGetPersonsSettings
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement SetAndGetPersonsSettings"))
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.persons_options, null)

            checkBoxes = arrayOf(
                view.findViewById(R.id.vcCheckbox),
                view.findViewById(R.id.vcsCheckbox),
                view.findViewById(R.id.eleElaCheckbox),
                view.findViewById(R.id.elesElasCheckbox),
                view.findViewById(R.id.senhorCheckbox),
                view.findViewById(R.id.senhoresCheckbox)
            )
            listener.getPersons().forEachIndexed{ index, isSet -> checkBoxes[index].isChecked = isSet}

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setTitle(R.string.set_enabled_third_person)
                .setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onSetPersons(this,
                        checkBoxes.map { checkBox -> checkBox.isChecked }.toBooleanArray()
                    )
                    (parentFragmentManager.primaryNavigationFragment!! as OptionsFragment).resetPersonsTextView()
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