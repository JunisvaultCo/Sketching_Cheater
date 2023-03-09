#include "monalisabot_StopFromKeyboard.h"
#include <windows.h>
#include <iostream>

using namespace std;

JNIEXPORT jboolean JNICALL Java_monalisabot_StopFromKeyboard_isKeyPressed
  (JNIEnv *, jobject)
{
    // first 2 we are not interested in
    for (int i = 0x41; i <= 0x5a; i++) {
        if (GetAsyncKeyState(i)) {
            return true;
        }
    }
    if (GetAsyncKeyState(VK_SPACE)) {
        return true;
    }
    return false;
}