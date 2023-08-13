import com.aman.keyswithkotlin.navigation.Screen
import com.aman.keyswithkotlin.passwords.presentation.add_edit_password.AddEditPasswordViewModel

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
    object SavePassword : UIEvents()

    object ShowAlertDialog:UIEvents()

    object ShowLoadingBar:UIEvents()

    data class ShowError(val errorMessage:String):UIEvents()
    object HideAlertDialog:UIEvents()

    object NavigateToNextScreen:UIEvents()
    object ChatUsrCreatedSuccessFully : UIEvents()

}






