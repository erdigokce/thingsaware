#include <Servo.h>
#define SERVO_DATA_PIN 9
#define DISTANCE_TRIG_PIN 13
#define DISTANCE_ECHO_PIN 12
#define LED_PASSIVE 11
#define LED_ACTIVE 10

Servo servoFlush;

int pos = 0;  //servo position

void setup() {
  pinMode(DISTANCE_TRIG_PIN, OUTPUT);
  pinMode(DISTANCE_ECHO_PIN, INPUT);
  pinMode(LED_PASSIVE, OUTPUT);
  pinMode(LED_ACTIVE, OUTPUT);
  servoFlush.attach(SERVO_DATA_PIN);  // attaches the servo on pin 9 to the servo object
}

void loop() {
  if(!distance_less_than_given_centimeter(5)) {
    delay(500);
  }
  operate(3500);
}

void operate(int wait_time) {
  servoFlush.write(120);
  delay(wait_time);
  servoFlush.write(0);
}

boolean distance_less_than_given_centimeter(int max_distance) {
  long duration, distance;
  digitalWrite(DISTANCE_TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(DISTANCE_TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(DISTANCE_TRIG_PIN, LOW);
  duration = pulseIn(DISTANCE_ECHO_PIN, HIGH);
  distance = (duration/2) / 29.1;
  if (distance > 0 && distance < max_distance) { // in centimeters
    digitalWrite(LED_ACTIVE,HIGH);
    digitalWrite(LED_PASSIVE,LOW);
    return true;
  } else {
    digitalWrite(LED_ACTIVE,LOW);
    digitalWrite(LED_PASSIVE,HIGH);
    return false;
  }
}