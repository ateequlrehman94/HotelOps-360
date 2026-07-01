package com.example.hotelmanagementapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.hotelmanagementapp.R
import com.example.hotelmanagementapp.data.repository.FirebaseDueRepository
import com.example.hotelmanagementapp.data.repository.FirebaseOrderRepository
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private val orderRepo = FirebaseOrderRepository()
    private val dueRepo = FirebaseDueRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setupHeader(view)
        setupQuickStats(view)
        applyRoleVisibility(view)
        setupNavigation(view)
    }

    private fun setupHeader(view: View) {
        view.findViewById<TextView>(R.id.tvWelcome)?.text =
            "Welcome, ${sessionManager.getFullName()} 👋"
        view.findViewById<TextView>(R.id.tvRoleBadge)?.text =
            sessionManager.getRole().replaceFirstChar { it.uppercase() }

        view.findViewById<android.widget.Button>(R.id.btnLogout)?.setOnClickListener {
            FirebaseManager.auth.signOut()
            sessionManager.clearSession()
            navigateToLogin()
        }
    }

    private fun setupQuickStats(view: View) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        orderRepo.listenToPaidOrdersSince(cal.timeInMillis) { orders ->
            activity?.runOnUiThread {
                val total = orders.sumOf { it.totalAmount }
                view.findViewById<TextView>(R.id.tvHomeSales)?.text = "Rs. $total"
            }
        }

        orderRepo.listenToActiveOrders { orders ->
            activity?.runOnUiThread {
                view.findViewById<TextView>(R.id.tvHomeActive)?.text =
                    orders.size.toString()
            }
        }

        dueRepo.listenToActiveDues { dues ->
            activity?.runOnUiThread {
                val total = dues.sumOf { it.dueAmount }
                view.findViewById<TextView>(R.id.tvHomeDues)?.text = "Rs. $total"
            }
        }
    }

    private fun applyRoleVisibility(view: View) {
        // Cards only admin can see
        val adminOnlyCards = listOf(
            R.id.cardExpense,
            R.id.cardInventory,
            R.id.cardReports,
            R.id.cardSettings,
            R.id.cardUsers,
            R.id.cardAudit
        )

        adminOnlyCards.forEach { id ->
            view.findViewById<LinearLayout>(id)?.visibility =
                if (sessionManager.hasPermission(
                        when (id) {
                            R.id.cardExpense -> RolePermissions.VIEW_EXPENSES
                            R.id.cardInventory -> RolePermissions.VIEW_INVENTORY
                            R.id.cardReports -> RolePermissions.VIEW_REPORTS
                            R.id.cardSettings -> RolePermissions.VIEW_SETTINGS
                            R.id.cardUsers -> RolePermissions.MANAGE_USERS
                            R.id.cardAudit -> RolePermissions.VIEW_AUDIT
                            else -> ""
                        }
                    )
                ) View.VISIBLE else View.GONE
        }

        // Dues visible to all roles
        view.findViewById<LinearLayout>(R.id.cardDues)?.visibility =
            if (sessionManager.hasPermission(RolePermissions.VIEW_DUES))
                View.VISIBLE else View.GONE
    }

    private fun setupNavigation(view: View) {
        view.findViewById<LinearLayout>(R.id.cardOrder)?.setOnClickListener {
            findNavController().navigate(R.id.orderFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardKitchen)?.setOnClickListener {
            findNavController().navigate(R.id.kitchenFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardDues)?.setOnClickListener {
            findNavController().navigate(R.id.duesFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardExpense)?.setOnClickListener {
            findNavController().navigate(R.id.expenseFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardInventory)?.setOnClickListener {
            findNavController().navigate(R.id.inventoryFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardReports)?.setOnClickListener {
            findNavController().navigate(R.id.statsFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardSettings)?.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardUsers)?.setOnClickListener {
            findNavController().navigate(R.id.userManagementFragment)
        }
        view.findViewById<LinearLayout>(R.id.cardAudit)?.setOnClickListener {
            findNavController().navigate(R.id.auditFragment)
        }
    }

    private fun navigateToLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()
        findNavController().navigate(R.id.loginFragment, null, navOptions)
    }
}