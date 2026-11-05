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
import com.example.pbdassignment.feature.income.data.IncomeRepositoryProvider
import com.example.pbdassignment.feature.income.data.LocalIncomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _customDisplayName = MutableStateFlow<String?>(null)
    val customDisplayName: StateFlow<String?> = _customDisplayName.asStateFlow()

    private var userListener: ListenerRegistration? = null
    private var incomeListener: ListenerRegistration? = null
    private var expenseListener: ListenerRegistration? = null
    private var goalListener: ListenerRegistration? = null
    private var baselineListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user != null) {
                ensureUserDocument(user)
                attachRealtimeListeners(user.uid)
            } else {
                detachListeners()
            }
        }
    }

    fun ensureUserDocument(user: FirebaseUser) {
        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
            ?: "User"
        val data = mapOf(
            "uid" to user.uid,
            "userId" to user.uid,
            "email" to (user.email ?: ""),
            "displayName" to name,
            "name" to name,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(user.uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnFailureListener { Log.w(TAG, "Ensure user document failed: ${it.message}") }
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

    fun updateDisplayName(newName: String, onResult: (Boolean, String?) -> Unit) {
        val trimmed = newName.trim()
        if (trimmed.isBlank()) {
            onResult(false, "User name cannot be empty")
            return
        }
        _customDisplayName.value = trimmed
        val user = auth.currentUser
        if (user == null) {
            onResult(true, null)
            return
        }

        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(trimmed)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener {
                firestore.collection("users").document(user.uid)
                    .set(mapOf("displayName" to trimmed, "name" to trimmed), com.google.firebase.firestore.SetOptions.merge())
                    .addOnFailureListener { e -> Log.w(TAG, "Save name to Firestore failed: ${e.message}") }
                _currentUser.value = auth.currentUser
                onResult(true, null)
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _customDisplayName.value = null
        detachListeners()
        FinanceRepository.clearLocalData()
        (com.example.pbdassignment.feature.goals.GoalRepositoryProvider.instance as? com.example.pbdassignment.data.repository.GoalRepositoryImpl)?.clearLocalGoals()
        IncomeRepositoryProvider.instance?.let { repo: LocalIncomeRepository ->
            CoroutineScope(Dispatchers.IO).launch {
                repo.clearIncomes()
            }
        }
    }

    // --- Firestore Realtime Synchronization ---

    fun attachRealtimeListeners(userId: String) {
        detachListeners()

        userListener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("displayName") ?: snapshot.getString("name")
                    if (!name.isNullOrBlank()) {
                        _customDisplayName.value = name
                    }
                }
            }

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
                            val raw = doc.getDouble("originalAmount") ?: doc.getDouble("rawAmount") ?: 0.0
                            val rate = doc.getDouble("exchangeRate") ?: 1.0
                            val lkr = doc.getDouble("convertedAmountLkr") ?: doc.getDouble("lkrAmount") ?: (raw * rate)
                            val invStatusStr = doc.getString("invoiceStatus") ?: "RECEIVED"
                            val parsedStatus = try {
                                InvoiceStatus.valueOf(invStatusStr)
                            } catch (_: Exception) {
                                InvoiceStatus.RECEIVED
                            }
                            IncomeItem(
                                id = doc.getString("id") ?: doc.id,
                                streamType = doc.getString("source") ?: doc.getString("streamType") ?: IncomeStreams.SALARY,
                                title = doc.getString("title") ?: "",
                                clientOrSource = doc.getString("clientName") ?: doc.getString("clientOrSource") ?: "",
                                rawAmount = raw,
                                currency = doc.getString("currency") ?: "LKR",
                                exchangeRate = rate,
                                lkrAmount = lkr,
                                invoiceStatus = parsedStatus
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (remoteIncomes.isNotEmpty()) {
                        remoteIncomes.forEach { FinanceRepository.upsertLocalIncome(it) }
                        IncomeRepositoryProvider.instance?.let { repo: LocalIncomeRepository ->
                            CoroutineScope(Dispatchers.IO).launch {
                                remoteIncomes.forEach { repo.addIncome(it) }
                            }
                        }
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
            .collection("goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) return@addSnapshotListener
                val doc = snapshot.documents.firstOrNull() ?: return@addSnapshotListener
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: doc.getString("name") ?: "Savings Goal"
                val saved = doc.getDouble("currentSaved") ?: doc.getDouble("totalSavedLkr") ?: 0.0
                val target = doc.getDouble("targetAmount") ?: doc.getDouble("targetAmountLkr") ?: 0.0
                val months = (doc.getLong("targetMonths") ?: 12L).toInt()
                FinanceRepository.updateLocalGoal(
                    GoalItem(
                        id = id,
                        title = title,
                        targetAmount = target,
                        currentSaved = saved,
                        targetMonths = months
                    )
                )
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
        val raw = if (income.rawAmount > 0) income.rawAmount else 1.0
        val lkr = if (income.lkrAmount > 0) income.lkrAmount else (raw * income.exchangeRate)
        val sourceStr = when (income.streamType.uppercase()) {
            "SALARY" -> "SALARY"
            "FREELANCE" -> "FREELANCE"
            "ADSENSE", "ADSENSE (USD)" -> "ADSENSE"
            "CRYPTO", "CRYPTO P&L" -> "CRYPTO"
            else -> "SALARY"
        }
        val docData = hashMapOf(
            "id" to income.id,
            "userId" to uid,
            "streamType" to income.streamType,
            "source" to sourceStr,
            "title" to income.title.ifBlank { "Income" },
            "clientOrSource" to income.clientOrSource,
            "clientName" to income.clientOrSource,
            "rawAmount" to raw,
            "originalAmount" to raw,
            "currency" to income.currency,
            "exchangeRate" to income.exchangeRate,
            "lkrAmount" to lkr,
            "convertedAmountLkr" to lkr,
            "invoiceStatus" to income.invoiceStatus.name,
            "date" to income.date,
            "dateTimestamp" to System.currentTimeMillis()
        )
        firestore.collection("users").document(uid)
            .collection("incomes").document(income.id)
            .set(docData, com.google.firebase.firestore.SetOptions.merge())
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
        userListener?.remove()
        userListener = null
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
