package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine

object Messages {
    val messages: HashMap<String, String> = hashMapOf()
    val defaultMessages: HashMap<String, String> = hashMapOf()

    val helpMessages: HashMap<String, List<String>> = hashMapOf()
    val defaultHelpMessage: HashMap<String, List<String>> = hashMapOf()

    var qrcodeDisplayName: String = ""
    var qrcodeLore: List<String> = listOf()

    fun load() {
        val defaultLanguage = MelodyMine.instance.languages["en_US"] ?: return
        val selectedLanguage = MelodyMine.instance.languages[Settings.language] ?: defaultLanguage
        val config = selectedLanguage.config

        val messagesSection = config.getConfigurationSection("messages") ?: return
        for (message in messagesSection.getValues(true)) {
            if (message.value is String) {
                messages[message.key] = message.value.toString()
                continue
            }
            if (message.value is List<*>) {
                helpMessages[message.key] = message.value as List<String>
                continue
            }
        }

        val defaultMessageSection = defaultLanguage.config.getConfigurationSection("messages") ?: return
        for (message in defaultMessageSection.getValues(true)) {
            if (message.value is String) {
                defaultMessages[message.key] = message.value.toString()
                continue
            }
            if (message.value is List<*>) {
                defaultHelpMessage[message.key] = message.value as List<String>
                continue
            }
        }

//        messages.keys.forEach { key ->
//            MelodyMine.instance.logger.info(key)
//            MelodyMine.instance.logger.info(messages[key])
//        }

        val qrcodeSection = config.getConfigurationSection("qrcode") ?: return
        qrcodeDisplayName = qrcodeSection.getString("display_name") ?: ""
        qrcodeLore = qrcodeSection.getStringList("lore")

    }

    private fun getDefaultMessage(path: String): String {
        return defaultMessages[path] ?: "Path Not Found."
    }

    private fun getDefaultHelpMessage(path: String): List<String> {
        return defaultHelpMessage[path] ?: listOf("<red>Path Not Found.")
    }

//    fun getMessage(path: String, placeholder: HashMap<String, Any>? = null): Component {
//        var message = messages[path] ?: getDefaultMessage(path)
//        placeholder?.keys?.forEach { key ->
//            message = message.replace(key, placeholder[key].toString())
//        }
//        return message.toComponent()
//
//    }

    fun getMessage(path: String, placeholder: HashMap<String, Any>? = null): String {
        var message = messages[path] ?: getDefaultMessage(path)
        placeholder?.keys?.forEach { key ->
            message = message.replace(key, placeholder[key].toString())
        }
        return message
    }

    fun getHelpMessage(path: String, placeholder: HashMap<String, Any>? = null): List<String> {
        var helpMessage = helpMessages[path] ?: getDefaultHelpMessage(path)

        placeholder?.keys?.forEach { key ->
            helpMessage = helpMessage.map { message ->
                message.replace(key, placeholder[key].toString())
            }
        }

        return helpMessage.map { message -> message }
    }
}
