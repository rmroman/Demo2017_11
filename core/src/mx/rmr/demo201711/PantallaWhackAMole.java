package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * El juego de WhackAMole
 */
public class PantallaWhackAMole extends Pantalla
{
    public static final int PUNTOS_SUBIENDO = 50;
    public static final int PUNTOS_BAJANDO = 70;
    // Imágenes que se utilizarán
    private Texture texturaFondo;
    private Texture texturaHoyo;
    private Texture texturaTopo;
    private Texture texturaEstrellas;
    private Texture texturaMazo;
    private Texture texturaBtnPausa;
    private Objeto btnPausa;

    // Ayuda a dibujar
    private SpriteBatch batch;

    // Los 9 hoyos en el pasto
    private final int NUM_HOYOS = 9;
    private Array<Objeto> arrHoyos;

    // Los 9 topos en el juego
    private final int NUM_TOPOS = NUM_HOYOS;
    private Array<Objeto> arrTopos;

    private final AssetManager manager;
    private Demo juego;

    // Audio
    private Sound efectoGolpe;  // Cuando el usuario golpea al topo
    private Sound efectoRisa;   // Cuando se esconde el topo

    // Marcador
    private int puntos = 0;     // 50 por cada topo cuando sube, 60 por cada topo cuando baja
    private int mazos = 5;      // Intentos
    private Texto texto;        // Para imprimir el estado
    private Texture texturaCuadro;

    // Estado del juego
    private EstadoJuego estado = EstadoJuego.JUGANDO;

    // Pantallas SECUNDARIAS
    private EscenaPierde escenaPierde;
    private EscenaPausa escenaPausa;

    // Procesador de eventos
    private final Procesador procesadorEntrada = new Procesador();

