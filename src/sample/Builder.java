package sample;

import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Builder {
    ArrayList<String> chains = new ArrayList<String>();
    private HashMap<String, String[]>
            rules = new HashMap<String, String[]>();
    private String[] start;
    private String mainSymbol;
    private int maxLength;
    private HashSet<String> resultArray = new HashSet<String>();

    public HashSet<String> build() {
        for (String elem : start) {//инициализация массива цепочкой из целевого символа
            chains.add(elem);
        }
        int size = chains.size();
        for(int j = 0; j < size; j++) {
            String chain;
            chain = chains.get(j);

            if(!chain.matches("(.*)[A-Z](.*)") && chain.length() <= maxLength) {
                resultArray.add(chain);
            }

            char[] chainArray = chain.toCharArray();

            for (int i = 0; i < chainArray.length; i++) {
                String key = String.valueOf(chainArray[i]);

                if (key.matches("(.*)[A-Z](.*)")) {
                    String[] substitution = rules.get(key);

                    for (String sub : substitution) {
                        String newChain = chain.replaceFirst("[A-Z]", sub);
                        if(!newChain.matches("(.*)[A-Z](.*)") && (newChain.length() <= maxLength)){
                            resultArray.add(newChain);
                        }else if(newChain.length() <= maxLength) {
                            chains.add(newChain);
                        }
                    }
                }
            }
            size = chains.size();
        }
        return resultArray;
    }

    public int parse(ArrayList<String> rules, String mainSym, int chainLength) {

        this.mainSymbol = mainSym;
        this.maxLength = chainLength;
        for (String rule : rules) {
            String key = rule.substring(0, 1);//ключ в мэп 'S' ->
            String val = rule.substring(3).replaceAll(" ", "");
            String[] valueArray = val.split("\\|");//массив возможных замен
            this.rules.put(key, valueArray);
        }
        String[] startChain = this.rules.get(mainSymbol);
        if (startChain != null) {
            start = startChain;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Для целевого символа отсутствует правило.");
            alert.show();
            return -1;
        }
        return 0;
    }
}
