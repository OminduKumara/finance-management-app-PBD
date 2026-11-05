package com.example.pbdassignment.core.firebase

import android.util.Log
import com.example.pbdassignment.core.data.FinanceRepository
import com.example.pbdassignment.core.model.DefaultExpenseCategories
import com.example.pbdassignment.core.model.ExpenseItem
import com.example.pbdassignment.core.model.ExpenseNature
import com.example.pbdassignment.core.model.FinancialBaseline
import com.example.pbdassignment.core.model.GoalItem
import com.example.pbdassignment.core.model.IncomeItem
import com.example.pbdassignment.core.model.IncomeStreams
import com.example.pbdassignment.core.model.InvoiceStatus
import com.example.pbdassignment.core.model.PaymentSource
import com.example.pbdassignment.feature.category.domain.Category
import com.example.pbdassignment.feature.expense.domain.Expense
import com.example.pbdassignment.feature.recurring.domain.RecurringExpense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private var incomeListener: ListenerRegistration? = null
    private var expenseListener: ListenerRegistration? = null
    private var goalListener: ListenerRegistration? = null
    private var baselineListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user != null) {
                attachRealtimeListeners(user.uid)
            } else {
                detachListeners()
            }
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserId(): String = auth.currentUser?.uid ?: "anonymous_kavindu"

    fun getCurrentUserEmail(): String = auth.currentUser?.email ?: "kavindu.silva@startup.lk"

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _currentUser.value = it.user
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Sign in failed: ${e.message}")
                onResult(false, e.localizedMessage ?: "Authentication failed")
            }
    }

    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _currentUser.value = it.user
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Sign up failed: ${e.message}")
                onResult(false, e.localizedMessage ?: "Registration failed")
            }
    }

    fun signInAsDemoKavindu(onResult: (Boolean, String?) -> Unit) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                _currentUser.value = it.user
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Anonymous auth failed, falling back to local demo: ${e.message}")
                onResult(true, null) // Allow offline viva demo
            }
    }

    fun updatePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "No active user logged in")
            return
        }
        user.updatePassword(newPassword)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e ->
                Log.e(TAG, "Update password failed: ${e.message}")
                onResult(false, e.localizedMessage ?: "Password update failed")
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        detachListeners()
    }

    // --- Firestore Realtime Synchronization ---

    fun attachRealtimeListeners(userId: String) {
        detachListeners()

        // Listen to Incomes
        incomeListener = firestore.collection("users").document(userId)
            .collection("incomes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen incomes failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val remoteIncomes = snapshot.documents.mapNotNull { doc ->
                        try {
                            IncomeItem(
                                id = doc.getString("id") ?: doc.id,
                                streamType = doc.getString("streamType") ?: IncomeStreams.SALARY,
                                title = doc.getString("title") ?: "",
                                clientOrSource = doc.getString("clientOrSource") ?: "",
                                rawAmount = doc.getDouble("rawAmount") ?: 0.0,
                                currency = doc.getString("currency") ?: "LKR",
                                exchangeRate = doc.getDouble("exchangeRate") ?: 1.0,
                                lkrAmount = doc.getDouble("lkrAmount") ?: 0.0,
                                invoiceStatus = InvoiceStatus.valueOf(doc.getString("invoiceStatus") ?: "RECEIVED")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteIncomes.isNotEmpty()) {
                        remoteIncomes.forEach { FinanceRepository.addIncome(it) }
                    }
                }
            }

        // Listen to Expenses
        expenseListener = firestore.collection("users").document(userId)
            .collection("expenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen expenses failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val remoteExpenses = snapshot.documents.mapNotNull { doc ->
                        try {
                            ExpenseItem(
                                id = doc.getString("id") ?: doc.id,
                                amount = doc.getDouble("amount") ?: 0.0,
                                category = doc.getString("category") ?: DefaultExpenseCategories.OTHER,
                                nature = ExpenseNature.valueOf(doc.getString("nature") ?: "DISCRETIONARY"),
                                paymentSource = PaymentSource.valueOf(doc.getString("paymentSource") ?: "PRIMARY_CARD"),
                                merchantPreset = doc.getString("merchantPreset"),
                                notes = doc.getString("notes") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteExpenses.isNotEmpty()) {
                        remoteExpenses.forEach { FinanceRepository.upsertLocalExpense(it) }
                    }
                }
            }

        // Listen to Goal
        goalListener = firestore.collection("users").document(userId)
            .collection("goals").document("macbook_pro_m4")
            .addSnapshotListener { doc, error ->
                if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
                val saved = doc.getDouble("currentSaved") ?: doc.getDouble("totalSavedLkr") ?: 11200.0
                val target = doc.getDouble("targetAmount") ?: doc.getDouble("targetAmountLkr") ?: 490000.0
                FinanceRepository.updateGoalTarget(target)
                FinanceRepository.allocateToGoal(saved - FinanceRepository.goal.value.currentSaved)
            }

        // Listen to Financial Baseline
        baselineListener = firestore.collection("users").document(userId)
            .collection("settings").document("baseline")
            .addSnapshotListener { doc, error ->
                if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
                try {
                    val defaultBaseline = FinancialBaseline()
                    @Suppress("UNCHECKED_CAST")
                    val rules = (doc.get("rules") as? List<*>)?.mapNotNull { it as? String } ?: defaultBaseline.rules
                    val remoteBaseline = FinancialBaseline(
                        baseSalary = doc.getDouble("baseSalary") ?: defaultBaseline.baseSalary,
                        committedRent = doc.getDouble("committedRent") ?: defaultBaseline.committedRent,
                        committedGym = doc.getDouble("committedGym") ?: defaultBaseline.committedGym,
                        discretionaryBudgetCap = doc.getDouble("discretionaryBudgetCap") ?: defaultBaseline.discretionaryBudgetCap,
                        targetSavingsRate = doc.getDouble("targetSavingsRate") ?: defaultBaseline.targetSavingsRate,
                        rules = rules
                    )
                    FinanceRepository.updateLocalBaseline(remoteBaseline)
                } catch (e: Exception) {
                    Log.w(TAG, "Parse baseline failed: ${e.message}")
                }
            }
    }

    fun syncIncome(income: IncomeItem) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to income.id,
            "userId" to uid,
            "streamType" to income.streamType,
            "title" to income.title,
            "clientOrSource" to income.clientOrSource,
            "rawAmount" to income.rawAmount,
            "currency" to income.currency,
            "exchangeRate" to income.exchangeRate,
            "lkrAmount" to income.lkrAmount,
            "invoiceStatus" to income.invoiceStatus.name,
            "date" to income.date
        )
        firestore.collection("users").document(uid)
            .collection("incomes").document(income.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync income failed: ${it.message}") }
    }

    fun syncExpense(expense: ExpenseItem) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to expense.id,
            "userId" to uid,
            "amount" to expense.amount,
            "currency" to "LKR",
            "category" to expense.category,
            "nature" to expense.nature.name,
            "paymentSource" to expense.paymentSource.name,
            "merchantPreset" to (expense.merchantPreset ?: ""),
            "notes" to expense.notes,
            "timestamp" to expense.timestamp
        )
        firestore.collection("users").document(uid)
            .collection("expenses").document(expense.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync expense failed: ${it.message}") }
    }

    fun syncExpense(expense: Expense) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to expense.id,
            "userId" to uid,
            "title" to expense.title,
            "amount" to expense.amount,
            "currencyCode" to expense.currencyCode,
            "categoryId" to expense.categoryId,
            "categoryName" to expense.categoryName,
            "dateMillis" to expense.dateMillis,
            "isCommitted" to expense.isCommitted,
            "paymentMethod" to expense.paymentMethod,
            "notes" to expense.notes,
            "category" to expense.categoryName,
            "nature" to if (expense.isCommitted) "COMMITTED" else "DISCRETIONARY",
            "paymentSource" to when (expense.paymentMethod.lowercase()) {
                "credit card", "credit_card", "card" -> "PRIMARY_CARD"
                "bank transfer", "debit card" -> "SECONDARY_ACCOUNT"
                else -> "CASH"
            },
            "merchantPreset" to expense.title,
            "timestamp" to expense.dateMillis
        )
        firestore.collection("users").document(uid)
            .collection("expenses").document(expense.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync expense failed: ${it.message}") }
    }

    fun syncCategory(category: Category) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to category.id,
            "userId" to uid,
            "name" to category.name,
            "icon" to category.icon,
            "color" to category.color,
            "isDefault" to category.isDefault
        )
        firestore.collection("users").document(uid)
            .collection("categories").document(category.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync category failed: ${it.message}") }
    }

    fun deleteCategory(id: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("categories").document(id)
            .delete()
            .addOnFailureListener { Log.w(TAG, "Delete category failed: ${it.message}") }
    }

    fun syncRecurringExpense(recurring: RecurringExpense) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to recurring.id,
            "userId" to uid,
            "title" to recurring.title,
            "amount" to recurring.amount,
            "currencyCode" to recurring.currencyCode,
            "categoryId" to recurring.categoryId,
            "categoryName" to recurring.categoryName,
            "paymentMethod" to recurring.paymentMethod,
            "frequency" to recurring.frequency,
            "startDateMillis" to recurring.startDateMillis,
            "endDateMillis" to (recurring.endDateMillis ?: 0L),
            "isActive" to recurring.isActive,
            "notes" to recurring.notes,
            "lastGeneratedDateMillis" to (recurring.lastGeneratedDateMillis ?: 0L)
        )
        firestore.collection("users").document(uid)
            .collection("recurring_expenses").document(recurring.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync recurring expense failed: ${it.message}") }
    }

    fun deleteRecurringExpense(id: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("recurring_expenses").document(id)
            .delete()
            .addOnFailureListener { Log.w(TAG, "Delete recurring expense failed: ${it.message}") }
    }

    fun deleteIncome(id: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("incomes").document(id)
            .delete()
            .addOnFailureListener { Log.w(TAG, "Delete income failed: ${it.message}") }
    }

    fun deleteExpense(id: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("expenses").document(id)
            .delete()
            .addOnFailureListener { Log.w(TAG, "Delete expense failed: ${it.message}") }
    }

    fun syncGoal(goal: GoalItem) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "id" to goal.id,
            "userId" to uid,
            "title" to goal.title,
            "targetAmount" to goal.targetAmount,
            "currentSaved" to goal.currentSaved,
            "targetMonths" to goal.targetMonths
        )
        firestore.collection("users").document(uid)
            .collection("goals").document(goal.id)
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync goal failed: ${it.message}") }
    }

    fun syncBaseline(baseline: FinancialBaseline) {
        val uid = auth.currentUser?.uid ?: return
        val docData = hashMapOf(
            "userId" to uid,
            "baseSalary" to baseline.baseSalary,
            "committedRent" to baseline.committedRent,
            "committedGym" to baseline.committedGym,
            "discretionaryBudgetCap" to baseline.discretionaryBudgetCap,
            "targetSavingsRate" to baseline.targetSavingsRate,
            "rules" to baseline.rules
        )
        firestore.collection("users").document(uid)
            .collection("settings").document("baseline")
            .set(docData)
            .addOnFailureListener { Log.w(TAG, "Sync baseline failed: ${it.message}") }
    }

    private fun detachListeners() {
        incomeListener?.remove()
        incomeListener = null
        expenseListener?.remove()
        expenseListener = null
        goalListener?.remove()
        goalListener = null
        baselineListener?.remove()
        baselineListener = null
    }
}
