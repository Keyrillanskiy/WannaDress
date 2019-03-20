package domain.models.responses

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * Модели для работы с погодой
 *
 * @author Keyrillanskiy
 * @since 15.01.2019, 20:45.
 */

data class WeatherResponse(
    val city: String,
    @SerializedName("current") val currentWeather: CurrentWeather,
    val forecast: List<Forecast>
)

data class CurrentWeather(@SerializedName("temp") val temperature: Int, val type: String)

fun CurrentWeather.temperature(): String {
    var strTemperature = temperature.toString()

    if (temperature > 0) {
        strTemperature = "+$strTemperature"
    }

    return "$strTemperature ˚C"
}

data class Forecast(
    val time: Long,
    val type: String,
    @SerializedName("temp") val temperature: Int
)

fun Forecast.time(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time * 1000))
}

fun Forecast.temperature(): String {
    var strTemperature = temperature.toString()

    if (temperature > 0) {
        strTemperature = "+$strTemperature"
    }

    return "$strTemperature ˚C"
}