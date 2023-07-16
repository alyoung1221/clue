package gdx.clue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static gdx.clue.CardEnum.*;
import gdx.clue.ClueMain.Suspect;

import java.util.ArrayList;
import java.util.List;
import gdx.clue.astar.Location;
import java.util.Objects;

public class Player {
    private final Suspect suspect;
    private String name;
    private String abbr;
    private Card card;
    private List<Card> cardsInHand = new ArrayList<>();
    private boolean computerPlayer;
    private Location location;	
    private List<Location> reachable;
    private Notebook notebook;
    private Actor stageActor;
    private boolean hasMadeFalseAccusation = false;

	public Player(Card card, String name, String abbr, Suspect suspect, boolean computer) {
        this.name = name;
        this.abbr = abbr;
        this.card = card;
        this.suspect = suspect;
        this.computerPlayer = computer;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.suspect);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.suspect != other.suspect) {
            return false;
        }
        return true;
    }

    public void setLocation(Location location) {
        if (location == null) {
            return;
        }

        //reset the height back to 100
        if (this.location != null) {
            this.location.setHeight(100);
        }

        this.location = location;

        //allow multiple players on a room tile but block regular tiles with one player
        if (!this.location.isRoom()) {
            this.location.setHeight(1000);
        }
    }

    public Location getLocation() {
        return this.location;
    }

	/**
	 * @return the reachable locations
	 */
	public List<Location> getReachable() {
		return reachable;
	}

	/**
	 * @param locations the reachable locations to set
	 */
	public void setReachable(List<Location> locations) {
		this.reachable = locations;
	}

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Notebook getNotebook() {
        return this.notebook;
    }

    public void addCard(Card card) {
        this.cardsInHand.add(card);
    }

    public void setHand(List<Card> hand) {
        this.cardsInHand = hand;
    }
    
    public List<Card> getHand() {
        return this.cardsInHand;
    }

    public boolean isCardInHand(Card card) {
        return this.cardsInHand.contains(card);
    }

    public boolean isCardInHand(String name) {
    	Card card = Card.valueOf(name);
        
        return this.cardsInHand.contains(card);
    }

    public boolean isHoldingCardInSuggestion(List<Card> suggestions) {
        boolean hasCards = suggestions.stream().anyMatch((c) -> this.cardsInHand.contains(c));

        return hasCards;
    }

    public Actor getStageActor() {
        return stageActor;
    }

    public void setStageActor(Actor stageActor) {
        this.stageActor = stageActor;
    }

    @Override
    public String toString() {
        return suspect + "\n" + this.notebook.toString();
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card playerCard) {
        this.card = playerCard;
    }

    public Color getPlayerColor() {
        return this.suspect.color();
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    /**
	 * @return the abbr
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * @param abbr the abbr to set
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public Suspect getSuspect() {
        return this.suspect;
    }

    public boolean isComputerPlayer() {
        return computerPlayer;
    }

    public void setComputerPlayer(boolean computerPlayer) {
        this.computerPlayer = computerPlayer;
    }

    public boolean hasMadeFalseAccusation() {
        return this.hasMadeFalseAccusation;
    }

    public void setHasMadeFalseAccusation(boolean falseAccusation) {
        this.hasMadeFalseAccusation = falseAccusation;
    }
}