    public PantallaWhackAMole(Demo juego) {
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    /**
     * Se ejecuta después del constructor, aquí configuramos/armamos la pantalla
     */
    @Override
    public void show() {
        // Aplicando PPP
        cargarRecursos();
        crearObjetos();
        // Definir quién atiende los eventos de touch
        Gdx.input.setInputProcessor(procesadorEntrada);

        // Crear rectángulo transparente
        Pixmap pixmap = new Pixmap((int)ANCHO, (int)(btnPausa.sprite.getHeight()), Pixmap.Format.RGBA8888 );
        pixmap.setColor( 0, 0, 0, 0.45f );
        pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
        texturaCuadro = new Texture( pixmap );
        pixmap.dispose();
    }

    /**
     * Crea los objetos que forman parte del juego
     */
    private void crearObjetos() {
        batch = new SpriteBatch();
        // Crea los hoyos y los guarda en el arreglo
        arrHoyos = new Array<Objeto>(NUM_HOYOS);
        for (int x = 0; x < NUM_HOYOS/3; x++) {
            for (int y = 0; y < NUM_HOYOS/3; y++) {
                float posX = (x+1) * Pantalla.ANCHO/4 - texturaHoyo.getWidth()/2;
                float posY = (y+1) * Pantalla.ALTO/4 - texturaHoyo.getHeight()/2;
                Hoyo hoyo = new Hoyo(texturaHoyo, posX, posY);
                arrHoyos.add(hoyo);
            }
        }
        // Crea los topos y los guarda en el arreglo
        arrTopos = new Array<Objeto>(NUM_TOPOS);
        for (int x = 0; x < NUM_TOPOS/3; x++) {
            for (int y = 0; y < NUM_TOPOS/3; y++) {
                float posX = (x+1) * Pantalla.ANCHO/4 - texturaTopo.getWidth()/2;
                float posY = (y+1) * Pantalla.ALTO/4 - texturaTopo.getHeight()/50;
                Topo topo = new Topo(texturaTopo, posX, posY);
                arrTopos.add(topo);
                topo.setTexturaEstrellas(texturaEstrellas);
            }
        }
        // Botón pausa
        // ACOMODARLO MEJOR :)
        btnPausa = new Objeto(texturaBtnPausa, ANCHO-3*texturaBtnPausa.getWidth()/2, ALTO-texturaBtnPausa.getHeight());
    }

    /**
     * Carga todas las imágenes que se usarán en esta pantalla
     */
    private void cargarRecursos() {
        /*
        ANTES
        texturaFondo = new Texture("fondoPasto.jpg");
        texturaHoyo = new Texture("hoyo.png");
        texturaTopo = new Texture("mole.png");
        */
        // Con el AssetManager
        texturaFondo = manager.get("whackamole/fondoPasto.jpg");
        texturaHoyo = manager.get("whackamole/hoyo.png");
        texturaTopo = manager.get("whackamole/mole.png");
        texturaEstrellas = manager.get("whackamole/estrellasGolpe.png");
        texturaMazo = manager.get("whackamole/mazo.png");
        texturaBtnPausa = manager.get("comun/btnPausa.png");
        // Audios
        efectoGolpe = manager.get("whackamole/golpe.mp3");
        efectoRisa = manager.get("whackamole/risa.mp3");

        // Texto
        texto = new Texto("fuentes/mole.fnt");
    }

    /**
     * Dibuja los elementos del juego, 60 fps aproximadamente
     *
     * @param delta El tiempo que ha pasado desde la última vez que se ejecutó el método
     */
    @Override
    public void render(float delta) {
        // ACTUALIZAR
        if ( estado==EstadoJuego.JUGANDO) {
            actualizarObjetos(delta);   // mandamos el tiempo para calcular distancia
        } // Si está pausado, perdió o ganó NO hace las actualizaciones
        // DIBUJAR
        borrarPantalla();   // Definido en la superclase
        batch.setProjectionMatrix(camara.combined); // Para ajustar la escala con la cámara

        batch.begin();
        batch.draw(texturaFondo,0,0);
        dibujarObjetos(arrHoyos);
        dibujarObjetos(arrTopos);
        dibujarEstado(batch);    // Puntos y mazos
        batch.end();

        if (estado==EstadoJuego.PIERDE) {
            escenaPierde.draw();
        } else if (estado==EstadoJuego.PAUSADO) {
            escenaPausa.draw();
        }
    }

    private void dibujarEstado(SpriteBatch batch) {
        batch.draw(texturaCuadro,0,ALTO-texturaCuadro.getHeight());
        // Dibuja intentos que quedan
        for (int i=1; i<=mazos; i++) {
            batch.draw(texturaMazo,texturaMazo.getWidth()*1f*i,ALTO-texturaCuadro.getHeight()/2-texturaMazo.getHeight()/2);
        }
        // Dibuja marcador
        texto.mostrarMensaje(batch,"Puntos: "+puntos,2*ANCHO/3,ALTO-texturaMazo.getHeight()/2);
        // Botón pausa
        btnPausa.dibujar(batch);
    }

    // Actualiza la posición de los objetos en pantalla
    private void actualizarObjetos(float delta) {
        // Topos
        for (Objeto topo :
                arrTopos) {
            Topo t = (Topo)topo;
            t.actualizar(delta);
            // Pregunta si se escondió (pierde un mazo)
            if (t.seHaEscondido()) {
                mazos--;
                if (estado==EstadoJuego.JUGANDO) {
                    efectoRisa.play();
                }
                // Ya perdió???
                if (mazos<=0) {
                    estado = EstadoJuego.PIERDE;
                    // Activar escenaPierde y pasarle el control
                    if (escenaPierde==null) {
                        escenaPierde = new EscenaPierde(vista, batch);
                    }
                    Gdx.input.setInputProcessor(escenaPierde);
                }
            }
        }
    }

    // Dibuja todos los objetos del arreglo
    private void dibujarObjetos(Array<Objeto> arreglo) {
        // Dibujar los objetos
        for (Objeto objeto : arreglo) {
            objeto.dibujar(batch);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        arrHoyos.clear();
        arrTopos.clear();
        /*texturaFondo.dispose();
        texturaTopo.dispose();
        texturaHoyo.dispose();*/
        manager.unload("whackamole/fondoPasto.jpg");
        manager.unload("whackamole/hoyo.png");
        manager.unload("whackamole/mole.png");
        manager.unload("whackamole/btnReintentar.png");
        manager.unload("whackamole/btnSalir.png");
    }

    // Para reintentar el juego
    private void reiniciarObjetos() {
        // Crea los hoyos y los guarda en el arreglo
        // Dibujar los objetos
        for (Objeto objeto : arrTopos) {
            ((Topo)objeto).reset();
        }
    }

    // Procesar entrada
    class Procesador implements InputProcessor
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
            v.set(screenX, screenY, 0);
            camara.unproject(v);
            if ( estado==EstadoJuego.JUGANDO ) {
                for (Objeto obj :
                        arrTopos) {
                    Topo topo = (Topo) obj;
                    if (topo.contiene(v)) {
                        // Tocó!!!!
                        efectoGolpe.play();
                        if (topo.getEstado() == EstadoTopo.SUBIENDO) {
                            puntos += PUNTOS_SUBIENDO;
                        } else {
                            puntos += PUNTOS_BAJANDO;
                        }
                        topo.desaparecer();
                    }
                }
            }
            // Prueba botón pausa
            if (btnPausa.contiene(v)) {
                // Se pausa el juego
                estado = estado==EstadoJuego.PAUSADO?EstadoJuego.JUGANDO:EstadoJuego.PAUSADO;
                if (estado==EstadoJuego.PAUSADO) {
                    // Activar escenaPausa y pasarle el control
                    if (escenaPausa==null) {
                        escenaPausa = new EscenaPausa(vista, batch);
                    }
                    Gdx.input.setInputProcessor(escenaPausa);
                }
            }
            return true;
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
    }

    // Escena pierde
    class EscenaPierde extends Stage
    {
        public EscenaPierde(Viewport vista, SpriteBatch batch) {
            super(vista, batch);
            // Crear círculo transparente
            Pixmap pixmap = new Pixmap((int)ANCHO, (int)(ALTO), Pixmap.Format.RGBA8888 );
            pixmap.setColor( 0, 0, 0, 0.55f );
            pixmap.fillCircle((int)ANCHO/2, (int)ALTO/2, (int)ALTO/2);
            Texture texturaCirculo = new Texture( pixmap );
            pixmap.dispose();
            this.addActor(new Image(texturaCirculo));

            // Agregar botones salir y reintentar
            Texture texturabtnSalir = manager.get("whackamole/btnSalir.png");
            TextureRegionDrawable trdSalir = new TextureRegionDrawable(
                    new TextureRegion(texturabtnSalir));
            ImageButton btnSalir = new ImageButton(trdSalir);
            btnSalir.setPosition(ANCHO/2-btnSalir.getWidth()/2, ALTO*0.3f);
            btnSalir.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Regresa al menú
                    juego.setScreen(new PantallaCargando(juego,Pantallas.MENU));
                }
            });
            this.addActor(btnSalir);

