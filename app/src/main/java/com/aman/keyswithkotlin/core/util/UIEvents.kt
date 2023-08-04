sealed class UIEvents {
    data class ShowSnackBar(
        val message: String,
        val showActionButton: Boolean = false,
        val actionButtonLabel: String? = null
    ) : UIEvents() {
        init {
            require(!(showActionButton && actionButtonLabel == null)) {
                "If showActionButton is true, actionButtonLabel must not be null."
            }
        }
    }
}






