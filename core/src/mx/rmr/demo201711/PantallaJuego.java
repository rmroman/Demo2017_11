package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * El juego
 */
public class PantallaJuego extends Pantalla
{
    // Imágenes que se utilizarán
    private Texture texturaFondo;
    private Texture texturaHoyo;
    private Texture texturaTopo;

    // Ayuda a dibujar
    private SpriteBatch batch;

    // Los 9 hoyos en el pasto
    private final int NUM_HOYOS = 9;
    private Array<Objeto> arrHoyos;

    // Los 9 topos en el juego
    private final int NUM_TOPOS = NUM_HOYOS;
    private Array<Objeto> arrTopos;

    /**
     * Se ejecuta después del constructor, aquí configuramos/armamos la pantalla
     */
    @Override
    public void show() {
        // Aplicando PPP
        cargarTexturas();
        crearObjetos();
        // Definir quien atiende eventos de touch
        Gdx.input.setInputProcessor(new Procesador());
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
            }
        }
    }

    /**
     * Carga todas las imágenes que se usarán en esta pantalla
     */
    private void cargarTexturas() {
        texturaFondo = new Texture("fondoPasto.jpg");
        texturaHoyo = new Texture("hoyo.png");
        texturaTopo = new Texture("mole.png");
    }

    /**
     * Dibuja los elementos del juego, 60 fps aproximadamente
     *
     * @param delta El tiempo que ha pasado desde la última vez que se ejecutó el método
     */
    @Override
    public void render(float delta) {

        actualizarObjetos(delta);   // mandamos el tiempo para calcular distancia

        borrarPantalla();   // Definido en la superclase

        batch.setProjectionMatrix(camara.combined); // Para ajustar la escala con la cámara
        batch.begin();

        batch.draw(texturaFondo,0,0);
        dibujarObjetos(arrHoyos);
        dibujarObjetos(arrTopos);

        batch.end();
    }

    // Actualiza la posición de los objetos en pantalla
    private void actualizarObjetos(float delta) {
        // Topos
        for (Objeto topo :
                arrTopos) {
            ((Topo)topo).actualizar(delta);
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
        texturaFondo.dispose();
        texturaTopo.dispose();
        texturaHoyo.dispose();
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
            for (Objeto obj :
                    arrTopos) {
                Topo topo = (Topo) obj;
                if (topo.contiene(v)) {
                    // Tocó!!!!
                    topo.desaparecer();
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
}
