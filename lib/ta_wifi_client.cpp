#include <Arduino.h>
#include "ta_wifi_client.h"
#include "ta_debugger.h"

const int led = LED_BUILTIN;
const char *ap_ssid = APSSID;

int address = 0;
String ssid;
String password;

ESP8266WebServer ap_server(80);

void configureAndPrepareFromEEPROM() {
  EEPROM.begin(4096);
  address = 0;
  EEPROM.get(address, ssid);
  DEBUG_OUTPUT.print("Read SSID = ");
  DEBUG_OUTPUT.println(ssid);
  address += sizeof(ssid); //update address value
  EEPROM.get(address, password); 
  DEBUG_OUTPUT.print("Read password = ");
  DEBUG_OUTPUT.println(password);
}

void connectWiFi() {
  WiFi.begin(ssid, password);
  DEBUG_OUTPUT.println("Waiting for connection to : " + ssid);
  delay(CONNECTION_WAIT_MILLIS); // Wait until the connection is established.
  while (!WiFi.isConnected()) {
    DEBUG_OUTPUT.print("WiFi not connected! Because : ");
    DEBUG_OUTPUT.println(WiFi.status());
    WiFi.disconnect(true);
    DEBUG_OUTPUT.println("WiFi disconnected.");
    MDNS.notifyAPChange();
    configureAccessPoint();
    unsigned long now = millis();
    while (!(ssid && password) && millis() - now > WIFI_INFO_RETRIEVAL_TIMEOUT) {
      ap_server.handleClient();
      MDNS.update();
      DEBUG_OUTPUT.print("_.");
    }
    disconnectAccessPoint();
    WiFi.begin(ssid, password);
    delay(CONNECTION_WAIT_MILLIS); // Wait until the connection is established.
  }
  MDNS.notifyAPChange();
  DEBUG_OUTPUT.print("Connected to ");
  DEBUG_OUTPUT.println(ssid);
  DEBUG_OUTPUT.print("IP address: ");
  DEBUG_OUTPUT.println(WiFi.localIP());
  EEPROM.end();
  DEBUG_OUTPUT.print("EEPROM connection is closed.");
}

void configureMDNS() {
  if (MDNS.begin(MDNS_HOSTNAME)) { // Start the mDNS responder for esp8266.local
    DEBUG_OUTPUT.println("mDNS responder started");
  } else {
    DEBUG_OUTPUT.println("Error setting up MDNS responder!");
  }
}
// Utility Functions BEGIN

String getContentType(String filename) { // convert the file extension to the MIME type
  if (filename.endsWith(".html")) return "text/html";
  return "text/plain";
}

bool isHttpMethodAllowed(ESP8266WebServer& server, short method) {
  if (server.method() != method) {
    digitalWrite(led, 1);
    server.send(405, "text/plain", "Method Not Allowed");
    digitalWrite(led, 0);
    return false;
  }
  return true;
}

// AP Mode Functions BEGIN

bool handleFileRead(String path) { // send the right file to the client (if it exists)
  DEBUG_OUTPUT.println("handleFileRead: " + path);
  if (path.endsWith("/")) path += "index.html";         // If a folder is requested, send the index file
  String contentType = getContentType(path);            // Get the MIME type
  if (SPIFFS.exists(path)) {                            // If the file exists
    File file = SPIFFS.open(path, "r");                 // Open it
    size_t sent = ap_server.streamFile(file, contentType); // And send it to the client
    file.close();                                       // Then close the file again
    return true;
  }
  DEBUG_OUTPUT.println("\tFile Not Found");
  return false;                                         // If the file doesn't exist, return false
}

void handleNotFound() {
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += ap_server.uri();
  message += "\nMethod: ";
  message += (ap_server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += ap_server.args();
  message += "\n";
  for (uint8_t i = 0; i < ap_server.args(); i++) {
    message += " " + ap_server.argName(i) + ": " + ap_server.arg(i) + "\n";
  }
  ap_server.send(404, "text/plain", message);
}

void handleRoot() {
  digitalWrite(led, 1);
  if (!handleFileRead("/ap_router_login.html")) {
    handleNotFound();
  }
  MDNS.update();
  digitalWrite(led, 0);
}

void handleSaveRouter() {
  digitalWrite(led, 1);
  if (!isHttpMethodAllowed(ap_server, HTTP_POST)) return;
  ssid = ap_server.hasArg("ssid") ? ap_server.arg("ssid") : "";
  password = ap_server.hasArg("password") ? ap_server.arg("password") : "";

  EEPROM.put(address, ssid);
  address += sizeof(ssid);
  EEPROM.put(address, password);
  EEPROM.commit();

  ap_server.send(200, "text/plain", "ssid : '" + ssid + "' password : '" + password + "'. It is saved to EEPROM.");
  digitalWrite(led, 0);
}

void configureAccessPoint() {
  DEBUG_OUTPUT.print("Configuring access point...");
  WiFi.softAP(ap_ssid);
  DEBUG_OUTPUT.print("AP IP address: ");
  DEBUG_OUTPUT.println(WiFi.softAPIP());
  SPIFFS.begin();
  ap_server.onNotFound(handleNotFound);
  ap_server.on("/", handleRoot);
  ap_server.on("/save_router/", handleSaveRouter);
  ap_server.begin();
  DEBUG_OUTPUT.println("HTTP ap_server started");
}

void disconnectAccessPoint() {
  DEBUG_OUTPUT.print("Disconnecting access point...");
  ap_server.close();
  WiFi.softAPdisconnect(true);
  DEBUG_OUTPUT.print("Disconnected from access point.");
}
