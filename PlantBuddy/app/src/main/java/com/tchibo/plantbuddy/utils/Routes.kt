package com.tchibo.plantbuddy.utils

class Routes {
    companion object {
        private const val LOGIN = "/login"
        private const val HOME = "/"
        private const val DETAILS = "/details/{id}"
        private const val WATERING_OPTIONS = "/watering-options/{id}"
        private const val ADD_DEVICE = "/add_device"
        private const val ADD_PROGRAM = "$WATERING_OPTIONS/add_program/{programId}"
        private const val EDIT = "$DETAILS/edit"
        private const val SETTINGS = "/settings"
        private const val LOGS = "/logs/{id}"
        private const val RASPBERRY_SETTINGS = "/raspberry_settings/{id}"
        private const val LOGIN_REQUEST = "/login_request"

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

        fun getNavigateAddProgramRaw() : String {
            return ADD_PROGRAM
        }

        fun getNavigateAddProgram(rpiId: String, programId: String? = null) : String {
            return ADD_PROGRAM.replace("{id}", rpiId).replace("{programId}", programId ?: "NULL")
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

        fun getNavigateLogsRaw() : String {
            return LOGS
        }

        fun getNavigateLogs(rpiId: String) : String {
            return LOGS.replace("{id}", rpiId)
        }

        fun getNavigateRaspberrySettingsRaw() : String {
            return RASPBERRY_SETTINGS
        }

        fun getNavigateRaspberrySettings(rpiId: String) : String {
            return RASPBERRY_SETTINGS.replace("{id}", rpiId)
        }

        fun getNavigateLoginRequest(): String {
            return LOGIN_REQUEST
        }
    }
}

