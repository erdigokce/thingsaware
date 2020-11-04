#include <SoftwareSerial.h>

const String WIFI_SSID = "GOKCEHOME";
const String WIFI_PWD = "Kuscu.1403";
const String OPENHUB_HOST = "erdi-pc.local";
const String OPENHUB_PORT = "30080";
const String OPENHUB_APIKEY = "key_here";

unsigned short rxPin = 10;
unsigned short txPin = 11;

SoftwareSerial esp(rxPin, txPin);

void setup() {
  pinMode(A0, INPUT);
  Serial.begin(9600);  //Seri port ile haberleşmemizi başlatıyoruz.
  Serial.println("Started");
  esp.begin(115200);                                          //ESP8266 ile seri haberleşmeyi başlatıyoruz.
  esp.println("AT");                                          //AT komutu ile modül kontrolünü yapıyoruz.
  Serial.println("AT Yollandı");
  while (!esp.find("OK")) {                                   //Modül hazır olana kadar bekliyoruz.
    esp.println("AT");
    Serial.println("ESP8266 Bulunamadı.");
  }
  Serial.println("OK Komutu Alındı");
  esp.println("AT+CWMODE=1");                                 //ESP8266 modülünü client olarak ayarlıyoruz.
  while (!esp.find("OK")) {                                   //Ayar yapılana kadar bekliyoruz.
    esp.println("AT+CWMODE=1");
    Serial.println("Ayar Yapılıyor....");
  }
  Serial.println("Client olarak ayarlandı");
  Serial.println("Aga Baglaniliyor...");
  esp.println("AT+CWJAP=\"" + WIFI_SSID + "\",\"" + WIFI_PWD + "\""); //Ağımıza bağlanıyoruz.
  while (!esp.find("OK"));                                    //Ağa bağlanana kadar bekliyoruz.
  Serial.println("Aga Baglandi.");
  delay(1000);
}
void loop() {
  connectOpenhab();
  submitDataToOpenhab();
  disconnectEsp();
  delay(5000);
}

void connectOpenhab() {
  esp.println("AT+CIPSTART=\"TCP\",\"" + OPENHUB_HOST + "\"," + OPENHUB_PORT + "");
  if (esp.find("Error")) { //Bağlantı hatası kontrolü yapıyoruz.
    Serial.println("AT+CIPSTART Error");
  }
}

void submitDataToOpenhab() {
  String request = "POST https://" + OPENHUB_HOST + "/publish?api_key=" + OPENHUB_APIKEY;
  request += "&humidity=";
  request += String(readHumidity());
  request += "\r\n\r\n";
  esp.print("AT+CIPSEND=");
  esp.println(request.length() + 2);
  delay(2000);
  if (esp.find(">")) {
    esp.print(request);
    delay(1000);
  }
}

unsigned int readHumidity() {
  unsigned int humidityValue = analogRead(A0);
  humidityValue = map(humidityValue, 0, 1023, 0, 100);
  return humidityValue;
}

void disconnectEsp() {
  esp.println("AT+CIPCLOSE");
}
