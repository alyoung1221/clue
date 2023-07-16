package gdx.clue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gdx.clue.CardEnum.*;
import gdx.clue.ClueMain.Suspect;

import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;

public class Clue {
    private final List<Player> players = new ArrayList<Player>(6);
    private int cardsPerPlayer;
    private List<Card> shuffled = new ArrayList<Card>(TOTAL);
    private final List<Card> victimSet = new ArrayList<Card>(3);
 
    private Map<CardType, List<Card>> deck = new HashMap<CardType, List<Card>>();

    public void createDeck() {
    	List<Card> cards = Arrays.asList(Card.values());
    	
    	List<Card> rooms = cards.stream().filter(c -> c.type() == CardType.ROOM).toList();
    	List<Card> suspects = cards.stream().filter(c -> c.type() == CardType.SUSPECT).toList();
    	List<Card> weapons = cards.stream().filter(c -> c.type() == CardType.WEAPON).toList();
    	
    	deck.put(CardType.SUSPECT, suspects);
        deck.put(CardType.WEAPON, weapons);    
        deck.put(CardType.ROOM, rooms);
        
        Random rand = new Random();
        
        // pull the victim set
        int s = rand.nextInt(NUM_SUSPECTS);
        int w = rand.nextInt(NUM_WEAPONS);
        int r = rand.nextInt(NUM_ROOMS);
        
        Card suspect = suspects.get(s);
        Card weapon = weapons.get(w);
        Card room = rooms.get(r);

        victimSet.add(suspect);
        victimSet.add(weapon);        
        victimSet.add(room);
        
        shuffled = this.deck
            .entrySet()
            .stream()
            .map(Entry::getValue)
            .flatMap(List::stream)
            .filter((c) -> !victimSet.contains(c))
            .collect(Collectors.toList());
        
        // shuffle it
        Collections.shuffle(shuffled);
    }

    public Player addPlayer(Suspect suspect, Card card, boolean computer) {
        Player player = new Player(card, suspect.title(), suspect.abbr(), suspect, computer);
        
        players.add(player);
        
        return player;
    }
    
    public boolean removePlayer(Player player) {
    	return this.players.remove(player);
    }

    public int getCurrentPlayerCount() {
        return players.size();
    }

    public boolean containsSuspect(Card card) {
    	List<Card> cards = players
    		.stream()
    		.map(p -> p.getHand())
    		.flatMap(List::stream)
            .toList();
    	
    	return cards.contains(card);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        Player player = players
            .stream()
            .filter(p -> p.getSuspect().id() == id)
            .findFirst()
            .get();

        return player;
    }

    public Player getPlayer(String name) {
        Player player = players
        	.stream()
        	.filter(p -> p.getPlayerName().equals(name))
        	.findFirst()
        	.get();

        return player;
    }

    /**
	 * @return the cardsPerPlayer
	 */
	public int getCardsPerPlayer() {
		return cardsPerPlayer;
	}

	/**
	 * @param cardsPerPlayer the cardsPerPlayer to set
	 */
	public void setCardsPerPlayer(int cardsPerPlayer) {
		this.cardsPerPlayer = cardsPerPlayer;
	}

	/**
	 * @return the deck
	 */
	public Map<CardType, List<Card>> getDeck() {
		return this.deck;
	}

	public List<Card> dealShuffledDeck() {
        // deal the cards
		cardsPerPlayer = shuffled.size() / players.size();

        List<Card> dealt = new ArrayList<Card>();

		StreamUtils.zipWithIndex(players.stream()).forEachOrdered(player -> {
			Long i = player.getIndex();
			Player p = player.getValue();

			Integer start = i.intValue() * cardsPerPlayer;
			Integer end = (i.intValue() + 1) * cardsPerPlayer;

			List<Card> hand = shuffled.subList(start, end);

			p.setHand(hand);
            dealt.addAll(hand);
		});

        List<Card> remaining = shuffled
        	.stream()
        	.filter((c) -> !dealt.contains(c))
        	.toList();

        return remaining;
        
		/*int player_index = 0;
        
        for (int i = 0; i < shuffled.size(); i++) {
            Card card = shuffled.get(i);
            
            if (player_index == players.size()) {
                player_index = 0;
            }
            
            Player player = players.get(player_index);
            
            player.addCard(card);
            player_index++;
        }*/
    }

    public String getAdjacentPlayerName(String name) {
        String adjPlayerName = null;
        
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            
            if (p.getPlayerName().equals(name)) {
                int next = i + 1;
                
                if (next == players.size()) {
                    next = 0;
                }
                
                adjPlayerName = players.get(next).getPlayerName();
                
                break;
            }
        }
        
        return adjPlayerName;
    }

    public List<Card> getShuffledDeck() {
        return shuffled;
    }

    public boolean matchesVictimSet(List<Card> accusation) {
        Card weapon = null, suspect = null, room = null;
        
        for (Card card: accusation) {
            if (card.type() == CardType.ROOM) {
                room = card;
            }
            
            if (card.type() == CardType.SUSPECT) {
                suspect = card;
            }            
            
            if (card.type() == CardType.WEAPON) {
                weapon = card;
            }
        }

        return matchesVictimSet(weapon, suspect, room);
    }

    public boolean matchesVictimSet(Card weapon, Card suspect, Card room) {
        return (victimSet.contains(weapon) && victimSet.contains(suspect) && victimSet.contains(room));
    }

    public String toString() {
        String text = "";

        for (Card c : victimSet) {
            text += c.toString() + "\n";
        }

        for (Player p : players) {
            text += "----------\n";
            text += p.toString();
        }
        return text;
    }
}