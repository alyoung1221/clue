package gdx.clue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import static gdx.clue.CardEnum.*;

// import static gdx.clue.Card.*;
import static gdx.clue.ClueMain.TILE_DIM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NotebookPanel {
    private Notebook notebook;
    private Player currentPlayer;
    
    private List<Player> players;
    private Map<CardType, List<Card>> deck;
    
    private List<Label> labels = new ArrayList<Label>(TOTAL);
    private Map<String, List<CheckboxEntry>> checkboxes = new HashMap<String, List<CheckboxEntry>>();
    
    private Table table;
    private ScrollPane pane;

    public void setNotebook(Notebook notebook, List<Player> players, Stage stage) {
        this.notebook = notebook;
        this.currentPlayer = notebook.getPlayer();
        this.players = players;
        this.deck = notebook.getDeck();
        
        this.table = new Table(ClueMain.skin);
        this.table.defaults().padLeft(5).align(Align.center);

        if (this.pane != null) {
            this.pane.remove();
        }      

        this.pane = new ScrollPane(this.table, ClueMain.skin);

        Label notebookTitle = new Label("Your Detective Notebook", ClueMain.skin);
    	notebookTitle.setAlignment(Align.center);
    	
        this.table.add(new Label("Your Detective Notebook", ClueMain.skin));
        this.table.row();

        this.table.add(new Label("", ClueMain.skin));
        this.table.row();
        this.table.add(new Label("SUSPECTS", ClueMain.skin, "default-yellow"));
        
        List<Card> rooms = this.deck.get(CardType.ROOM);
        List<Card> weapons = this.deck.get(CardType.WEAPON);
        List<Card> suspects = this.deck.get(CardType.SUSPECT);

        Collections.sort(this.players, (curr, prev) -> curr.getPlayerName().compareTo(prev.getPlayerName()));
        Collections.swap(this.players, players.indexOf(currentPlayer), 0);

        for (Player p: this.players) {
        	Label label = new Label(p.getAbbr(), ClueMain.skin);
        	
        	label.setColor(p.getPlayerColor());
	        
        	this.table.add(label);
        	this.checkboxes.put(p.getAbbr(), new ArrayList<CheckboxEntry>());
        }
        
        for (Card c: suspects) {
        	this.table.row();
        	
        	Label label = new Label(c.toString(), ClueMain.skin);
        	
        	if (notebook.isCardInHand(c)) {
        		label.setColor(this.currentPlayer.getPlayerColor());
        	}
        	
        	this.table.add(label);
        	this.labels.add(label);
        	
            for (Player p: this.players) {
            	CheckboxEntry entry = new CheckboxEntry(c);
    	        CheckBox checkbox = entry.checkbox;
    	        	
    	        if (p == this.currentPlayer && notebook.isCardInHand(c)) {
    	            checkbox.setChecked(true);
    	        }
    	        
    	        this.table.add(entry);
    	        
    	        List<CheckboxEntry> entries = this.checkboxes.get(p.getAbbr());
    	        
    	        entries.add(entry);
    	        
    	        this.checkboxes.replace(p.getAbbr(), entries);    
            }
        }

        this.table.row();
        this.table.add(new Label("", ClueMain.skin));
        
        this.table.row();
        this.table.add(new Label("WEAPONS", ClueMain.skin, "default-yellow"));
        
        for (Card c: weapons) {
        	this.table.row();
        	
        	Label label = new Label(c.toString(), ClueMain.skin);
        	
        	if (notebook.isCardInHand(c)) {
        		label.setColor(this.currentPlayer.getPlayerColor());
        	}
        	
        	this.table.add(label);
        	this.labels.add(label);
        	
            for (Player p: this.players) {
            	CheckboxEntry entry = new CheckboxEntry(c);
    	        CheckBox checkbox = entry.checkbox;
    	        	
    	        checkbox.setName(p.getAbbr());
    	        
    	        if (p == this.currentPlayer && notebook.isCardInHand(c)) {
    	            checkbox.setChecked(true);
    	        }

    	        this.table.add(entry);
    	        
    	        List<CheckboxEntry> entries = this.checkboxes.get(p.getAbbr());
    	        
    	        entries.add(entry);
    	        
    	        this.checkboxes.replace(p.getAbbr(), entries);   
            }
        }

        this.table.row();
        this.table.add(new Label("", ClueMain.skin));
        
        this.table.row();
        this.table.add(new Label("ROOMS", ClueMain.skin, "default-yellow"));
        
        for (Card c: rooms) {
        	this.table.row();
        	
        	Label label = new Label(c.toString(), ClueMain.skin);
        	
        	if (notebook.isCardInHand(c)) {
        		label.setColor(this.currentPlayer.getPlayerColor());
        	}
        	
        	this.table.add(label);
        	this.labels.add(label);
        	
            for (Player p: this.players) {
            	CheckboxEntry entry = new CheckboxEntry(c);
    	        CheckBox checkbox = entry.checkbox;
    	        	
    	        if (p == this.currentPlayer && notebook.isCardInHand(c)) {
    	            checkbox.setChecked(true);
    	        }

    	        this.table.add(entry);
    	        
    	        List<CheckboxEntry> entries = this.checkboxes.get(p.getAbbr());
    	        
    	        entries.add(entry);
    	        
    	        this.checkboxes.replace(p.getAbbr(), entries);   
            }
        }
        
        float height = (float) (TILE_DIM * 23.5);
        		
        pane.setBounds(TILE_DIM * 8 + TILE_DIM * 24 + 2, 0, TILE_DIM * 8, height);
        stage.addActor(pane);
    }

    public Map<String, List<CheckboxEntry>> getCheckBoxes() {
		return checkboxes;
	}
    
    public Label getLabelByCard(Card card_to_show) {
    	return this.labels
        	.stream()
        	.filter(l -> card_to_show.toString().equals(l.getText().toString()))
        	.findFirst()
        	.get();
    }
    
    public CheckBox getCheckBoxByPlayerAndCard(Player player, Card card_to_show) {
    	Entry<String, List<CheckboxEntry>> entry = this.checkboxes
    		.entrySet()
    		.stream()
    		.filter(p -> p.getKey().equals(player.getAbbr()) && p.getValue().stream().anyMatch(cb -> cb.card.toString() == card_to_show.toString()))
    		.findFirst()
    		.get();
    	
    	CheckBox checkbox = entry.getValue()
    		.stream()
    		.filter(cb -> cb.card.toString().equals(card_to_show.toString()))
    		.map(e -> e.checkbox)
    		.findFirst()
    		.get();
    	
    	return checkbox;
    }
    
    public List<CheckBox> getCheckBoxesByPlayerAndCards(Player player, List<Card> suggestions) {
    	Entry<String, List<CheckboxEntry>> entry = this.checkboxes
        	.entrySet()
        	.stream()
        	.filter(p -> p.getKey().equals(player.getAbbr()) && p.getValue().stream().anyMatch(cb -> suggestions.contains(cb.card)))
        	.findFirst()
        	.get();
        	
        List<CheckBox> checkboxes = entry.getValue()
        	.stream()
        	.filter(cb -> suggestions.contains(cb.card))
        	.map(e -> e.checkbox)
        	.toList();
        	
        return checkboxes;
    }

	private class CheckboxEntry extends Group {
        Card card;
        CheckBox checkbox;

        CheckboxEntry(Card card) {
            this.card = card;

            CheckBox checkbox = new CheckBox("", ClueMain.skin);
            
            checkbox.setProgrammaticChangeEvents(true);

            this.checkbox = checkbox;
            this.checkbox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    notebook.setToggled(CheckboxEntry.this.card);
                    Sounds.play(Sound.BUTTON);
                }
            });
            
            this.addActor(this.checkbox);
            this.setBounds(0, 0, 20, 20);
            // this.setBounds(getX(), getY(), 200, 20);
        }
    }
}