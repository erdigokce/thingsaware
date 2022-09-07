#ifndef ta_wifi_client_h
#define ta_wifi_client_h

#include <Arduino.h>
#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266mDNS.h>
#include <ESP8266WebServer.h>
#include <FS.h>

#ifndef CONNECTION_WAIT_MILLIS
#define CONNECTION_WAIT_MILLIS 5000
#endif

#ifndef COMMAND_WAIT_MILLIS
#define COMMAND_WAIT_MILLIS 500
#endif

#ifndef WIFI_INFO_RETRIEVAL_TIMEOUT
#define WIFI_INFO_RETRIEVAL_TIMEOUT 5 * 60 * 1000 // Wait for 5 minutes
#endif

#ifndef MDNS_HOSTNAME
#define MDNS_HOSTNAME "thingsaware.local"
#endif

#ifndef APSSID
#define APSSID "Things Aware AP"
#endif

// Preperation
void configureAndPrepareFromEEPROM();
void connectWiFi();
// Utility Functions
String getContentType(String filename); // convert the file extension to the MIME type
bool isHttpMethodAllowed(ESP8266WebServer& server, short method);
// AP Mode Functions
bool handleFileRead(String path);       // send the right file to the client (if it exists)
void handleRoot();
void handleSaveRouter();
void configureAccessPoint();
void configureMDNS();
void disconnectAccessPoint();

#endif //ta_wifi_client_h