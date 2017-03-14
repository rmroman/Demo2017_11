package mx.rmr.demo201711;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Esta es la aplicación principal.
 */
public class Demo extends Game
{
    // Hay un SOLO assetManager para el juego
    private final AssetManager assetManager;

    public Demo() {
        assetManager = new AssetManager();
    }

	@Override
	public void create () {
        // Lo preparamos para que cargue mapas
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        // Pone la pantalla inicial (Splash)
        setScreen(new PantallaInicial(this));
	}

	// Para que las otras pantallas usen el assetManager
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        assetManager.clear();
    }
}
