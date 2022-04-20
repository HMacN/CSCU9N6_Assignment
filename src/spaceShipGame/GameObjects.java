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

public class GameObjects
{
    //Set up arrays of in-game objects.
    private ArrayList<IDrawable> starFieldLayer1 = new ArrayList<>();
    private ArrayList<IDrawable> starFieldLayer2 = new ArrayList<>();
    private ArrayList<IDrawable> starFieldLayer3 = new ArrayList<>();
    private ArrayList<IDrawable> spaceStationLayer = new ArrayList<>();
    private ArrayList<IDrawable> spaceShipLayer = new ArrayList<>();
    private ArrayList<IDrawable> UILayer = new ArrayList<>();

    private ArrayList<IGameSound> sounds = new ArrayList<>();
    private ArrayList<Collider> colliders = new ArrayList<>();
    private PhysicsEngine physicsEngine;

    private TileMap tileMap;
    private float playerXOffset = 0.0f;
    private float playerYOffset = 0.0f;
    private int screenWidth;
    private int screenHeight;

    public void addSound(IGameSound sound)
    {
        sound.play();
        this.sounds.add(sound);
    }

    public void setPhysicsEngine(PhysicsEngine engine)
    {
        this.physicsEngine = engine;
    }

    public void addTileMap(TileMap tileMap, int screenWidth, int screenHeight)
    {
        this.tileMap = tileMap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void addPhysicsEntity(Collider collider)
    {
        this.colliders.add(collider);
    }

    public void addDrawable(LinkedList<IDrawable> drawableList, ERenderLayer layer)
    {
        for (IDrawable drawable : drawableList)
        {
            this.addDrawable(drawable, layer);
        }
    }

    public void addDrawable(IDrawable drawable, ERenderLayer layer)
    {
        switch (layer)
        {
            case UILayer: this.UILayer.add(drawable); break;
            case spaceShipLayer: this.spaceShipLayer.add(drawable); break;
            case spaceStationLayer: this.spaceStationLayer.add(drawable); break;
            case starFieldLayer1: this.starFieldLayer1.add(drawable); break;
            case starFieldLayer2: this.starFieldLayer2.add(drawable); break;
            case starFieldLayer3: this.starFieldLayer3.add(drawable); break;
        }
    }

    public void draw(Graphics2D graphics2D)
    {
        drawLayer(this.starFieldLayer1, graphics2D, false);
        drawLayer(this.starFieldLayer2, graphics2D, false);
        drawLayer(this.starFieldLayer3, graphics2D, false);
        drawLayer(this.spaceStationLayer, graphics2D, false);

        if (this.tileMap != null)
        {
            this.tileMap.draw(graphics2D, (int) this.playerXOffset, (int) this.playerYOffset);
        }

        drawLayer(this.spaceShipLayer, graphics2D, true);
        drawLayer(this.UILayer, graphics2D, false);
    }

    private void drawLayer(ArrayList<IDrawable> layer, Graphics2D graphics2D, boolean offsetToPlayer)
    {
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

    public void update(EntityUpdate updateData)
    {
        this.playerXOffset = updateData.getPlayerXOffset();
        this.playerYOffset = updateData.getPlayerYOffset();

        //If there is a physics engine set, and there are physics entities, then do physics updates:
        if (this.physicsEngine != null && this.colliders.size() > 0)
        {
            updatePhysicsObjects(this.colliders, updateData);
        }

        updateSounds(sounds);

        updateGraphicsLayer(this.starFieldLayer1, updateData);
        updateGraphicsLayer(this.starFieldLayer2, updateData);
        updateGraphicsLayer(this.starFieldLayer3, updateData);
        updateGraphicsLayer(this.spaceStationLayer, updateData);
        updateGraphicsLayer(this.spaceShipLayer, updateData);
        updateGraphicsLayer(this.UILayer, updateData);
    }

    private void updateSounds(ArrayList<IGameSound> sounds)
    {
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

    public ArrayList<Collider> getColliders()
    {
        return this.colliders;
    }

    private void updatePhysicsObjects(ArrayList<Collider> colliders, EntityUpdate update)
    {
        LinkedList<Collider> collidersToDelete = new LinkedList<>();
        for (Collider collider : colliders)
        {
            //Delete background entities that have travelled off screen.
            if (collider.getSelfDestructStatus())
            {
                collidersToDelete.add(collider);
            }
            else
            {
                collider.update(update.getMillisSinceLastUpdate());
            }
        }

        colliders.removeAll(collidersToDelete);

        physicsEngine.update(update);
    }

    private void updateGraphicsLayer(ArrayList<IDrawable> layer, EntityUpdate update)
    {
        LinkedList<IDrawable> entitiesToDelete = new LinkedList<>();
        for (IDrawable backgroundEntity : layer)
        {
            //Delete background entities that have travelled off screen.
            if (backgroundEntity.getSelfDestructStatus())
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

    //TODO change this to a flag to be acted on.
    public void clearForeground(float spaceShipXSpeed, float spaceShipYSpeed)
    {
        this.colliders.clear();

        for (IDrawable drawable : this.UILayer)
        {
            drawable.setXSpeed(-spaceShipXSpeed);
            drawable.setYSpeed(-spaceShipYSpeed);
        }

        for (IDrawable drawable : this.spaceShipLayer)
        {
            drawable.setXSpeed(-spaceShipXSpeed);
            drawable.setYSpeed(-spaceShipYSpeed);
        }
    }

    public TileMap getTileMap()
    {
        return this.tileMap;
    }

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
