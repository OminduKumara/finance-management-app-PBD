package com.example.pbdassignment.feature.category.data

import com.example.pbdassignment.feature.category.domain.Category

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )
}