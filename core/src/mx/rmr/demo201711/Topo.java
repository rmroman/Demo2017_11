package mx.rmr.demo201711;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by roberto on 11/02/17.
 */
public class Topo extends Objeto
{
    private EstadoTopo estado;

    // Movimiento
    private float vy = 20;   // velocidad en y (pixeles por segundo)
    private float alturaActual; // El tamaño real actual (cambiando)
    private float alturaOriginal;   // Altura inicial (no cambia)
    private float tiempoEscondido;
    private float tiempoAtontado;

    private Texture texturaEstrellas;   // Golpeado

    private boolean seEscondio = false;

    public void setTexturaEstrellas(Texture texturaEstrellas) {
        this.texturaEstrellas = texturaEstrellas;
    }

    public Topo(Texture textura, float x, float y) {
        super(textura, x, y);
        if (MathUtils.randomBoolean()) {
            estado = EstadoTopo.BAJANDO;
            alturaActual = sprite.getHeight();
        } else {
            estado = EstadoTopo.SUBIENDO;
            alturaActual = 5;
        }
        alturaOriginal = sprite.getHeight();
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        super.dibujar(batch);
        if (estado==EstadoTopo.ATONTADO) {
            batch.draw(texturaEstrellas,
                    sprite.getX()+sprite.getWidth()/2-texturaEstrellas.getWidth()/2,
                    sprite.getY()+sprite.getHeight()/2-texturaEstrellas.getHeight()/2);
        }
    }

    // Actualiza la posición del objeto (tamaño, subiendo y bajando)
    public void actualizar(float delta) {
        switch (estado) {
            case BAJANDO:
                alturaActual -= delta*vy;
                if (alturaActual<=0) {
                    alturaActual = 0;
                    tiempoEscondido = MathUtils.random(0.1f,1.5f);
                    estado = EstadoTopo.ESCONDIDO;
                    // Se escapó del usuario!
                    seEscondio = true;
                }
                break;
            case SUBIENDO:
                alturaActual += delta*vy;
                if (alturaActual>=alturaOriginal) {
                    alturaActual = alturaOriginal;
                    estado = EstadoTopo.BAJANDO;
                }
                break;
            case ESCONDIDO:
                tiempoEscondido -= delta;
                if (tiempoEscondido<=0) {
                    estado = EstadoTopo.SUBIENDO;
                    seEscondio = false;
                    // Aumenta la velocidad
                    vy *= 1.2f;
                }
                break;
            case ATONTADO:
                sprite.setScale(sprite.getScaleX()/1.05f);
                sprite.rotate(20);
                tiempoAtontado -= delta;
                if (tiempoAtontado<=0) {
                    tiempoEscondido = MathUtils.random(0.1f,1.5f);
                    estado = EstadoTopo.ESCONDIDO;
                    sprite.setRotation(0);
                    alturaActual = 0;
                    sprite.setScale(1);
                }
        }

        // Recortar la imagen para cambiar su altura vertical
        sprite.setRegion(0, 0, (int)sprite.getWidth(), (int)alturaActual);
        sprite.setSize(sprite.getWidth(), alturaActual);
    }

    @Override
    public boolean contiene(Vector3 v) {
        if (estado==EstadoTopo.BAJANDO || estado==EstadoTopo.SUBIENDO) {
            return super.contiene(v);
        }
        return false;
    }

    public void desaparecer() {
        tiempoAtontado = MathUtils.random(0.1f,1.5f);
        estado = EstadoTopo.ATONTADO;
    }

    public EstadoTopo getEstado() {
        return estado;
    }

    public boolean seHaEscondido() {
        if (seEscondio) {
            seEscondio = false;
            return true;
        }
        return false;
    }

    public void reset() {
        seEscondio = false;
        vy = 20;
        if (MathUtils.randomBoolean()) {
            estado = EstadoTopo.BAJANDO;
            alturaActual = alturaOriginal;
        } else {
            estado = EstadoTopo.SUBIENDO;
            alturaActual = 0;
        }
    }
}
