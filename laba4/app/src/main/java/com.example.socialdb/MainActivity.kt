package com.example.laba4
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var textOutput: TextView
    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPost: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        textOutput    = findViewById(R.id.textOutput)
        editUsername  = findViewById(R.id.editUsername)
        editEmail     = findViewById(R.id.editEmail)
        editPost      = findViewById(R.id.editPost)

        findViewById<Button>(R.id.btnAddUser).setOnClickListener { addUser() }
        findViewById<Button>(R.id.btnAddPost).setOnClickListener { addPost() }
        findViewById<Button>(R.id.btnShowUsers).setOnClickListener { showUsers() }
        findViewById<Button>(R.id.btnShowPosts).setOnClickListener { showPosts() }
        findViewById<Button>(R.id.btnShowStats).setOnClickListener { showStats() }

        showUsers()
    }

    private fun addUser() {
        val name  = editUsername.text.toString().trim()
        val email = editEmail.text.toString().trim()
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Введіть ім'я та email", Toast.LENGTH_SHORT).show()
            return
        }
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_NAME, name)
            put(DatabaseHelper.COL_EMAIL, email)
            put(DatabaseHelper.COL_PASSWORD, "hash_$name")
        }
        val id = db.writableDatabase.insert(DatabaseHelper.TABLE_USERS, null, values)
        if (id == -1L) {
            Toast.makeText(this, "Користувач вже існує!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Користувача додано (id=$id)", Toast.LENGTH_SHORT).show()
            editUsername.text.clear()
            editEmail.text.clear()
            showUsers()
        }
    }

    private fun addPost() {
        val content = editPost.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, "Введіть текст поста", Toast.LENGTH_SHORT).show()
            return
        }
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_POST_USER_ID, 1)
            put(DatabaseHelper.COL_POST_CONTENT, content)
        }
        val id = db.writableDatabase.insert(DatabaseHelper.TABLE_POSTS, null, values)
        Toast.makeText(this, "Пост додано (id=$id)", Toast.LENGTH_SHORT).show()
        editPost.text.clear()
        showPosts()
    }

    private fun showUsers() {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT ${DatabaseHelper.COL_USER_ID}, ${DatabaseHelper.COL_USER_NAME}, ${DatabaseHelper.COL_EMAIL} FROM ${DatabaseHelper.TABLE_USERS}",
            null
        )
        val sb = StringBuilder("=== КОРИСТУВАЧІ ===\n")
        while (cursor.moveToNext()) {
            sb.append("ID: ${cursor.getInt(0)} | ${cursor.getString(1)} | ${cursor.getString(2)}\n")
        }
        cursor.close()
        textOutput.text = sb.toString()
    }

    private fun showPosts() {
        val cursor = db.readableDatabase.rawQuery(
            """SELECT p.${DatabaseHelper.COL_POST_ID}, u.${DatabaseHelper.COL_USER_NAME}, p.${DatabaseHelper.COL_POST_CONTENT}
               FROM ${DatabaseHelper.TABLE_POSTS} p
               JOIN ${DatabaseHelper.TABLE_USERS} u ON p.${DatabaseHelper.COL_POST_USER_ID} = u.${DatabaseHelper.COL_USER_ID}""",
            null
        )
        val sb = StringBuilder("=== ПОСТИ ===\n")
        while (cursor.moveToNext()) {
            sb.append("ID: ${cursor.getInt(0)} | ${cursor.getString(1)}: ${cursor.getString(2)}\n")
        }
        cursor.close()
        textOutput.text = sb.toString()
    }

    private fun showStats() {
        val dbRead = db.readableDatabase
        val users    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}", null)
        val posts    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_POSTS}", null)
        val comments = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_COMMENTS}", null)
        val likes    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_LIKES}", null)
        val friends  = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_FRIENDS}", null)

        users.moveToFirst(); posts.moveToFirst()
        comments.moveToFirst(); likes.moveToFirst(); friends.moveToFirst()

        textOutput.text = """
            === СТАТИСТИКА ===
            Користувачів: ${users.getInt(0)}
            Постів: ${posts.getInt(0)}
            Коментарів: ${comments.getInt(0)}
            Лайків: ${likes.getInt(0)}
            Зв'язків друзів: ${friends.getInt(0)}
        """.trimIndent()

        users.close(); posts.close(); comments.close(); likes.close(); friends.close()
    }
}