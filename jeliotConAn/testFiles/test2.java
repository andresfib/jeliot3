//Class:MyClass

public class MyClass {

    static int i = 0;    
    int k = 0;
    public MyClass() {
        i++;
        k = i + 1;
    }

    public static void main() {
        int j = 3;
        int[] array = {1,2,3};
        array[i++] += ++j;
        array[--i] += --j;
        array[++i] += j++;
        array[i--] += j--;

        array[i++] -= ++j;
        array[--i] -= --j;
        array[++i] -= j++;
        array[i--] -= j--;

        array[i++] %= ++j;
        array[--i] %= --j;
        array[++i] %= j++;
        array[i--] %= j--;

         array[i++] *= ++j;
        array[--i] *= --j;
        array[++i] *= j++;
        array[i--] *= j--;

        array[i++] /= ++j;
        array[--i] /= --j;
        array[++i] /= j++;
        array[i--] /= j--;

        --array[i++];
        ++array[--i];
        array[++i]++;
        array[i--]--;
        
    }
}
