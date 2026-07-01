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
import androidx.lifecycle.lifecycleScope
import com.example.hotelmanagementapp.data.repository.FirebaseMenuItem
import com.example.hotelmanagementapp.data.repository.FirebaseMenuRepository
import com.example.hotelmanagementapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val menuRepo = FirebaseMenuRepository()
    private var menuItems = listOf<FirebaseMenuItem>()
    private val priceEditTexts = mutableMapOf<String, EditText>()

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

        menuRepo.listenToMenuItems { items ->
            menuItems = items
            activity?.runOnUiThread { buildMenuList(items) }
        }

        binding.btnSavePrices.setOnClickListener {
            lifecycleScope.launch {
                priceEditTexts.forEach { (itemId, editText) ->
                    val newPrice = editText.text.toString().toIntOrNull()
                    if (newPrice != null && newPrice > 0) {
                        menuRepo.updatePrice(itemId, newPrice)
                    }
                }
                Toast.makeText(requireContext(),
                    "✅ Prices saved!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddItem.setOnClickListener {
            val name = binding.etNewName.text.toString().trim()
            val price = binding.etNewPrice.text.toString().toIntOrNull()

            if (name.isEmpty() || price == null || price <= 0) {
                Toast.makeText(requireContext(),
                    "Please enter name and valid price!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                menuRepo.addMenuItem(
                    FirebaseMenuItem(
                        name = name,
                        price = price,
                        emoji = "🍽"
                    )
                )
                binding.etNewName.setText("")
                binding.etNewPrice.setText("")
                Toast.makeText(requireContext(),
                    "✅ $name added!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildMenuList(items: List<FirebaseMenuItem>) {
        val list = binding.menuItemsList
        list.removeAllViews()
        priceEditTexts.clear()

        items.forEach { item ->
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
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

            val deleteBtn = android.widget.Button(requireContext())
            deleteBtn.text = "🗑"
            deleteBtn.textSize = 12f
            deleteBtn.setBackgroundColor(0xFFE24B4A.toInt())
            deleteBtn.setTextColor(0xFFFFFFFF.toInt())
            val delParams = LinearLayout.LayoutParams(80,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            delParams.marginStart = 6
            deleteBtn.layoutParams = delParams
            deleteBtn.setOnClickListener {
                lifecycleScope.launch {
                    menuRepo.deleteMenuItem(item.id)
                    Toast.makeText(requireContext(),
                        "${item.name} removed!", Toast.LENGTH_SHORT).show()
                }
            }

            row.addView(nameView)
            row.addView(priceEdit)
            row.addView(deleteBtn)
            list.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}