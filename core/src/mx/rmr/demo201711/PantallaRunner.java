package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

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

    // Mapa
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;

    public PantallaRunner(Demo juego) {
        super();
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    private void cargarMapa() {
        mapa = manager.get("runner/marioOriginal.tmx");
        rendererMapa = new OrthogonalTiledMapRenderer(mapa, 3.3f, batch);
    }

    @Override
    public void show() {
        texturaFondo = manager.get("runner/fondoRunnerD.png");
        fondo = new Fondo(texturaFondo);
        //cargarMapa();

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    @Override
    public void render(float delta) {
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fondo.dibujar(batch, delta);
        batch.end();

        //rendererMapa.setView(camara);
        //rendererMapa.render();
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    private class ProcesadorEntrada implements InputProcessor
    {
        private Vector3 v = new Vector3();

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            v.set(screenX, screenY, 0);
            camara.unproject(v);
            Gdx.app.log("touchDown"," >>>>>>>> pointer="+pointer);
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
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
}
