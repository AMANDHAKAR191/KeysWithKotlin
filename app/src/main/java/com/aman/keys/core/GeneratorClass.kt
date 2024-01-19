package com.aman.keys.core

import java.util.Random

class GeneratorClass {
    fun generatePassword(
        max_length: Int,
        upperCase: Boolean = true,
        lowerCase: Boolean = true,
        numbers: Boolean = true,
        specialCharacters: Boolean = true
    ): String {
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
        return sb.toString()
    }

    fun randomIdGenerator(
        max_length: Int,
        numbers: Boolean = true,
    ): String {
        val rn = Random()
        val sb = StringBuilder(max_length)
        try {
            val numberChars = "0123456789"
            var allowedChars = ""


            //this will fulfill the requirements of atleast one character of a type.
            if (numbers) {
                allowedChars += numberChars
                sb.append(numberChars[rn.nextInt(numberChars.length - 1)])
            }
            //fill the allowed length from different chars now.
            for (i in sb.length until max_length) {
                sb.append(allowedChars[rn.nextInt(allowedChars.length)])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}