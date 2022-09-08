#include <Servo.h>
#include <avr/sleep.h>
#define FLUSH_TIMOUT_IN_MILLIS 3500
#define SERVO_DATA_PIN 9
#define DISTANCE_TRIG_PIN 6
#define DISTANCE_ECHO_PIN 5
#define LDR_PIN 2
#define LED_PASSIVE_PIN 11
#define LED_ACTIVE_PIN 10
#define SERVO_INITIAL_DEGREE 30
#define SERVO_PUSHDOWN_DEGREE 20
#define SERVO_FINAL_DEGREE 90

Servo servoFlush;

void setup() {
  Serial.begin(9600);
  pinMode(DISTANCE_TRIG_PIN, OUTPUT);
  pinMode(DISTANCE_ECHO_PIN, INPUT);
  pinMode(LDR_PIN, INPUT);
  pinMode(LED_PASSIVE_PIN, OUTPUT);
  pinMode(LED_ACTIVE_PIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
  servoFlush.attach(SERVO_DATA_PIN);  // attaches the servo on pin 9 to the servo object
}

void loop() {
  Serial.println("======");
  if (isEnvironmentDark()) {
    sleep_enable();
    Serial.println("Sleep enabled! ");
    attachInterrupt(digitalPinToInterrupt(LDR_PIN), wakeUp, RISING);
    Serial.println("LDR Pin attached to interrupt! ");
    set_sleep_mode(SLEEP_MODE_PWR_DOWN);
    Serial.println("Sleep mode has been set! ");
    digitalWrite(LED_BUILTIN, LOW);
    delay(1000);
    Serial.println("Going to sleep... ");
    sleep_cpu();
    Serial.println("Just woke up! ");
    digitalWrite(LED_BUILTIN, HIGH);
  } else {
    boolean is_hand_detected = hand_detected();
    Serial.print("Hand detected? : ");
    Serial.println(is_hand_detected == 0 ? "No" : "Yes");
    if (is_hand_detected) {
      pullLeverUp();
      delay(FLUSH_TIMOUT_IN_MILLIS);
      pushLeverDown();
      delay(500);
      setInitialPosition();
      delay(500);
    }
  }
  delay(500);
}

void wakeUp() {
  Serial.println("Interrupt fired!");
  sleep_disable();
  Serial.println("Sleep disabled... ");
  detachInterrupt(digitalPinToInterrupt(LDR_PIN));
  Serial.println("LDR PIN detached... ");
}

boolean isEnvironmentDark() {
  return digitalRead(LDR_PIN) == LOW;
}

boolean hand_detected() {
  return distance_less_than_given_centimeter(10);
}

boolean distance_less_than_given_centimeter(int max_distance) {
  long duration, distance;
  digitalWrite(DISTANCE_TRIG_PIN, LOW);
  delayMicroseconds(5);
  digitalWrite(DISTANCE_TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(DISTANCE_TRIG_PIN, LOW);
  duration = pulseIn(DISTANCE_ECHO_PIN, HIGH);
  Serial.print("Duration : ");
  Serial.println(duration);
  distance = duration / 2 / 29.1;
  if (distance > 200)
    distance = 200;
  Serial.print("Distance : ");
  Serial.println(distance);
  if (distance > 0 && distance < max_distance) { // in centimeters
    digitalWrite(LED_ACTIVE_PIN, HIGH);
    digitalWrite(LED_PASSIVE_PIN, LOW);
    return true;
  } else {
    digitalWrite(LED_ACTIVE_PIN, LOW);
    digitalWrite(LED_PASSIVE_PIN, HIGH);
    return false;
  }
}

void pullLeverUp() {
  servoFlush.write(SERVO_FINAL_DEGREE);
  Serial.print("Final degree : ");
  Serial.println(SERVO_FINAL_DEGREE);
}

void pushLeverDown() {
  servoFlush.write(SERVO_PUSHDOWN_DEGREE);
  Serial.print("Push Down degree : ");
  Serial.println(SERVO_PUSHDOWN_DEGREE);
}

void setInitialPosition() {
  Serial.print("Initial degree : ");
  servoFlush.write(SERVO_INITIAL_DEGREE);
  Serial.println(SERVO_INITIAL_DEGREE);
}
