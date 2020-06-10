import java.util.HashMap;
import java.util.Map;
public class KeyMapper{
    final Map<Character, Integer> characterIntegerMap;
    KeyMapper(){
        characterIntegerMap = new HashMap<Character, Integer>();
        for (int i=0; i<10; i++){
            characterIntegerMap.put((char)(i+'0'), i);
        }

    }
}



