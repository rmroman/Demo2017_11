package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Created by roberto on 13/03/17.
 */

class PantallaCargando extends Pantalla
{
    // Animación cargando
    private static final float TIEMPO_ENTRE_FRAMES = 0.05f;
    private Sprite spriteCargando;
    private float timerAnimacion = TIEMPO_ENTRE_FRAMES;

    // AssetManager
    private AssetManager manager;

    private Demo juego;
    private Pantallas siguientePantalla;
    private int avance; // % de carga
    private Texto texto;

    private Texture texturaCargando;

    public PantallaCargando(Demo juego, Pantallas siguientePantalla) {
        this.juego = juego;
        this.siguientePantalla = siguientePantalla;
    }

    @Override
    public void show() {
        texturaCargando = new Texture(Gdx.files.internal("cargando/loading.png"));
        spriteCargando = new Sprite(texturaCargando);
        spriteCargando.setPosition(ANCHO/2-spriteCargando.getWidth()/2,ALTO/2-spriteCargando.getHeight()/2);
        cargarRecursosSigPantalla();
        texto = new Texto("fuentes/fuente.fnt");
    }

    // Carga los recursos de la siguiente pantalla
    private void cargarRecursosSigPantalla() {
        manager = juego.getAssetManager();
        avance = 0;
        switch (siguientePantalla) {
            case MENU:
                cargarRecursosMenu();
                break;
            case NIVEL_MARIO:
                cargarRecursosMario();
                break;
            case NIVEL_WHACK_A_MOLE:
                cargarRecursosWhackAMole();
                break;
            case NIVEL_RUNNER:
                cargarRecursosRunner();
                break;
        }
    }

    private void cargarRecursosRunner() {
        manager.load("runner/fondoRunnerD.jpg", Texture.class);
    }

    private void cargarRecursosWhackAMole() {
        manager.load("whackamole/fondoPasto.jpg", Texture.class);
        manager.load("whackamole/hoyo.png", Texture.class);
        manager.load("whackamole/mole.png", Texture.class);
        manager.load("whackamole/estrellasGolpe.png", Texture.class);
        manager.load("whackamole/mazo.png", Texture.class);
        manager.load("whackamole/golpe.mp3", Sound.class);
        manager.load("whackamole/risa.mp3", Sound.class);
        manager.load("comun/btnPausa.png", Texture.class);
        manager.load("whackamole/btnSalir.png", Texture.class);
        manager.load("whackamole/btnReintentar.png", Texture.class);
        manager.load("whackamole/btnContinuar.png", Texture.class);
    }

    private void cargarRecursosMario() {
        manager.load("mario/marioSprite.png", Texture.class);
        manager.load("mario/mapaMario.tmx", TiledMap.class);
        manager.load("mario/marioBros.mp3",Music.class);
        manager.load("mario/moneda.mp3",Sound.class);
        manager.load("mario/padBack.png", Texture.class);
        manager.load("mario/padKnob.png", Texture.class);
        manager.load("mario/jumpBtn.png", Texture.class);
        manager.load("mario/jumpBtnBajo.png", Texture.class);
        manager.load("comun/btnSalir.png", Texture.class);
        manager.load("comun/btnPausa.png", Texture.class);

    }

    private void cargarRecursosMenu() {
        manager.load("menu/btnJugarMario.png", Texture.class);
        manager.load("menu/btnJugarMarioP.png", Texture.class);
        manager.load("menu/btnJugarRunner.png", Texture.class);
        manager.load("menu/btnJugarRunnerP.png", Texture.class);
        manager.load("menu/btnJugarWhackAMole.png", Texture.class);
        manager.load("menu/btnJugarWhackAMoleP.png", Texture.class);
        manager.load("menu/fondoSinFin.jpg", Texture.class);
        manager.load("menu/fondoSinFin.jpg", Texture.class);
    }

    @Override
    public void render(float delta) {
        borrarPantalla(0.5f, 0.5f, 0.5f);
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        spriteCargando.draw(batch);
        texto.mostrarMensaje(batch,avance+" %",ANCHO/2,ALTO/2);
        batch.end();
        // Actualizar
        timerAnimacion -= delta;
        if (timerAnimacion<=0) {
            timerAnimacion = TIEMPO_ENTRE_FRAMES;
            spriteCargando.rotate(20);
        }
        // Actualizar carga
        actualizarCargaRecursos();
    }

    private void actualizarCargaRecursos() {
        if (manager.update()) { // Terminó?
            switch (siguientePantalla) {
                case MENU:
                    juego.setScreen(new PantallaMenu(juego));   // 100% de carga
                    break;
                case NIVEL_MARIO:
                    juego.setScreen(new PantallaMario(juego));   // 100% de carga
                    break;
                case NIVEL_WHACK_A_MOLE:
                    juego.setScreen(new PantallaWhackAMole(juego));
                    break;
                case NIVEL_RUNNER:
                    juego.setScreen(new PantallaRunner(juego));
                    break;
            }
        }
        avance = (int)(manager.getProgress()*100);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        texturaCargando.dispose();
    }
}
