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
    private float tileMapXSpeed = 0.0f;
    private float tileMapYSpeed = 0.0f;
    private float tileMapXLocation = 0.0f;
    private float tileMapYLocation = 0.0f;

    private float playerXOffset = 0.0f;
    private float playerYOffset = 0.0f;

    public void addSound(IGameSound sound)
    {
        sound.play();
        this.sounds.add(sound);
    }

    public void setPhysicsEngine(PhysicsEngine engine)
    {
        this.physicsEngine = engine;
    }

    public void addTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
        this.tileMapXLocation = 0.0f;
        this.tileMapYLocation = 0.0f;
        this.tileMapXSpeed = 0.0f;
        this.tileMapYSpeed = 0.0f;
    }

    private void resolveTileMapSpeeds(long elapsedTimeInMillis)
    {
        this.tileMapXLocation = this.tileMapXLocation + (elapsedTimeInMillis * this.tileMapXSpeed);
        this.tileMapYLocation = this.tileMapYLocation + (elapsedTimeInMillis * this.tileMapYSpeed);
    }

    public void removeTileMap()
    {
        this.tileMap = null;
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
            this.tileMap.draw(
                    graphics2D,
                    (int) (this.playerXOffset + this.tileMapXLocation),
                    (int) (this.playerYOffset + this.tileMapYLocation));
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

        resolveTileMapSpeeds(updateData.getMillisSinceLastUpdate());

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
        }

        colliders.removeAll(collidersToDelete);

        physicsEngine.update(update);
    }

    public void clearPhysicsObjects()
    {
        for (Collider collider : this.colliders)
        {
            collider.setToSelfDestruct();
        }
    }

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

    public TileMap getTileMap()
    {
        return this.tileMap;
    }

    public void stopAllSounds()
    {
        for (IGameSound sound : this.sounds)
        {
            sound.finishPlaying();
        }
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
