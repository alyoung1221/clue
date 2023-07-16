package gdx.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gdx.clue.CardEnum.*;
import gdx.clue.ClueMain.Suspect;

public class PlayerSelectionDialog extends Window {
    public static int WIDTH = 300;
    public static int HEIGHT = 400;

    public static List<Suspect> players = Arrays.asList(Suspect.values());
    public static List<Card> cards = Arrays.asList(Card.values())
    	.stream()
    	.filter((c) -> c.type() == CardType.SUSPECT)
    	.toList();

    Actor previousKeyboardFocus, previousScrollFocus;
    private final FocusListener focusListener;
    private final GameScreen screen;

    public PlayerSelectionDialog(Clue game, GameScreen screen) {
        super("Player Selection", ClueMain.skin.get("dialog", Window.WindowStyle.class));
        this.screen = screen;

        setSkin(ClueMain.skin);
        setModal(true);
        defaults().pad(10);

        Table table = new Table();

        table.align(Align.left | Align.top).pad(5);
        table.columnDefaults(0).expandX().left().uniformX();
        table.columnDefaults(1).expandX().left().uniformX();

        ScrollPane sp = new ScrollPane(table, ClueMain.skin);
        add(sp).expand().fill().minWidth(200);
        row();
        
        SelectBox<Object> select = new SelectBox<Object>(ClueMain.skin);
        Object[] options = players.stream().map(Suspect::title).toArray();
        
        select.setItems(options);
        
        table.add(new Label("Your player: ", ClueMain.skin));
        table.add(select);
        
        table.add(new Label("", ClueMain.skin));
        table.row().pad(20, 0, 20, 0);

        SelectBox<Object> opponents = new SelectBox<Object>(ClueMain.skin);

        opponents.setItems(options);
        opponents.getSelection().setMultiple(true);

        table.add(new Label("Opposing players: ", ClueMain.skin));
        table.add(opponents);

        table.add(new Label("", ClueMain.skin));
        table.row().padBottom(10);

        TextButton close = new TextButton("OK", ClueMain.skin);
        
        close.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
            	if (event.toString().equals("touchDown")) {
            		Object player = select.getSelected();		
	            	Iterable<Object> iterable = () -> opponents.getSelection().iterator();
	            	
	            	List<Object> suspects = StreamSupport
	            		.stream(iterable.spliterator(), false)
	            		.collect(Collectors.toList());

	            	boolean duplicate = suspects
	            		.stream()
	            		.anyMatch(o -> o.equals(player));

	                if (duplicate || suspects.size() < 2) {
	                	return false;
	                }

	                suspects.add(player);

	                suspects.stream().forEach((p) -> {
	                	Suspect suspect = players
	                		.stream()
	                		.filter(s -> s.title().equals(p.toString()))
	                		.findFirst()
	                		.get();
	                	
	                	Card card = cards
	                		.stream()
	                		.filter(c -> c.id() == suspect.id())
	                		.findFirst()
	                		.get();

	                	game.addPlayer(suspect, card, !p.equals(player));
	                });

	                hide();
	                
	                ClueMain.START_BUTTON.setDisabled(true);
	
	                screen.startGame();
            	}

                return false;
            }
        });
        
        table.add(close).size(120, 25);

        focusListener = new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    focusChanged(event);
                }
            }

            @Override
            public void scrollFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    focusChanged(event);
                }
            }

            private void focusChanged(FocusListener.FocusEvent event) {
                Stage stage = getStage();
                if (isModal() && stage != null && stage.getRoot().getChildren().size > 0 && stage.getRoot().getChildren().peek() == PlayerSelectionDialog.this) {
                    Actor newFocusedActor = event.getRelatedActor();
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(PlayerSelectionDialog.this) && !(newFocusedActor.equals(previousKeyboardFocus) || newFocusedActor.equals(previousScrollFocus))) {
                        event.cancel();
                    }
                }
            }
        };
    }

    public void show(Stage stage) {
        clearActions();

        removeCaptureListener(ignoreTouchDown);

        previousKeyboardFocus = null;
        Actor actor = stage.getKeyboardFocus();
        if (actor != null && !actor.isDescendantOf(this)) {
            previousKeyboardFocus = actor;
        }

        previousScrollFocus = null;
        actor = stage.getScrollFocus();
        if (actor != null && !actor.isDescendantOf(this)) {
            previousScrollFocus = actor;
        }

        pack();

        stage.addActor(this);
        //stage.setKeyboardFocus(playerSelection);
        stage.setScrollFocus(this);

        Gdx.input.setInputProcessor(stage);

        Action action = sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade));
        addAction(action);

        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
    }

    public void hide() {
        Action action = sequence(fadeOut(0.4f, Interpolation.fade), Actions.removeListener(ignoreTouchDown, true), Actions.removeActor());

        Stage stage = getStage();

        if (stage != null) {
            removeListener(focusListener);
        }

        if (action != null) {
            addCaptureListener(ignoreTouchDown);
            addAction(sequence(action, Actions.removeListener(ignoreTouchDown, true), Actions.removeActor()));
        } else {
            remove();
        }

        Gdx.input.setInputProcessor(new InputMultiplexer(screen, stage));
    }

    protected InputListener ignoreTouchDown = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            event.cancel();
            return false;
        }
    };

}
