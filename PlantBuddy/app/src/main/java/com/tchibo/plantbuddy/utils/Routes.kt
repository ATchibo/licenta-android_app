package com.tchibo.plantbuddy.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Routes {
    companion object {
        private const val LOGIN = "/login"
        private const val HOME = "/"
        private const val DETAILS = "/details/{id}"
        private const val ADD = "/add"
        private const val EDIT = "$DETAILS/edit"
        private const val SETTINGS = "/settings"

        fun getNavigateLogin(): String {
            return LOGIN
        }
        fun getNavigateHome() : String {
            return HOME
        }

        fun getNavigateDetailsRaw() : String {
            return DETAILS
        }

        fun getNavigateDetails(id: String) : String {
            return DETAILS.replace("{id}", id)
        }

        fun getNavigateEditRaw() : String {
            return EDIT
        }

        fun getNavigateEdit(rpiId: String) : String {
            return EDIT.replace("{id}", rpiId)
        }

        fun getNavigateAdd() : String {
            return ADD
        }

        fun getNavigateSettings() : String {
            return SETTINGS
        }
    }
}

