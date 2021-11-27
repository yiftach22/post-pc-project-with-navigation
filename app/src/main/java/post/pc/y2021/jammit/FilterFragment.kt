package post.pc.y2021.jammit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import post.pc.y2021.jammit.Database.Companion.possibleInstruments


class FilterFragment : Fragment() {
    private lateinit var radiusValueView: TextView
    private lateinit var seekBar: SeekBar
    private var distance = 10
    private lateinit var applyButton: Button
    private lateinit var cancelButton: Button
    private  val filterViewModel: FilterViewModel by activityViewModels()
    private lateinit var checkedInstruments:MutableList<String>
    private lateinit var guitarCheckBox: CheckBox
    private lateinit var instrumentsCheckboxTable:TableLayout
    private val database = JammitApp.instance.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkedInstruments = filterViewModel.checkedInstruments.toMutableList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        radiusValueView = view.findViewById(R.id.radiusValue)
        seekBar = view.findViewById(R.id.seekBar)
        applyButton = view.findViewById(R.id.applyButton)
        cancelButton = view.findViewById(R.id.cancelButton)
        guitarCheckBox = view.findViewById(R.id.guitarCheckBox)
        instrumentsCheckboxTable =  view.findViewById(R.id.instrumentsCheckboxTable)


        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radiusValueView.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                distance = seekBar?.progress ?: 10
            }
        })

        // Set instruments checkBoxes
        var index = 0
        for (i in 0 until instrumentsCheckboxTable.size){ // Iterate over tableLayout
            val row:TableRow = instrumentsCheckboxTable.getChildAt(i) as TableRow
            for (j in 0 until row.size){
                val checkBox:CheckBox = row.getChildAt(j) as CheckBox
                checkBox.text = if (index < possibleInstruments.size) Database.possibleInstruments[index] else ""
                checkBox.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
                    if (checkBox.isChecked){
                        checkedInstruments.add(checkBox.text.toString())
                    } else{
                        checkedInstruments.remove(checkBox.text.toString())
                    }
                }
                if (checkedInstruments.contains(checkBox.text.toString()))
                    checkBox.isChecked = true
                index++
            }
        }


        applyButton.setOnClickListener {
            filterViewModel.filterUsersByInstrument(checkedInstruments)
            filterViewModel.checkedInstruments = checkedInstruments
            activity?.onBackPressed()
        }

        cancelButton.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }

}