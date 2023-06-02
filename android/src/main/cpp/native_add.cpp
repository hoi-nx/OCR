#include <jni.h>

extern "C" JNIEXPORT jint JNICALL
Java_com_example_paddel_PaddelPlugin_nativeAdd(JNIEnv* env, jobject, jint x, jint y) {
    return x + y;
}