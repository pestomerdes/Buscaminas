package com.example.especials.buscaminas;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Casilla {

    private int bombStyle;
    private Tablero tablero;
    private Context context;
    private boolean isCovered; // is block covered yet
    private boolean isMined; // does the block has a mine underneath
    private boolean isFlagged; // is block flagged as a potential mine
    private boolean isQuestionMarked; // is block question marked
    private boolean isClickable; // can block accept click events
    private int numberOfMinesInSurrounding; // number of mines in nearby blocks
    private List<Casilla> minesInSurrounding;
    private int position=-1;
    public Button imThis;
    private Contexto contexto;

    private int numberOfColumnsInMineField;
    public String state;
    private int numCasillas;

    private QuestionMarkedState questionMarkedState;
    private BlankState blankState;
    private BombState bombState;
    private FlagState flagState;
    private OpenState openState;


    public Casilla(int bombStyle, int numberOfColumnsInMineField, int numCasillas, Context context) {
        this.isMined = false;
        this.bombStyle = bombStyle;
        tablero = Tablero.getTablero();
        this.numberOfColumnsInMineField = numberOfColumnsInMineField;
        this.numCasillas = numCasillas;
        this.context = context;

        minesInSurrounding = new ArrayList<>();
        setDefaults();

    }
    public Casilla(int position,boolean isCovered, String state, int numBombs) {

        this.position = position;
        this.isCovered = isCovered;
        this.state = state;
        this.numberOfMinesInSurrounding = numBombs;
        tablero = Tablero.getTablero();
        inicialiceStates();
    }

    // set default properties for the block
    public void setDefaults()
    {
        isCovered = true;
        isFlagged = false;
        isQuestionMarked = false;
        isClickable = true;
        inicialiceStates();

    }
    private void inicialiceStates() {
        questionMarkedState = new QuestionMarkedState();
        blankState = new BlankState();
        bombState = new BombState();
        flagState = new FlagState();
        openState = new OpenState();
    }

    public void calculateCellsSurrounding() {
        try {
            int pos;

                //N
                //System.out.println("N " + position);
                pos = position - numberOfColumnsInMineField;
                //System.out.println("Soc una mina " + position);
                if (pos >= 0) putCellSurrounding(pos);
                //NO
                //System.out.println("NO " + position);
                pos = position - numberOfColumnsInMineField - 1;
                if (pos >= 0 && (pos+1)%numberOfColumnsInMineField!=0) putCellSurrounding(pos);
                //NE
                //System.out.println("NE " + position);
                pos = position - numberOfColumnsInMineField + 1;
                if (pos >= 0 && pos%numberOfColumnsInMineField!=0) putCellSurrounding(pos);
                //S
                //System.out.println("S " + position);
                pos = position + numberOfColumnsInMineField;
                if (pos < numCasillas) putCellSurrounding(pos);
                //SO
                //System.out.println("SO " + position);
                pos = position + numberOfColumnsInMineField - 1;
                if (pos < numCasillas && (pos+1)%numberOfColumnsInMineField!=0) putCellSurrounding(pos);
                //SE
                //System.out.println("SE " + position);
                pos = position + numberOfColumnsInMineField + 1;
                if (pos < numCasillas && pos%numberOfColumnsInMineField!=0) putCellSurrounding(pos);
                //O
                //System.out.println("O " + position);
                pos = position - 1;
                if (position % numberOfColumnsInMineField != 0)
                    putCellSurrounding(pos);
                //E
                //System.out.println("E " + position);
                pos = position + 1;
                if ((position + 1) % numberOfColumnsInMineField != 0)
                    putCellSurrounding(pos);

        }catch (Exception e){
            System.out.println("He petat mirant la pos ");
        }

    }

    private void putCellSurrounding(int pos){
        Casilla casilla =  tablero.casillas.get(pos);
        if(isMined)casilla.addMineInSurrounding();
        minesInSurrounding.add(casilla);
    }

    // set block as disabled/opened if true is passed
    // else enable/close it
    public void setBlockAsDisabled(boolean disabled)
    {
        if (disabled) imThis.setBackgroundResource(R.drawable.cuadrado_gris);
        else imThis.setBackgroundResource(R.drawable.cuadrado_azul);
    }

    // uncover this block
    public void openBlock()
    {

        // cannot uncover a mine which is not covered
        if (!isCovered)
            return;
        if (isMined){
            showToast("BUUUUUUUUUUUUUM");
        }
        imThis.setClickable(false);
        setCovered(false);
        if(isMined()) imThis.setBackgroundResource(bombStyle);
        else changeState(openState);
        destaparCeldasSinBombas();
        System.out.println("He obert el block de la posicio: " + position + " " + numberOfMinesInSurrounding);

    }




    public void updateBombs() {
        String message = String.format(context.getString(R.string.infoBombes), tablero.numBombes);
        tablero.textView.setText(message);
        tablero.partida.numBombasRestantes = tablero.numBombes;
    }
    private void destaparCeldasSinBombas() {
        if(numberOfMinesInSurrounding == 0 && !isMined && !isFlagged){
            System.out.println("Casilla " + position + " " + minesInSurrounding);
            for(Casilla c: minesInSurrounding)
                try {
                    if (!c.isFlagged() && !c.isQuestionMarked()) c.openBlock();
                }catch (Exception e){
                    System.out.println(c);
                }
        }
    }
    public void putMinesInSurrounding(){
       setBlockAsDisabled(true);
       changeState(openState);
    }

    public void clean(){
        changeState(blankState);
        setQuestionMarked(false);
        imThis.setClickable(true);

    }

    public void putFlag(){
        changeState(flagState);
        if (!isFlagged)tablero.numBombes -=1;
        setFlagged(true);
        updateBombs();
    }


    public void putQuestionMarked(){
        setBlockAsDisabled(false);
        changeState(questionMarkedState);
        if ((tablero.numBombes > 0 || isFlagged) && !isQuestionMarked) tablero.numBombes+=1;
        setFlagged(false);
        setQuestionMarked(true);

        updateBombs();
    }
    public void setBomb() {
        changeState(bombState);
    }

    private void changeState(State state) {
        state.doAction(contexto);
    }

    public boolean isCovered() {
        return isCovered;
    }

    public void setCovered(boolean isCovered) {
        this.isCovered = isCovered;
    }

    public boolean isMined() {
        return isMined;
    }

    public void setMined() { this.isMined = true; }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean isFlagged) { this.isFlagged = isFlagged;  }

    public boolean isQuestionMarked() {
        return isQuestionMarked;
    }

    public void setQuestionMarked(boolean isQuestionMarked) {
        this.isQuestionMarked = isQuestionMarked;
    }

    public void addMineInSurrounding() {
        this.numberOfMinesInSurrounding += 1;
    }


    public int getMinesInSurrounding() {
        return numberOfMinesInSurrounding;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setButton(Button b){imThis = b;}

    public Contexto getContexto() {
        return contexto;
    }

    public void setContexto(Contexto contexto) {
        this.contexto = contexto;
        changeState(blankState);
    }
    public void setContexto(Contexto contexto,State state) {
        this.contexto = contexto;
        changeState(state);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Casilla{" +
                "position=" + position +
                '}';
    }

    public boolean isClickable() {
        return imThis.isClickable();
    }
    private void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Casilla)) return false;

        Casilla casilla = (Casilla) o;

        if (bombStyle != casilla.bombStyle) return false;
        if (isClickable != casilla.isClickable) return false;
        if (isCovered != casilla.isCovered) return false;
        if (isFlagged != casilla.isFlagged) return false;
        if (isMined != casilla.isMined) return false;
        if (isQuestionMarked != casilla.isQuestionMarked) return false;
        if (numCasillas != casilla.numCasillas) return false;
        if (numberOfColumnsInMineField != casilla.numberOfColumnsInMineField) return false;
        if (numberOfMinesInSurrounding != casilla.numberOfMinesInSurrounding) return false;
        if (position != casilla.position) return false;
        if (imThis != null ? !imThis.equals(casilla.imThis) : casilla.imThis != null) return false;
        return !(minesInSurrounding != null ? !minesInSurrounding.equals(casilla.minesInSurrounding) : casilla.minesInSurrounding != null);

    }
}