            // Reintentar
            // Agregar botones salir y reintentar
            Texture texturabtnReintentar = manager.get("whackamole/btnReintentar.png");
            TextureRegionDrawable trdReintentar = new TextureRegionDrawable(
                    new TextureRegion(texturabtnReintentar));
            ImageButton btnReintentar = new ImageButton(trdReintentar);
            btnReintentar.setPosition(ANCHO/2-btnReintentar.getWidth()/2, ALTO*0.6f);
            btnReintentar.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Reiniciar el juego
                    puntos = 0;
                    mazos = 5;
                    estado = EstadoJuego.JUGANDO;
                    reiniciarObjetos();
                    // Regresa el control a la pantalla
                    Gdx.input.setInputProcessor(procesadorEntrada);
                }
            });
            this.addActor(btnReintentar);
        }
    }

    private class EscenaPausa extends Stage
    {
        public EscenaPausa(Viewport vista, SpriteBatch batch) {
            super(vista, batch);
            // Crear triángulo transparente
            Pixmap pixmap = new Pixmap((int)(ANCHO*0.7f), (int)(ALTO*0.8f), Pixmap.Format.RGBA8888 );
            pixmap.setColor( 0.2f, 0, 0.3f, 0.65f );
            pixmap.fillTriangle(0,pixmap.getHeight(),pixmap.getWidth(),pixmap.getHeight(),pixmap.getWidth()/2,0);
            Texture texturaTriangulo = new Texture( pixmap );
            pixmap.dispose();
            Image imgTriangulo = new Image(texturaTriangulo);
            imgTriangulo.setPosition(0.15f*ANCHO, 0.1f*ALTO);
            this.addActor(imgTriangulo);

            // Salir
            Texture texturabtnSalir = manager.get("whackamole/btnSalir.png");
            TextureRegionDrawable trdSalir = new TextureRegionDrawable(
                    new TextureRegion(texturabtnSalir));
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

            // Continuar
            Texture texturabtnReintentar = manager.get("whackamole/btnContinuar.png");
            TextureRegionDrawable trdReintentar = new TextureRegionDrawable(
                    new TextureRegion(texturabtnReintentar));
            ImageButton btnReintentar = new ImageButton(trdReintentar);
            btnReintentar.setPosition(ANCHO/2-btnReintentar.getWidth()/2, ALTO*0.5f);
            btnReintentar.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Continuar el juego
                    estado = EstadoJuego.JUGANDO;
                    // Regresa el control a la pantalla
                    Gdx.input.setInputProcessor(procesadorEntrada);
                }
            });
            this.addActor(btnReintentar);
        }
    }
}
