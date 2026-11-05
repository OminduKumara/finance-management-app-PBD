package com.example.pbdassignment.data.repository

import com.example.pbdassignment.domain.repository.DashboardRepository

/**
 * Clean provider for obtaining the active [DashboardRepository] instance.
 * Allows effortless swapping with [FakeDashboardRepository] in tests and previews.
 */
object DashboardRepositoryProvider {
    var instance: DashboardRepository = DashboardRepositoryImpl()
}
