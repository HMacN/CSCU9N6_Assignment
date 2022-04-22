package spaceShipGame;

import CSCU9N6Library.TileMap;
import helperClasses.EntityUpdate;
import physics.Collider;
import physics.PhysicsEngine;
import renderableObjects.IDrawable;
import soundsAndMusic.IGameSound;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The class which stores all physics, sound, and graphics objects in the game.  Responsible for passing updates to all
 * objects, drawing them in the correct order, and removing them when their self-destruct flags are set.
 *
 * Also handles safe adding and removing of objects to the lists of game objects, which guards against concurrent
 * modification exceptions causing hard crashes.
 */
public class GameObjects
{
    //Set up arrays of in-game objects.
    private ArrayList<IDrawable> starFieldLayer1 = new ArrayList<>();
    private ArrayList<IDrawable> starFieldLayer1ToAdd = new ArrayList<>();

    private ArrayList<IDrawable> starFieldLayer2 = new ArrayList<>();
    private ArrayList<IDrawable> starFieldLayer2ToAdd = new ArrayList<>();

    private ArrayList<IDrawable> starFieldLayer3 = new ArrayList<>();
    private ArrayList<IDrawable> starFieldLayer3ToAdd = new ArrayList<>();

    private ArrayList<IDrawable> spaceStationLayer = new ArrayList<>();
    private ArrayList<IDrawable> spaceStationLayerToAdd = new ArrayList<>();

    private ArrayList<IDrawable> spaceShipLayer = new ArrayList<>();
    private ArrayList<IDrawable> spaceShipLayerToAdd = new ArrayList<>();

    private ArrayList<IDrawable> UILayer = new ArrayList<>();
    private ArrayList<IDrawable> UILayerToAdd = new ArrayList<>();

    private ArrayList<IGameSound> sounds = new ArrayList<>();
    private ArrayList<IGameSound> soundsToAdd = new ArrayList<>();

    private ArrayList<Collider> colliders = new ArrayList<>();
    private ArrayList<Collider> collidersToAdd = new ArrayList<>();

    private PhysicsEngine physicsEngine;

    private TileMap tileMap;
    private float tileMapXSpeed = 0.0f;
    private float tileMapYSpeed = 0.0f;
    private float tileMapXLocation = 0.0f;
    private float tileMapYLocation = 0.0f;

    private float playerXOffset = 0.0f;
    private float playerYOffset = 0.0f;

    /**
     * Adds a sound to the list of playing sounds.  Sets sounds to play upon adding.
     * @param sound An IGameSound object to add to the list of playing sounds.
     */
    public void addSound(IGameSound sound)
    {
        sound.play();
        this.soundsToAdd.add(sound);
    }

    /**
     * Associates this class with a physics engine to perform physics updates on colliders.
     * @param engine    A PhysicsEngine object to use to update colliders.
     */
    public void setPhysicsEngine(PhysicsEngine engine)
    {
        this.physicsEngine = engine;
    }

