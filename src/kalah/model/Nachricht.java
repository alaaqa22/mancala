package kalah.model;

import java.io.Serializable;

public class Nachricht implements Serializable {
    private String text;
    private int laenge;
    private String farbe;
    private int counter;
    private static final int MAX_LENGTH = 280;

    private String[] farben = {"gruen,orange,rot"};


    public Nachricht(String text,int laenge, String farbe){
        if(text.length() > MAX_LENGTH){
            throw new IllegalArgumentException();
        }
        this.text = text;
        this.laenge = laenge;
        this.farbe = farbe;
    }

    public void setFarbe(String farbe) {
        this.farbe = farbe;
    }

}
