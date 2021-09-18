package ru.alexgladkov.jetpackcomposedemo.base

interface EventHandler<T> {
    fun obtainEvent(event: T)
}