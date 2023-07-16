package gdx.clue;

import static gdx.clue.CardEnum.*;

import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Notebook {
    private Player player;
    private List<Player> opponents;
    
    private Map<CardType, List<Card>> deck;
    
    private final LinkedHashMap<Card, CardEntry> entries = new LinkedHashMap<>();

    public Notebook(Clue game, Player player) {
        this.setPlayer(player);
        this.setDeck(game.getDeck());

        List<Card> cards = this.deck
        	.entrySet()
        	.stream()
        	.map(Entry::getValue)
        	.flatMap(List::stream)
        	.toList();

        for (Card card: cards) {
        	CardEntry entry = new CardEntry(card);
        	
        	entry.setInHand(player.getHand().contains(card));
            entries.put(card, entry);
        }
    }

    public void setToggled(Card card) {
        CardEntry entry = entries.get(card);
        entry.setToggled(!entry.getToggled());
    }

    public boolean isCardInHand(Card card) {
        CardEntry entry = entries.get(card);
        
        return entry.inHand();
    }

    public boolean isCardToggled(Card card) {
        CardEntry entry = entries.get(card);
        
        return entry.getToggled();
    }

    /*public boolean isLocationCardInHandOrToggled(Location location) {
        Card roomCard = (location.getRoomId() != -1 ? new Card(CardType.ROOM, location.getRoomId()) : null);
        
        return isLocationCardInHandOrToggled(roomCard);
    }*/

    public boolean isLocationCardInHandOrToggled(Card card) {
        if (isCardInHand(card) || isCardToggled(card)) {
            return true;
        }
        
        return false;
    }

    public String toString() {
        String text = "";
        
        for (CardEntry entry : entries.values()) {
            text += entry.toString();
        }
        
        return text;
    }

    public Card randomlyPickCardOfType(CardType type) {
        // select a card of indicated type and check the cards in your hand
        List<Card> picks = this.deck.get(type)
        	.stream()
        	.filter(card -> !isCardInHand(card) && !isCardToggled(card))
        	.collect(Collectors.toList());
        
        Collections.shuffle(picks);

        Card picked_card = picks.get(0);

        return picked_card;
    }

    // TODO Update method
    /*public boolean canMakeAccusation() {
        int scount = 0;
        
        for (int i = 0; i < NUM_SUSPECTS; i++) {
            Card card = new Card(CardType.SUSPECT, i);
            
            if (!isCardInHand(card) && !isCardToggled(card)) {
                scount++;
            }
        }
        
        int wcount = 0;
        
        for (int i = 0; i < NUM_WEAPONS; i++) {
            Card card = new Card(CardType.WEAPON, i);
            
            if (!isCardInHand(card) && !isCardToggled(card)) {
                wcount++;
            }
        }
        
        int lcount = 0;
        
        for (int i = 0; i < NUM_ROOMS; i++) {
            Card card = new Card(CardType.ROOM, i);
            
            if (!isCardInHand(card) && !isCardToggled(card)) {
                lcount++;
            }
        }

        return scount <= 2 && wcount <= 2 && lcount <= 2;
    }*/

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
	 * @return the opponents
	 */
	public List<Player> getOpponents() {
		return opponents;
	}

	/**
	 * @param opponents the opponents to set
	 */
	public void setOpponents(List<Player> opponents) {
		this.opponents = opponents;
	}

	/**
	 * @return the deck
	 */
	public Map<CardType, List<Card>> getDeck() {
		return deck;
	}

	/**
	 * @param map the deck to set
	 */
	public void setDeck(Map<CardType, List<Card>> map) {
		this.deck = map;
	}

	class CardEntry {
        Card value;
        boolean inHand = false;
        boolean toggled = false;

        CardEntry(Card card) {
            this.value = card;
        }

        boolean inHand() {
            return inHand;
        }

        void setInHand(boolean inHand) {
            this.inHand = inHand;
        }

        boolean getToggled() {
            return toggled;
        }

        void setToggled(boolean toggled) {
            this.toggled = toggled;
        }

        String getValue() {
            return value.toString();
        }

        public String toString() {
            return value + "\t" + (inHand ? "X" : "-") + "\t" + (toggled ? "X" : "-") + "\n";
        }
    }
}