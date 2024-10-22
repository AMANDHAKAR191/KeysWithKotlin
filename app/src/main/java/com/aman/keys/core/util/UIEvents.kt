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

    data class ShowErrorDialog(val message: String):UIEvents()
    object SavePassword : UIEvents()
    object SaveNote : UIEvents()
    data class SendNotification(val publicUid:String, val messageBody:String? = null) :UIEvents()

    object ShowLoadingBar:UIEvents()

    object ShowAlertDialog:UIEvents()

    object HideAlertDialog:UIEvents()
    data class ShowAuthorizationAlertDialog(val requesterID:String,val authorizationCode:Int):UIEvents()
    object HideAuthorizationAlertDialog:UIEvents()

    data class ShowError(val errorMessage:String):UIEvents()

    object NavigateToNextScreen:UIEvents()
    object ChatUsrCreatedSuccessFully : UIEvents()

}






