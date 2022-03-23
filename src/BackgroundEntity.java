import game2D.*;

public class BackgroundEntity {

	private Sprite sprite;
	private int renderOrder;
	private int MAX_RENDER_ORDER;
	private boolean parallax;

	/**
	 *
	 */
	BackgroundEntity()
	{

	}

	/**
	 * 
	 * @param renderOrder
	 */
	public void setRenderOrder(int renderOrder) {
		this.renderOrder = renderOrder;
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	/**
	 * 
	 * @param sprite
	 */
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}

	/**
	 * 
	 * @param entityUpdate
	 */
	public void update(EntityUpdate entityUpdate)
	{
		this.sprite.update(entityUpdate.getMillisSinceLastUpdate());
	}

	public boolean getParallax() {
		return this.parallax;
	}

	/**
	 * 
	 * @param parallax
	 */
	public void setParallax(boolean parallax) {
		this.parallax = parallax;
	}

	public double getXSpeed()
	{
		return this.sprite.getVelocityX();
	}

	public void setXSpeed(float xSpeed)
	{
		this.sprite.setVelocityX(xSpeed);
	}

	public double getYSpeed()
	{
		return this.sprite.getVelocityY();
	}

	public void setYSpeed(float ySpeed)
	{
		this.sprite.setVelocityY(ySpeed);
	}

	public float getXCoord()
	{
		return this.sprite.getX();
	}

	public void setXCoord(float xCoord)
	{
		this.sprite.setX(xCoord);
	}

	public float getYCoord()
	{
		return this.sprite.getY();
	}

	public void setYCoord(float yCoord)
	{
		this.sprite.setY(yCoord);
	}

	public boolean tooFarOffScreen()
	{
		boolean farOffScreen = false;
		int minX = (int) (SpaceshipGame.SCREEN_WIDTH * 0);
		int maxX = (int) (SpaceshipGame.SCREEN_WIDTH * 1);
		int minY = (int) (SpaceshipGame.SCREEN_HEIGHT * 0);
		int maxY = (int) (SpaceshipGame.SCREEN_HEIGHT * 1);


		if (this.sprite.getY() < minY
				|| this.sprite.getY() > maxY
				|| this.sprite.getX() < minX
				|| this.sprite.getX() > maxX )
		{
			farOffScreen = true;
		}

		return farOffScreen;
	}
}