package com.example.pbd.data.repository

import com.example.pbd.domain.repository.DashboardRepository

/**
 * Clean provider for obtaining the active [DashboardRepository] instance.
 * Allows effortless swapping with [FakeDashboardRepository] in tests and previews.
 */
object DashboardRepositoryProvider {
    var instance: DashboardRepository = DashboardRepositoryImpl()
}
