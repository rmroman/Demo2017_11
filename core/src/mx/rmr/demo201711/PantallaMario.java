package mx.rmr.demo201711;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by roberto on 21/02/17.
 * Agrega HUD para el joystick y marcador
 * Audio
 */

public class PantallaMario extends Pantalla
{
    public static final int ANCHO_MAPA = 2560;
    public static final int ALTO_MAPA = 800;
    private final Demo juego;

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderarMapa;

    private Texture texturaMario;
    private Personaje mario;

    // HUD
    private OrthographicCamera camaraHUD;
    private Viewport vistaHUD;
    // El HUD lo manejamos con una escena (opcional)
    private Stage escenaHUD;

    private Texture texturaBtnPausa;
    private Objeto btnPausa;    // Botón de pausa, <para salir>

    // Música
    private Music musicaFondo;  // Sonidos largos
    private Sound efectoMoneda; // Sonido cortos

    // Joystick virtual
    private Touchpad pad;

    // Partículas
    private ParticleEffect pe;
    private ParticleEmitter emisorHumo;

    // AssetManager
    private AssetManager manager;
    private EstadoJuego estado = EstadoJuego.JUGANDO;
    private EscenaPausa escenaPausa;

    public PantallaMario(Demo juego) {
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        cargarTexturas();
        crearObjetos();
        cargarMapa();
        crearHUD();

        // MUSICA
        cargarMusica();

        // El input es el joystick virtual y el botón
        Gdx.input.setInputProcessor(escenaHUD);
    }

    private void cargarMusica() {
        musicaFondo = manager.get("mario/marioBros.mp3");
        efectoMoneda = manager.get("mario/moneda.mp3");

        musicaFondo.setLooping(true);
        musicaFondo.play();
    }

    private void crearHUD() {
        // Cámara HUD
        camaraHUD = new OrthographicCamera(ANCHO,ALTO);
        camaraHUD.position.set(ANCHO/2, ALTO/2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO, ALTO, camaraHUD);

        // HUD
        Skin skin = new Skin();
        skin.add("padBack", manager.get("mario/padBack.png"));
        skin.add("padKnob", manager.get("mario/padKnob.png"));

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
        pad.setColor(1,1,1,0.7f);

        // Salto
        Texture texturaBtn = manager.get("mario/jumpBtn.png");
        TextureRegionDrawable trBtn = new TextureRegionDrawable(new TextureRegion(texturaBtn));
        Texture texturaBtnBAjo = manager.get("mario/jumpBtn.png");
        TextureRegionDrawable trBtnBajo = new TextureRegionDrawable(new TextureRegion(texturaBtnBAjo));
        ImageButton btnSalto = new ImageButton(trBtn, trBtnBajo);
        btnSalto.setPosition(ANCHO-btnSalto.getWidth(), 0);
        btnSalto.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mario.saltar();
                return true;
            }
        });
        escenaHUD.addActor(btnSalto);

        // Pausa
        Texture texturaPausa = manager.get("comun/btnPausa.png");
        TextureRegionDrawable trBtnPausa = new TextureRegionDrawable(new TextureRegion(texturaPausa));
        ImageButton btnPausa = new ImageButton(trBtnPausa);
        btnPausa.setPosition(ANCHO-btnPausa.getWidth(), ALTO-btnPausa.getHeight());
        btnPausa.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Se pausa el juego
                estado = estado==EstadoJuego.PAUSADO?EstadoJuego.JUGANDO:EstadoJuego.PAUSADO;
                if (estado==EstadoJuego.PAUSADO) {
                    // Activar escenaPausa y pasarle el control
                    if (escenaPausa==null) {
                        escenaPausa = new EscenaPausa(vistaHUD, batch);
                    }
                    Gdx.input.setInputProcessor(escenaPausa);
                }
                return true;
            }
        });
        escenaHUD.addActor(btnPausa);
    }

    private void crearObjetos() {
        mario = new Personaje(texturaMario, 0,64);
        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);

        // Partículas
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("mario/efectos.pe"),Gdx.files.internal("mario/"));
        Array<ParticleEmitter> emisores = pe.getEmitters();
        emisores.get(0).setPosition(0, ALTO);
        emisores.get(1).setPosition(ANCHO/2,ALTO/4);
        pe.start();
        emisorHumo = emisores.get(1);
    }

    private void cargarTexturas() {
        texturaMario = manager.get("mario/marioSprite.png");
        texturaBtnPausa = manager.get("comun/btnPausa.png");
    }

    private void cargarMapa() {
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
        // Actualizar
        mario.actualizar(mapa);
        buscarMonedas();
        actualizarCamara();

        pe.update(delta);

        // Dibujar
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);
        renderarMapa.setView(camara);
        renderarMapa.render();  // DIBUJA el mapa

        batch.begin();
        mario.dibujar(batch);
        pe.draw(batch);
        batch.end();

        // HUD
        batch.setProjectionMatrix(camaraHUD.combined);
        escenaHUD.draw();
        batch.begin();
        //pe.draw(batch);
        batch.end();

        // Pausa?
        if (estado==EstadoJuego.PAUSADO) {
            escenaPausa.draw();
        }
    }

    private void buscarMonedas() {
        if (mario.recolectarMonedas(mapa)) {
            efectoMoneda.play();
            emisorHumo.setPosition(mario.sprite.getX()+mario.sprite.getWidth()/2,mario.sprite.getY()+mario.sprite.getHeight()/2);
            emisorHumo.start();
        }
    }

    // Actualiza la posición de la cámara para que el personaje esté en el centro,
    // excepto cuando está en la primera y última parte del mundo
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


    @Override
    public void resize(int width, int height) {
        vista.update(width, height);
        vistaHUD.update(width, height);
    }

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
        manager.unload("mario/jumpBtn.png");
        manager.unload("mario/jumpBtnBajo.png");
        manager.unload("comun/btnSalir.png");
    }

    // La escena que se muestra cuando el juego se pausa
    // (simplificado, ver la misma escena en PantallaWhackAMole)
    private class EscenaPausa extends Stage
    {
        public EscenaPausa(Viewport vista, SpriteBatch batch) {
            super(vista, batch);
            // Crear rectángulo transparente
            Pixmap pixmap = new Pixmap((int)(ANCHO*0.7f), (int)(ALTO*0.8f), Pixmap.Format.RGBA8888 );
            pixmap.setColor( 0.1f, 0.1f, 0.1f, 0.65f );
            pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
            Texture texturaRectangulo = new Texture( pixmap );
            pixmap.dispose();
            Image imgRectangulo = new Image(texturaRectangulo);
            imgRectangulo.setPosition(0.15f*ANCHO, 0.1f*ALTO);
            this.addActor(imgRectangulo);

            // Salir
            Texture texturaBtnSalir = manager.get("comun/btnSalir.png");
            TextureRegionDrawable trdSalir = new TextureRegionDrawable(
                    new TextureRegion(texturaBtnSalir));
            ImageButton btnSalir = new ImageButton(trdSalir);
            btnSalir.setPosition(ANCHO/2-btnSalir.getWidth()/2, ALTO*0.2f);
            btnSalir.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Regresa al menú
                    juego.setScreen(new PantallaCargando(juego,Pantallas.MENU));
                }
            });
            this.addActor(btnSalir);
        }
    }
}
