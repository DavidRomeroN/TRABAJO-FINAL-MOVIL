package pe.edu.upeu.granturismojpc.ui.presentation.screens.recomendacionPorAI

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecomendacionModule {

    @Provides
    @Singleton
    fun provideRecomendacionRepository(
        api: RecomendacionApiService
    ): RecomendacionRepository {
        return RecomendacionRepositoryImpl(api)
    }
}
