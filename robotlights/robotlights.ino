#include <Adafruit_NeoPixel.h>

#define PIN 5
#define STATE0 9
#define STATE1 10
#define STATE2 11
#define LIGHT 13
#define NUM_LIGHTS 7

Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LIGHTS, PIN, NEO_GRB + NEO_KHZ800);

int currentState = 0;
int lastState    = 0;
int i = 0;
double lps = 20.0/NUM_LIGHTS;//lights per second
void setup() {
  Serial.begin(9600);
  strip.begin();
  strip.show();
  pinMode(STATE0, INPUT);
  pinMode(STATE1, INPUT);
  pinMode(STATE2, INPUT);
  pinMode(LIGHT,OUTPUT);
}

void loop() {
    Serial.println(currentState);
    digitalWrite(LIGHT,HIGH);
    switch(getState()){
      case 0:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(255, 0, 0));
          strip.show();
        }
      break;
      
      case 1:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 0, 255));
        }
        strip.show();
      break; 
      
      case 2:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 255, 0));
        }
        strip.show();
      break;
      
      case 3:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(255, 255, 100));
        }
        strip.show();
        delay(300);
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 255, 0));
        }
        strip.show();
        delay(300);
      break;
      
      case 4:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 0, 255));
        }
        strip.show();
        delay(300);
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 0, 0));
        }
        strip.show();
        delay(300);
      break;
      
      case 5:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 255, 0));
        }
        strip.show();
        delay(300);
      break;
      
      case 6:
      break;
      
      case 7:
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(255, 0, 0));
        }
        strip.show();
        delay(300);
        for(i = 0; i < NUM_LIGHTS; i++){
          strip.setPixelColor(i, strip.Color(0, 0, 0));
        }
        strip.show();
        delay(300);
      break;
      
      default:
        for(i = 0; i < NUM_LIGHTS; i++){
            strip.setPixelColor(i, strip.Color(255, 255, 255));
          }
        strip.show();
      break;
     }
     lastState = currentState;
}
void setState(int state){
 currentState = state; 
}
int getState() {
  int i = 0;
  int val;
  val = digitalRead(STATE0);
  if(val == HIGH){
    i += 1;
  }
  val = digitalRead(STATE1);
  if(val == HIGH){
    i += 2;
  }
  val = digitalRead(STATE2);
  if(val == HIGH){
    i += 4;
  }
  return i;
}
