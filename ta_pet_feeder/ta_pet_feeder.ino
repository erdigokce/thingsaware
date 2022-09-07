#include "ta_pet_feeder.h"
#include "ta_debugger.h"

ESP8266WebServer server(80);

void setup() {
  Serial.begin(115200);
  configureMDNS();
  delay(COMMAND_WAIT_MILLIS);
  configureAndPrepareFromEEPROM();
  delay(COMMAND_WAIT_MILLIS);
  connectWiFi();
  server.onNotFound([]() {
    String message = "File Not Found\n\n";
    message += "URI: ";
    message += server.uri();
    message += "\nMethod: ";
    message += (server.method() == HTTP_GET) ? "GET" : "POST";
    message += "\nArguments: ";
    message += server.args();
    message += "\n";
    for (uint8_t i = 0; i < server.args(); i++) {
      message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
    }
    server.send(404, "text/plain", message);
  });
  server.on("/roll/", handleRoll);
  server.on("/schedule/", handleSetSchedule);
  server.on("/residual/", handleGetResidual);
  server.begin();
  DEBUG_OUTPUT.print("Server is ready for requests...");
}

void loop() {
  server.handleClient();
  MDNS.update();
  delay(10);
}

void handleRoll() {
  DEBUG_OUTPUT.println("Handling Roll");
  if (!isHttpMethodAllowed(server, HTTP_POST)) return;
  // TODO Roll motor for x seconds (or until sensor cuts with a timeout)
  server.send(200, "text/plain", "done");
}

void handleSetSchedule() {
  DEBUG_OUTPUT.println("Handling Set Schedule");
  if (!isHttpMethodAllowed(server, HTTP_PUT)) return;
  // TODO Set a RTC to roll the motor
  server.send(200, "text/plain", "done");
}

void handleGetResidual() {
  DEBUG_OUTPUT.println("Handling Get Residual");
  if (!isHttpMethodAllowed(server, HTTP_GET)) return;
  server.send(200, "text/plain", "done");
}