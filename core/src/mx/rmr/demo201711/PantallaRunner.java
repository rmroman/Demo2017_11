package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private MarioRunner mario;
    private float dx = 0;
    private float dy = 0;

    // Para salir
    private EscenaPausa escenaPausa;


    public PantallaRunner(Demo juego) {
        super();
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        texturaFondo = manager.get("runner/fondoRunnerD.jpg");
        fondo = new Fondo(texturaFondo);
        mario = new MarioRunner((Texture)(manager.get("mario/marioSprite.png")), ANCHO/2, 70);

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    @Override
    public void render(float delta) {
        // Actualizar
        //actualizarMario();
        actualizarSaltoMario(delta);
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fondo.dibujar(batch, delta);
        mario.dibujar(batch);
        batch.end();

        // termina
        if (fondo.getX()<-3800) {   // Solo para probar la salida
            terminar();
        }
        if (escenaPausa!=null) {
            escenaPausa.draw();
        }
    }

    private void terminar() {
        // Activar escenaPausa y pasarle el control
        if (escenaPausa==null) {
            escenaPausa = new EscenaPausa(vista, batch);
        }
        Gdx.input.setInputProcessor(escenaPausa);
    }

    private void actualizarSaltoMario(float delta) {
        mario.actualizar(delta);
    }

    // Ejercicio anterior, por ahora deshabilitado
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
        manager.unload("mario/marioSprite.png");
        manager.unload("comun/btnSalir.png");
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
            mario.saltar();
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
