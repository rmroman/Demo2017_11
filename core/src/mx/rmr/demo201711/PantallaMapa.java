package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by roberto on 21/02/17.
 */

public class PantallaMapa extends Pantalla
{
    public static final int ANCHO_MAPA = 2560;
    public static final int ALTO_MAPA = 800;
    private final Demo juego;

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderarMapa;
    private SpriteBatch batch;

    // Mario
    private Personaje mario;
    private Texture texturaMario;

    // Música
    private Music musicaFondo;  // Sonidos largos
    private Sound efectoMoneda; // Sonido cortos

    // Joystick
    private Touchpad pad;

    // HUD
    private OrthographicCamera camaraHUD;
    private Viewport vistaHUD;
    private Stage escenaHUD;

    // AssetManager
    private AssetManager manager;

    public PantallaMapa(Demo juego) {
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        texturaMario = manager.get("mario/marioSprite.png"); //new Texture("marioSprite.png");
        mario = new Personaje(texturaMario,0,64);
        cargarMapa();

        crearPad();

        //Gdx.input.setInputProcessor(new ProcesadorEntrada());
        Gdx.input.setInputProcessor(escenaHUD);
        Gdx.input.setCatchBackKey(true);
    }

    private void crearPad() {
        camaraHUD = new OrthographicCamera(ANCHO,ALTO);
        camaraHUD.position.set(ANCHO/2, ALTO/2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO, ALTO, camaraHUD);
        // HUD
        Skin skin = new Skin();
        skin.add("padBack", manager.get("mario/padBack.png")); //new Texture("padBack.png"));
        skin.add("padKnob", manager.get("mario/padKnob.png")); //new Texture("padKnob.png"));

        Touchpad.TouchpadStyle estilo = new Touchpad.TouchpadStyle();
        estilo.background = skin.getDrawable("padBack");
        estilo.knob = skin.getDrawable("padKnob");

        pad = new Touchpad(20, estilo);
        pad.setBounds(0, 0, 200, 200);

        pad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Touchpad pad = (Touchpad) actor;
                if (pad.getKnobPercentX()>0.20) {
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);
                } else if (pad.getKnobPercentX()<-0.20){
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
                } else {
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
                }
            }
        });

        escenaHUD = new Stage(vistaHUD);
        escenaHUD.addActor(pad);
    }

    private void cargarMapa() {
        /*
        AssetManager manager = new AssetManager();

        manager.setLoader(TiledMap.class,
                new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load("mapaMario.tmx", TiledMap.class);
        // Cargar audios
        manager.load("audio/marioBros.mp3",Music.class);
        manager.load("audio/moneda.mp3",Sound.class);
        manager.finishLoading();
        */
        mapa = manager.get("mario/mapaMario.tmx");
        musicaFondo = manager.get("mario/marioBros.mp3");
        musicaFondo.setLooping(true);
        musicaFondo.play();

        efectoMoneda = manager.get("mario/moneda.mp3");
        batch = new SpriteBatch();

        renderarMapa = new OrthogonalTiledMapRenderer(mapa, batch);
        renderarMapa.setView(camara);
    }

    @Override
    public void render(float delta) {
        mario.actualizar(mapa);
        if (mario.recolectarMonedas(mapa)) {
            efectoMoneda.play();
        }
        // ACTUALIZAR LA CAMARA
        actualizarCamara();

        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);
        renderarMapa.setView(camara);
        renderarMapa.render();  // DIBUJA el mapa
        float nuevaX = camara.position.x + 1;
        float nuevaY = camara.position.y;

        batch.begin();
        mario.dibujar(batch);
        batch.end();
        // Cámara HUD
        batch.setProjectionMatrix(camaraHUD.combined);
        escenaHUD.draw();

        // Salir?
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            juego.setScreen(new PantallaCargando(juego, Pantallas.MENU));
        }
    }

    private void actualizarCamara() {
        float posX = mario.sprite.getX();
        // Si está en la parte 'media'
        if (posX>=ANCHO/2 && posX<=ANCHO_MAPA-ANCHO/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>ANCHO_MAPA-ANCHO/2) {    // Si está en la última mitad
            // La cámara se queda a media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-ANCHO/2, camara.position.y, 0);
        } else if ( posX<ANCHO/2 ) { // La primera mitad
            camara.position.set(ANCHO/2, ALTO/2,0);
        }
        camara.update();
    }
    // Usar resize para actualizar camaHUD

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        manager.unload("mario/marioSprite.png");
        manager.unload("mario/mapaMario.tmx");
        manager.unload("mario/marioBros.mp3");
        manager.unload("mario/moneda.mp3");
        manager.unload("mario/padBack.png");
        manager.unload("mario/padKnob.png");
    }

    private class ProcesadorEntrada implements InputProcessor
    {
        private Vector3 v = new Vector3();
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

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            v.set(screenX,screenY,0);
            camara.unproject(v);
            if (v.x>=ANCHO/2) {
                mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);
            } else {
                mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
            }
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
            return true;
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
    }
}
