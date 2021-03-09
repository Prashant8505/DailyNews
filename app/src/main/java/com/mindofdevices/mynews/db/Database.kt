package com.mindofdevices.mynews.db

import android.content.Context
import androidx.room.*
import androidx.room.Database
import com.mindofdevices.mynews.Article

@Database(
        entities = [Article::class],
        version = 1

)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun getarticleDao(): articleDao

    companion object{
        @Volatile
        private var instance: com.mindofdevices.mynews.db.Database? = null
        private val LOCK  = Any()

        operator  fun invoke(context : Context) = instance?: synchronized(LOCK){
            instance ?: createDatabase(context).also {instance = it}
        }

        private fun createDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        com.mindofdevices.mynews.db.Database::class.java,
                        "article_db.db"
                ).build()
    }

}