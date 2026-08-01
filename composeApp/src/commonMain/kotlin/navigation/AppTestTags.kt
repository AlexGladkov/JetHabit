package navigation

/** Stable Compose semantics tags used by smoke tests. */
object AppTestTags {
    const val BottomNavigation: String = "bottom_navigation"

    /** Returns a stable tag for a bottom navigation item by route. */
    fun bottomNavigationItem(route: String): String = "bottom_navigation_item_$route"
}
