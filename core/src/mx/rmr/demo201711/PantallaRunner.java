package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
 * Muestra algunas cartacterísticas de libgdx como:
 * - Cámara fija, scroll en el fondo
 * - Touch simultáneo con dos dedos
 */

class PantallaRunner extends Pantalla
{
    private final float DELTA_X = 10;    // Desplazamiento del personaje
    private final float DELTA_Y = 10;
    private final float UMBRAL = 50; // Para asegurar que hay movimiento

    private final Demo juego;
    private final AssetManager manager;
    private float tiempoMaximo = 4.0f;

    // Fondo
    private Fondo fondo;
    private Texture texturaFondo;

    // Punteros (dedo para paneo horizontal, vertical)
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

    // Enemigos (hongos :)
    private Texture texturaHongo;
    private Array<Hongo> enemigos;
    private float tiempoEnemigo;    // Tiempo aleatorio para generar un enemigo

    // Proyectiles que lanza mario
    private Texture texturaBala;
    private Array<Bala> balas;

    public PantallaRunner(Demo juego) {
        super();
        this.juego = juego;
        manager = juego.getAssetManager();
    }

    @Override
    public void show() {
        enemigos = new Array<>();   // Arreglo de topos
        balas = new Array<>();
        tiempoEnemigo = MathUtils.random(1.5f,5.0f);
        texturaFondo = manager.get("runner/fondoRunnerD.jpg");
        texturaHongo = manager.get("runner/enemigo.png");
        texturaBala = manager.get("runner/bala.png");
        fondo = new Fondo(texturaFondo);
        mario = new MarioRunner((Texture)(manager.get("mario/marioSprite.png")), ANCHO/2, 70);

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    @Override
    public void render(float delta) {
        // Actualizar
        //actualizarMario();
        actualizarSaltoMario(delta);
        actualizarEnemigos(delta);
        actualizarBalas(delta);

        // Dibujar
        borrarPantalla();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        fondo.dibujar(batch, delta);
        mario.dibujar(batch);
        // Dibujar enemigo
        for (Hongo hongo : enemigos) {
            hongo.dibujar(batch);
        }
        for (Bala bala : balas) {
            bala.dibujar(batch);
        }
        batch.end();

        // termina
        if (fondo.getX()<-3800) {   // Solo para probar la salida
            terminar();
        }
        if (escenaPausa!=null) {
            escenaPausa.draw();
        }
    }

    private void actualizarBalas(float delta) {
        // ¿POR QUÉ CREES QUE SE RECORRE AL REVÉS EL ARREGLO?
        for(int i=balas.size-1; i>=0; i--) {
            Bala bala = balas.get(i);
            bala.mover(delta);
            if (bala.sprite.getX()>ANCHO) {
                // Se salió de la pantalla
                balas.removeIndex(i);
                break;
            }
            // Prueba choque contra todos los enemigos
            for (int j=enemigos.size-1; j>=0; j--) {
                Hongo hongo = enemigos.get(j);
                if (bala.chocaCon(hongo)) {
                    // Borrar hongo, bala, aumentar puntos, etc.
                    enemigos.removeIndex(j);
                    balas.removeIndex(i);
                    break;  // Siguiente bala, ésta ya no existe
                }
            }
        }
    }

    private void actualizarEnemigos(float delta) {
        // Generar nuevo enemigo
        tiempoEnemigo -= delta;
        if (tiempoEnemigo<=0) {
            tiempoEnemigo = MathUtils.random(0.5f, tiempoMaximo);
            tiempoMaximo -= tiempoMaximo>0.5f?10*delta:0;
            Hongo hongo = new Hongo(texturaHongo, ANCHO+1, 70*MathUtils.random(1,4));
            enemigos.add(hongo);
        }
        // Actualizar enemigos
        for (Hongo hongo :enemigos) {
            hongo.mover(delta);
        }
        // Verificar choque
        for(int k=enemigos.size-1; k>=0; k--) {
            Hongo hongo = enemigos.get(k);
            if (hongo.chocaCon(mario)) {
                // Pierde!!!
                enemigos.removeIndex(k);
                // Activar escenaPausa y pasarle el control
                if (escenaPausa==null) {
                    escenaPausa = new EscenaPausa(vista, batch);
                }
                Gdx.input.setInputProcessor(escenaPausa);
            } else if (hongo.sprite.getX()<-hongo.sprite.getWidth()) {
                enemigos.removeIndex(k);
            }
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

    private void disparar() {
        Bala bala = new Bala(texturaBala,
                mario.sprite.getX()+mario.sprite.getWidth()/2,
                mario.sprite.getY()+mario.sprite.getHeight()/2-texturaBala.getHeight()/2);
        balas.add(bala);
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
        manager.unload("runner/enemigo.png");
        manager.unload("runner/bala.png");
        manager.unload("comun/btnSalir.png");
    }

    private class ProcesadorEntrada implements InputProcessor
    {
        private Vector3 v = new Vector3();

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            v.set(screenX, screenY, 0);
            camara.unproject(v);
            /* Para detectar paneo izquierdo-derecho
            if (v.x < Pantalla.ANCHO/2 && punteroHorizontal == INACTIVO) {
                // Horizontal
                punteroHorizontal = pointer;
                xHorizontal = v.x;
            } else if (v.x >= Pantalla.ANCHO/2 && punteroVertical == INACTIVO ) {
                // Vertical
                punteroVertical = pointer;
                yVertical = v.y;
            }
            */
            // Pantalla izquierda salta, derecha dispara
            if (v.x<ANCHO/2) {
                mario.saltar();
            } else {
                disparar();
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
