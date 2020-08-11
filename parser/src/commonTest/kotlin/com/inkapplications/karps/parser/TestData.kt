package com.inkapplications.karps.parser

import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import com.soywiz.klock.DateTime
import com.soywiz.klock.Month

interface Parsable {
    val packet: String
    val expected: AprsPacket
}

@Suppress("unused")
object TestData {
    val now = SystemClock.current
    val all = TestData::class.nestedClasses
        .filter { Parsable::class in it.supertypes.map { it.classifier } }
        .map { it.objectInstance as Parsable }

    object Position: Parsable {
        override val packet = "KV4JW>APDR15,TCPIP*,qAC,T2HUN:=3746.72N/08402.19W\$112/002/A=000761 https://aprsdroid.org/"
        override val expected = AprsPacket.Position(
            received = now,
            dataTypeIdentifier = '=',
            source = Address("KV4JW"),
            destination = Address("APDR15"),
            coordinates = Coordinates(
                Latitude(37, 46, 43.2f, Cardinal.North),
                Longitude(84, 2, 11.4f, Cardinal.West)
            ),
            digipeaters = listOf(
                Digipeater(Address("TCPIP"), heard = true),
                Digipeater(Address("qAC")),
                Digipeater(Address("T2HUN"))
            ),
            symbol = Symbol.Primary('$'),
            comment = "/A=000761 https://aprsdroid.org/",
            extension = DataExtension.TrajectoryExtra(112.degreesBearing at 2.mph),
            timestamp = null
        )
    }

    object CompressedPosition: Parsable {
        override val packet = "REDKNL>APOT30,KE7JVX-10*,WIDE2-1,qAR,K7YI-4:!S;an%2#Co#   130F N7YSE Red Knoll"
        override val expected = AprsPacket.Position(
            received = now,
            dataTypeIdentifier = '!',
            source = Address("REDKNL"),
            destination = Address("APOT30"),
            coordinates = Coordinates(
                Latitude(37, 9, 19.83f, Cardinal.North),
                Longitude(112, 38, 7.87f, Cardinal.West)
            ),
            digipeaters = listOf(
                Digipeater(Address("KE7JVX", "10"), heard = true),
                Digipeater(Address("WIDE2", "1")),
                Digipeater(Address("qAR")),
                Digipeater(Address("K7YI", "4"))
            ),
            symbol = Symbol.Alternate('#', overlay = 'S'),
            comment = "130F N7YSE Red Knoll",
            timestamp = null
        )
    }

    object CompressedPositionWithRange: Parsable {
        override val packet = "PD9MWO-10>APX210,TCPIP*,qAC,SEVENTH:=/4'HcO]?;-{!Chttps://megasounds.nl/externalradio"
        override val expected = AprsPacket.Position(
            received = now,
            dataTypeIdentifier = '=',
            source = Address("PD9MWO", "10"),
            destination = Address("APX210"),
            coordinates = Coordinates(
                Latitude(52, 16,  23.2f, Cardinal.North),
                Longitude(4, 37, 23.3f, Cardinal.East)
            ),
            digipeaters = listOf(
                Digipeater(Address("TCPIP"), heard = true),
                Digipeater(Address("qAC")),
                Digipeater(Address("SEVENTH"))
            ),
            symbol = Symbol.Primary('-'),
            comment = "https://megasounds.nl/externalradio",
            extension = DataExtension.RangeExtra(5280.feet),
            timestamp = null
        )
    }

    object CompressedPositionWithAltitude: Parsable {
        override val packet = "YO8KGA-2>APDS01,YO8A-2,YO8M-2*,qAR,YO8SDE-10:!/6Eu'U,b+#Gw6Suceava DIGI"
        override val expected = AprsPacket.Position(
            received = now,
            dataTypeIdentifier = '!',
            source = Address("YO8KGA", "2"),
            destination = Address("APDS01"),
            coordinates = Coordinates(
                Latitude(47, 39,  13.7f, Cardinal.North),
                Longitude(26, 14, 54.5f, Cardinal.East)
            ),
            digipeaters = listOf(
                Digipeater(Address("YO8A", "2")),
                Digipeater(Address("YO8M", "2"), heard = true),
                Digipeater(Address("qAR")),
                Digipeater(Address("YO8SDE", "10"))
            ),
            symbol = Symbol.Primary('#'),
            comment = "Suceava DIGI",
            extension = DataExtension.AltitudeExtra(1189.feet),
            timestamp = null
        )
    }

