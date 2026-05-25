package com.example.laba4

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "social_network.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "Users"
        const val COL_USER_ID = "user_id"
        const val COL_USER_NAME = "username"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password_hash"
        const val COL_CREATED_AT = "created_at"

        const val TABLE_FRIENDS = "Friends"
        const val COL_FRIEND_USER_ID = "user_id"
        const val COL_FRIEND_ID = "friend_id"
        const val COL_FRIEND_STATUS = "status"

        const val TABLE_POSTS = "Posts"
        const val COL_POST_ID = "post_id"
        const val COL_POST_USER_ID = "user_id"
        const val COL_POST_CONTENT = "content"
        const val COL_POST_CREATED_AT = "created_at"

        const val TABLE_COMMENTS = "Comments"
        const val COL_COMMENT_ID = "comment_id"
        const val COL_COMMENT_POST_ID = "post_id"
        const val COL_COMMENT_USER_ID = "user_id"
        const val COL_COMMENT_TEXT = "comment_text"
        const val COL_COMMENT_CREATED_AT = "created_at"

        const val TABLE_LIKES = "Likes"
        const val COL_LIKE_POST_ID = "post_id"
        const val COL_LIKE_USER_ID = "user_id"
        const val COL_LIKE_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL UNIQUE,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_CREATED_AT TEXT DEFAULT (datetime('now'))
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_FRIENDS (
                $COL_FRIEND_USER_ID INTEGER NOT NULL,
                $COL_FRIEND_ID INTEGER NOT NULL,
                $COL_FRIEND_STATUS TEXT DEFAULT 'pending'
                    CHECK($COL_FRIEND_STATUS IN ('pending', 'accepted', 'blocked')),
                PRIMARY KEY ($COL_FRIEND_USER_ID, $COL_FRIEND_ID),
                FOREIGN KEY ($COL_FRIEND_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
                    ON DELETE CASCADE,
                FOREIGN KEY ($COL_FRIEND_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
                    ON DELETE CASCADE,
                CHECK ($COL_FRIEND_USER_ID != $COL_FRIEND_ID)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_POSTS (
                $COL_POST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_POST_USER_ID INTEGER NOT NULL,
                $COL_POST_CONTENT TEXT NOT NULL,
                $COL_POST_CREATED_AT TEXT DEFAULT (datetime('now')),
                FOREIGN KEY ($COL_POST_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_COMMENTS (
                $COL_COMMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_COMMENT_POST_ID INTEGER NOT NULL,
                $COL_COMMENT_USER_ID INTEGER NOT NULL,
                $COL_COMMENT_TEXT TEXT NOT NULL,
                $COL_COMMENT_CREATED_AT TEXT DEFAULT (datetime('now')),
                FOREIGN KEY ($COL_COMMENT_POST_ID) REFERENCES $TABLE_POSTS($COL_POST_ID)
                    ON DELETE CASCADE,
                FOREIGN KEY ($COL_COMMENT_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_LIKES (
                $COL_LIKE_POST_ID INTEGER NOT NULL,
                $COL_LIKE_USER_ID INTEGER NOT NULL,
                $COL_LIKE_CREATED_AT TEXT DEFAULT (datetime('now')),
                PRIMARY KEY ($COL_LIKE_POST_ID, $COL_LIKE_USER_ID),
                FOREIGN KEY ($COL_LIKE_POST_ID) REFERENCES $TABLE_POSTS($COL_POST_ID)
                    ON DELETE CASCADE,
                FOREIGN KEY ($COL_LIKE_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        // Користувачі
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_NAME, $COL_EMAIL, $COL_PASSWORD) VALUES ('Ярослав', 'yaroslav@gmail.com', 'hash_yar')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_NAME, $COL_EMAIL, $COL_PASSWORD) VALUES ('Олексій', 'oleksiy@gmail.com', 'hash_ole')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_NAME, $COL_EMAIL, $COL_PASSWORD) VALUES ('Марія', 'maria@gmail.com', 'hash_mar')")

        // Пости
        db.execSQL("INSERT INTO $TABLE_POSTS ($COL_POST_USER_ID, $COL_POST_CONTENT) VALUES (1, 'Сьогодні чудовий день для програмування!')")
        db.execSQL("INSERT INTO $TABLE_POSTS ($COL_POST_USER_ID, $COL_POST_CONTENT) VALUES (2, 'Вивчаю Kotlin — дуже цікава мова!')")
        db.execSQL("INSERT INTO $TABLE_POSTS ($COL_POST_USER_ID, $COL_POST_CONTENT) VALUES (3, 'Люблю Android розробку!')")

        // Друзі
        db.execSQL("INSERT INTO $TABLE_FRIENDS ($COL_FRIEND_USER_ID, $COL_FRIEND_ID, $COL_FRIEND_STATUS) VALUES (1, 2, 'accepted')")
        db.execSQL("INSERT INTO $TABLE_FRIENDS ($COL_FRIEND_USER_ID, $COL_FRIEND_ID, $COL_FRIEND_STATUS) VALUES (1, 3, 'accepted')")
        db.execSQL("INSERT INTO $TABLE_FRIENDS ($COL_FRIEND_USER_ID, $COL_FRIEND_ID, $COL_FRIEND_STATUS) VALUES (2, 3, 'pending')")

        // Коментарі
        db.execSQL("INSERT INTO $TABLE_COMMENTS ($COL_COMMENT_POST_ID, $COL_COMMENT_USER_ID, $COL_COMMENT_TEXT) VALUES (1, 2, 'Згоден, Kotlin — найкраще!')")
        db.execSQL("INSERT INTO $TABLE_COMMENTS ($COL_COMMENT_POST_ID, $COL_COMMENT_USER_ID, $COL_COMMENT_TEXT) VALUES (1, 3, 'Гарний пост, Ярославе!')")
        db.execSQL("INSERT INTO $TABLE_COMMENTS ($COL_COMMENT_POST_ID, $COL_COMMENT_USER_ID, $COL_COMMENT_TEXT) VALUES (2, 1, 'Kotlin — це супер!')")

        // Лайки
        db.execSQL("INSERT INTO $TABLE_LIKES ($COL_LIKE_POST_ID, $COL_LIKE_USER_ID) VALUES (1, 2)")
        db.execSQL("INSERT INTO $TABLE_LIKES ($COL_LIKE_POST_ID, $COL_LIKE_USER_ID) VALUES (1, 3)")
        db.execSQL("INSERT INTO $TABLE_LIKES ($COL_LIKE_POST_ID, $COL_LIKE_USER_ID) VALUES (2, 1)")
        db.execSQL("INSERT INTO $TABLE_LIKES ($COL_LIKE_POST_ID, $COL_LIKE_USER_ID) VALUES (3, 1)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LIKES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FRIENDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}