package com.example.pbdassignment.feature.income.data

import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.income.domain.CryptoTradeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRecord
import com.example.pbdassignment.feature.income.domain.IncomeRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class IncomeRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : IncomeRepository {
    override fun getIncomes(userId: String): Flow<List<IncomeRecord>> = callbackFlow {
        val listener = firestore
            .collection("users")
            .document(userId)
            .collection("incomes")
            .orderBy("dateTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val incomes = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(IncomeDto::class.java)?.also { it.id = doc.id }?.toDomain()
                    }?.distinctBy { it.id }
                    ?: emptyList()
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }
    override suspend fun addIncome(income: IncomeRecord): Resource<Unit> {
        return try {
            val docRef = if (income.id.isNotBlank()) {
                firestore
                    .collection("users")
                    .document(income.userId)
                    .collection("incomes")
                    .document(income.id)
            } else {
                firestore
                    .collection("users")
                    .document(income.userId)
                    .collection("incomes")
                    .document()
            }
            val dto = income.toDto().copy(id = docRef.id)
            docRef.set(dto).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to save income record")
        }
    }
    override suspend fun deleteIncome(incomeId: String, userId: String): Resource<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .document(incomeId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to delete income record")
        }
    }
    override fun getCryptoTrades(userId: String): Flow<List<CryptoTradeRecord>> = callbackFlow {
        val listener = firestore
            .collection("users")
            .document(userId)
            .collection("crypto_trades")
            .orderBy("dateTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val trades = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(CryptoTradeDto::class.java)?.also { it.id = doc.id }?.toDomain()
                    } ?: emptyList()
                trySend(trades)
            }
        awaitClose { listener.remove() }
    }
    override suspend fun addCryptoTrade(trade: CryptoTradeRecord): Resource<Unit> {
        return try {
            val docRef = firestore
                .collection("users")
                .document(trade.userId)
                .collection("crypto_trades")
                .document()
            val dto = trade.toDto().copy(id = docRef.id)
            docRef.set(dto).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to save crypto trade")
        }
    }
}