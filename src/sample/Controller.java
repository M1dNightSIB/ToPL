package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class Controller {
    boolean flagVT = false;
    boolean flagVN = false;
    boolean flagRules = false;
    boolean flagMainSym = false;
    String setRight = "-fx-background-color: green;";
    String setWrong = "-fx-background-color: #ffa582;";
    @FXML
    private Button buildChainsButton;
    @FXML
    private Button addRule;
    @FXML
    private Button removeRule;
    @FXML
    private Button exitButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField alphabetVN;
    @FXML
    private TextField alphabetVT;
    @FXML
    private ScrollPane rulesContainer;
    private LinkedList<TextField> rules = new LinkedList<TextField>();
    private FlowPane rulesFlowContainer = new FlowPane();
    @FXML
    private Slider lengthController;
    @FXML
    private Label chainsLength;
    @FXML
    private TextField mainSymbol;
    @FXML
    private TextArea chainsField;

    @FXML
    public void initialize() {
        lengthController.setValue(1);
        lengthController.setMax(10);
        lengthController.setMin(1);
        setLength((int)lengthController.getValue());

        TextField firstRule = new TextField();
        firstRule.setPrefSize(this.rulesContainer.getPrefWidth(), 25);
        this.rules.add(firstRule);
        this.rulesFlowContainer.getChildren().add(firstRule);
        this.rulesFlowContainer.setOrientation(Orientation.VERTICAL);
        this.alphabetVT.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkAlphVT(newValue);
            }
        });

        this.alphabetVN.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkAlphVN(newValue);
            }
        });

        this.mainSymbol.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkMainSym(newValue);
            }
        });

        this.lengthController.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setLength(newValue.intValue());
            }
        });
        for(final TextField rule : this.rules){
            rule.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    checkRule(newValue, rule);
                }
            });
            rule.setStyle(this.setWrong);
        }
        this.rulesContainer.setContent(rulesFlowContainer);
        this.alphabetVT.setStyle(this.setWrong);
        this.alphabetVN.setStyle(this.setWrong);
        this.mainSymbol.setStyle(this.setWrong);
    }

    @FXML
    void buildChains(ActionEvent event) {
        for(TextField rule : this.rules){
            checkRule(rule.getText(), rule);
        }
        checkAlphVN(alphabetVN.getText());
        checkAlphVT(alphabetVT.getText());
        checkMainSym(mainSymbol.getText());
        if(this.flagVT && this.flagVN &&
                this.flagMainSym && this.flagRules){
            Builder builder = new Builder();
            ArrayList<String> rules = new ArrayList<String>();
            for(TextField field : this.rules){
                rules.add(field.getText());
            }
            int check = builder.parse(rules, mainSymbol.getText(), (int)lengthController.getValue());
           if(check == 0){
              HashSet<String> result = builder.build();
              if(result.size() == 0){
                  chainsField.setText("Невозможно получить цепочки длины " + (int)lengthController.getValue());
              }
              for(String res: result){
                  chainsField.appendText(res+"\n");
              }
           }
        }
    }

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void clearChainsField(ActionEvent event) {
        this.chainsField.clear();
    }

    void checkAlphVT(String value) {
        if (value.matches("^([a-z0-9()~*], ?)*[a-z0-9()~*]$")) {
            this.flagVT = true;
            this.alphabetVT.setStyle(this.setRight);
        } else {
            this.flagVT = false;
            this.alphabetVT.setStyle(this.setWrong);
        }
    }

    void checkAlphVN(String value) {
        if (value.matches("^([A-Z], ?)*[A-Z]$")) {
            this.flagVN = true;
            this.alphabetVN.setStyle(this.setRight);
        } else {
            this.flagVN = false;
            this.alphabetVN.setStyle(this.setWrong);
        }
    }

    void checkRule(String value, TextField rule) {
        String nonTermSym = "";
        String termSym = "";

        if (value.matches("^[A-Z]-> ?([a-zA-Z0-9()~*]* ?\\| ?)*[a-zA-Z0-9()~*]*[a-zA-Z0-9()~*]$")) {
            if (this.alphabetVN.getText().contains(value.substring(0, 1))) {
                termSym = value.replaceAll("^([A-Z]-> ?)", "")
                        .replaceAll("[A-Z]", "")
                        .replaceAll(" ?\\| ?", "")
                        .replaceAll("(.)(?=.*\\1)", "");

                nonTermSym = value.replaceAll("^([A-Z]-> ?)", "")
                        .replaceAll("[a-z0-9()~*]", "")
                        .replaceAll(" ?\\| ?", "")
                        .replaceAll("(.)(?=.*\\1)", "");

                String termAlphabet = this.alphabetVT.getText()
                        .replaceAll(",?( )?", "")
                        .replaceAll("(.)(?=.*\\1)", "");

                String nonTermAlphabet = this.alphabetVN.getText()
                        .replaceAll(",?( )?", "")
                        .replaceAll("(.)(?=.*\\1)", "");

                boolean flagTerm = true;
                boolean flagNonTerm = true;
                for(char i : termSym.toCharArray()){
                    if(!termAlphabet.contains(String.valueOf(i))){
                        flagTerm = false;
                        break;
                    }
                }
                for(char i : nonTermSym.toCharArray()){
                    if(!nonTermAlphabet.contains(String.valueOf(i))){
                        flagNonTerm = false;
                        break;
                    }
                }

                if(flagTerm && flagNonTerm){
                    this.flagRules = true;
                    rule.setStyle(this.setRight);
                } else{
                    this.flagRules = false;
                    rule.setStyle(this.setWrong);
                }

            }else {
                this.flagRules = false;
                rule.setStyle(this.setWrong);
            }
        } else {
            this.flagRules = false;
            rule.setStyle(this.setWrong);
        }
    }

    void checkMainSym(String value) {
        if (value.matches("^[A-Z]$")) {
            if (this.alphabetVN.getText().contains(value)) {
                this.flagMainSym = true;
                this.mainSymbol.setStyle(this.setRight);
            }
        } else {
            this.flagMainSym = false;
            this.mainSymbol.setStyle(this.setWrong);
        }
    }

    void setLength(int value){
        this.chainsLength.setText("l = " + String.valueOf(value));
    }

    @FXML
    void addRuleEvent(ActionEvent event) {
        final TextField rule = new TextField();
        rule.setPrefSize(this.rulesContainer.getPrefWidth(), 25);
        this.rules.add(rule);
        this.rulesFlowContainer.getChildren().add(rule);
        rule.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                checkRule(newValue, rule);
            }
        });
        rule.setStyle(this.setWrong);
    }

    @FXML
    void removeRuleEvent(ActionEvent event) {
        int indexLast = this.rules.size() - 1;
        if(indexLast >= 0) {
            TextField remField = this.rules.get(indexLast);
            this.rulesFlowContainer.getChildren().remove(remField);
            this.rules.remove(remField);
        }else{
            this.flagRules = false;
        }
    }

}
