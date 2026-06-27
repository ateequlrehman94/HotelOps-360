package com.example.hotelmanagementapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hotelmanagementapp.data.model.MenuItem
import com.example.hotelmanagementapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MenuViewModel by viewModels()
    private var menuItems = listOf<MenuItem>()
    private val priceEditTexts = mutableMapOf<Int, EditText>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allMenuItems.observe(viewLifecycleOwner) { items ->
            menuItems = items
            buildMenuList(items)
        }

        binding.btnSavePrices.setOnClickListener {
            priceEditTexts.forEach { (itemId, editText) ->
                val newPrice = editText.text.toString().toIntOrNull()
                if (newPrice != null && newPrice > 0) {
                    viewModel.updatePrice(itemId, newPrice)
                }
            }
            Toast.makeText(requireContext(), "Prices saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddItem.setOnClickListener {
            val name = binding.etNewName.text.toString().trim()
            val urdu = binding.etNewUrdu.text.toString().trim()
            val price = binding.etNewPrice.text.toString().toIntOrNull()
            val emoji = binding.etNewEmoji.text.toString().trim()

            if (name.isEmpty() || price == null || price <= 0) {
                Toast.makeText(requireContext(),
                    "Please enter name and valid price!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.insertMenuItem(
                MenuItem(
                    name = name,
                    urduName = urdu.ifEmpty { name },
                    price = price,
                    emoji = emoji.ifEmpty { "🍽" }
                )
            )

            binding.etNewName.setText("")
            binding.etNewUrdu.setText("")
            binding.etNewPrice.setText("")
            binding.etNewEmoji.setText("")
            Toast.makeText(requireContext(), "$name added!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildMenuList(items: List<MenuItem>) {
        val list = binding.menuItemsList
        list.removeAllViews()
        priceEditTexts.clear()

        items.forEach { item ->
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 12
            row.layoutParams = params

            val nameView = TextView(requireContext())
            nameView.text = "${item.emoji} ${item.name}"
            nameView.textSize = 14f
            nameView.layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val priceEdit = EditText(requireContext())
            priceEdit.setText(item.price.toString())
            priceEdit.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            priceEdit.textSize = 14f
            val priceParams = LinearLayout.LayoutParams(180,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            priceEdit.layoutParams = priceParams
            priceEditTexts[item.id] = priceEdit

            row.addView(nameView)
            row.addView(priceEdit)
            list.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}