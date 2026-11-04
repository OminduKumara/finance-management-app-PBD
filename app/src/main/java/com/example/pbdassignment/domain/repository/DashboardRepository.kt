package com.example.pbdassignment.domain.repository

import com.example.pbdassignment.core.model.DatePeriod
import com.example.pbdassignment.core.model.FinancialSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining the financial overview and snapshot contracts.
 * Decouples the presentation layer from specific data sources (Firestore, Room, or Fake).
 */
interface DashboardRepository {

    /**
     * Emits real-time reactive updates of the financial snapshot for the specified period.
     */
    fun observeFinancialSnapshot(period: DatePeriod = DatePeriod.allTime()): Flow<FinancialSnapshot>

    /**
     * One-shot fetch of the financial snapshot for synchronous/coroutine evaluations.
     */
    suspend fun getFinancialSnapshot(period: DatePeriod = DatePeriod.allTime()): FinancialSnapshot
}
