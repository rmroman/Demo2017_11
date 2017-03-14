package mx.rmr.demo201711;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Representa un elemento gráfico del juego
 */

public class Objeto
{
    protected Sprite sprite;    // Imagen

    public Objeto() {

    }

    public Objeto(Texture textura, float x, float y) {
        sprite = new Sprite(textura);
        sprite.setPosition(x, y);
    }

    public void dibujar(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
