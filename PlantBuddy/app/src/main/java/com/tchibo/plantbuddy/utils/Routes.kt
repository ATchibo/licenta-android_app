package com.tchibo.plantbuddy.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Routes {
    companion object {
        private const val LOGIN = "/login"
        private const val REGISTER = "/register"
        private const val HOME = "/"
        private const val DETAILS = "/details/{taskId}"
        private const val ADD = "/add"
        private const val EDIT = "$DETAILS/edit"

        fun getNavigateLogin(): String {
            return LOGIN
        }
        fun getNavigateHome() : String {
            return HOME
        }

        fun getNavigateDetailsRaw() : String {
            return DETAILS
        }

        fun getNavigateDetails(taskId: Int) : String {
            return DETAILS.replace("{taskId}", Json.encodeToString(taskId))
        }

        fun getNavigateEditRaw() : String {
            return EDIT
        }

        fun getNavigateEdit(taskId: Int) : String {
            return EDIT.replace("{taskId}", Json.encodeToString(taskId))
        }

        fun getNavigateAdd() : String {
            return ADD
        }

        fun getNavigateRegister(): String {
            return REGISTER
        }
    }
}

