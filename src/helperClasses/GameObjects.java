package helperClasses;

import CSCU9N6Library.Tile;
import CSCU9N6Library.TileMap;
import physics.IPhysicsEntity;
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
    private ArrayList<IPhysicsEntity> physicsEntities = new ArrayList<>();

    private TileMap tileMap;
    private int tileMapXOffset = 0;
    private int tileMapYOffset = 0;

    public void addSound(IGameSound sound)
    {
        sound.play();
        this.sounds.add(sound);
    }

    public void addTileMap(TileMap tileMap)
    {
        this.tileMap = tileMap;
    }

    public void addPhysicsEntity(IPhysicsEntity physicsEntity)
    {
        this.physicsEntities.add(physicsEntity);
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
        //Draw background objects.
        drawLayer(this.starFieldLayer1, graphics2D);
        drawLayer(this.starFieldLayer2, graphics2D);
        drawLayer(this.starFieldLayer3, graphics2D);
        drawLayer(this.spaceStationLayer, graphics2D);
        drawLayer(this.spaceShipLayer, graphics2D);
        drawLayer(this.UILayer, graphics2D);

        if (this.tileMap != null)
        {
            this.tileMap.draw(graphics2D, this.tileMapXOffset, this.tileMapYOffset);
        }
    }

    private void drawLayer(ArrayList<IDrawable> layer, Graphics2D graphics2D)
    {
        for (IDrawable entity : layer)
        {
            entity.draw(graphics2D);
        }
    }

    public void update(EntityUpdate updateData)
    {
        this.tileMapXOffset = updateData.getPlayerXOffset();
        this.tileMapYOffset = updateData.getPlayerYOffset();

        updatePhysicsObjects(this.physicsEntities, updateData);
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

    public ArrayList<IPhysicsEntity> getPhysicsEntities()
    {
        return this.physicsEntities;
    }

    private void updatePhysicsObjects(ArrayList<IPhysicsEntity> physicsEntities, EntityUpdate update)
    {


        LinkedList<IPhysicsEntity> entitiesToDelete = new LinkedList<>();
        for (IPhysicsEntity physicsEntity : physicsEntities)
        {
            //Delete background entities that have travelled off screen.
            if (physicsEntity.getSelfDestructStatus())
            {
                entitiesToDelete.add(physicsEntity);
            }
            else
            {
                physicsEntity.update(update);
            }
        }

        physicsEntities.removeAll(entitiesToDelete);
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

    public void clearForeground(float spaceShipXSpeed, float spaceShipYSpeed)
    {
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
