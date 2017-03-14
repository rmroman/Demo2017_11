package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Esta clase representa cualquier pantalla que se quiera mostrar
 *
 * Contiene una cámara y la vista (@see StretchViewport)
 */
public abstract class Pantalla implements Screen
{
    public static final float ANCHO = 1280;
    public static final float ALTO = 800;

    // Disponibles en las subclases
    // Cámara, vista y objeto batch para dibujar
    protected OrthographicCamera camara;
    protected Viewport vista;
    protected SpriteBatch batch;

    // Constructor. Crea la cámara, vista y batch.
    public Pantalla() {
        camara = new OrthographicCamera(ANCHO, ALTO);
        camara.position.set(ANCHO/2, ALTO/2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO, ALTO, camara);
        batch = new SpriteBatch();
    }

    // Borra en negro
    protected void borrarPantalla() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    // Borra con el color que indique el usuario
    protected void borrarPantalla(float r, float g, float b) {
        Gdx.gl.glClearColor(r,g,b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width, height);
        actualizarVista();
    }

    public void actualizarVista() {

    }

    @Override
    public void hide() {
        // Llama a dispose para liberar los recursos utilizados por cada pantalla
        dispose();
    }
}
