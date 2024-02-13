package com.tchibo.plantbuddy.utils

class Routes {
    companion object {
        private const val LOGIN = "/login"
        private const val HOME = "/"
        private const val DETAILS = "/details/{id}"
        private const val WATERING_OPTIONS = "/watering-options/{id}"
        private const val ADD_DEVICE = "$WATERING_OPTIONS/add_device"
        private const val ADD_PROGRAM = "/add_program"
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
            return ADD_DEVICE
        }

        fun getNavigateAddProgram() : String {
            return ADD_PROGRAM
        }

        fun getNavigateSettings() : String {
            return SETTINGS
        }

        fun getNavigateWateringOptionsRaw() : String {
            return WATERING_OPTIONS
        }

        fun getNavigateWateringOptions(rpiId: String) : String {
            return WATERING_OPTIONS.replace("{id}", rpiId)
        }
    }
}

