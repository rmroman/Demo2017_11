package mx.rmr.demo201711;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * SplashScreen. Muestra el logo del Tec por 2 segundos
 */

class PantallaInicial extends Pantalla
{
    private float tiempoVisible = 1.2f;

    // Es la referencia a la aplicación (la única que puede cambiar pantallas)
    private Demo juego;

    // Logo del tec
    private Texture texturaLogo;
    private Sprite spriteLogo;

    // Constructor, guarda la referencia al juego
    public PantallaInicial(Demo demo) {
        this.juego = demo;
    }

    @Override
    public void show() {
        texturaLogo = new Texture(Gdx.files.internal("inicial/logo.png"));
        spriteLogo = new Sprite(texturaLogo);
        spriteLogo.setPosition(ANCHO/2-spriteLogo.getWidth()/2, ALTO/2-spriteLogo.getHeight()/2);
        escalarLogo();
    }

    // hace que mantenga la proporción ancho-alto. (SOLO en landscape)
    private void escalarLogo() {
        float factorCamara = ANCHO / ALTO;
        float factorPantalla = 1.0f*Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float escala = factorCamara / factorPantalla;
        spriteLogo.setScale(escala, 1);
    }

    @Override
    public void render(float delta) {

        // Dibujar
        borrarPantalla(1,0.55f,0);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        // Dibuja el logo centrado
        spriteLogo.draw(batch);
        batch.end();

        // Actualizar para cambiar pantalla
        tiempoVisible -= delta;
        if (tiempoVisible<=0) { // Se acabaron los dos segundos
            // Cambia a la pantalla del MENU
            //juego.setScreen(new PantallaMenu(juego));
            // AHORA cambia a la pantalla "Cargando..." y después al menú
            juego.setScreen(new PantallaCargando(juego, Pantallas.MENU));
        }
    }

    @Override
    public void actualizarVista() {
        escalarLogo();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        // Libera las texturas
        texturaLogo.dispose();
    }
}
