package com.example.especials.buscaminas;

/**
 * Created by Gary on 28/06/2015.
 */
public class StateFactoryMethod {
    public State getState(String state){
        if (state.equals("-")) return new BlankState();
        else if (state.equals("?")) return new QuestionMarkedState();
        else if (state.equals("b")) return new BombState();
        else if (state.equals("f")) return new FlagState();
        else if (state.equals("o")) return new OpenState();
        return null;
    }
}
