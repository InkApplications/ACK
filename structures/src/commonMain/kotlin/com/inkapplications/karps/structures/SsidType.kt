package com.inkapplications.karps.structures

/**
 * Recommended SSID Type categories.
 *
 * Based on: http://www.aprs.org/aprs11/SSIDs.txt
 */
enum class SsidType {
    /** Your primary station usually fixed and message capable */
    Primary,
    /** generic additional station, digi, mobile, wx, etc */
    Generic,
    /** Other networks (Dstar, Iphones, Androids, Blackberry's etc) */
    Network,
    /** Special activity, Satellite ops, camping or 6 meters, etc */
    Special,
    /** Walkie talkies, HT's or other human portable */
    Ht,
    /** Boats, sailboats, RV's or second main mobile */
    Boat,
    /** Primary Mobile (usually message capable) */
    Mobile,
    /** Internet, Igates, echolink, winlink, AVRS, APRN, etc */
    Internet,
    /** Balloons, aircraft, spacecraft, etc */
    Air,
    /** APRStt, DTMF, RFID, devices, one-way trackers*, etc */
    Tracker,
    /** Weather stations */
    Weather,
    /** Truckers or generally full time drivers */
    Trucking,
    /** Unclassified types */
    Unknown
}
