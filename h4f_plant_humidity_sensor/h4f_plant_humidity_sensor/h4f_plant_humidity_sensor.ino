void setup() {
  pinMode(A0, INPUT);
  Serial.begin(9600);
}
void loop() {
  unsigned int dryness = readHumidity();
  Serial.println(dryness);
  if (dryness > 70)
    digitalWrite(LED_BUILTIN, HIGH);
  else
    digitalWrite(LED_BUILTIN, LOW);
  delay(1000);
}

unsigned int readHumidity() {
  unsigned int humidityValue = analogRead(A0);
  humidityValue = map(humidityValue, 0, 1023, 0, 100);
  return humidityValue;
}
