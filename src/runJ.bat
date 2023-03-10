javac monalisabot/Main.java
javac -h monalisabot/  monalisabot/*.java
g++ -c -I"%JAVA_HOME%"\include -I"%JAVA_HOME%"\include\win32 monalisabot/monalisabot_StopFromKeyboard.cpp -o monalisabot/monalisabot_StopFromKeyboard.o
g++ -shared -o monalisabot/native.dll monalisabot/monalisabot_StopFromKeyboard.o
java -Djava.library.path=monalisabot/  monalisabot.Main