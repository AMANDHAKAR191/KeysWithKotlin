package com.aman.keyswithkotlin.notes.domain.use_cases

import com.aman.keyswithkotlin.notes.domain.repository.NoteRepository
import com.aman.keyswithkotlin.passwords.domain.repository.PasswordRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Random


class ShareNote(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(
        max_length: Int,
        upperCase: Boolean,
        lowerCase: Boolean,
        numbers: Boolean,
        specialCharacters: Boolean
    ): Flow<String> {
        return generatePassword(max_length, upperCase, lowerCase, numbers, specialCharacters)
    }

    private fun generatePassword(
        max_length: Int,
        upperCase: Boolean,
        lowerCase: Boolean,
        numbers: Boolean,
        specialCharacters: Boolean
    ): Flow<String> = callbackFlow {
        val rn = Random()
        val sb = StringBuilder(max_length)
        try {
            val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
            val numberChars = "0123456789"
            val specialChars = "!@#$%^&*()_-+=<>?/{}~|"
            var allowedChars = ""


            //this will fulfill the requirements of atleast one character of a type.
            if (upperCase) {
                allowedChars += upperCaseChars
                sb.append(upperCaseChars[rn.nextInt(upperCaseChars.length - 1)])
            }
            if (lowerCase) {
                allowedChars += lowerCaseChars
                sb.append(lowerCaseChars[rn.nextInt(lowerCaseChars.length - 1)])
            }
            if (numbers) {
                allowedChars += numberChars
                sb.append(numberChars[rn.nextInt(numberChars.length - 1)])
            }
            if (specialCharacters) {
                allowedChars += specialChars
                sb.append(specialChars[rn.nextInt(specialChars.length - 1)])
            }
            //fill the allowed length from different chars now.
            for (i in sb.length until max_length) {
                sb.append(allowedChars[rn.nextInt(allowedChars.length)])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        trySend(sb.toString())
        awaitClose {
            close()
        }
    }
//    protected fun genrate_password() {
//        slider.addOnChangeListener(object : OnChangeListener() {
//            @SuppressLint("RestrictedApi")
//            fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
//                maxLength = slider.getValue()
//            }
//        })
//        generatedPassword = generateRandomPassword(
//            maxLength,
//            swCapitalCaseLetter.isChecked(),
//            swLowerCaseLetter.isChecked(),
//            swNumbers.isChecked(),
//            swSymbols.isChecked()
//        )
//        tvPassword.setText("Password: $generatedPassword")
//        val pattern: Pattern = Pattern.compile("[a-zA-Z0-9]")
//        val pattern1: Pattern = Pattern.compile("[^a-zA-Z0-9]")
//        val matcher: Matcher = pattern.matcher(generatedPassword)
//        val matcher1: Matcher = pattern1.matcher(generatedPassword)
//        val matchFound = matcher.find()
//        val matchFound1 = matcher1.find()
//        if (matchFound and !matchFound1) {
//            if (generatedPassword.length() < 8) {
//                tvPasswordStrength.setText("Strength: Very week")
//                tvPasswordStrength.setTextColor(Color.RED)
//            } else {
//                if (generatedPassword.length() < 12) {
//                    tvPasswordStrength.setText("Strength: week")
//                    tvPasswordStrength.setTextColor(Color.YELLOW)
//                } else {
//                    if (generatedPassword.length() > 12) {
//                        tvPasswordStrength.setText("Strength: strong")
//                        tvPasswordStrength.setTextColor(Color.GREEN)
//                    }
//                }
//            }
//        } else if (!matchFound and matchFound1) {
//            if (generatedPassword.length() < 8) {
//                tvPasswordStrength.setText("Strength: week")
//                tvPasswordStrength.setTextColor(Color.RED)
//            } else {
//                if (generatedPassword.length() < 12) {
//                    tvPasswordStrength.setText("Strength: strong")
//                    tvPasswordStrength.setTextColor(Color.YELLOW)
//                } else {
//                    if (generatedPassword.length() > 12) {
//                        tvPasswordStrength.setText("Strength: strong")
//                        tvPasswordStrength.setTextColor(Color.GREEN)
//                    }
//                }
//            }
//        } else {
//            if (generatedPassword.length() < 8) {
//                tvPasswordStrength.setText("Strength: strong")
//                tvPasswordStrength.setTextColor(Color.RED)
//            } else {
//                if (generatedPassword.length() < 12) {
//                    tvPasswordStrength.setText("Strength: very strong")
//                    tvPasswordStrength.setTextColor(Color.YELLOW)
//                } else {
//                    if (generatedPassword.length() > 12) {
//                        tvPasswordStrength.setText("Strength: very strong")
//                        tvPasswordStrength.setTextColor(Color.GREEN)
//                    }
//                }
//            }
//        }
//    }

}