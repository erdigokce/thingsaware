#include <Arduino.h>
#include "ta_pet_feeder.h"
#include <ta_debugger.h>
#include <sstream>

ESP8266WebServer server(80);
#define MOTOR_PIN D4
#define TRIGGER   D2
#define ECHO      D1

void setup() {
  analogWrite(MOTOR_PIN, 0);
  pinMode(TRIGGER, OUTPUT);
  pinMode(ECHO, INPUT);

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
  server.on("/roll", handleRoll);
  server.on("/residual", handleGetResidual);
  server.begin();
  DEBUG_OUTPUT.println("Server is ready for requests...");
}

void loop() {
  server.handleClient();
  MDNS.update();
  delay(10);
}

void handleRoll() {
  DEBUG_OUTPUT.println("Handling Roll");
  if (!isHttpMethodAllowed(server, HTTP_POST)) return;
  rollForGivenPortion(1);
  server.send(200, "text/plain");
}

void handleGetResidual() {
  DEBUG_OUTPUT.println("Handling Get Residual");
  if (!isHttpMethodAllowed(server, HTTP_GET)) return;
  digitalWrite(TRIGGER, LOW);  
  delayMicroseconds(2); 
  
  digitalWrite(TRIGGER, HIGH);
  delayMicroseconds(10); 
  
  digitalWrite(TRIGGER, LOW);
  long duration = pulseIn(ECHO, HIGH);
  long distance = (duration/2) / 29.1;
  distance = distance > 28 ? 28 : distance;
  distance = map(distance, 0, 28, 0, 100);
  long remainingPercentage = 100 - distance;

  std::ostringstream oss;
  oss << "{\"remainingPercentage\":" << remainingPercentage << "}";
  server.send(200, "application/json", oss.str().c_str());
}

void rollForGivenPortion(uint8_t portion) {
  analogWrite(MOTOR_PIN, 512);
  delay(portion * 3000);
  analogWrite(MOTOR_PIN, 0);
}