# Git 12-Stage Incremental Commit Workflow
## Member 3: Financial Intelligence & Goal Engineer

This document outlines the 12-stage incremental Git commit workflow for Member 3's deliverables. Each stage represents a focused, modular engineering milestone following the Conventional Commits specification.

---

### Prerequisites & Branch Setup

To apply these commits cleanly onto a feature branch without altering your existing `main` history:

```bash
# 1. Ensure you are in the project root directory
cd "C:\Users\Heshan Ranwala\Desktop\PBD\Assignment 1 PBD"

# 2. Create and switch to a dedicated feature branch
git checkout -b feature/member3-financial-intelligence
```

---

### Stage 1: Core Domain Entities & Period Models
**Objective:** Establish the immutable snapshot carrier, time periods, and grouped categorical data models.

```bash
git add application/app/src/main/java/com/example/pbd/core/model/FinancialSnapshot.kt
git commit -m "feat(domain): define FinancialSnapshot, DatePeriod, and categorical models"
```

* **Files:** `FinancialSnapshot.kt`
* **Summary:** Defines `FinancialSnapshot`, `DatePeriod`, `IncomeBySource`, and `ExpenseByCategory` data structures.

---

### Stage 2: Savings Goal & Contribution Domain Models
**Objective:** Model multi-goal savings targets, deadlines, and contribution deposit records.

```bash
git add application/app/src/main/java/com/example/pbd/core/model/SavingsGoal.kt
git commit -m "feat(domain): define SavingsGoal, GoalContribution, and GoalStatus models"
```

* **Files:** `SavingsGoal.kt`
* **Summary:** Implements `SavingsGoal`, `GoalContribution`, and `GoalStatus` with default MacBook Pro M4 pre-seeded factory.

---

### Stage 3: Repository Interfaces & Domain Contracts
**Objective:** Define clean repository interfaces decoupling presentation logic from concrete data sources.

```bash
git add application/app/src/main/java/com/example/pbd/domain/repository/DashboardRepository.kt
git add application/app/src/main/java/com/example/pbd/domain/repository/GoalRepository.kt
git commit -m "feat(repository): define DashboardRepository and GoalRepository contracts"
```

* **Files:** `DashboardRepository.kt`, `GoalRepository.kt`
* **Summary:** Establishes reactive `Flow` and `Result<Unit>` signatures for dashboard snapshots and savings goals.

---

### Stage 4: Goal Runway & Velocity Calculation Engine
**Objective:** Implement pure Kotlin business logic for goal deficit, monthly saving velocity, and overdue deadline detection.

```bash
git add application/app/src/main/java/com/example/pbd/domain/goals/CalculateGoalProgress.kt
git commit -m "feat(domain): implement goal runway and required monthly velocity interactor"
```

* **Files:** `CalculateGoalProgress.kt`
* **Summary:** Pure Kotlin math engine computing remaining deficit, monthly pace, and clamped visual progress.

---

### Stage 5: Goal Mathematics Automated Test Suite
**Objective:** Validate all goal edge cases (overdue deadlines, exceeded targets, invalid inputs) via unit tests.

```bash
git add application/app/src/test/java/com/example/pbd/domain/goals/CalculateGoalProgressTest.kt
git commit -m "test(domain): add unit test suite for CalculateGoalProgress edge cases"
```

* **Files:** `CalculateGoalProgressTest.kt`
* **Summary:** 7 automated test cases verifying normal runway, overdue status, clamped progress, and invalid targets.

---

### Stage 6: Financial Snapshot Aggregation Engine
**Objective:** Implement the interactor aggregating raw income, expense, and goal data into a unified snapshot.

```bash
git add application/app/src/main/java/com/example/pbd/domain/dashboard/CalculateFinancialSnapshot.kt
git commit -m "feat(domain): implement CalculateFinancialSnapshot aggregation interactor"
```

* **Files:** `CalculateFinancialSnapshot.kt`
* **Summary:** Computes net cash flow, savings rate, committed vs. discretionary segregation, and daily safe-to-spend buffer.

---

### Stage 7: Financial Snapshot Automated Test Suite
**Objective:** Validate financial arithmetic, zero-division protection, and negative cashflow handling.

```bash
git add application/app/src/test/java/com/example/pbd/domain/dashboard/CalculateFinancialSnapshotTest.kt
git commit -m "test(domain): add unit test suite for CalculateFinancialSnapshot"
```

* **Files:** `CalculateFinancialSnapshotTest.kt`
* **Summary:** 8 automated test cases covering zero income, negative net cash flow, uncollected invoice exclusion, and date filtering.

---

### Stage 8: In-Memory Fake Repositories & Verification
**Objective:** Provide deterministic test repositories for isolated development without backend dependencies.

