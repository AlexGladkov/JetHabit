package base

interface EventHandler<T> {
    fun obtainEvent(event: T)
}