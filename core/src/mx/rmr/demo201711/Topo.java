package mx.rmr.demo201711;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by roberto on 11/02/17.
 */
public class Topo extends Objeto
{
    private EstadoTopo estado;

    // Movimiento
    private float vy = 40;   // velocidad en y (pixeles por segundo)
    private float alturaActual; // El tamaño real actual (cambiando)
    private float alturaOriginal;   // Altura inicial (no cambia)
    private float tiempoEscondido;
    private float tiempoAtontado;

    public Topo(Texture textura, float x, float y) {
        super(textura, x, y);
        estado = EstadoTopo.BAJANDO;
        alturaActual = sprite.getHeight();
        alturaOriginal = sprite.getHeight();
    }

    // Actualiza la posición del objeto (tamaño, subiendo y bajando)
    public void actualizar(float delta) {
        switch (estado) {
            case BAJANDO:
                alturaActual -= delta*vy;
                if (alturaActual<=0) {
                    alturaActual = 0;
                    tiempoEscondido = MathUtils.random(0.2f,1.5f);
                    estado = EstadoTopo.ESCONDIDO;
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
                }
                break;
            case ATONTADO:
                sprite.rotate(10);
                tiempoAtontado -= delta;
                if (tiempoAtontado<=0) {
                    tiempoEscondido = MathUtils.random(0.5f,1.5f);
                    estado = EstadoTopo.ESCONDIDO;
                    sprite.setRotation(0);
                    alturaActual = 0;
                }
        }

        // Recortar la imagen para cambiar su altura vertical
        sprite.setRegion(0, 0, (int)sprite.getWidth(), (int)alturaActual);
        sprite.setSize(sprite.getWidth(), alturaActual);
    }

    public boolean contiene(Vector3 v) {
        float x = v.x;
        float y = v.y;

        return x>=sprite.getX() && x<=sprite.getX()+sprite.getWidth()
                && y>=sprite.getY() && y<=sprite.getY()+sprite.getHeight();
    }

    public void desaparecer() {
        //alturaActual = 0;
        tiempoAtontado = MathUtils.random(0.2f,1.5f);
        estado = EstadoTopo.ATONTADO;
    }
}