```bash
git add application/app/src/main/java/com/example/pbd/data/repository/FakeDashboardRepository.kt
git add application/app/src/main/java/com/example/pbd/data/repository/FakeGoalRepository.kt
git add application/app/src/test/java/com/example/pbd/data/repository/FakeGoalRepositoryTest.kt
git commit -m "test(repository): implement in-memory FakeDashboardRepository and FakeGoalRepository"
```

* **Files:** `FakeDashboardRepository.kt`, `FakeGoalRepository.kt`, `FakeGoalRepositoryTest.kt`
* **Summary:** Thread-safe mock stores pre-seeded with Kavindu's financial scenario and verified via 7 unit tests.

---

### Stage 9: Savings Goal State Machine & Quick-Deposit Dialog
**Objective:** Build the Goal ViewModel, immutable UI state, and quick inflow deposit dialog.

```bash
git add application/app/src/main/java/com/example/pbd/feature_goals/GoalUiState.kt
git add app/src/main/java/com/example/pbd/feature_goals/GoalViewModel.kt
git add app/src/main/java/com/example/pbd/feature_goals/components/AddContributionDialog.kt
git commit -m "feat(goals): implement GoalViewModel, GoalUiState, and AddContributionDialog"
```

* **Files:** `GoalUiState.kt`, `GoalViewModel.kt`, `AddContributionDialog.kt`
* **Summary:** Manages goal reactive states and provides 1-tap quick deposit preset chips (+5k, +10k, +25k freelance, +40k salary).

---

### Stage 10: Savings Goal UI Screen Suite (List, Detail, Create)
**Objective:** Deliver the complete user-facing screens for goal tracking, deep runway breakdown, and goal creation.

```bash
git add app/src/main/java/com/example/pbd/feature_goals/GoalListScreen.kt
git add app/src/main/java/com/example/pbd/feature_goals/GoalDetailScreen.kt
git add app/src/main/java/com/example/pbd/feature_goals/CreateGoalScreen.kt
git commit -m "feat(goals): implement GoalListScreen, GoalDetailScreen, and CreateGoalScreen"
```

* **Files:** `GoalListScreen.kt`, `GoalDetailScreen.kt`, `CreateGoalScreen.kt`
* **Summary:** Full Jetpack Compose screen suite supporting status badges, runway progress bars, and contribution history ledger.

---

### Stage 11: Financial Analytics Engine & Native Canvas Charts
**Objective:** Build Module C analytics featuring native Compose visualisations for category and income breakdowns.

```bash
git add app/src/main/java/com/example/pbd/feature_analytics/AnalyticsUiState.kt
git add app/src/main/java/com/example/pbd/feature_analytics/AnalyticsViewModel.kt
git add app/src/main/java/com/example/pbd/feature_analytics/AnalyticsScreen.kt
git add app/src/main/java/com/example/pbd/feature_analytics/components/ExpenseCategoryChart.kt
git add app/src/main/java/com/example/pbd/feature_analytics/components/CommittedVsDiscretionaryCard.kt
git add app/src/main/java/com/example/pbd/feature_analytics/components/IncomeSourceChart.kt
git commit -m "feat(analytics): implement AnalyticsScreen with native Canvas donut and split ratio cards"
```

* **Files:** `feature_analytics/*`
* **Summary:** Native Compose Canvas donut chart, committed vs. discretionary lifestyle ratio card, and multi-source income breakdown with period selector chips.

---

### Stage 12: Real-Time Sync, Navigation Shell & Documentation
**Objective:** Connect production repository streams, register feature navigation routes, and add architecture documentation.

```bash
git add app/src/main/java/com/example/pbd/data/repository/DashboardRepositoryImpl.kt
git add app/src/main/java/com/example/pbd/data/repository/GoalRepositoryImpl.kt
git add app/src/main/java/com/example/pbd/data/repository/DashboardRepositoryProvider.kt
git add app/src/main/java/com/example/pbd/ui/navigation/NavRoutes.kt
git add app/src/main/java/com/example/pbd/ui/navigation/AppNavGraph.kt
git add app/src/main/java/com/example/pbd/feature_dashboard/DashboardScreen.kt
git add app/src/test/java/com/example/pbd/data/repository/DashboardRepositoryImplTest.kt
git add MEMBER_3_TECHNICAL_DESIGN_DOCUMENT.md
git commit -m "feat(integration): integrate real-time streams, navigation routes, and technical documentation"
```

* **Files:** `DashboardRepositoryImpl.kt`, `GoalRepositoryImpl.kt`, `AppNavGraph.kt`, `NavRoutes.kt`, `DashboardScreen.kt`, `MEMBER_3_TECHNICAL_DESIGN_DOCUMENT.md`
* **Summary:** Final integration connecting real-time Firestore/Room data flows, deep navigation routing, and technical documentation.

---

### Verification After All Stages

Once all 12 stages are committed, verify build and test health:

```bash
cd application
./gradlew test
./gradlew assembleDebug
```

