package jeliot.lang;

import java.lang.reflect.Array;

public class ArrayUtilities {

    public static boolean nextIndex(int[] indexCounters, int[] lengths) {
        int length = lengths.length;
        int index = length - 1;

        while (index >= 0) {

            indexCounters[index]++;

            if (indexCounters[index] < lengths[index]) {
                break;
            }

            indexCounters[index] = 0;
            index--;
        }

        for (int i = 0; i < length - 1; i++) {
            if (indexCounters[i] != 0) {
                return true;
            }
        }
        return false;
    }

    public static Object getObjectAt(Object array, int[] index) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                return Array.get(tempArray, index[i]);
            } else {
                tempArray = Array.get(tempArray, index[i]);
            }
        }
        return null;
    }

    public static void setObjectAt(Object array, int[] index, Object newObject) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                Array.set(array, index[i], newObject);
            } else {
                tempArray = Array.get(tempArray, index[i]);
            }
        }
    }

}