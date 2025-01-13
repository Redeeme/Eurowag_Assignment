package eurowag.assignment

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eurowag.assignment.database.LocationPointDao
import eurowag.assignment.database.LocationRepoImpl
import eurowag.assignment.database.LocationRepository
import eurowag.assignment.database.MyDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

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