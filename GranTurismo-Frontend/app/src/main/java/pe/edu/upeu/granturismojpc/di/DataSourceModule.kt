package pe.edu.upeu.granturismojpc.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import pe.edu.upeu.granturismojpc.data.local.DbDataSource
import pe.edu.upeu.granturismojpc.data.local.dao.FavoritoDao
import pe.edu.upeu.granturismojpc.data.local.dao.PaqueteDao
import pe.edu.upeu.granturismojpc.data.remote.RestActividad
import pe.edu.upeu.granturismojpc.data.remote.RestActividadDetalle
import pe.edu.upeu.granturismojpc.data.remote.RestCarrito
import pe.edu.upeu.granturismojpc.data.remote.RestCarritoItem
import pe.edu.upeu.granturismojpc.data.remote.RestDestino
import pe.edu.upeu.granturismojpc.data.remote.RestFavorito
import pe.edu.upeu.granturismojpc.data.remote.RestPaquete
import pe.edu.upeu.granturismojpc.data.remote.RestPaqueteDetalle
import pe.edu.upeu.granturismojpc.data.remote.RestPreference
import pe.edu.upeu.granturismojpc.data.remote.RestProveedor
import pe.edu.upeu.granturismojpc.data.remote.RestQrBlockchain
import pe.edu.upeu.granturismojpc.data.remote.RestResena
import pe.edu.upeu.granturismojpc.data.remote.RestReserva
import pe.edu.upeu.granturismojpc.data.remote.RestServicio
import pe.edu.upeu.granturismojpc.data.remote.RestServicioAlimentacion
import pe.edu.upeu.granturismojpc.data.remote.RestServicioArtesania
import pe.edu.upeu.granturismojpc.data.remote.RestServicioHotelera
import pe.edu.upeu.granturismojpc.data.remote.RestTipoServicio
import pe.edu.upeu.granturismojpc.data.remote.RestUsuario
import pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI.RecomendacionApiService
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    var retrofit: Retrofit?=null
    @Singleton
    @Provides
    @Named("BaseUrl")
    fun provideBaseUrl()= TokenUtils.API_URL
    @Singleton
    @Provides
    fun provideRetrofit(@Named("BaseUrl") baseUrl:String):
            Retrofit {
        val okHttpClient= OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        if (retrofit==null){
            retrofit= Retrofit.Builder()

                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(baseUrl).build()
        }
        return retrofit!!
    }
    @Singleton
    @Provides
    fun restUsuario(retrofit: Retrofit): RestUsuario{
        return retrofit.create(RestUsuario::class.java)
    }

    @Singleton
    @Provides
    fun restPaquete(retrofit: Retrofit): RestPaquete{
        return retrofit.create(RestPaquete::class.java)
    }

    @Singleton
    @Provides
    fun restProveedor(retrofit: Retrofit): RestProveedor{
        return retrofit.create(RestProveedor::class.java)
    }

    @Singleton
    @Provides
    fun restDestino(retrofit: Retrofit): RestDestino{
        return retrofit.create(RestDestino::class.java)
    }
    @Singleton
    @Provides
    fun restServicio(retrofit: Retrofit): RestServicio{
        return retrofit.create(RestServicio::class.java)
    }
    @Singleton
    @Provides
    fun restTipoServicio(retrofit: Retrofit): RestTipoServicio{
        return retrofit.create(RestTipoServicio::class.java)
    }

    @Singleton
    @Provides
    fun restTipoServicioAlimentacion(retrofit: Retrofit): RestServicioAlimentacion{
        return retrofit.create(RestServicioAlimentacion::class.java)
    }
    @Singleton
    @Provides
    fun restTipoServicioArtesania(retrofit: Retrofit): RestServicioArtesania{
        return retrofit.create(RestServicioArtesania::class.java)
    }

    @Singleton
    @Provides
    fun restTipoServicioHotelera(retrofit: Retrofit): RestServicioHotelera{
        return retrofit.create(RestServicioHotelera::class.java)
    }

    @Singleton
    @Provides
    fun restResenas(retrofit: Retrofit): RestResena{
        return retrofit.create(RestResena::class.java)
    }

    @Singleton
    @Provides
    fun restPaqueteDetalle(retrofit: Retrofit): RestPaqueteDetalle{
        return retrofit.create(RestPaqueteDetalle::class.java)
    }
    @Singleton
    @Provides
    fun restActividad(retrofit: Retrofit): RestActividad{
        return retrofit.create(RestActividad::class.java)
    }
    @Singleton
    @Provides
    fun restActividadDetalle(retrofit: Retrofit): RestActividadDetalle{
        return retrofit.create(RestActividadDetalle::class.java)
    }

    @Singleton
    @Provides
    fun restCarrito(retrofit: Retrofit): RestCarrito{
        return retrofit.create(RestCarrito::class.java)
    }
    @Singleton
    @Provides
    fun restCarritoItem(retrofit: Retrofit): RestCarritoItem{
        return retrofit.create(RestCarritoItem::class.java)
    }

    @Singleton
    @Provides
    fun restFavorito(retrofit: Retrofit): RestFavorito{
        return retrofit.create(RestFavorito::class.java)
    }
    @Singleton
    @Provides
    fun restPreference(retrofit: Retrofit): RestPreference{
        return retrofit.create(RestPreference::class.java)
    }

    @Singleton
    @Provides
    fun restReserva(retrofit: Retrofit): RestReserva {
        return retrofit.create(RestReserva::class.java)
    }

    @Singleton
    @Provides
    fun restQrBlockchain(retrofit: Retrofit): RestQrBlockchain {
        return retrofit.create(RestQrBlockchain::class.java)
    }


    @Provides
    @Singleton
    fun provideRecomendacionApiService(retrofit: Retrofit): RecomendacionApiService {
        return retrofit.create(RecomendacionApiService::class.java)
    }

    //Configuracion BD Local
    @Singleton
    @Provides
    fun dbDataSource(@ApplicationContext context: Context):
            DbDataSource{
        return Room.databaseBuilder(context,
            DbDataSource::class.java,
            "almacen_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun favoritoDao(db:DbDataSource): FavoritoDao{
        return db.favoritoDao()
    }
    @Singleton
    @Provides
    fun paqueteDao(db:DbDataSource): PaqueteDao{
        return db.paqueteDao()
    }


}