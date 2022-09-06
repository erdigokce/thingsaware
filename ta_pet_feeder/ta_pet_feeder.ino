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
  server.on("/roll/", handleRoll);
  server.on("/schedule/", handleSetSchedule);
  server.on("/residual/", handleGetResidual);
  DEBUG_OUTPUT.print("Server is ready for requests...");
}

void loop() {
  server.handleClient();
  MDNS.update();
}

void handleRoll() {
  if (!isHttpMethodAllowed(server, HTTP_POST)) return;
  // TODO Roll motor for x seconds (or until sensor cuts with a timeout)
  server.send(200, "text/plain", "done");
}
void handleSetSchedule() {
  if (!isHttpMethodAllowed(server, HTTP_PUT)) return;
  // TODO Set a RTC to roll the motor
  server.send(200, "text/plain", "done");
}
void handleGetResidual() {
  if (!isHttpMethodAllowed(server, HTTP_GET)) return;
  server.send(200, "text/plain", "done");
}