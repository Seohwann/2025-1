#include <Wire.h>
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);

// RGB LED 핀
const int redPin = 3;
const int greenPin = 4;
const int bluePin = 5;

// 초음파 핀
const int trigPin = 8;
const int echoPin = 9;

// 사운드 센서 핀 (AO)
const int soundPin = A0;

int previousState = -1;
unsigned long lastSurpriseTime = 0;
const unsigned long surpriseDuration = 10000;  // 10초 유지
bool inSurprise = false;

unsigned long lastNormalLoopTime = 0;
const unsigned long normalLoopInterval = 1000;  // 1초마다 갱신

void setup() {
  Serial.begin(9600);

  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);

  lcd.init();
  lcd.backlight();

  lcd.setCursor(0, 0);
  lcd.print("Starting ...");
  delay(1000);
  lcd.clear();
}

void loop() {
  unsigned long currentTime = millis();

  // 1. 소리 감지 (아날로그 방식)
  int soundLevel = analogRead(soundPin);
  

  if (soundLevel > 70 && !inSurprise) {
    Serial.println("🔊 큰 소리 감지됨!");
    inSurprise = true;
    lastSurpriseTime = currentTime;
    Serial.print("Sound level: ");
    Serial.println(soundLevel);
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Master !");
    lcd.setCursor(0, 1);
    lcd.print("What's Wrong ?");
    setColor(255, 0, 255); // 보라색 LED
  }

  // 2. Surprise 상태 유지 (10초)
  if (inSurprise) {
    if (currentTime - lastSurpriseTime >= surpriseDuration) {
      inSurprise = false;
      previousState = -1;  // 상태 초기화
      lcd.clear();
    } else {
      return; // Surprise 상태면 나머지 코드 무시하고 대기
    }
  }

  // 3. 정상 초음파 기반 상호작용 (2초 주기)
  if (currentTime - lastNormalLoopTime >= normalLoopInterval) {
    lastNormalLoopTime = currentTime;

    long distance = getDistance();
    Serial.print("거리: ");
    Serial.print(distance);
    Serial.println(" cm");

    int currentState = getState(distance);

    if (currentState != previousState) {
      lcd.clear();
      switch (currentState) {
        case 0:
          lcd.setCursor(0, 0);
          lcd.print("Happy ^_^");
          setColor(0, 0, 255); // 파랑
          break;
        case 1:
          lcd.setCursor(0, 0);
          lcd.print("Please pet me !");
          setColor(0, 255, 0); // 초록
          break;
        case 2:
          lcd.setCursor(0, 0);
          lcd.print("where are you");
          lcd.setCursor(0, 1);
          lcd.print("going ?");
          setColor(255, 0, 0); // 꺼짐
          break;
        case 3:
          lcd.setCursor(0, 0);
          lcd.print("Sad T_T");
          setColor(0, 0, 0); // 꺼짐
          break;
      }
      previousState = currentState;
    }
  }
}

// 거리 상태 분류
int getState(long distance) {
  if (distance >= -1 && distance <= 3) return 0;
  else if (distance > 3 && distance <= 10) return 1;
  else if (distance > 10 && distance < 25) return 2;
  else if (distance >= 25) return 3;
  return -1;
}

// 초음파 거리 측정
long getDistance() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  long duration = pulseIn(echoPin, HIGH, 30000);
  if (duration == 0) return -1;

  return duration * 0.034 / 2;
}

// RGB LED 제어
void setColor(int r, int g, int b) {
  digitalWrite(redPin, r > 0 ? HIGH : LOW);
  digitalWrite(greenPin, g > 0 ? HIGH : LOW);
  digitalWrite(bluePin, b > 0 ? HIGH : LOW);
}
