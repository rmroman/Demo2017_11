package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by roberto on 23/03/17.
 */

class PantallaRunner extends Pantalla
{
    private final float DELTA_X = 10;    // Desplazamiento del personaje
    private final float DELTA_Y = 10;
    private final float UMBRAL = 50; // Para asegurar que hay movimiento

    private final Demo juego;
    private final AssetManager manager;

    // Fondo
    private Fondo fondo;
    private Texture texturaFondo;

    // Punteros (dedo para pan horizontal, vertical)
    private final int INACTIVO = -1;
    private int punteroHorizontal = INACTIVO;
    private int punteroVertical = INACTIVO;

    // Coordenadas
    private float xHorizontal = 0;
    private float yVertical = 0;

    // Objeto de prueba
    private Personaje mario;
    private float dx = 0;
    private float dy = 0;


    public PantallaRunner(Demo juego) {
        super();
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        texturaFondo = manager.get("runner/fondoRunnerD.jpg");
        fondo = new Fondo(texturaFondo);
        mario = new Personaje((Texture)(manager.get("mario/marioSprite.png")), 50, 50);

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    @Override
    public void render(float delta) {
        // Actualizar
        actualizarMario();
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fondo.dibujar(batch, delta);
        mario.dibujar(batch);
        batch.end();
    }

    private void actualizarMario() {
        mario.sprite.setPosition(mario.sprite.getX()+dx, mario.sprite.getY()+dy);
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        manager.unload("runner/fondoRunnerD.jpg");
    }

    private class ProcesadorEntrada implements InputProcessor
    {
        private Vector3 v = new Vector3();

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            v.set(screenX, screenY, 0);
            camara.unproject(v);
            if (v.x < Pantalla.ANCHO/2 && punteroHorizontal == INACTIVO) {
                // Horizontal
                punteroHorizontal = pointer;
                xHorizontal = v.x;
            } else if (v.x >= Pantalla.ANCHO/2 && punteroVertical == INACTIVO ) {
                // Vertical
                punteroVertical = pointer;
                yVertical = v.y;
            }

            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (pointer == punteroHorizontal) {
                punteroHorizontal = INACTIVO;
                dx = 0; // Deja de moverse en x
            } else if (pointer == punteroVertical) {
                punteroVertical = INACTIVO;
                dy = 0; // Deja de moverse en y
            }
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {

            v.set(screenX, screenY, 0);
            camara.unproject(v);
            if ( pointer == punteroHorizontal && Math.abs(v.x-xHorizontal)>UMBRAL ) {
                if (v.x > xHorizontal) {
                    dx = DELTA_X;   // Derecha
                } else {
                    dx = -DELTA_X;  // Izquierda
                }
                xHorizontal = v.x;
            } else if ( pointer == punteroVertical && Math.abs(v.y-yVertical)>UMBRAL ) {
                if (v.y > yVertical) {
                    dy = DELTA_Y;   // Arriba
                } else {
                    dy = -DELTA_Y;  // Abajo
                }
                yVertical = v.y;
            }
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }
    }
}