    object CompressedPositionWithCourse: Parsable {
        override val packet = "HS4TGK-10>APNN08,WIDE1-1,qAS,HS2PQV-1:!/Gdzph(=kkJMG/A=000106VIN:13.9V"
        override val expected = AprsPacket.Position(
            received = now,
            dataTypeIdentifier = '!',
            source = Address("HS4TGK", "10"),
            destination = Address("APNN08"),
            coordinates = Coordinates(
                Latitude(13, 20,  53.1f, Cardinal.North),
                Longitude(101, 13, 52.1f, Cardinal.East)
            ),
            digipeaters = listOf(
                Digipeater(Address("WIDE1", "1")),
                Digipeater(Address("qAS")),
                Digipeater(Address("HS2PQV", "1"))
            ),
            symbol = Symbol.Primary('k'),
            comment = "/A=000106VIN:13.9V",
            extension = DataExtension.TrajectoryExtra(164.degreesBearing at 30.mph),
            timestamp = null
        )
    }

    object PositionlessWeather: Parsable {
        override val packet = "W0YC-5>APX200,TCPIP*,qAC,SEVENTH:_10090556c220s004g005t077r002p006P004h50b09900l234wRSW"
        override val expected = AprsPacket.Weather(
            received = now,
            dataTypeIdentifier = '_',
            source = Address("W0YC", "5"),
            destination = Address("APX200"),
            digipeaters = listOf(
                Digipeater(Address("TCPIP"), heard = true),
                Digipeater(Address("qAC")),
                Digipeater(Address("SEVENTH"))
            ),
            windData = WindData(
                direction = 220.degreesBearing,
                speed = 4.mph,
                gust = 5.mph
            ),
            precipitation = Precipitation(
                rainLastHour = 2.hundredthsOfInch,
                rainLast24Hours = 6.hundredthsOfInch,
                rainToday = 4.hundredthsOfInch
            ),
            temperature = 77.degreesFahrenheit,
            humidity = 50.percent,
            pressure = 9900.decapascals,
            irradiance = 1234.wattsPerSquareMeter,
            timestamp = DateTime.now()
                .copyDayOfMonth(month = Month.October, dayOfMonth = 9, hours = 5, minutes = 56, seconds = 0, milliseconds = 0)
                .unixMillisLong
                .asTimestamp
        )
    }

    object CompleteWeather: Parsable {
        override val packet = "W0YC-5>APX200,TCPIP*,qAC,SEVENTH:@092345z4903.50N/07201.75W_220/004g005t-07r002p006P004h50b09900l234wRSW"
        override val expected = AprsPacket.Weather(
            received = now,
            dataTypeIdentifier = '@',
            source = Address("W0YC", "5"),
            destination = Address("APX200"),
            digipeaters = listOf(
                Digipeater(Address("TCPIP"), heard = true),
                Digipeater(Address("qAC")),
                Digipeater(Address("SEVENTH"))
            ),
            windData = WindData(
                direction = 220.degreesBearing,
                speed = 4.mph,
                gust = 5.mph
            ),
            precipitation = Precipitation(
                rainLastHour = 2.hundredthsOfInch,
                rainLast24Hours = 6.hundredthsOfInch,
                rainToday = 4.hundredthsOfInch
            ),
            position = Coordinates(
                Latitude(49, 3, 30f, Cardinal.North),
                Longitude(72, 1, 45f, Cardinal.West)
            ),
            temperature = (-7).degreesFahrenheit,
            humidity = 50.percent,
            pressure = 9900.decapascals,
            irradiance = 1234.wattsPerSquareMeter,
            timestamp = DateTime.now()
                .copyDayOfMonth(dayOfMonth = 9, hours = 23, minutes = 45, seconds = 0, milliseconds = 0)
                .unixMillisLong
                .asTimestamp,
            symbol = Symbol.Primary('_')
        )
    }

    object CompressedWeather: Parsable {
        override val packet = "W0YC-5>APX200,TCPIP*,qAC,SEVENTH:@092345z/5L!!<*e7_7P[g005t077r002p006P004h50b09900l234wRSW"
        override val expected = AprsPacket.Weather(
            received = now,
            dataTypeIdentifier = '@',
            source = Address("W0YC", "5"),
            destination = Address("APX200"),
            digipeaters = listOf(
                Digipeater(Address("TCPIP"), heard = true),
                Digipeater(Address("qAC")),
                Digipeater(Address("SEVENTH"))
            ),
            windData = WindData(
                direction = 88.degreesBearing,
                speed = 39.mph,
                gust = 5.mph
            ),
            precipitation = Precipitation(
                rainLastHour = 2.hundredthsOfInch,
                rainLast24Hours = 6.hundredthsOfInch,
                rainToday = 4.hundredthsOfInch
            ),
            position = Coordinates(
                Latitude(49, 30, 0f, Cardinal.North),
                Longitude(72, 45, 0, Cardinal.West)
            ),
            temperature = 77.degreesFahrenheit,
            humidity = 50.percent,
            pressure = 9900.decapascals,
            irradiance = 1234.wattsPerSquareMeter,
            timestamp = DateTime.now()
                .copyDayOfMonth(dayOfMonth = 9, hours = 23, minutes = 45, seconds = 0, milliseconds = 0)
                .unixMillisLong
                .asTimestamp,
            symbol = Symbol.Primary('_')
        )
    }
}
