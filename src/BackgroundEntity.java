import game2D.*;

public class BackgroundEntity {

	private Sprite sprite;
	private int renderOrder;
	private int MAX_RENDER_ORDER;
	private boolean parallax;

	/**
	 * 
	 * @param sprite
	 */
	BackgroundEntity(Sprite sprite) {
		// TODO - implement BackgroundEntity.BackgroundEntity
		throw new UnsupportedOperationException();
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
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	/**
	 * 
	 * @param elapsed
	 */
	public void update(long elapsed) {
		// TODO - implement BackgroundEntity.update
		throw new UnsupportedOperationException();
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

}