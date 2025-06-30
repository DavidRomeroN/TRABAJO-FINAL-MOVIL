package pe.edu.upeu.granturismojpc.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.edu.upeu.granturismojpc.repository.ActividadDetalleRepository
import pe.edu.upeu.granturismojpc.repository.ActividadDetalleRepositoryImpl
import pe.edu.upeu.granturismojpc.repository.ActividadRepository
import pe.edu.upeu.granturismojpc.repository.ActividadRepositoryImp
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepository
import pe.edu.upeu.granturismojpc.repository.CarritoItemRepositoryImp
import pe.edu.upeu.granturismojpc.repository.CarritoRepository
import pe.edu.upeu.granturismojpc.repository.CarritoRepositoryImp
import pe.edu.upeu.granturismojpc.repository.DestinoRepository
import pe.edu.upeu.granturismojpc.repository.DestinoRepositoryImp
import pe.edu.upeu.granturismojpc.repository.FavoritoRepository
import pe.edu.upeu.granturismojpc.repository.FavoritoRepositoryImp
import pe.edu.upeu.granturismojpc.repository.PaqueteDetalleRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteDetalleRepositoryImpl
import pe.edu.upeu.granturismojpc.repository.PaqueteRepository
import pe.edu.upeu.granturismojpc.repository.PaqueteRepositoryImp
import pe.edu.upeu.granturismojpc.repository.PreferenceRepository
import pe.edu.upeu.granturismojpc.repository.PreferenceRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ProveedorRepository
import pe.edu.upeu.granturismojpc.repository.ProveedorRepositoryImp
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepository
import pe.edu.upeu.granturismojpc.repository.QrBlockchainRepositoryImpl
import pe.edu.upeu.granturismojpc.repository.ResenaRepository
import pe.edu.upeu.granturismojpc.repository.ResenaRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ReservaRepository
import pe.edu.upeu.granturismojpc.repository.ReservaRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepository
import pe.edu.upeu.granturismojpc.repository.ServicioAlimentacionRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepository
import pe.edu.upeu.granturismojpc.repository.ServicioArtesaniaRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepository
import pe.edu.upeu.granturismojpc.repository.ServicioHoteleraRepositoryImp
import pe.edu.upeu.granturismojpc.repository.ServicioRepository
import pe.edu.upeu.granturismojpc.repository.ServicioRepositoryImp
import pe.edu.upeu.granturismojpc.repository.TipoServicioRepository
import pe.edu.upeu.granturismojpc.repository.TipoServicioRepositoryImp
import pe.edu.upeu.granturismojpc.repository.UsuarioRepository
import pe.edu.upeu.granturismojpc.repository.UsuarioRepositoryImp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun userRepository(userRepos:
                                    UsuarioRepositoryImp): UsuarioRepository
    @Binds
    @Singleton
    abstract fun paqueteRepository(packRepos:
                                PaqueteRepositoryImp): PaqueteRepository
    @Binds
    @Singleton
    abstract fun proveedorRepository(provRepos:
                                   ProveedorRepositoryImp): ProveedorRepository
    @Binds
    @Singleton
    abstract fun destinoRepository(destinoRepos:
                                         DestinoRepositoryImp): DestinoRepository


    @Binds
    @Singleton
    abstract fun servicioRepository(servRepos:
                                     ServicioRepositoryImp): ServicioRepository
    @Binds
    @Singleton
    abstract fun tipoServicioRepository(tiposervRepos:
                                     TipoServicioRepositoryImp): TipoServicioRepository

    @Binds
    @Singleton
    abstract fun servicioAliRepository(servaliRepos:
                                        ServicioAlimentacionRepositoryImp): ServicioAlimentacionRepository

    @Binds
    @Singleton
    abstract fun servicioArtRepository(servartRepos:
                                       ServicioArtesaniaRepositoryImp): ServicioArtesaniaRepository

    @Binds
    @Singleton
    abstract fun servicioHotelRepository(servhotRepos:
                                       ServicioHoteleraRepositoryImp): ServicioHoteleraRepository

    @Binds
    @Singleton
    abstract fun resenaRepository(resRepos:
                                         ResenaRepositoryImp): ResenaRepository
    @Binds
    @Singleton
    abstract fun paqueteDetalleRepository(packdetRepos:
                                   PaqueteDetalleRepositoryImpl): PaqueteDetalleRepository

    @Binds
    @Singleton
    abstract fun actividadRepository(actvRepos:
                                          ActividadRepositoryImp): ActividadRepository

    @Binds
    @Singleton
    abstract fun actividadDetalleRepository(actvdetRepos:
                                          ActividadDetalleRepositoryImpl): ActividadDetalleRepository

    @Binds
    @Singleton
    abstract fun carritoRepository(carRepos:
                                            CarritoRepositoryImp): CarritoRepository

    @Binds
    @Singleton
    abstract fun carritoItemRepository(carItemRepos:
                                   CarritoItemRepositoryImp): CarritoItemRepository

    @Binds
    @Singleton
    abstract fun favoritoRepository(favRepos:
                                       FavoritoRepositoryImp): FavoritoRepository
    @Binds
    @Singleton
    abstract fun preferenceRepository(prefRepos:
                                    PreferenceRepositoryImp): PreferenceRepository

    @Binds
    @Singleton
    abstract fun reservaRepository(reservaRepos: ReservaRepositoryImp): ReservaRepository

    @Binds
    @Singleton
    abstract fun qrBlockchainRepository(
        qrBlockchainRepos: QrBlockchainRepositoryImpl
    ): QrBlockchainRepository
}
