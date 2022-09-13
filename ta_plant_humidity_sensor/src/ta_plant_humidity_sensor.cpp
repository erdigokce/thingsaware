#include <Arduino.h>
#include "ta_plant_humidity_sensor.h"

#define INPUT_PIN A0

ESP8266WebServer server(80);
u_int8_t dryness;

void setup() {
  pinMode(INPUT_PIN, INPUT);
  Serial.begin(115200);
  configureMDNS();
  delay(COMMAND_WAIT_MILLIS);
  configureAndPrepareFromEEPROM();
  delay(COMMAND_WAIT_MILLIS);
  connectWiFi();
  server.on("/humidty", handleGetHumidity);
  server.begin();
}

void loop() {
  server.handleClient();
  MDNS.update();
  dryness = readHumidity();
  if (dryness > 70)
    digitalWrite(LED_BUILTIN, HIGH);
  else
    digitalWrite(LED_BUILTIN, LOW);
  delay(10);
}

uint8_t readHumidity() {
  uint8_t humidityValue = analogRead(A0);
  return map(humidityValue, 0, 1023, 0, 100);
}

void handleGetHumidty() {
  DEBUG_OUTPUT.println("Handling Get Humidty");
  if (!isHttpMethodAllowed(server, HTTP_GET)) return;

  Serial.println(dryness);

  std::ostringstream oss;
  oss << "{\"dryness\":" << dryness << "}";
  server.send(200, "application/json", oss.str().c_str());
}