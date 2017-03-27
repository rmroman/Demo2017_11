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
    private final Demo juego;
    private final AssetManager manager;

    // Fondo
    private Fondo fondo;
    private Texture texturaFondo;

    // Punteros (dedo para pan horizontal, vertical)
    private final int CERO = 0;
    private final int UNO = 1;
    private int numeroPunteroHorizontal;    // Puede ser 0 o 1


    public PantallaRunner(Demo juego) {
        super();
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        texturaFondo = manager.get("runner/fondoRunnerD.jpg");
        fondo = new Fondo(texturaFondo);

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    @Override
    public void render(float delta) {
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fondo.dibujar(batch, delta);
        batch.end();
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
            if (v.x < Pantalla.ANCHO) {
                // Horizontal
            }

            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {

            v.set(screenX, screenY, 0);
            camara.unproject(v);
            Gdx.app.log("touchDragged"," >>>>>>>> pointer="+pointer);
            return false;
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

    /*
    private class ProcesaGestos implements GestureDetector.GestureListener
    {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Gdx.app.log("pan",">>>>>>> touchDown: "+ pointer);
            return true;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            if ( x<Pantalla.ANCHO/2) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    Gdx.app.log("pan", deltaX > 0 ? ">>>>>>> " : "<<<<<<<");
                }
            }else {
                Gdx.app.log("pan", deltaY>0?"^^^^^^ ":"__________");
            }
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }

        @Override
        public void pinchStop() {

        }
    }
    */
}
