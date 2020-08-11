package com.inkapplications.karps.parser.weather

import com.inkapplications.karps.parser.PacketInformation
import com.inkapplications.karps.parser.PacketInformationParser
import com.inkapplications.karps.parser.weather.WeatherChunkParser.DATA
import com.inkapplications.karps.parser.weather.WeatherChunkParser.ID
import com.inkapplications.karps.structures.DataExtension
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import com.inkapplications.karps.structures.unit.*

class WeatherParser: PacketInformationParser {
    override val dataTypeFilter = charArrayOf('_', '!', '/', '@', '=')
    val format = Regex("""^((?:${ID}${DATA})+)(.)?(.{2,4})?""")

    override fun parse(data: PacketInformation): PacketInformation {
        val result = format.find(data.body) ?: return data
        val weatherData = WeatherChunkParser.getChunks(result.groupValues[1])

        return data.copy(
            body = data.body.substring(result.groupValues[0].length),
            windData = WindData(
                direction = (data.extension as? DataExtension.TrajectoryExtra)?.value?.direction ?: weatherData['c']?.degreesBearing,
                speed = (data.extension as? DataExtension.TrajectoryExtra)?.value?.speed ?: weatherData['s']?.mph,
                gust = weatherData['g']?.mph
            ),
            precipitation = Precipitation(
                rainLastHour = weatherData['r']?.hundredthsOfInch,
                rainLast24Hours = weatherData['p']?.hundredthsOfInch,
                rainToday = weatherData['P']?.hundredthsOfInch,
                rawRain = weatherData['#']
            ),
            temperature = weatherData['t']?.degreesFahrenheit,
            humidity = weatherData['h']?.percent,
            pressure = weatherData['b']?.decapascals,
            irradiance = weatherData['L']?.wattsPerSquareMeter ?: weatherData['l']?.plus(1000)?.wattsPerSquareMeter
        )
    }
}