    /**
     * Sets the TileMap that the other objects will interact with.
     * @param tileMap   A TileMap object to add to the game.
     */
    public void addTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
        this.tileMapXLocation = 0.0f;
        this.tileMapYLocation = 0.0f;
        this.tileMapXSpeed = 0.0f;
        this.tileMapYSpeed = 0.0f;
    }

    /**
     * Updates the render location of the TileMap based on the TileMap velocity that has been set.
     * @param elapsedTimeInMillis   A long which is the elapsed time in milliseconds since the last update.
     */
    private void resolveTileMapSpeeds(long elapsedTimeInMillis)
    {
        this.tileMapXLocation = this.tileMapXLocation + (elapsedTimeInMillis * this.tileMapXSpeed);
        this.tileMapYLocation = this.tileMapYLocation + (elapsedTimeInMillis * this.tileMapYSpeed);
    }

    /**
     * Gets rid of the current TileMap by setting it to null.
     */
    public void removeTileMap()
    {
        this.tileMap = null;
    }

    /**
     * Adds a collider to the game.
     * @param collider  A Collider to be added to the game.
     */
    public void addPhysicsEntity(Collider collider)
    {
        this.collidersToAdd.add(collider);
    }

    /**
     * Adds a list of drawable objects to a particular render layer.
     * @param drawableList  A LinkedList of drawable objects to add to the game.
     * @param layer An enum which represents the layer ot add the new objects at.
     */
    public void addDrawable(LinkedList<IDrawable> drawableList, ERenderLayer layer)
    {
        for (IDrawable drawable : drawableList)
        {
            this.addDrawable(drawable, layer);
        }
    }

    /**
     * Adds a drawable object to a particular render layer.
     * @param drawable  A drawable object to add to the game.
     * @param layer An enum which represents the layer ot add the new object at.
     */
    public void addDrawable(IDrawable drawable, ERenderLayer layer)
    {
        switch (layer)
        {
            case UILayer: this.UILayerToAdd.add(drawable); break;
            case spaceShipLayer: this.spaceShipLayerToAdd.add(drawable); break;
            case spaceStationLayer: this.spaceStationLayerToAdd.add(drawable); break;
            case starFieldLayer1: this.starFieldLayer1ToAdd.add(drawable); break;
            case starFieldLayer2: this.starFieldLayer2ToAdd.add(drawable); break;
            case starFieldLayer3: this.starFieldLayer3ToAdd.add(drawable); break;
        }
    }

    /**
     * Iterates over all drawable objects in the game, and instructs them to draw themselves in the display area.  Adds new entries to the lists of things to be rendered.
     * @param graphics2D    A Graphics2D object which is the display area.
     */
    public void draw(Graphics2D graphics2D)
    {
        drawLayer(this.starFieldLayer1, this.starFieldLayer1ToAdd, graphics2D, false);
        drawLayer(this.starFieldLayer2, this.starFieldLayer2ToAdd, graphics2D, false);
        drawLayer(this.starFieldLayer3, this.starFieldLayer3ToAdd, graphics2D, false);
        drawLayer(this.spaceStationLayer, this.spaceStationLayerToAdd, graphics2D, false);

        if (this.tileMap != null)
        {
            this.tileMap.draw(
                    graphics2D,
                    (int) (this.playerXOffset + this.tileMapXLocation),
                    (int) (this.playerYOffset + this.tileMapYLocation));
        }

        drawLayer(this.spaceShipLayer, this.spaceShipLayerToAdd, graphics2D, true);
        drawLayer(this.UILayer, this.UILayerToAdd, graphics2D, false);
    }

    /**
     * Iterates over a render layer and draws all the objects in it.  Adds new objects in the "toAdd" list into the actual list.
     * @param layer An ArrayList of IDrawable objects which is to be rendered.
     * @param toAdd An ArrayList of IDrawable objects which will be added to the main list.
     * @param graphics2D    A Graphics2D object to render to.
     * @param offsetToPlayer    A boolean which describes whether or not to offset this layer to the player.
     */
    private void drawLayer(ArrayList<IDrawable> layer, ArrayList<IDrawable> toAdd, Graphics2D graphics2D, boolean offsetToPlayer)
    {
        //Stave off crashes due to concurrent modification.
        layer.addAll(toAdd);
        toAdd.clear();

        for (IDrawable entity : layer)
        {
            if (offsetToPlayer)
            {
                entity.draw(graphics2D, this.playerXOffset, this.playerYOffset);
            }
            else
            {
                entity.draw(graphics2D, 0.0f, 0.0f);
            }
        }
    }

    /**
     * Passes the update data for this update cycle to all in-game objects.  Removes any objects which are set to "self-destruct".
     * @param updateData    An EntityUpdate object which contains the update data for this update cycle.
     */
    public void update(EntityUpdate updateData)
    {
        this.playerXOffset = updateData.getPlayerXOffset();
        this.playerYOffset = updateData.getPlayerYOffset();

        resolveTileMapSpeeds(updateData.getMillisSinceLastUpdate());

        //If there is a physics engine set, and there are physics entities, then do physics updates:
        if (this.physicsEngine != null && (this.colliders.size() > 0 || this.collidersToAdd.size() > 0))
        {
            updatePhysicsObjects(updateData);
        }

        updateSounds();

        updateGraphicsLayer(this.starFieldLayer1, updateData);
        updateGraphicsLayer(this.starFieldLayer2, updateData);
        updateGraphicsLayer(this.starFieldLayer3, updateData);
        updateGraphicsLayer(this.spaceStationLayer, updateData);
        updateGraphicsLayer(this.spaceShipLayer, updateData);
        updateGraphicsLayer(this.UILayer, updateData);
    }

    /**
     * Updates the sound objects.  Adds new sounds to the list of playing sounds, and removes ones which are flagged as "self-destruct".
     */
    private void updateSounds()
    {
        //Do this to stop concurrent modification errors.
        this.sounds.addAll(this.soundsToAdd);
        this.soundsToAdd.clear();

        LinkedList<IGameSound> entitiesToDelete = new LinkedList<>();
        for (IGameSound sound : sounds)
        {
            //Delete background entities that have travelled off screen.
            if (sound.getFinished())
            {
                entitiesToDelete.add(sound);
            }
            else
            {

            }
        }

        sounds.removeAll(entitiesToDelete);
    }

    /**
     * Getter for the collection of colliders in the game.
     * @return  The ArrayList of Collider objects which contains all active Colliders in the game.
     */
    public ArrayList<Collider> getColliders()
    {
        return this.colliders;
    }

    /**
     * Adds any new colliders to the main list, and removes any flagged as "self-destruct".  Then passes update data to the physics engine.
     * @param update    An EntityUpdate object which contains the data for this update cycle.
     */
    private void updatePhysicsObjects(EntityUpdate update)
    {
        //Do this to stop concurrent modification errors.
        this.colliders.addAll(this.collidersToAdd);
        this.collidersToAdd.clear();

        LinkedList<Collider> collidersToDelete = new LinkedList<>();
        for (Collider collider : this.colliders)
        {
            //Delete background entities that have travelled off screen.
            if (collider.getSelfDestructStatus())
            {
                collidersToDelete.add(collider);
            }
        }

        this.colliders.removeAll(collidersToDelete);

        physicsEngine.update(update);
    }

    /**
     * Sets all current colliders to self destruct, to be removed in the next update cycle.
     */
    public void clearPhysicsObjects()
    {
        for (Collider collider : this.colliders)
        {
            collider.setToSelfDestruct();
        }
    }

    /**
     * Updates all drawable objects in the given render layer.  Removes any objects which are flagged as "self-destruct".
     * @param layer An ArrayList of IDrawable objects to be rendered.
     * @param update    An EntityUpdate object which contains the data for this update cycle.
     */
    private void updateGraphicsLayer(ArrayList<IDrawable> layer, EntityUpdate update)
    {
        LinkedList<IDrawable> entitiesToDelete = new LinkedList<>();
        for (IDrawable backgroundEntity : layer)
        {
            //Delete background entities that have travelled off screen.
            if (backgroundEntity.getSelfDestructWhenOffScreen())
            {
                entitiesToDelete.add(backgroundEntity);
            }
            else
            {
                backgroundEntity.update(update);
            }
        }

        layer.removeAll(entitiesToDelete);
    }

    /**
     * Clears all foreground and physics objects from the game.  Does this by setting them all to "self-destruct when
     * off screen" and then setting their speeds to the ones given as parameters.  Also sets all physics colliders to be
     * deleted, so that they stop causing in-game objects to move.
     * @param spaceShipXSpeed   A float which is the horizontal speed in pixels per millisecond to apply to all in-game objects.
     * @param spaceShipYSpeed   A float which is the vertical speed in pixels per millisecond to apply to all in-game objects.
     */
    public void clearForeground(float spaceShipXSpeed, float spaceShipYSpeed)
    {
        clearPhysicsObjects();

        for (IDrawable drawable : this.UILayer)
        {
            drawable.setSelfDestructWhenOffScreen();
            drawable.setXSpeed(-spaceShipXSpeed);
            drawable.setYSpeed(-spaceShipYSpeed);
        }

        for (IDrawable drawable : this.spaceShipLayer)
        {
            drawable.setSelfDestructWhenOffScreen();
            drawable.setXSpeed(-spaceShipXSpeed);
            drawable.setYSpeed(-spaceShipYSpeed);
        }

        for (IDrawable drawable : this.spaceStationLayer)
        {
            drawable.setSelfDestructWhenOffScreen();
            drawable.setXSpeed(-spaceShipXSpeed);
            drawable.setYSpeed(-spaceShipYSpeed);
        }

        this.tileMapXSpeed = -spaceShipXSpeed;
        this.tileMapYSpeed = -spaceShipYSpeed;
    }

    /**
     * A getter for the current TileMap.
     * @return  The TileMap object assigned to this game.
     */
    public TileMap getTileMap()
    {
        return this.tileMap;
    }

    /**
     * Sets all sounds in the game to stop playing.
     */
    public void stopAllSounds()
    {
        for (IGameSound sound : this.sounds)
        {
            sound.finishPlaying();
        }
    }

    /**
     * An enum which describes which render layer an IDrawable should be added to.
     */
    public enum ERenderLayer
    {
        starFieldLayer1,
        starFieldLayer2,
        starFieldLayer3,
        spaceStationLayer,
        spaceShipLayer,
        UILayer
    }
}
