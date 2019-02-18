/* Header for class com_example_finance_tradestrategy_utils_ToolMath */
#include "com_example_finance_tradestrategy_utils_ToolMath.h"

/*
 * Class:     com_example_finance_tradestrategy_utils_ToolMath
 * Method:    isSegmentsIntersection
 * Signature: (FFFFFFFF)Z
 */
jboolean JNICALL Java_com_example_finance_tradestrategy_utils_ToolMath_isSegmentsIntersection
  (JNIEnv *env, jclass cls, jfloat p0x, jfloat p0y, jfloat p1x, jfloat p1y, jfloat p2x, jfloat p2y, jfloat p3x, jfloat p3y) {
        jfloat s1_x, s1_y, s2_x, s2_y;
        s1_x = p1x - p0x;     s1_y = p1y - p0y;
        s2_x = p3x - p2x;     s2_y = p3y - p2y;

        jfloat s, t;
        s = (-s1_y * (p0x - p2x) + s1_x * (p0y - p2y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0y - p2y) - s2_y * (p0x - p2x)) / (-s2_x * s1_y + s1_x * s2_y);

        return (s >= 0 && s <= 1 && t >= 0 && t <= 1);
  }


jboolean JNICALL Java_com_example_finance_tradestrategy_utils_ToolMath_isLinesIntersect
  (JNIEnv *env, jclass cls, jfloat x1, jfloat y1,  jfloat x2, jfloat y2, jfloat x3, jfloat y3, jfloat x4, jfloat y4) {
        if (x1 == x2) {
            return !(x3 == x4 && x1 != x3);
        } else if (x3 == x4) {
            return true;
        } else {
            // Both lines are not parallel to the y-axis
            float m1 = (y1-y2)/(x1-x2);
            float m2 = (y3-y4)/(x3-x4);
            return m1 != m2;
        }
  }

jobject JNICALL Java_com_example_finance_tradestrategy_utils_ToolMath_getSegmentsIntersection
  (JNIEnv *env, jclass cls, jfloat p0x, jfloat p0y, jfloat p1x, jfloat p1y, jfloat p2x, jfloat p2y, jfloat p3x, jfloat p3y) {
        jfloat s1_x, s1_y, s2_x, s2_y;
        s1_x = p1x - p0x;     s1_y = p1y - p0y;
        s2_x = p3x - p2x;     s2_y = p3y - p2y;

        jfloat s, t;
        s = (-s1_y * (p0x - p2x) + s1_x * (p0y - p2y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p0y - p2y) - s2_y * (p0x - p2x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            //找到类
            jclass pointClass = env->FindClass("android/graphics/PointF");
            //找到指定类型构造方法
            jmethodID midConstructor = env->GetMethodID(pointClass, "<init>", "(FF)V");
            //创建对象
            jobject createObj = env->NewObject(pointClass, midConstructor, p0x + (t * s1_x), p0y + (t * s1_y));

            return createObj;
        }

        return 0;
  }