package whatsapp.messaging.sdk

import `in`.myinnos.wpaysdk.retro.RetrofitHelper
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class WhatsappBuilder {

    @DelicateCoroutinesApi
    companion object {
        fun sendMessage(
            context: Context,
            bearerToken: String,
            version: String,
            phoneNumberID: String,
            toPhoneNumber: String,
            message: String,
            languageCode: String,
            callback: (wResult: WResults?) -> Unit
        ) {

            if (bearerToken.isNullOrEmpty()) {
                Toast.makeText(context, "Bearer token is required", Toast.LENGTH_SHORT).show()
            } else if (version.isNullOrEmpty()) {
                Toast.makeText(context, "Version is required", Toast.LENGTH_SHORT).show()
            } else if (phoneNumberID.isNullOrEmpty()) {
                Toast.makeText(context, "Phone Number ID is required", Toast.LENGTH_SHORT).show()
            } else if (toPhoneNumber.isNullOrEmpty()) {
                Toast.makeText(context, "Phone Number is required", Toast.LENGTH_SHORT).show()
            } else if (message.isNullOrEmpty()) {
                Toast.makeText(context, "Message is required", Toast.LENGTH_SHORT).show()
            } else if (languageCode.isNullOrEmpty()) {
                Toast.makeText(context, "Language Code is required", Toast.LENGTH_SHORT).show()
            } else {
                val wPayAPI = RetrofitHelper.getInstance().create(WAPI::class.java)

                // Create JSON using JSONObject
                val jsonObject = JSONObject()
                jsonObject.put("messaging_product", "whatsapp")
                jsonObject.put("to", toPhoneNumber)
                jsonObject.put("type", "template")

                val jsonTemplateObject = JSONObject()
                jsonTemplateObject.put("name", message)
                val jsonLanguageObject = JSONObject()
                jsonLanguageObject.put("code", languageCode)
                jsonTemplateObject.put("language", jsonLanguageObject)

                jsonObject.put("template", jsonTemplateObject)

                // Convert JSONObject to String
                val jsonObjectString = jsonObject.toString()
                val requestBody =
                    jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

                GlobalScope.launch {
                    val result = wPayAPI.sendMessage(
                        "Bearer $bearerToken",
                        version,
                        phoneNumberID,
                        requestBody
                    )
                    Log.d("WHATSAPP_SDK", "RESPONSE_CODE: ${result.code()}")

                    val wResults = WResults()
                    if (result.code() == 400) {
                        val jsonObj = JSONObject(result.errorBody()!!.charStream().readText())
                        Log.d("WHATSAPP_SDK", "ERROR_MESSAGE: $jsonObj")
                        wResults.setMessage("MESSAGE SENT FAILED: CHECK THE LOGS")
                    } else {
                        Log.d("WHATSAPP_SDK", "MESSAGE_SENT")
                        wResults.setMessage("MESSAGE SUCCESSFULLY SENT!")
                    }

                    callback(wResults)
                }
            }
        }
    }
}