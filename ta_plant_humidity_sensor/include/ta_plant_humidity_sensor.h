#ifndef ta_plant_humidity_sensor_h
#define ta_plant_humidity_sensor_h

#include <ta_wifi_connector.h>
#include <ta_debugger.h>
#include <sstream>

uint8_t readHumidity();
void handleGetHumidity();

#endif //ta_plant_humidity_sensor_h