package eurowag.assignment.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eurowag.assignment.database.daos.LocationPointDao
import eurowag.assignment.database.LocationRepoImpl
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.database.MyDatabase
import eurowag.assignment.utils.MySharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher


@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    fun provideSharedPreferences(
        context: Context
    ): MySharedPreferences = MySharedPreferences(context)

    @Provides
    @IoDispatcher
    fun provideIoDispatcher() : CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher() : CoroutineDispatcher = Dispatchers.Default


    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): MyDatabase {
        return Room
            .databaseBuilder(
                context,
                MyDatabase::class.java,
                MyDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLocationPointDao(
        myDatabase: MyDatabase
    ): LocationPointDao = myDatabase.locationPointDao()

    @Provides
    fun provideLocationPointRepository(
        locationPointDao: LocationPointDao
    ): LocationRepository = LocationRepoImpl(locationPointDao)
}